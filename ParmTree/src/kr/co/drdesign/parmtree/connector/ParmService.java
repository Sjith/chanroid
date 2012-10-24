package kr.co.drdesign.parmtree.connector;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import kr.co.drdesign.parmtree.util.ParmUtil;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttBrokerUnavailableException;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttNotConnectedException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;

public class ParmService extends Service implements MqttSimpleCallback {
	/************************************************************************/
	/*                             ���                                      */
	/************************************************************************/

	// �������� ���� �ĺ��ϴ� ������ ��.
	// ȯ�� ������ �׼��� �Ѵٰų� �ϴ� �뵵�� ���.
	public static final String APP_ID = "kr.co.drdesign.client";

	// �޽��� ���Ž� UI �� �뺸�ϴ� �� ���.
	public static final String MSG_RECEIVED_INTENT = "kr.co.drdesign.client.service.MSGRECVD";
	public static final String MSG_RECEIVED_TOPIC  = "kr.co.drdesign.client.service.MSGRECVD_TOPIC";
	public static final String MSG_RECEIVED_MSG    = "kr.co.drdesign.client.service.MSGRECVD_MSGBODY";

	// �������� ���� ���¸� ǥ���ϴ� �� ���.
	public static final String STATUS_INTENT = "kr.co.drdesign.client.service.STATUS";
	public static final String STATUS_MSG    = "kr.co.drdesign.client.service.STATUS_MSG";

	// ���� �� �����ٸ��� ���.
	public static final String PING_ACTION = "kr.co.drdesign.client.service.PING";

	// ����ǥ���� �˸��� ���.
	public static final String TAG = "Gruvice";
	public static final int NOTIFICATION_ONGOING = 1; // ������ �˸�
	public static final int NOTIFICATION_UPDATE  = 2; // �׳� �˸�

	// ���� ���¸� ��Ÿ���� �����
	public enum ConnectionStatus
	{
		INITIAL,                            // �ʱ����
		CONNECTING,                         // ������
		CONNECTED,                          // �����
		NOTCONNECTED_WAITINGFORINTERNET,    // ���ͳ� ���� ��������� ������ ������� ����
		NOTCONNECTED_USERDISCONNECT,        // ����ڰ� ��������� ���� ������ ��û
		NOTCONNECTED_DATADISABLED,          // ����ڰ� ������ �׼����� ����
		NOTCONNECTED_UNKNOWNREASON          // �� ���� � ������ ���� ������� ����        
	}

	// MQTT ���. ���� ID ���� �ִ� �ڸ����� ����.
	// 2011-05-06 �ܸ��� ���̵�� �÷���, ���, ��Ż�, �����縶�� �� �޶� �� ��ġ������...
	// 2011-05-17 �÷��� ���� ��������ʳ�? ���� �ȵ���̵��ε� ���� ���������ۿ��� ����
	// ���ķ� �׽�Ʈ�Ҷ� ��¿?
	// 2011-05-06 22 -> 26
	// 2011-05-11 26 -> 40
	public static final int MAX_CLIENTID_LENGTH = 40;

	/************************************************************************/
	/*           �������� ���� ���¸� �����ϴµ� �ʿ��� ����                        */
	/************************************************************************/

	// MQTT client ���� ���� ���¸� ������ �ִ� ��ü.
	private ConnectionStatus connectionStatus = ConnectionStatus.INITIAL;

	/************************************************************************/
	/*                    Ǫ�ü��� ���ῡ �ʿ��� ����                            */
	/************************************************************************/


	private String          HOST       = "";
	private String			Updateurl  = "";

	public static String          clientID            = "";    

	// �� ������ �޽��� ���Ŀ�� ��ȣ�ۿ��� �ϱ� ���� �⺻���� ���� ����Ѵ�.
	private int             PORT     = 1883; // ���� ������ ���� ��Ʈ ��ȣ
	private MqttPersistence usePersistence       = null;
	private boolean         cleanStart           = false;
	private int[]           QOS   = { 0 } ; // QOS

	//   �������� ������ �����ϱ� ���� ���� ������ �ֱ�.
	//
	//   �ʹ� ª�� �����ϸ� ���͸� �Ҹ� ��������.
	//   �ʹ� ��� �����ϸ� ������ ���� �� ���� ������� Ǫ�� �˸��� ���� �� ����.
	//
	//   ���͸� ���ɰ� ������ ������ ����Ͽ� ������ �ڵ鸵 �� �־�� �Ѵ�.
	//
	//   �̰��� ��Ʈ��ũ ���� �����ð��� �� ��쿡 ���� �ֱ��̴�.
	//   �Ϲ������� �̰��� ��Ʈ��ũ ��ڰ� ������ ���� �ֱ⺸�� ����.
	//   ��� ������ Keep-Alive �޽����� ���� �־�� �Ѵ�.

	private short           keepAliveSeconds     = 10 * 60;
	// 2011-05-30 �и� Second ��� ��õǾ� �ִµ��� �ұ��ϰ� ���� ms �� �����ϴ°� ����.

