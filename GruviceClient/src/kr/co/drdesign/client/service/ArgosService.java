package kr.co.drdesign.client.service;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import kr.co.drdesign.util.GruviceUtillity;
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

public class ArgosService extends Service implements MqttSimpleCallback {
    public static final String APP_ID = "kr.co.drdesign.client";

    public static final String MSG_RECEIVED_INTENT = "kr.co.drdesign.client.service.MSGRECVD";
    public static final String MSG_RECEIVED_TOPIC  = "kr.co.drdesign.client.service.MSGRECVD_TOPIC";
    public static final String MSG_RECEIVED_MSG    = "kr.co.drdesign.client.service.MSGRECVD_MSGBODY";

    public static final String STATUS_INTENT = "kr.co.drdesign.client.service.STATUS";
    public static final String STATUS_MSG    = "kr.co.drdesign.client.service.STATUS_MSG";

    public static final String PING_ACTION = "kr.co.drdesign.client.service.PING";

    public static final String TAG = "Gruvice";
    public static final int NOTIFICATION_ONGOING = 1;
    public static final int NOTIFICATION_UPDATE  = 2;
    
    public enum ConnectionStatus
    {
        INITIAL,                            
        CONNECTING,                         
        CONNECTED,                          
        NOTCONNECTED_WAITINGFORINTERNET,    
        NOTCONNECTED_USERDISCONNECT,        
        NOTCONNECTED_DATADISABLED,          
        NOTCONNECTED_UNKNOWNREASON             
    }

    public static final int MAX_CLIENTID_LENGTH = 40;

    private ConnectionStatus connectionStatus = ConnectionStatus.INITIAL;

    private String          HOST       = "121.254.228.178";
    
    public static String          clientID            = "";    

    private int             PORT     = 1883;
    private MqttPersistence usePersistence       = null;
    private boolean         cleanStart           = false;
    private int[]           QOS   = { 0 } ; // QOS

    private short           keepAliveSeconds     = 10 * 60;
    private String          mqttClientId = null; 

    private IMqttClient mqttClient = null;
    
    private GruviceUtillity gUtil;
    
	private Socket mSocket = null;

    private NetworkConnectionIntentReceiver netConnReceiver;

    private BackgroundDataChangeIntentReceiver dataEnabledReceiver;
    private PingSender pingSender;
    
    private ConnectionThread mConnectionThread;
    

    
	public static void actionStart(Context ctx)
	{
		Log.i("mqtt", "Argos ActionStart()");
		Intent i = new Intent(ctx, ArgosService.class);
		ctx.startService(i);
	}

    @Override
    public void onCreate()
    {
        super.onCreate();

        connectionStatus = ConnectionStatus.INITIAL;
  
        mBinder = new LocalBinder<ArgosService>(this);
        
        gUtil = GruviceUtillity.getInstance(getApplicationContext());
        
        mConnectionThread = new ConnectionThread(HOST, PORT);
        
        clientID = gUtil.getClientId();

        dataEnabledReceiver = new BackgroundDataChangeIntentReceiver();
        registerReceiver(dataEnabledReceiver,
                         new IntentFilter(ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED));

        defineConnectionToBroker(HOST);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        handleStart(intent, startId);
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        handleStart(intent, startId);

        
        return START_STICKY;
    }

    public synchronized void handleStart(Intent intent, int startId)
    {
    	Log.i("Argos", "Argos client is handleStart");
        if (mqttClient == null)
        {
        	defineConnectionToBroker(HOST);
        	subscribeToTopic(clientID);
            return;
        }

        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getBackgroundDataSetting() == false)
        {
        	Log.i("Argos", "background data disabled");
            connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

            return;
        }

