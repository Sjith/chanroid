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
	/*                             상수                                      */
	/************************************************************************/

	// 서버에서 앱을 식별하는 고유의 값.
	// 환경 설정을 액세스 한다거나 하는 용도로 사용.
	public static final String APP_ID = "kr.co.drdesign.client";

	// 메시지 수신시 UI 로 통보하는 데 사용.
	public static final String MSG_RECEIVED_INTENT = "kr.co.drdesign.client.service.MSGRECVD";
	public static final String MSG_RECEIVED_TOPIC  = "kr.co.drdesign.client.service.MSGRECVD_TOPIC";
	public static final String MSG_RECEIVED_MSG    = "kr.co.drdesign.client.service.MSGRECVD_MSGBODY";

	// 서버와의 연결 상태를 표시하는 데 사용.
	public static final String STATUS_INTENT = "kr.co.drdesign.client.service.STATUS";
	public static final String STATUS_MSG    = "kr.co.drdesign.client.service.STATUS_MSG";

	// 다음 핑 스케줄링에 사용.
	public static final String PING_ACTION = "kr.co.drdesign.client.service.PING";

	// 상태표시줄 알림에 사용.
	public static final String TAG = "Gruvice";
	public static final int NOTIFICATION_ONGOING = 1; // 진행중 알림
	public static final int NOTIFICATION_UPDATE  = 2; // 그냥 알림

	// 연결 상태를 나타내는 상수값
	public enum ConnectionStatus
	{
		INITIAL,                            // 초기상태
		CONNECTING,                         // 연결중
		CONNECTED,                          // 연결됨
		NOTCONNECTED_WAITINGFORINTERNET,    // 인터넷 연결 대기중으로 서버에 연결되지 않음
		NOTCONNECTED_USERDISCONNECT,        // 사용자가 명시적으로 연결 해제를 요청
		NOTCONNECTED_DATADISABLED,          // 사용자가 데이터 액세스를 차단
		NOTCONNECTED_UNKNOWNREASON          // 그 외의 어떤 이유로 인해 연결되지 않음        
	}

	// MQTT 상수. 고유 ID 값의 최대 자리수를 설정.
	// 2011-05-06 단말기 아이디는 플랫폼, 기계, 통신사, 제조사마다 다 달라서 좀 골치아프네...
	// 2011-05-17 플랫폼 따위 상관없지않나? 여긴 안드로이드인데 만세 웰컴투구글월드 예압
	// 에뮬로 테스트할땐 어쩔?
	// 2011-05-06 22 -> 26
	// 2011-05-11 26 -> 40
	public static final int MAX_CLIENTID_LENGTH = 40;

	/************************************************************************/
	/*           서버와의 연결 상태를 유지하는데 필요한 변수                        */
	/************************************************************************/

	// MQTT client 와의 연결 상태를 가지고 있는 객체.
	private ConnectionStatus connectionStatus = ConnectionStatus.INITIAL;

	/************************************************************************/
	/*                    푸시서버 연결에 필요한 변수                            */
	/************************************************************************/


	private String          HOST       = "";
	private String			Updateurl  = "";

	public static String          clientID            = "";    

	// 이 예제는 메시지 브로커와 상호작용을 하기 위한 기본적인 값을 사용한다.
	private int             PORT     = 1883; // 서버 접근을 위한 포트 번호
	private MqttPersistence usePersistence       = null;
	private boolean         cleanStart           = false;
	private int[]           QOS   = { 0 } ; // QOS

	//   서버와의 연결을 유지하기 위해 핑을 보내는 주기.
	//
	//   너무 짧게 설정하면 배터리 소모가 심해진다.
	//   너무 길게 설정하면 연결을 잃을 시 다음 연결까지 푸시 알림을 받을 수 없다.
	//
	//   배터리 성능과 데이터 연결을 고려하여 적절히 핸들링 해 주어야 한다.
	//
	//   이것은 네트워크 연결 유지시간이 길 경우에 대한 주기이다.
	//   일반적으로 이것은 네트워크 운영자가 연결을 끊을 주기보다 적다.
	//   계속 서버로 Keep-Alive 메시지를 보내 주어야 한다.

	private short           keepAliveSeconds     = 10 * 60;
	// 2011-05-30 분명 Second 라고 명시되어 있는데도 불구하고 가끔 ms 로 동작하는것 같다.

	// 이것은 클라이언트 앱이 브로커 서버에 자신을 식별하게 하는 ID이다.
	// 브로커 서버는 고유해야 하며, 두 클라이언트가 동일한 ID를 사용하여 연결할 수 없다.
	private String          mqttClientId = null; // 지금은 null 이지만 밑에서 설정해 줄 것이다.

	/************************************************************************/
	/*                           기타 로컬 객체                                */
	/************************************************************************/
	// 메시지 브로커 연결 객체
	private IMqttClient mqttClient = null;

	// deviceID 를 가져오기 위한 유틸리티 객체
	private ParmUtil util;

	// 환경설정 객체
	// 2011-07-04 아직 구현되지 않은 부분이 많아 사용하지 않는 것으로 함.
	//    private SharedPreferences mPref;

	// 2011-06-08 소켓 컨트롤을 위해 클래스 멤버로 변경.
	private Socket mSocket = null;

	// 서버로부터 푸시요청(인텐트)을 받는 객체
	private NetworkConnectionIntentReceiver netConnReceiver;

	// 사용자가 데이터 통신 설정을 변경했을 시 서비스에 통보해주는 객체
	// 2011-07-04 와이파이 제어가 문제가 많아서 일단 비활성화.
	private BackgroundDataChangeIntentReceiver dataEnabledReceiver;
	//    private WifiSensitiveIntentReceiver wifiSensitiveReceiver;

	// 서버와의 통신 여부를 확인하기 위한 핑을 보내는 객체
	private PingSender pingSender;

	// 웹서버와의 세션 유지를 위한 스레드
	private ConnectionThread mConnectionThread;



	/************************************************************************/
	/*                         라이프사이클 관련 메서드                          */
	/************************************************************************/

	public static void actionStart(Context ctx)
	{
		// BR이나 다른 서비스 등의 액티비티 외부에서 호출할 때 사용하는 메서드.
		Log.i("mqtt", "Argos ActionStart()");
		Intent i = new Intent(ctx, ParmService.class);
		ctx.startService(i);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		// 초기 연결 상태값을 지정
		connectionStatus = ConnectionStatus.INITIAL;

		// 액티비티가 서비스와 통신하기 위한 바인더 생성
		mBinder = new LocalBinder<ParmService>(this);

		// deviceID 를 가져오기 위한 util 객체 생성
		util = ParmUtil.getInstance(getApplicationContext());

		// 웹서버와 연결 유지
		mConnectionThread = new ConnectionThread(HOST, PORT);

		// 환경설정 객체 설정
		//        mPref = getSharedPreferences("mPref", 2); 
		// MODE_WORLD_WRITABLE
		// 환경설정 등에서 브로커(MQTT Server) ID 설정을 가져옴. 여기서는 클라이언트 id를 사용한다.
		// 값만 가지고 올 수 있으면 다른 방법을 사용해도 무관함. 하지만 클라이언트 마다 고유한 값을 사용해야 함.
		clientID = util.getAccountID();

		// 사용자가 백그라운드 데이터 설정을 변경할 때마다 통지를 받을 수 있도록 리시버 등록.
		dataEnabledReceiver = new BackgroundDataChangeIntentReceiver();
		registerReceiver(dataEnabledReceiver,
				new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED));

		// 2011-05-11 와이파이 수신감도 변경시 통지를 받을 수 있도록 리시버 등록.
		//        wifiSensitiveReceiver = new WifiSensitiveIntentReceiver();
		//        registerReceiver(wifiSensitiveReceiver,
		//        				new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));


		// 브로커에 대한 연결을 정의하는 메서드.
		defineConnectionToBroker(HOST);
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// 안드로이드 2.0이하 버전에서 사용되던 서비스 시작 메서드.
		// 따라서 2.1이상의 버전에서는 이 메서드가 호출되지 않는다.
		handleStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// 안드로이드 2.1이상에서 변경된 서비스 시작 메서드.
		handleStart(intent, startId);


		return START_STICKY;
	}

	public synchronized void handleStart(Intent intent, int startId)
	{
		// 이 메서드가 시작되기 전에, 서비스가 시작되지 않을 이유가 있으면
		// 그것을 체크하고 조치해야 한다.
		Log.i("Argos", "Argos client is handleStart");
		if (mqttClient == null)
		{
			// 서버와의 연결이 정의되지 않으면, 이 서비스는 즉시 중지된다.
			// 2011-06-07 서비스가 절대로 중지되지 못하도록 중지기능 막음.
			//          stopSelf();
			defineConnectionToBroker(HOST);
			subscribeToTopic(clientID);
			//        	handleStart(intent, startId);
			return;
		}

		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		if (cm.getBackgroundDataSetting() == false) // 유저가 백그라운드 데이터 사용을 막았을 때
		{
			Log.i("Argos", "background data disabled");
			// 서버와의 연결상태를 변경한다.
			connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

			// 연결이 해제되었음을 표시한다.
			//            broadcastServiceStatus("Not connected - background data disabled");

			// 여기에서 상태가 변경된 것이 감지되면, 서비스를 종료하고
			// 다시 handleStart() 메서드를 호출한다.
			return;
		}

		// 액티비티에서 서비스를 시작한다. - 서비스가 최초로 실행되었거나 또는 서비스가 끝난 후라면
		// 약간의 시간이 걸린다. (startService를 여러번 호출하더라도 서비스가 여러개 실행되지는 않는다.)
		// 서비스가 이미 실행되어 있는 상태라면 서버와의 통신에 필요한 값을 전송한다.

		//        rebroadcastStatus();
		//        rebroadcastReceivedMessages();

		// 만약 서비스가 실행되어 있고, 서버와의 연결이 정상적으로 성공한 상태라면,
		// 아래에서는 아무것도 처리되지 않는다.
		if (isAlreadyConnected() == false)
		{
			// 서버와의 연결상태를 변경한다.
			Log.i("Argos", "isAlreadyConnected false");
			connectionStatus = ConnectionStatus.CONNECTING;

			// 이것은 사용자가 백그라운드 서비스를 임의로 종료하였을 때까지 영원히 실행된다.
			// 그래서 사용자가 배터리 소모를 감소시키기 위한 이유나, 다른 여러가지 이유로 종료하기 전까지는
			// 지속적으로 노티피케이션을 발생시켜 서비스가 실행되고 있다는 것을 알린다.         
			// 2011-05-11 onGoing 알림은 띄우지 않는 것으로 변경.
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
			//            notification.setLatestEventInfo(this, "Gruvice", "서비스 실행중", contentIntent);
			//            nm.notify(NOTIFICATION_ONGOING, notification);

			// 연결을 시도하기 전에 클라이언트와 서버가 데이터 통신을 하고있는지를 검사한다.
			if (isOnline())
			{
				Log.i("Argos", "isOnline() true.");
				// 메시지 브로커에 연결한다.
				if (connectToBroker())
				{
					Log.i("Argos", "isn't subcribed.");
					// 푸시 메시지를 받기 위해 서버에 등록한다.
					// 앱에서는 필요에 따라 여러가지 정보를 등록할 수 있으나
					// 그루비스에서는 사용자 정보만 있으면 되므로 그냥 사용.
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
				// 서버와의 연결상태를 변경한다.
				connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

				// 아직 연결되지 않았거나 연결 대기중이라고 통보한다.
				//                broadcastServiceStatus("Waiting for network connection");
			}
		}

		// 단말기의 네트워크 연결 상태가 변경될 경우
		// 예를들어 Wi-Fi에서 3G로, 또는 그 반대의 경우
		// 푸시 서버와의 연결이 해제될 수 있기 때문에
		// 여기에서는 이를 감지하여 변경된 네트워크로 다시 연결이 가능하게 한다.
		if (netConnReceiver == null)
		{
			netConnReceiver = new NetworkConnectionIntentReceiver();
			registerReceiver(netConnReceiver,
					new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		}

		// 서버에 핑을 호출할때 휴대폰을 깨우기 위해 사용되는 인텐트를 생성한다.
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
		// 즉시 연결을 해제한다.
		disconnectFromBroker();

		// 성공적으로 연결이 해제되면 노티피케이션을 출력한다.
		//        broadcastServiceStatus("Disconnected");

		// 서버와의 데이터 통신을 해제한다.
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
	/*                          알림 관련 메서드                               */
	/************************************************************************/

	// 이 메서드들은 무언가가 일어나면 액티비티의 UI에 통보하기 위해 사용된다.
	// 이것들은 서버에서 데이터를 수신하면 상태를 반영하도록 업데이트 할 수 있다.

	//    private void broadcastServiceStatus(String statusDescription)
	//    {
	//        // 이것은 현재의 브로커 연결 상태에 따라 UI를 업데이트 할 수 있도록 응용프로그램에 알린다.
	//    	// 직접 업데이트 하지는 않고 신호만 보낸다.
	//        Intent broadcastIntent = new Intent();
	//        broadcastIntent.setAction(STATUS_INTENT);
	//        broadcastIntent.putExtra(STATUS_MSG, statusDescription);
	//        sendBroadcast(broadcastIntent);
	//    }

	//    private void broadcastReceivedMessage(String topic, String message)
	//    {
	//        // 이것은 실행중일때 UI에서 표시될 수 있도록 서버에서 받은 메시지를 전달한다.
	//    	// 직접 업데이트 하지는 않고 신호만 보낸다.
	//        Intent broadcastIntent = new Intent();
	//        broadcastIntent.setAction(MSG_RECEIVED_INTENT);
	//        broadcastIntent.putExtra(MSG_RECEIVED_TOPIC, topic);
	//        broadcastIntent.putExtra(MSG_RECEIVED_MSG,   message);
	//        sendBroadcast(broadcastIntent);
	//    }


	// 연결 상태가 변경되거나 하는걸 노티로 알림. 메시지는 안알림.
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
	/*                        액티비티에서 서비스에 접근하는 메서드                */
	/************************************************************************/

	// 메모리 릭을 최소화하면서 로컬에서 바인딩을 수행하기 위한 코드로 이루어져 있다.
	// 출처 : Geoff Bruckner - 자세한 정보
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
	// 액티비티를 통해 서비스에 바인딩 할 수 있는 public 메서드
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
	//                status = "서버에 연결할 수 없습니다.";
	//                break;
	//        }
	//
	//        //
	//        // 서비스와의 연결상태를 알림.
	//        broadcastServiceStatus(status);
	//    }

	public void disconnect()
	{
		disconnectFromBroker();

		// 서버와의 연결상태 설정
		connectionStatus = ConnectionStatus.NOTCONNECTED_USERDISCONNECT;

		// 성공적으로 연결이 해제되었다는 것을 알림.
		//        broadcastServiceStatus("Disconnected");
	}

	/************************************************************************/
	/*                     MQTT 클래스로부터 상속받는 메서드                     */
	/************************************************************************/

	/*
	 * 메시지 서버에 연결되어 있지 않을때 호출되는 메서드.
	 */
	public void connectionLost() throws Exception
	{

		Log.e("Argos", "Argos connection lost!!! DRMService actionStop! ");
		// 이 메서드가 호출되면 응용프로그램을 깨운다. 화면은 깨어나지 않을 수도 있다.
		// 2011-05-27 에너지 절약 차원에서 깨어나지 않도록 설정.
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();

		mSocket.close();

		//
		// 데이터 연결이 해제되었을때
		//

		if (isOnline() == false)
		{
			Log.i("Argos", "isOnline() false");
			connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

			// 네트워크가 연결되어 있지 않음을 알림.
			//            broadcastServiceStatus("Connection lost - no network connection");

			//
			// 더이상 메시지를 받을 수 없다는 것을 사용자에게 알림.
			//            notifyUser("Connection lost - no network connection",
			//                       TAG, "Connection lost - no network connection");

			//
			// 단말기가 네트워크에 다시 연결할때, 네트워크 연결 리시버를 해제하고,
			// 다른 연결을 시도할 것이다.
		}
		else
		{
			//
			// 가장 흔한 이유는 
			// 온라인 상태일때 모바일 네트워크에서 와이파이로 전환될때이다.
			// 

			Log.i("Argos", "isOnline()");
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

			// 더이상 연결되어있지 않다고 알리고, 다시 연결하려고 시도한다.
			//            broadcastServiceStatus("Connection lost - reconnecting...");

			// 다시 연결을 시도한다.
			if (connectToBroker()) {
				subscribeToTopic(clientID);
			}
		}

		wl.release();
	}

	/*
	 *   서버로부터 메시지를 수신하였을 때 실행되는 메서드.
	 */
	public void publishArrived(String topic, byte[] payloadbytes, int qos, boolean retained)
	{

		Log.i("Argos", "Argos publish arrived.");
		// 여기서 폰을 깨울 동안 휴대폰이 잠금상태에 들어가지 않도록 보호한다.
		// 중요한 작업이므로 여기서는 반드시 깨어있어야 한다.
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();


		if (payloadbytes.toString().contains("message")) {
			// 메시지를 갖고 온다.
			new MsgReceiver(clientID, "", "", false).start();
		} else if (payloadbytes.toString().contains("update")) {
			// DB 업데이트를 한다. 노티를 띄울지 안띄울진 너님 맘대로.
			DBUpdater update = new DBUpdater();
			update.setUrl(Updateurl);
			update.setWork(DBUpdater.UPDATE_ALL, 0);
			update.start();
		}

		//
		//  이 메시지가 Argos에서 처리되는 것은 아니므로 꼭 String 로 받을 필요는 없다.
		//  암호화를 위해 byte[] 또는 HexString, 또는 고유의 알고리즘으로 변환해서 받는것도 유효하다.
		//  여기서는 그냥 byte[] 로 받아서 캐스팅했지만 실제 사용시에는 암호화를 해 줘야 할것 같다.
		//  앱에서도 반드시 문자열로 보낼 필요는 없다.
		//
		//        String messageBody = new String(payloadbytes);

		//
		//  액티비티가 실행중이 아닐 때에도 서비스에서 수신한 데이터를 저장할 수 있어야 한다.
		//        if (addReceivedMessageToStore(topic, messageBody))
		//        {
		// 읽지 않은 새로운 메시지를 체크한다.

		//
		// 액티비티가 실행중이라면 새로운 메시지를 액티비티에 업데이트 할 수 있도록 알린다.
		//            broadcastReceivedMessage(topic, messageBody);

		//
		// 액티비티가 실행중이 아니라면 새로운 메시지가 수신되었음을 유저에게 알린다.
		//            notifyUser("New data received", topic, messageBody);
		//        }

		// 메시지 수신시 서비스는 연결이 유지되어 있는것으로 간주하고,
		// 서버로 핑을 보내는 주기를 연기한다.
		scheduleNextPing();

		wl.release();
	}

	/************************************************************************/
	/*                   사용되는 일부 메서드에 대해 래핑                         */
	/************************************************************************/

	/*
	 * 어플리케이션과 서버를 연결하는 클라이언트 객체를 생성한다.
	 */
	private void defineConnectionToBroker(String HOST)
	{
		String mqttConnSpec = "tcp://" + HOST + "@" + PORT;
		Log.i("Argos", "defineConnectionToBroker");

		try
		{
			// 브로커에 대한 연결을 정의한다.
			mqttClient = MqttClient.createMqttClient(mqttConnSpec, usePersistence);

			// 메시지를 받을 수 있도록 이 클라이언트를 등록한다.
			// 2011-05-11 이것도 일단 스레드로 분리해 보자.
			mqttClient.registerSimpleHandler(this);
			Log.i("Argos", "mqttClient registerHandler");
		}
		catch (MqttException e)
		{
			// 틀림없이 무언가 잘못되었다.
			Log.i("Argos", "registerHandler Exception!!");
			mqttClient = null;
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

			//
			// 서버에 연결하지 못했다고 알리거나 액티비티의 UI를 업데이트한다.
			//            broadcastServiceStatus("Invalid connection parameters");

			//
			// 서버에 연결하지 못했다고 사용자에게 알린다.
			//            notifyUser("서버에 연결할 수 없습니다.", "Gruvice", "서버에 연결할 수 없습니다.");
		}
	}

	/*
	 * 메시지 브로커에 (재)연결한다.
	 */
	private boolean connectToBroker()
	{

		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();

		try
		{
			// 연결을 시도한다.
			// 2011-05-23 앱이 멈추는 경우 여기서 멈추는것이다.
			// 2011-05-24 스레드로 분리. 의도된 대로 성공적으로 동작함.

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
			// 정상적으로 연결되었다고 알린다.
			//            broadcastServiceStatus("Connected");

			// 연결되었다. re : 응.(?)
			connectionStatus = ConnectionStatus.CONNECTED;

			// 서버에 연결된 것으로 간주하고 서버로의 핑 요청 주기를 연기한다.
			scheduleNextPing();
			wl.release();
			return mqttClient.isConnected();
		}
		catch (Exception e)
		{
			// 분명히 무언가 잘못되었다.
			Log.e("Argos", "connectToBroker Exception!!");
			connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
			//
			// 서버에 연결하지 못했다고 알리거나 액티비티의 UI를 업데이트한다.
			//            broadcastServiceStatus("서버에 연결할 수 없습니다.");

			//
			// 서버에 연결하지 못했다고 사용자에게 알린다.
			//            notifyUser("서버에 연결할 수 없습니다.", "Gruvice", "서버에 연결할 수 없습니다. 다시 시도해 주세요.");        

			// 서버 연결에 실패한다면, 연결을 재시도하고, 서버로의 Keep-Alive 메시지 주기를 연기한다.
			// 당신이 이것을 여러번 시도한다면, 카운트를 유지하기 위해서 시간을 계속 연기한다.
			// 대신 Keep-Alive 메시지는 영원히 주기적으로 보내진다.
			// 일시적인 네트워크 문제가 자주 발생한다면, 이것의 횟수를 제한하는 것도 좋은 방법이다.
			scheduleNextPing();
			wl.release();
			return false;
		}
	}

	/*
	 * 메시지를 보낼 브로커에 지정된 주제 이름(기기번호?)을 보낸다. 와일드카드도 허용된다.
	 */
	private void subscribeToTopic(final String clientID)
	{



		if (isAlreadyConnected() == false)
		{
			// 빠른 연결 검사 - 연결이 되지 않았을 경우 시도하지 않는다.
			Log.e("Argos", "Unable to subscribe as we are not connected");
		}
		else
		{
			// 받을 주제가 여러개라면 인자값을 배열로 받는 방법도 사용할 수 있다.
			// 여기는 아이디에 해당하는것만 받으면 되니까 그냥 1개로
			// 2011-05-11 네트워크 처리 부분이므로 스레드로 분리
			// 2011-05-24 하니까 안돌아간다. 스레드만이 능사는 아닌가보다.
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
		//            // UI가 오류를 표시할 수 있도록 알림.
		//            broadcastServiceStatus("Unable to subscribe");
		//            //
		//            // 액티비티가 실행중이 아닐때 유저에게 오류메시지를 알림.
		//            notifyUser("Unable to subscribe", TAG, "Unable to subscribe");
		//        }
	}

	/*
	 * 메시지 브로커와의 연결을 종료.
	 */
	private void disconnectFromBroker()
	{
		// 인터넷 연결을 기다리고 있는 중에도 취소할 수 있다.
		// 인터넷 연결이 되어있지 않다면 아무 동작도 하지 않는다.
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
			// 아무것도 등록하지 않았을 때 주로 발생한다.
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

		// 진행중 노티피케이션과 유저 알림을 모두 해제한다.
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}

	/*
	 * Argos Client 의 연결 상태를 체크한다.
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
			// 뭔가 상태가 바뀌거나 메시지를 수신하면 일단 폰을 깨운다.
			// BR에서는 명시적으로 Wake-lock 해줄 필요는 없다. 자동으로 깨어난다.
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wl.acquire();

			ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			if (cm.getBackgroundDataSetting())
			{
				// 사용자가 백그라운드 데이터를 활성화하면 다시 연결한다.
				defineConnectionToBroker(HOST);
				connectToBroker();
				Log.i("mqtt", "call handleStart()");
			}
			else
			{
				// 사용자가 백그라운드 데이터를 비활성화 하면 연결 상태를 변경한다.
				connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

				// 브로커 서버와의 연결을 종료한다.
				disconnectFromBroker();
			}            

			wl.release();
		}
	}


	/*
	 * 네트워크 연결 상태가 변경되면, 데이터 연결이 가능해 질 때 까지 기다린다.
	 */
	private class NetworkConnectionIntentReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context ctx, Intent intent)
		{
			// 뭔가 상태가 바뀌면 일단 폰을 깨운다.
			Log.i(TAG, "Network State now changed.");
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wl.acquire();

			if (isOnline())
			{
				// 인터넷 연결이 되면, 서버와의 연결을 다시 시도한다.
				if (connectToBroker())
				{
					// 특정 주제에 대한 푸시 알림을 수신하도록 등록.
					subscribeToTopic(clientID);
				}
			}

			wl.release();
		}
	}

	// 와이파이 수신감도 변경시 리시버 작동
	// 사용되는 리시버 중 배터리를 가장 많이 소모할 것으로 예상됨. 이에 따른 조치 필요.
	// 우선 사용하지 않는 것으로 함. 차후 이슈에 관해 해결 방안이 나오면 다시 구현.
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
	 *  2011-05-12 웹서버와의 세션 유지.
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
			// 이 메서드는 호출하는 부분이 없다. 소켓 통신이 필요한 경우에만 사용한다.

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
			i.setAction(PING_ACTION); // 서버로 핑을 보내야 한다는 사실을 자신에게 알림.
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
			// 자꾸 소켓 오류가 생겨서 일단 닫아봤다.
			os.close();			
		}


	}


	/*
	 * 서버와의 핑 연결 주기를 다음으로 연기한다.
	 */
	private void scheduleNextPing()
	{
		// 클라이언트가 핑을 요청했을때 서버의 반응이 없는 경우, 연결이 해제된 것으로 간주된다.
		// 서버로 메시지를 요청하는 동안, 서비스를 유지하기 위해 적어도 한번 이상 cpu 가 깨어있는 지를 확인해야 한다.
		// 이것은 다음 예약된 핑이 수행되기 전에 여러 번 호출할 수 있다.
		// 이것이 다시 호출되면 이전에 호출된 것은 취소된다.
		// 무언가 다른 것이 계속 호출되면, 서비스가 계속 유지되며, 서버로의 핑을 연기한다.
		Log.i("Argos", "scheduleNextPing()");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				new Intent(PING_ACTION),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 서비스와 서버의 연결이 만료되기 전에 서버에 핑을 보낸다.
		// 필요 이상으로 자주 핑을 보내야 할 수도 있다.
		Calendar wakeUpTime = Calendar.getInstance();
		wakeUpTime.add(Calendar.SECOND, keepAliveSeconds);

		AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		aMgr.set(AlarmManager.RTC_WAKEUP,
				wakeUpTime.getTimeInMillis(),
				pendingIntent);
	}

	/*
	 * Keep-Alive 메시지를 보내는 데에 사용된다.
	 * 정의된 간격마다 서버에 핑을 보낸다.
	 */
	public class PingSender extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// 여기에서는 꼭 휴대폰을 깨울 필요는 없다.
			// 공식 문서에 의하면, onReceive() 메서드가 실행되는 동안
			// 알람매니저 클래스가 CPU를 반드시 사용하기 때문이다.
			// 이 클래스는 이 브로드캐스트에 대한 명령을 수행할 때 까지
			// 휴대폰이 슬립에 들어가지 않도록 보장한다.

			try
			{
				// 서버로 핑을 요청.
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
					// 음... 소켓이 GC되나...;;
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
				// 만약 무언가 잘못되었다면, 연결 해제시의 명령이 호출될 것이다.
				// 이에 대한 핸들링을 해줘야 한다.
				Log.e("Argos", "ping failed - MQTT exception, try disconnect.", e);

				// 서버와의 연결이 끊어진 것으로 간주한다.
				try {
					mqttClient.disconnect();
				} catch (MqttPersistenceException e1) {
					Log.e("mqtt", "disconnect failed - persistence exception", e1);
					defineConnectionToBroker(HOST);
				}

				// 재접속 시도
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
					// 최악의 경우 서비스를 리셋시켜야 한다.
					ParmService.actionStart(getApplicationContext());
				}
			}

			// 다음 핑 주기를 설정한다.
			scheduleNextPing();
		}
	}

	/************************************************************************/
	/*                      내부 유틸리티 메서드                                */
	/************************************************************************/    

	private String generateClientId()
	{
		return util.getAccountID();
	}

	// 온라인 상태를 체크한다.
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