	// �̰��� Ŭ���̾�Ʈ ���� ���Ŀ ������ �ڽ��� �ĺ��ϰ� �ϴ� ID�̴�.
	// ���Ŀ ������ �����ؾ� �ϸ�, �� Ŭ���̾�Ʈ�� ������ ID�� ����Ͽ� ������ �� ����.
	private String          mqttClientId = null; // ������ null ������ �ؿ��� ������ �� ���̴�.

	/************************************************************************/
	/*                           ��Ÿ ���� ��ü                                */
	/************************************************************************/
	// �޽��� ���Ŀ ���� ��ü
	private IMqttClient mqttClient = null;

	// deviceID �� �������� ���� ��ƿ��Ƽ ��ü
	private ParmUtil util;

	// ȯ�漳�� ��ü
	// 2011-07-04 ���� �������� ���� �κ��� ���� ������� �ʴ� ������ ��.
	//    private SharedPreferences mPref;

	// 2011-06-08 ���� ��Ʈ���� ���� Ŭ���� ����� ����.
	private Socket mSocket = null;

	// �����κ��� Ǫ�ÿ�û(����Ʈ)�� �޴� ��ü
	private NetworkConnectionIntentReceiver netConnReceiver;

	// ����ڰ� ������ ��� ������ �������� �� ���񽺿� �뺸���ִ� ��ü
	// 2011-07-04 �������� ��� ������ ���Ƽ� �ϴ� ��Ȱ��ȭ.
	private BackgroundDataChangeIntentReceiver dataEnabledReceiver;
	//    private WifiSensitiveIntentReceiver wifiSensitiveReceiver;

	// �������� ��� ���θ� Ȯ���ϱ� ���� ���� ������ ��ü
	private PingSender pingSender;

	// ���������� ���� ������ ���� ������
	private ConnectionThread mConnectionThread;



	/************************************************************************/
	/*                         ����������Ŭ ���� �޼���                          */
	/************************************************************************/

	public static void actionStart(Context ctx)
	{
		// BR�̳� �ٸ� ���� ���� ��Ƽ��Ƽ �ܺο��� ȣ���� �� ����ϴ� �޼���.
		Log.i("mqtt", "Argos ActionStart()");
		Intent i = new Intent(ctx, ParmService.class);
		ctx.startService(i);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		// �ʱ� ���� ���°��� ����
		connectionStatus = ConnectionStatus.INITIAL;

		// ��Ƽ��Ƽ�� ���񽺿� ����ϱ� ���� ���δ� ����
		mBinder = new LocalBinder<ParmService>(this);

		// deviceID �� �������� ���� util ��ü ����
		util = ParmUtil.getInstance(getApplicationContext());

		// �������� ���� ����
		mConnectionThread = new ConnectionThread(HOST, PORT);

		// ȯ�漳�� ��ü ����
		//        mPref = getSharedPreferences("mPref", 2); 
		// MODE_WORLD_WRITABLE
		// ȯ�漳�� ��� ���Ŀ(MQTT Server) ID ������ ������. ���⼭�� Ŭ���̾�Ʈ id�� ����Ѵ�.
		// ���� ������ �� �� ������ �ٸ� ����� ����ص� ������. ������ Ŭ���̾�Ʈ ���� ������ ���� ����ؾ� ��.
		clientID = util.getAccountID();

		// ����ڰ� ��׶��� ������ ������ ������ ������ ������ ���� �� �ֵ��� ���ù� ���.
		dataEnabledReceiver = new BackgroundDataChangeIntentReceiver();
		registerReceiver(dataEnabledReceiver,
				new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED));

		// 2011-05-11 �������� ���Ű��� ����� ������ ���� �� �ֵ��� ���ù� ���.
		//        wifiSensitiveReceiver = new WifiSensitiveIntentReceiver();
		//        registerReceiver(wifiSensitiveReceiver,
		//        				new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));


		// ���Ŀ�� ���� ������ �����ϴ� �޼���.
		defineConnectionToBroker(HOST);
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// �ȵ���̵� 2.0���� �������� ���Ǵ� ���� ���� �޼���.
		// ���� 2.1�̻��� ���������� �� �޼��尡 ȣ����� �ʴ´�.
		handleStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// �ȵ���̵� 2.1�̻󿡼� ����� ���� ���� �޼���.
		handleStart(intent, startId);