        if (isAlreadyConnected() == false)
        {
        	Log.i("Argos", "isAlreadyConnected false");
            connectionStatus = ConnectionStatus.CONNECTING;

            if (isOnline())
            {
            	Log.i("Argos", "isOnline() true.");
                if (connectToBroker())
                {
                	Log.i("Argos", "isn't subcribed.");
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
                connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

            }
        }

        if (netConnReceiver == null)
        {
            netConnReceiver = new NetworkConnectionIntentReceiver();
            registerReceiver(netConnReceiver,
                             new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }

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
        disconnectFromBroker();

//        broadcastServiceStatus("Disconnected");

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

//    private void broadcastServiceStatus(String statusDescription)
//    {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(STATUS_INTENT);
//        broadcastIntent.putExtra(STATUS_MSG, statusDescription);
//        sendBroadcast(broadcastIntent);
//    }

//    private void broadcastReceivedMessage(String topic, String message)
//    {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(MSG_RECEIVED_INTENT);
//        broadcastIntent.putExtra(MSG_RECEIVED_TOPIC, topic);
//        broadcastIntent.putExtra(MSG_RECEIVED_MSG,   message);
//        sendBroadcast(broadcastIntent);
//    }

    
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

    private LocalBinder<ArgosService> mBinder;

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
//                status = "������ ������ �� ����ϴ�.";
//                break;
//        }
//
//        broadcastServiceStatus(status);
//    }

    public void disconnect()
    {
        disconnectFromBroker();

        connectionStatus = ConnectionStatus.NOTCONNECTED_USERDISCONNECT;

//        broadcastServiceStatus("Disconnected");
    }

    public void connectionLost() throws Exception
    {
    	
    	Log.e("Argos", "Argos connection lost!!! DRMService actionStop! ");
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();

        DRMService.actionStop(getApplicationContext());
        mSocket.close();
        

        if (isOnline() == false)
        {
        	Log.i("Argos", "isOnline() false");
            connectionStatus = ConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

//            broadcastServiceStatus("Connection lost - no network connection");

//            notifyUser("Connection lost - no network connection",
//                       TAG, "Connection lost - no network connection");

        }
        else
        {

        	Log.i("Argos", "isOnline()");
            connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

//            broadcastServiceStatus("Connection lost - reconnecting...");

            if (connectToBroker()) {
            	subscribeToTopic(clientID);
            }
        }

        wl.release();
    }

    public void publishArrived(String topic, byte[] payloadbytes, int qos, boolean retained)
    {
    	
    	Log.i("Argos", "Argos publish arrived.");
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
        
        
        DRMService.actionStart(getApplicationContext());

        scheduleNextPing();

        wl.release();
    }

    private void defineConnectionToBroker(String HOST)
    {
        String mqttConnSpec = "tcp://" + HOST + "@" + PORT;
        Log.i("Argos", "defineConnectionToBroker");

        try
        {
            mqttClient = MqttClient.createMqttClient(mqttConnSpec, usePersistence);

            mqttClient.registerSimpleHandler(this);
            Log.i("Argos", "mqttClient registerHandler");
        }
        catch (MqttException e)
        {
        	Log.i("Argos", "registerHandler Exception!!");
            mqttClient = null;
            connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

        }
    }

    private boolean connectToBroker()
    {

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();
        
        try
        {
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

            connectionStatus = ConnectionStatus.CONNECTED;

            scheduleNextPing();
            wl.release();
            return mqttClient.isConnected();
        }
        catch (Exception e)
        {
        	Log.e("Argos", "connectToBroker Exception!!");
            connectionStatus = ConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
            DRMService.actionStop(getApplicationContext());
            scheduleNextPing();
            wl.release();
            return false;
        }
    }

    private void subscribeToTopic(final String clientID)
    {
        if (isAlreadyConnected() == false)
        {
            Log.e("Argos", "Unable to subscribe as we are not connected");
        }
        else
        {
			try {
                String[] topics = { clientID };
                mqttClient.subscribe(topics, QOS);
                Log.i("Argos", "finish subscribedToTopic");
                DRMService.actionStart(getApplicationContext());
			} catch (Exception e) {
				Log.e("Argos", "failed subscribedToTopic");
			}
        }

    }

    private void disconnectFromBroker()
    {
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

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    private boolean isAlreadyConnected()
    {
        return ((mqttClient != null) && (mqttClient.isConnected()));
    }

    private class BackgroundDataChangeIntentReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context ctx, Intent intent)
        {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wl.acquire();

            ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            if (cm.getBackgroundDataSetting())
            {
                defineConnectionToBroker(HOST);
                connectToBroker();
                Log.i("mqtt", "call handleStart()");
//                handleStart(intent, 0);
            }
            else
            {
                connectionStatus = ConnectionStatus.NOTCONNECTED_DATADISABLED;

//                broadcastServiceStatus("Not connected - background data disabled");
                disconnectFromBroker();
            }            

            wl.release();
        }
    }


    private class NetworkConnectionIntentReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context ctx, Intent intent)
        {
        	Log.i(TAG, "Network State now changed.");
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wl.acquire();

            if (isOnline())
            {
                if (connectToBroker())
                {
                	subscribeToTopic(clientID);
                }
            }

            wl.release();
        }
    }
    
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
			i.setClass(getApplicationContext(), ArgosService.class);
			i.setAction(PING_ACTION);
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
//			os.close();			
		}
		

	}


    private void scheduleNextPing()
    {
    	Log.i("Argos", "scheduleNextPing()");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                                                                 new Intent(PING_ACTION),
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar wakeUpTime = Calendar.getInstance();
        wakeUpTime.add(Calendar.SECOND, keepAliveSeconds);

        AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        aMgr.set(AlarmManager.RTC_WAKEUP,
                 wakeUpTime.getTimeInMillis(),
                 pendingIntent);
    }

    public class PingSender extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            try
            {
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
                Log.e("Argos", "ping failed - MQTT exception, try disconnect.", e);

                try {
                    mqttClient.disconnect();
                } catch (MqttPersistenceException e1) {
                    Log.e("mqtt", "disconnect failed - persistence exception", e1);
                    defineConnectionToBroker(HOST);
                }

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
                	ArgosService.actionStart(getApplicationContext());
                }
            }
            scheduleNextPing();
        }
    }

    private String generateClientId()
    {
        return gUtil.getClientId();
    }

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
