package kr.co.drdesign.client.argos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import kr.co.drdesign.client.R;
import kr.co.drdesign.client.service.DRMService;
import kr.co.drdesign.util.GruviceUtillity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/* 
 * 
 * 2011-05-11 @SmoothButWild
 * MQTT 랑 구동방식이 약간 다르기때문에
 * 그냥 예제코드로만 남겨두고 당분간 쓰지 않기로 함.
 * 
 * 
 * PushService that does all of the work.
 * Most of the logic is borrowed from KeepAliveService.
 * http://code.google.com/p/android-random/source/browse/trunk/TestKeepAlive/src/org/devtcg/demo/keepalive/KeepAliveService.java?r=219
 */
public class DisableArgosService extends Service 
{
	public static final String TAG = "ARGOS"; // 설정값을 공유하기위해 필요한 태그

	private static final String HOST = "121.254.228.178"; // 그루비스 서버
	private static final int PORT = 1883; // 접속 포트

	private static final String ACTION_START 	= "kr.co.drdesign.client.argos.START"; // 서비스 시작
	private static final String ACTION_STOP 	= "kr.co.drdesign.client.argos.STOP"; // 서비스 종료
	private static final String ACTION_KEEPALIVE = "kr.co.drdesign.client.argos.KEEP_ALIVE"; // 실행중
	private static final String ACTION_RECONNECT = "kr.co.drdesign.client.argos.RECONNECT"; // 재접속 시도

	public static final String PREF_DEVICE_ID = "DEVICE_ID"; // 장치 고유 id

	private ConnectionLog mLog;

	private ConnectivityManager mConnMan; // 서버 연결 관리자
	private NotificationManager mNotifMan; // 알림 표시 관리자

	private boolean mStarted; // 서비스 실행여부
	private ConnectionThread mConnection; // 서버 연결상태를 유지하는 스레드

	private static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 10;

	private static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 120;

	private SharedPreferences mPrefs; // 어플리케이션에서 설정된 값

	public static final int NOTIF_CONNECTED = 2145;

	// 서비스 시작 상태값, 시작시 종료시 변경해야함
	private static final String PREF_STARTED = "isStarted";