		return START_STICKY;
	}

	public synchronized void handleStart(Intent intent, int startId)
	{
		// �� �޼��尡 ���۵Ǳ� ����, ���񽺰� ���۵��� ���� ������ ������
		// �װ��� üũ�ϰ� ��ġ�ؾ� �Ѵ�.
		Log.i("Argos", "Argos client is handleStart");
		if (mqttClient == null)
		{
			// �������� ������ ���ǵ��� ������, �� ���񽺴� ��� �����ȴ�.
			// 2011-06-07 ���񽺰� ����� �������� ���ϵ��� ������� ����.
			//          stopSelf();
			defineConnectionToBroker(HOST);
			subscribeToTopic(clientID);
			//        	handleStart(intent, startId);
			return;
		}

		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		if (cm.getBackgroundDataSetting() == false) // ������ ��׶��� ������ ����� ������ ��
		{
			Log.i("Argos", "background data disabled");
			// �������� ������¸� �����Ѵ�.
			connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

			// ������ �����Ǿ����� ǥ���Ѵ�.
			//            broadcastServiceStatus("Not connected - background data disabled");

			// ���⿡�� ���°� ����� ���� �����Ǹ�, ���񽺸� �����ϰ�
			// �ٽ� handleStart() �޼��带 ȣ���Ѵ�.
			return;
		}

		// ��Ƽ��Ƽ���� ���񽺸� �����Ѵ�. - ���񽺰� ���ʷ� ����Ǿ��ų� �Ǵ� ���񽺰� ���� �Ķ��
		// �ణ�� �ð��� �ɸ���. (startService�� ������ ȣ���ϴ��� ���񽺰� ������ ��������� �ʴ´�.)
		// ���񽺰� �̹� ����Ǿ� �ִ� ���¶�� �������� ��ſ� �ʿ��� ���� �����Ѵ�.

		//        rebroadcastStatus();
		//        rebroadcastReceivedMessages();

		// ���� ���񽺰� ����Ǿ� �ְ�, �������� ������ ���������� ������ ���¶��,
		// �Ʒ������� �ƹ��͵� ó������ �ʴ´�.
		if (isAlreadyConnected() == false)
		{
			// �������� ������¸� �����Ѵ�.
			Log.i("Argos", "isAlreadyConnected false");
			connectionStatus = ConnectionStatus.CONNECTING;

			// �̰��� ����ڰ� ��׶��� ���񽺸� ���Ƿ� �����Ͽ��� ������ ������ ����ȴ�.
			// �׷��� ����ڰ� ���͸� �Ҹ� ���ҽ�Ű�� ���� ������, �ٸ� �������� ������ �����ϱ� ��������
			// ���������� ��Ƽ�����̼��� �߻����� ���񽺰� ����ǰ� �ִٴ� ���� �˸���.         
			// 2011-05-11 onGoing �˸��� ����� �ʴ� ������ ����.
			//            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			//            Notification notification = new Notification(R.drawable.icon,
			//                                                         "Gruvice",
			//                                                         System.currentTimeMillis());
			//            notification.flags |= Notification.FLAG_ONGOING_EVENT;
			//            notification.flags |= Notification.FLAG_NO_CLEAR;
			//            Intent notificationIntent = new Intent(this, MsgReceiver.class);
			//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
			//                                                                    notificationIntent,
			//                                                                    PendingIntent.FLAG_UPDATE_CURRENT);
			//            notification.setLatestEventInfo(this, "Gruvice", "���� ������", contentIntent);
			//            nm.notify(NOTIFICATION_ONGOING, notification);

			// ������ �õ��ϱ� ���� Ŭ���̾�Ʈ�� ������ ������ ����� �ϰ��ִ����� �˻��Ѵ�.
			if (isOnline())
			{
				Log.i("Argos", "isOnline() true.");
				// �޽��� ���Ŀ�� �����Ѵ�.
				if (connectToBroker())
				{
					Log.i("Argos", "isn't subcribed.");
					// Ǫ�� �޽����� �ޱ� ���� ������ ����Ѵ�.
					// �ۿ����� �ʿ信 ���� �������� ������ ����� �� ������
					// �׷�񽺿����� ����� ������ ������ �ǹǷ� �׳� ���.
					subscribeToTopic(clientID);
					try {
						mConnectionThread.start();
						mConnectionThread.sendKeepAlive();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.getMessage();
					}
					mConnectionThread.startKeepAlives();
				}
			}
			else
			{
				Log.i("Argos", "isOnline() false");
				// �������� ������¸� �����Ѵ�.
				connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

				// ���� ������� �ʾҰų� ���� ������̶�� �뺸�Ѵ�.
				//                broadcastServiceStatus("Waiting for network connection");
			}
		}

		// �ܸ����� ��Ʈ��ũ ���� ���°� ����� ���
		// ������� Wi-Fi���� 3G��, �Ǵ� �� �ݴ��� ���
		// Ǫ�� �������� ������ ������ �� �ֱ� ������
		// ���⿡���� �̸� �����Ͽ� ����� ��Ʈ��ũ�� �ٽ� ������ �����ϰ� �Ѵ�.
		if (netConnReceiver == null)
		{
			netConnReceiver = new NetworkConnectionIntentReceiver();
			registerReceiver(netConnReceiver,
					new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		}

		// ������ ���� ȣ���Ҷ� �޴����� ����� ���� ���Ǵ� ����Ʈ�� �����Ѵ�.
		if (pingSender == null)
		{
			pingSender = new PingSender();
			registerReceiver(pingSender, new IntentFilter(PING_ACTION));
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		Log.e("Argos", "service onDestroy()");
		// ��� ������ �����Ѵ�.
		disconnectFromBroker();

		// ���������� ������ �����Ǹ� ��Ƽ�����̼��� ����Ѵ�.
		//        broadcastServiceStatus("Disconnected");

		// �������� ������ ����� �����Ѵ�.
		if (dataEnabledReceiver != null)
		{
			unregisterReceiver(dataEnabledReceiver);
			dataEnabledReceiver = null;
		}

		if (mBinder != null) {
			mBinder.close();
			mBinder = null;
		}
	}

	/************************************************************************/
	/*                          �˸� ���� �޼���                               */
	/************************************************************************/

	// �� �޼������ ���𰡰� �Ͼ�� ��Ƽ��Ƽ�� UI�� �뺸�ϱ� ���� ���ȴ�.
	// �̰͵��� �������� �����͸� �����ϸ� ���¸� �ݿ��ϵ��� ������Ʈ �� �� �ִ�.

	//    private void broadcastServiceStatus(String statusDescription)
	//    {
	//        // �̰��� ������ ���Ŀ ���� ���¿� ���� UI�� ������Ʈ �� �� �ֵ��� �������α׷��� �˸���.
	//    	// ���� ������Ʈ ������ �ʰ� ��ȣ�� ������.
	//        Intent broadcastIntent = new Intent();
	//        broadcastIntent.setAction(STATUS_INTENT);
	//        broadcastIntent.putExtra(STATUS_MSG, statusDescription);
	//        sendBroadcast(broadcastIntent);
	//    }

	//    private void broadcastReceivedMessage(String topic, String message)
	//    {
	//        // �̰��� �������϶� UI���� ǥ�õ� �� �ֵ��� �������� ���� �޽����� �����Ѵ�.
	//    	// ���� ������Ʈ ������ �ʰ� ��ȣ�� ������.
	//        Intent broadcastIntent = new Intent();
	//        broadcastIntent.setAction(MSG_RECEIVED_INTENT);
	//        broadcastIntent.putExtra(MSG_RECEIVED_TOPIC, topic);
	//        broadcastIntent.putExtra(MSG_RECEIVED_MSG,   message);
	//        sendBroadcast(broadcastIntent);
	//    }


	// ���� ���°� ����ǰų� �ϴ°� ��Ƽ�� �˸�. �޽����� �Ⱦ˸�.
	//    private void notifyUser(String alert, String title, String body)
	//    {
	//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	//        Notification notification = new Notification(R.drawable.icon, alert,
	//                                                     System.currentTimeMillis());
	//        notification.defaults |= Notification.DEFAULT_LIGHTS;
	//        notification.defaults |= Notification.DEFAULT_SOUND;
	//        notification.defaults |= Notification.DEFAULT_VIBRATE;
	//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	//        notification.ledARGB = Color.MAGENTA;
	//        Intent notificationIntent = new Intent(this, MenuActivity.class);
	//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	//        		notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	//        notification.setLatestEventInfo(this, title, body, contentIntent);
	//        nm.notify(NOTIFICATION_UPDATE, notification);
	//    }

	/************************************************************************/
	/*                        ��Ƽ��Ƽ���� ���񽺿� �����ϴ� �޼���                */
	/************************************************************************/

	// �޸� ���� �ּ�ȭ�ϸ鼭 ���ÿ��� ���ε��� �����ϱ� ���� �ڵ�� �̷���� �ִ�.
	// ��ó : Geoff Bruckner - �ڼ��� ����
	// http://groups.google.com/group/cw-android/browse_thread/thread/d026cfa71e48039b/c3b41c728fedd0e7?show_docid=c3b41c728fedd0e7

	private LocalBinder<ParmService> mBinder;

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public class LocalBinder<S> extends Binder
	{
		private WeakReference<S> mService;

		public LocalBinder(S service)
		{
			mService = new WeakReference<S>(service);
		}
		public S getService()
		{
			return mService.get();
		}
		public void close()
		{
			mService = null;
		}
	}

	//
	// ��Ƽ��Ƽ�� ���� ���񽺿� ���ε� �� �� �ִ� public �޼���
	//

	public ConnectionStatus getConnectionStatus()
	{
		return connectionStatus;
	}    

	//    public void rebroadcastStatus()
	//    {
	//        String status = "";
	//
	//        switch (connectionStatus)
	//        {
	//            case INITIAL:
	//                status = "Please wait";
	//                break;
	//            case CONNECTING:
	//                status = "Connecting...";
	//                break;
	//            case CONNECTED:
	//                status = "Connected";
	//                break;
	//            case NOTCONNECTED_UNKNOWNREASON:
	//                status = "Not connected - waiting for network connection";
	//                break;
	//            case NOTCONNECTED_USERDISCONNECT:
	//                status = "Disconnected";
	//                break;
	//            case NOTCONNECTED_DATADISABLED:
	//                status = "Not connected - background data disabled";
	//                break;
	//            case NOTCONNECTED_WAITINGFORINTERNET:
	//                status = "������ ������ �� �����ϴ�.";
	//                break;
	//        }
	//
	//        //
	//        // ���񽺿��� ������¸� �˸�.
	//        broadcastServiceStatus(status);
	//    }

	public void disconnect()
	{
		disconnectFromBroker();

		// �������� ������� ����
		connectionStatus = ConnectionStatus.NOTCONNECTED_USERDISCONNECT;

		// ���������� ������ �����Ǿ��ٴ� ���� �˸�.
		//        broadcastServiceStatus("Disconnected");
	}

	/************************************************************************/
	/*                     MQTT Ŭ�����κ��� ��ӹ޴� �޼���                     */
	/************************************************************************/

	/*
	 * �޽��� ������ ����Ǿ� ���� ������ ȣ��Ǵ� �޼���.
	 */
	public void connectionLost() throws Exception
	{

		Log.e("Argos", "Argos connection lost!!! DRMService actionStop! ");
		// �� �޼��尡 ȣ��Ǹ� �������α׷��� �����. ȭ���� ����� ���� ���� �ִ�.
		// 2011-05-27 ������ ���� �������� ����� �ʵ��� ����.
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();

		mSocket.close();

		//
		// ������ ������ �����Ǿ�����
		//

		if (isOnline() == false)
		{
			Log.i("Argos", "isOnline() false");
			connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

			// ��Ʈ��ũ�� ����Ǿ� ���� ������ �˸�.
			//            broadcastServiceStatus("Connection lost - no network connection");

			//
			// ���̻� �޽����� ���� �� ���ٴ� ���� ����ڿ��� �˸�.
			//            notifyUser("Connection lost - no network connection",
			//                       TAG, "Connection lost - no network connection");

			//
			// �ܸ��Ⱑ ��Ʈ��ũ�� �ٽ� �����Ҷ�, ��Ʈ��ũ ���� ���ù��� �����ϰ�,
			// �ٸ� ������ �õ��� ���̴�.
		}
		else
		{
			//
			// ���� ���� ������ 
			// �¶��� �����϶� ����� ��Ʈ��ũ���� �������̷� ��ȯ�ɶ��̴�.
			// 

			Log.i("Argos", "isOnline()");
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

			// ���̻� ����Ǿ����� �ʴٰ� �˸���, �ٽ� �����Ϸ��� �õ��Ѵ�.
			//            broadcastServiceStatus("Connection lost - reconnecting...");

			// �ٽ� ������ �õ��Ѵ�.
			if (connectToBroker()) {
				subscribeToTopic(clientID);
			}
		}

		wl.release();
	}

	/*
	 *   �����κ��� �޽����� �����Ͽ��� �� ����Ǵ� �޼���.
	 */
	public void publishArrived(String topic, byte[] payloadbytes, int qos, boolean retained)
	{

		Log.i("Argos", "Argos publish arrived.");
		// ���⼭ ���� ���� ���� �޴����� ��ݻ��¿� ���� �ʵ��� ��ȣ�Ѵ�.
		// �߿��� �۾��̹Ƿ� ���⼭�� �ݵ�� �����־�� �Ѵ�.
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();


		if (payloadbytes.toString().contains("message")) {
			// �޽����� ���� �´�.
			new MsgReceiver(clientID, "", "", false).start();
		} else if (payloadbytes.toString().contains("update")) {
			// DB ������Ʈ�� �Ѵ�. ��Ƽ�� ����� �ȶ���� �ʴ� �����.
			DBUpdater update = new DBUpdater();
			update.setUrl(Updateurl);
			update.setWork(DBUpdater.UPDATE_ALL, 0);
			update.start();
		}

		//
		//  �� �޽����� Argos���� ó���Ǵ� ���� �ƴϹǷ� �� String �� ���� �ʿ�� ����.
		//  ��ȣȭ�� ���� byte[] �Ǵ� HexString, �Ǵ� ������ �˰������� ��ȯ�ؼ� �޴°͵� ��ȿ�ϴ�.
		//  ���⼭�� �׳� byte[] �� �޾Ƽ� ĳ���������� ���� ���ÿ��� ��ȣȭ�� �� ��� �Ұ� ����.
		//  �ۿ����� �ݵ�� ���ڿ��� ���� �ʿ�� ����.
		//
		//        String messageBody = new String(payloadbytes);

		//
		//  ��Ƽ��Ƽ�� �������� �ƴ� ������ ���񽺿��� ������ �����͸� ������ �� �־�� �Ѵ�.
		//        if (addReceivedMessageToStore(topic, messageBody))
		//        {
		// ���� ���� ���ο� �޽����� üũ�Ѵ�.

		//
		// ��Ƽ��Ƽ�� �������̶�� ���ο� �޽����� ��Ƽ��Ƽ�� ������Ʈ �� �� �ֵ��� �˸���.
		//            broadcastReceivedMessage(topic, messageBody);

		//
		// ��Ƽ��Ƽ�� �������� �ƴ϶�� ���ο� �޽����� ���ŵǾ����� �������� �˸���.
		//            notifyUser("New data received", topic, messageBody);
		//        }

		// �޽��� ���Ž� ���񽺴� ������ �����Ǿ� �ִ°����� �����ϰ�,
		// ������ ���� ������ �ֱ⸦ �����Ѵ�.
		scheduleNextPing();

		wl.release();
	}

	/************************************************************************/
	/*                   ���Ǵ� �Ϻ� �޼��忡 ���� ����                         */
	/************************************************************************/

	/*
	 * ���ø����̼ǰ� ������ �����ϴ� Ŭ���̾�Ʈ ��ü�� �����Ѵ�.
	 */
	private void defineConnectionToBroker(String HOST)
	{
		String mqttConnSpec = "tcp://" + HOST + "@" + PORT;
		Log.i("Argos", "defineConnectionToBroker");

		try
		{
			// ���Ŀ�� ���� ������ �����Ѵ�.
			mqttClient = MqttClient.createMqttClient(mqttConnSpec, usePersistence);

			// �޽����� ���� �� �ֵ��� �� Ŭ���̾�Ʈ�� ����Ѵ�.
			// 2011-05-11 �̰͵� �ϴ� ������� �и��� ����.
			mqttClient.registerSimpleHandler(this);
			Log.i("Argos", "mqttClient registerHandler");
		}
		catch (MqttException e)
		{
			// Ʋ������ ���� �߸��Ǿ���.
			Log.i("Argos", "registerHandler Exception!!");
			mqttClient = null;
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

			//
			// ������ �������� ���ߴٰ� �˸��ų� ��Ƽ��Ƽ�� UI�� ������Ʈ�Ѵ�.
			//            broadcastServiceStatus("Invalid connection parameters");

			//
			// ������ �������� ���ߴٰ� ����ڿ��� �˸���.
			//            notifyUser("������ ������ �� �����ϴ�.", "Gruvice", "������ ������ �� �����ϴ�.");
		}
	}

	/*
	 * �޽��� ���Ŀ�� (��)�����Ѵ�.
	 */
	private boolean connectToBroker()
	{

		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();

		try
		{
			// ������ �õ��Ѵ�.
			// 2011-05-23 ���� ���ߴ� ��� ���⼭ ���ߴ°��̴�.
			// 2011-05-24 ������� �и�. �ǵ��� ��� ���������� ������.

			if (isOnline()) {
				if (!mqttClient.isConnected()) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								mqttClient.connect(generateClientId(), cleanStart, keepAliveSeconds);
								Log.e("Argos", "Argos Connected.");
								subscribeToTopic(clientID);							
							} catch (MqttNotConnectedException e) {
								// TODO Auto-generated catch block
								Log.e("Argos", "Argos Connection error.");
								scheduleNextPing();
							} catch (MqttPersistenceException e) {
								// TODO Auto-generated catch block
								Log.e("Argos", "Argos Connection Persistence error");
								scheduleNextPing();
							} catch (MqttBrokerUnavailableException e) {
								// TODO Auto-generated catch block
								Log.e("Argos", "Argos Connection BrokerUnavailable error");
								defineConnectionToBroker(HOST);
								connectToBroker();
							} catch (MqttException e) {
								// TODO Auto-generated catch block
								Log.e("Argos", "Argos Connection Error");
								scheduleNextPing();
							}
						}
					}).start();
				} else {
					subscribeToTopic(clientID);
				}
			}

			//
			// ���������� ����Ǿ��ٰ� �˸���.
			//            broadcastServiceStatus("Connected");

			// ����Ǿ���. re : ��.(?)
			connectionStatus = ConnectionStatus.CONNECTED;

			// ������ ����� ������ �����ϰ� �������� �� ��û �ֱ⸦ �����Ѵ�.
			scheduleNextPing();
			wl.release();
			return mqttClient.isConnected();
		}
		catch (Exception e)
		{
			// �и��� ���� �߸��Ǿ���.
			Log.e("Argos", "connectToBroker Exception!!");
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
			//
			// ������ �������� ���ߴٰ� �˸��ų� ��Ƽ��Ƽ�� UI�� ������Ʈ�Ѵ�.
			//            broadcastServiceStatus("������ ������ �� �����ϴ�.");

			//
			// ������ �������� ���ߴٰ� ����ڿ��� �˸���.
			//            notifyUser("������ ������ �� �����ϴ�.", "Gruvice", "������ ������ �� �����ϴ�. �ٽ� �õ��� �ּ���.");        

			// ���� ���ῡ �����Ѵٸ�, ������ ��õ��ϰ�, �������� Keep-Alive �޽��� �ֱ⸦ �����Ѵ�.
			// ����� �̰��� ������ �õ��Ѵٸ�, ī��Ʈ�� �����ϱ� ���ؼ� �ð��� ��� �����Ѵ�.
			// ��� Keep-Alive �޽����� ������ �ֱ������� ��������.
			// �Ͻ����� ��Ʈ��ũ ������ ���� �߻��Ѵٸ�, �̰��� Ƚ���� �����ϴ� �͵� ���� ����̴�.
			scheduleNextPing();
			wl.release();
			return false;
		}
	}

	/*
	 * �޽����� ���� ���Ŀ�� ������ ���� �̸�(����ȣ?)�� ������. ���ϵ�ī�嵵 ���ȴ�.
	 */
	private void subscribeToTopic(final String clientID)
	{



		if (isAlreadyConnected() == false)
		{
			// ���� ���� �˻� - ������ ���� �ʾ��� ��� �õ����� �ʴ´�.
			Log.e("Argos", "Unable to subscribe as we are not connected");
		}
		else
		{
			// ���� ������ ��������� ���ڰ��� �迭�� �޴� ����� ����� �� �ִ�.
			// ����� ���̵� �ش��ϴ°͸� ������ �Ǵϱ� �׳� 1����
			// 2011-05-11 ��Ʈ��ũ ó�� �κ��̹Ƿ� ������� �и�
			// 2011-05-24 �ϴϱ� �ȵ��ư���. �����常�� �ɻ�� �ƴѰ�����.
			try {
				String[] topics = { clientID };
				mqttClient.subscribe(topics, QOS);
				Log.i("Argos", "finish subscribedToTopic");
			} catch (Exception e) {
				Log.e("Argos", "failed subscribedToTopic");
			}
			new MsgReceiver(clientID, "", "", false).start();
		}

		//        if (subscribed == false)
		//        {
		//            //
		//            // UI�� ������ ǥ���� �� �ֵ��� �˸�.
		//            broadcastServiceStatus("Unable to subscribe");
		//            //
		//            // ��Ƽ��Ƽ�� �������� �ƴҶ� �������� �����޽����� �˸�.
		//            notifyUser("Unable to subscribe", TAG, "Unable to subscribe");
		//        }
	}

	/*
	 * �޽��� ���Ŀ���� ������ ����.
	 */
	private void disconnectFromBroker()
	{
		// ���ͳ� ������ ��ٸ��� �ִ� �߿��� ����� �� �ִ�.
		// ���ͳ� ������ �Ǿ����� �ʴٸ� �ƹ� ���۵� ���� �ʴ´�.
		if (mSocket.isConnected()) {
			try {
				mSocket.close();
				mConnectionThread.interrupt();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.e(TAG, "socket close error.");
				e1.getMessage();
			}
		}

		try
		{
			if (netConnReceiver != null)
			{
				unregisterReceiver(netConnReceiver);
				netConnReceiver = null;
			}

			if (pingSender != null)
			{
				unregisterReceiver(pingSender);
				pingSender = null;
			}
		}
		catch (Exception eee)
		{
			// �ƹ��͵� ������� �ʾ��� �� �ַ� �߻��Ѵ�.
			Log.e("Argos", "unregister failed", eee);
		}

		try
		{
			if (mqttClient != null)
			{
				mqttClient.disconnect();
			}
		}
		catch (MqttPersistenceException e)
		{
			Log.e("Argos", "disconnect failed - persistence exception", e);
		}
		finally
		{
			mqttClient = null;
		}

		// ������ ��Ƽ�����̼ǰ� ���� �˸��� ��� �����Ѵ�.
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	/*
	 * Argos Client �� ���� ���¸� üũ�Ѵ�.
	 */
	private boolean isAlreadyConnected()
	{
		return ((mqttClient != null) && (mqttClient.isConnected()));
	}

	private class BackgroundDataChangeIntentReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent intent)
		{
			// ���� ���°� �ٲ�ų� �޽����� �����ϸ� �ϴ� ���� �����.
			// BR������ ��������� Wake-lock ���� �ʿ�� ����. �ڵ����� �����.
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wl.acquire();

			ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			if (cm.getBackgroundDataSetting())
			{
				// ����ڰ� ��׶��� �����͸� Ȱ��ȭ�ϸ� �ٽ� �����Ѵ�.
				defineConnectionToBroker(HOST);
				connectToBroker();
				Log.i("mqtt", "call handleStart()");
			}
			else
			{
				// ����ڰ� ��׶��� �����͸� ��Ȱ��ȭ �ϸ� ���� ���¸� �����Ѵ�.
				connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

				// ���Ŀ �������� ������ �����Ѵ�.
				disconnectFromBroker();
			}            

			wl.release();
		}
	}


	/*
	 * ��Ʈ��ũ ���� ���°� ����Ǹ�, ������ ������ ������ �� �� ���� ��ٸ���.
	 */
	private class NetworkConnectionIntentReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent intent)
		{
			// ���� ���°� �ٲ�� �ϴ� ���� �����.
			Log.i(TAG, "Network State now changed.");
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wl.acquire();

			if (isOnline())
			{
				// ���ͳ� ������ �Ǹ�, �������� ������ �ٽ� �õ��Ѵ�.
				if (connectToBroker())
				{
					// Ư�� ������ ���� Ǫ�� �˸��� �����ϵ��� ���.
					subscribeToTopic(clientID);
				}
			}

			wl.release();
		}
	}

	// �������� ���Ű��� ����� ���ù� �۵�
	// ���Ǵ� ���ù� �� ���͸��� ���� ���� �Ҹ��� ������ �����. �̿� ���� ��ġ �ʿ�.
	// �켱 ������� �ʴ� ������ ��. ���� �̽��� ���� �ذ� ����� ������ �ٽ� ����.
	//    public class WifiSensitiveIntentReceiver extends BroadcastReceiver {
	//
	//    	WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
	//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	//    	
	//		@Override
	//		public void onReceive(Context arg0, Intent arg1) {
	//			
	//		}
	//    }

	/*
	 *  2011-05-12 ���������� ���� ����.
	 */
	private class ConnectionThread extends Thread
	{
		private final String mHost;
		private final int mPort;

		public ConnectionThread (String host, int port)
		{
			mHost = host;
			mPort = port;
			try {
				mSocket = new Socket(host, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				defineConnectionToBroker(HOST);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run()
		{
			// �� �޼���� ȣ���ϴ� �κ��� ����. ���� ����� �ʿ��� ��쿡�� ����Ѵ�.

			try {
				Log.i("Argos", "connectionThread Start");
				Socket s = mSocket;
				s.connect(new InetSocketAddress(mHost, mPort), 10000);
				s.setKeepAlive(true);
				Log.i(TAG, "socket connected.");
				startKeepAlives();
				Log.i("Argos", "connectionThread Finish");
			} catch (IOException e) {
				e.getMessage();
			} 
		}

		private void startKeepAlives()
		{
			Intent i = new Intent();
			i.setClass(getApplicationContext(), ParmService.class);
			i.setAction(PING_ACTION); // ������ ���� ������ �Ѵٴ� ����� �ڽſ��� �˸�.
			PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);
			AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
			alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + keepAliveSeconds,
					keepAliveSeconds, pi);
		}

		public void sendKeepAlive()
		throws IOException
		{
			Date d = new Date();
			OutputStream os = mSocket.getOutputStream();
			os.write((d.toString() + "\n").getBytes());
			// �ڲ� ���� ������ ���ܼ� �ϴ� �ݾƺô�.
			os.close();			
		}


	}


	/*
	 * �������� �� ���� �ֱ⸦ �������� �����Ѵ�.
	 */
	private void scheduleNextPing()
	{
		// Ŭ���̾�Ʈ�� ���� ��û������ ������ ������ ���� ���, ������ ������ ������ ���ֵȴ�.
		// ������ �޽����� ��û�ϴ� ����, ���񽺸� �����ϱ� ���� ��� �ѹ� �̻� cpu �� �����ִ� ���� Ȯ���ؾ� �Ѵ�.
		// �̰��� ���� ����� ���� ����Ǳ� ���� ���� �� ȣ���� �� �ִ�.
		// �̰��� �ٽ� ȣ��Ǹ� ������ ȣ��� ���� ��ҵȴ�.
		// ���� �ٸ� ���� ��� ȣ��Ǹ�, ���񽺰� ��� �����Ǹ�, �������� ���� �����Ѵ�.
		Log.i("Argos", "scheduleNextPing()");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				new Intent(PING_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// ���񽺿� ������ ������ ����Ǳ� ���� ������ ���� ������.
		// �ʿ� �̻����� ���� ���� ������ �� ���� �ִ�.
		Calendar wakeUpTime = Calendar.getInstance();
		wakeUpTime.add(Calendar.SECOND, keepAliveSeconds);

		AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		aMgr.set(AlarmManager.RTC_WAKEUP,
				wakeUpTime.getTimeInMillis(),
				pendingIntent);
	}

	/*
	 * Keep-Alive �޽����� ������ ���� ���ȴ�.
	 * ���ǵ� ���ݸ��� ������ ���� ������.
	 */
	public class PingSender extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// ���⿡���� �� �޴����� ���� �ʿ�� ����.
			// ���� ������ ���ϸ�, onReceive() �޼��尡 ����Ǵ� ����
			// �˶��Ŵ��� Ŭ������ CPU�� �ݵ�� ����ϱ� �����̴�.
			// �� Ŭ������ �� ��ε�ĳ��Ʈ�� ���� ����� ������ �� ����
			// �޴����� ������ ���� �ʵ��� �����Ѵ�.

			try
			{
				// ������ ���� ��û.
				Log.i("Argos", "Argos ping send.");
				mqttClient.ping();
				//                Socket s = mSocket;
				try {
					OutputStream out = mSocket.getOutputStream();
					out.write( (generateClientId() + "\n").getBytes() );
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException npe) {
					// ��... ������ GC�ǳ�...;;
					npe.getMessage();
					try {
						mSocket = new Socket(HOST, PORT);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					scheduleNextPing();
				}
			} catch (MqttNotConnectedException e) {
				if (connectToBroker()) {
					subscribeToTopic(mqttClientId);
				} else {
					scheduleNextPing();
				}
			} catch (MqttException e) {
				// ���� ���� �߸��Ǿ��ٸ�, ���� �������� ����� ȣ��� ���̴�.
				// �̿� ���� �ڵ鸵�� ����� �Ѵ�.
				Log.e("Argos", "ping failed - MQTT exception, try disconnect.", e);

				// �������� ������ ������ ������ �����Ѵ�.
				try {
					mqttClient.disconnect();
				} catch (MqttPersistenceException e1) {
					Log.e("mqtt", "disconnect failed - persistence exception", e1);
					defineConnectionToBroker(HOST);
				}

				// ������ �õ�
				if (connectToBroker()) {
					subscribeToTopic(clientID);
				} else {
					if (mSocket.isConnected()) {
						try {
							mSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							Log.e(TAG, "socket close error");
							e.getMessage();
						}
					}
					// �־��� ��� ���񽺸� ���½��Ѿ� �Ѵ�.
					ParmService.actionStart(getApplicationContext());
				}
			}

			// ���� �� �ֱ⸦ �����Ѵ�.
			scheduleNextPing();
		}
	}

	/************************************************************************/
	/*                      ���� ��ƿ��Ƽ �޼���                                */
	/************************************************************************/    

	private String generateClientId()
	{
		return util.getAccountID();
	}

	// �¶��� ���¸� üũ�Ѵ�.
	private boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() &&
				cm.getActiveNetworkInfo().isConnected()) {
			return true;
		}

		return false;
	}

}