	public static void actionStart(Context ctx)
	{
		// 서비스 시작. 외부에서 시작시킬순 없고 내부에서 호출해줘야 한다.
		Intent i = new Intent(ctx, DisableArgosService.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
		Log.i("DR", "action Start" );
	}

	public static void actionStop(Context ctx)
	{
		// 서비스 정지
		Intent i = new Intent(ctx, DisableArgosService.class);
		i.setAction(ACTION_STOP);
		ctx.stopService(i);
		Log.i("DR", "action Stop" );
	}

	public static void actionPing(Context ctx)
	{
		Intent i = new Intent(ctx, DisableArgosService.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
		Log.i("DR", "action Keep Alive" );		
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		try {
			mLog = new ConnectionLog();
			Log.i(TAG, "Opened log at " + mLog.getPath());
		} catch (IOException e) {}

		// 환경설정 참조 객체 선언
		mPrefs = getSharedPreferences(TAG, MODE_PRIVATE);

		// 서버 연결 관리자
		mConnMan =
			(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

		// 알림 표시 관리자
		mNotifMan =
			(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		/* If our process was reaped by the system for any reason we need
		 * to restore our state with merely a call to onCreate.  We record
		 * the last "started" value and restore it here if necessary. */
		handleCrashedService();
	}

	private void handleCrashedService()
	{
		// 서비스가 실행중일때
		if (wasStarted() == true)
		{
			
			// 알림창 및 알림 서비스 해제
			hideNotification();                     
			stopKeepAlives();

			/* Formally start and attempt connection. */
			start();
		}
	}

	@Override
	public void onDestroy()
	{
		log("Service destroyed (started=" + mStarted + ")");

		if (mStarted == true)
			stop();

		try {
			if (mLog != null)
				mLog.close();
		} catch (IOException e) {}
	}

	private void log(String message)
	{
		Log.i(TAG, message);

		if (mLog != null)
		{
			try {
				mLog.println(message);
			} catch (IOException e) {}
		}
	}

	public boolean wasStarted()
	{
		return mPrefs.getBoolean(PREF_STARTED, false);
	}

	private void setStarted(boolean started)
	{
		mPrefs.edit().putBoolean(PREF_STARTED, started).commit();
		mStarted = started;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		log("Service started with intent=" + intent);

		super.onStart(intent, startId);

		if ( intent == null || intent.getAction() == null ){}
		else if (intent.getAction().equals(ACTION_STOP) == true)
		{
			stop();
			stopSelf();
		}
		else if (intent.getAction().equals(ACTION_START) == true)
			start();
		else if (intent.getAction().equals(ACTION_KEEPALIVE) == true)
			keepAlive();
		else if (intent.getAction().equals(ACTION_RECONNECT) == true)
			reconnectIfNecessary();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	// 스레드로 돌려야할 작업
	private synchronized void start()
	{
		// 실행중 여부 확인
		if (mStarted == true)
		{
			Log.w(TAG, "Attempt to start connection that is already active");
			return;
		}

        
		setStarted(true);

		registerReceiver(mConnectivityChanged,
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		log("Connecting...");

		mConnection = new ConnectionThread(HOST, PORT);
		mConnection.start();
	}

	private synchronized void stop()
	{
		// 실행중 여부 확인
		if (mStarted == false)
		{
			Log.w(TAG, "Attempt to stop connection not active.");
			return;
		}

		setStarted(false);

		unregisterReceiver(mConnectivityChanged);               
		cancelReconnect();

		if (mConnection != null)
		{
			mConnection.abort();
			mConnection = null;
		}
	}

	private synchronized void keepAlive()
	{
		try {
			if (mStarted == true && mConnection != null)
				mConnection.sendKeepAlive();
		} catch (IOException e) {}
	}

	private void startKeepAlives()
	{
		Intent i = new Intent();
		i.setClass(this, DisableArgosService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + KEEP_ALIVE_INTERVAL,
				KEEP_ALIVE_INTERVAL, pi);
	}

	private void stopKeepAlives()
	{
		Intent i = new Intent();
		i.setClass(this, DisableArgosService.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	public void scheduleReconnect(long startTime)
	{
		long interval =
			mPrefs.getLong("retryInterval", INITIAL_RETRY_INTERVAL);

		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		if (elapsed < interval)
			interval = Math.min(interval * 4, MAXIMUM_RETRY_INTERVAL);
		else
			interval = INITIAL_RETRY_INTERVAL;

		log("Rescheduling connection in " + interval + "ms.");

		mPrefs.edit().putLong("retryInterval", interval).commit();

		Intent i = new Intent();
		i.setClass(this, DisableArgosService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, now + interval, pi);
	}

	public void cancelReconnect()
	{
		Intent i = new Intent();
		i.setClass(this, DisableArgosService.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary()
	{
	//	if (mStarted == true && mConnection == null)
		{
			log("Reconnecting...");

			mConnection = new ConnectionThread(HOST, PORT);
			mConnection.start();
		}
	}

	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			NetworkInfo info = (NetworkInfo)intent.getParcelableExtra
			(ConnectivityManager.EXTRA_NETWORK_INFO);

			boolean hasConnectivity = (info != null && info.isConnected()) 
			? true : false;

			log("Connecting changed: connected=" + hasConnectivity);

			if (hasConnectivity)
				reconnectIfNecessary();
		}
	};

	private void showNotification()
	{
		Notification n = new Notification();

		n.flags = Notification.FLAG_NO_CLEAR |
		Notification.FLAG_ONGOING_EVENT;

		n.icon = R.drawable.icon;
		n.when = System.currentTimeMillis();

		PendingIntent pi = PendingIntent.getActivity(this, 0,
				new Intent(this, DisableArgosService.class), 0);

		n.setLatestEventInfo(this, "KeepAlive connected",
				"Connected to HOST", pi);

		boolean isAliveConnectionNoti = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("ConnectionNoti", false);
		if ( isAliveConnectionNoti )
		{
			mNotifMan.notify(NOTIF_CONNECTED, n);
		}
	}

	private void hideNotification()
	{
		mNotifMan.cancel(NOTIF_CONNECTED);
	}

	// 서버 연결에 사용되는 스레드
	private class ConnectionThread extends Thread
	{
		private final Socket mSocket;
		private final String mHost;
		private final int mPort;

		private volatile boolean mAbort = false;

		public ConnectionThread(String host, int port)
		{
			mHost = host;
			mPort = port;
			mSocket = new Socket();
		}

//		public boolean isConnected()
//		{
//			return mSocket.isConnected();
//		}

		private boolean isNetworkAvailable()
		{
			NetworkInfo info = mConnMan.getActiveNetworkInfo();
			if (info == null)
				return false;

			return info.isConnected();
		}

		public void run()
		{
			Socket s = mSocket;
			


			long startTime = System.currentTimeMillis();

			try {
				s.connect(new InetSocketAddress(mHost, mPort), 10000);

				/* This is a special case for our demonstration.  The
				 * keep-alive is sent from the client side but since I'm
				 * testing it with just netcat, no response is sent from the
				 * server.  This means that we might never actually read
				 * any data even though our connection is still alive.  Most
				 * instances of a persistent TCP connection would have some
				 * sort of application-layer acknowledgement from the server
				 * and so should set a read timeout of KEEP_ALIVE_INTERVAL 
				 * plus an arbitrary timeout such as 2 minutes. */
				//s.setSoTimeout((int)KEEP_ALIVE_INTERVAL + 120000);

				log("Connection established to " + s.getInetAddress() +
						":" + mPort);
				sendClientID();
				startKeepAlives();
				showNotification();

				InputStream in = s.getInputStream();
				OutputStream out = s.getOutputStream();

				/* Note that T-Mobile appears to implement an opportunistic
				 * connect algorithm where the connect call may succeed
				 * even when the remote peer would reject the connection.
				 * Shortly after an attempt to send data an exception
				 * will occur indicating the connection was reset. */

				byte[] b = new byte[1024];
				int n;

				while ((n = in.read(b)) >= 0)
				{
					String ack = new String(b).trim();
					Log.i("DR", "!!!" + ack + "");
					out.write(b, 0, n);

					if( ack != null && ack.equalsIgnoreCase("start"))
					{
						log( "bindService is " + "kr.co.drdesign.client.DRMServiceController");
//						bindService(new Intent("kr.co.drdesign.client.DRMServiceController"),new RemoteServiceConnection() ,BIND_AUTO_CREATE);
						Intent intent = new Intent( getApplicationContext(), DRMService.class);
						startService(intent);
					}
				}

				if (mAbort == false)
					log("Server closed connection unexpectedly.");
			} catch (IOException e) {
				log("Unexpected I/O error: " + e.toString());
			} finally {
				stopKeepAlives();
				hideNotification();

				if (mAbort == true)
					log("Connection aborted, shutting down.");
				else
				{
					try {
						s.close();
					} catch (IOException e) {}

					synchronized(DisableArgosService.this) {
						mConnection = null;
					}

					/* If our local interface is still up then the connection
					 * failure must have been something intermittent.  Try
					 * our connection again later (the wait grows with each
					 * successive failure).  Otherwise we will try to
					 * reconnect when the local interface comes back. */
					if (isNetworkAvailable() == true)
						scheduleReconnect(startTime);
				}
			}
		}

		public void sendClientID() 
		throws IOException
		{
			Socket s = mSocket;
			String clientID = GruviceUtillity.getInstance(getApplicationContext()).getClientId();
			s.getOutputStream().write((clientID + "\n").getBytes());

			log("Client-ID sent. : " + clientID);
		}

		public void sendKeepAlive()
		throws IOException
		{
			Socket s = mSocket;
			Date d = new Date();
			s.getOutputStream().write((d.toString() + "\n").getBytes());

			log("Keep-alive sent.");
		}

		public void abort()
		{
			log("Connection aborting.");

			mAbort = true;

			try {
				mSocket.shutdownOutput();
			} catch (IOException e) {}

			try {
				mSocket.shutdownInput();
			} catch (IOException e) {}

			try {
				mSocket.close();
			} catch (IOException e) {}

			while (true)
			{
				try {
					join();
					break;
				} catch (InterruptedException e) {}
			}
		}
	}

}
