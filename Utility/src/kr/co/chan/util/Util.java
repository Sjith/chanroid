package kr.co.chan.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * 걍 이것저것 넣은 유틸리티 클래스
 * 
 * @author chan-woo park
 * 
 */
@SuppressLint({ "NewApi", "DefaultLocale" })
public class Util {

	public static class Collection {
		public static Object[] listToArray(List<?> list) {
			Object[] o = new Object[list.size()];
			return list.toArray(o);
		}

		public static List<?> arrayToList(Object[] array) {
			List<Object> list = new ArrayList<Object>();
			for (Object item : array) {
				list.add(item);
			}
			return list;
		}
	}

	public static class Connection {
		/**
		 * 단말기가 어떤 종류의 네트워크에 연결되어 있는지 확인
		 * 
		 * @param context
		 *            호출한 액티비티든 뭐든
		 * @return 네트워크 종류(ConnectivityManager 상수)
		 */
		public static int getType(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			boolean isWifiAvail = ni.isAvailable();
			boolean isWifiConn = ni.isConnected();
			ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			boolean isMobileAvail = ni == null ? false : ni.isAvailable();
			boolean isMobileConn = ni == null ? false : ni.isConnected();
			if (isWifiAvail && isWifiConn) {
				return ConnectivityManager.TYPE_WIFI;
			} else if (isMobileAvail && isMobileConn) {
				return ni.getType();
			} else {
				return ConnectivityManager.TYPE_DUMMY; // 네트워크 안됨
			}
		}
	}

	public static class Wifi {
		/**
		 * 감지된 와이파이들의 정보를 반환
		 * 
		 * @param context
		 * @return
		 */
		public static List<WifiConfiguration> getConf(Context context) {
			WifiManager mainWifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			List<WifiConfiguration> list = mainWifi.getConfiguredNetworks();
			return list;
		}

		/**
		 * 현재 연결된 와이파이의 정보를 반환
		 * 
		 * @param context
		 * @return
		 */
		public static WifiInfo getConnInfo(Context context) {
			WifiManager mainWifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			;
			WifiInfo wifiInfo = mainWifi.getConnectionInfo();
			return wifiInfo;
		}

		/**
		 * 받은 와이파이 정보에 해당하는 와이파이에 연결
		 * 
		 * @param context
		 * @param wifiConfig
		 *            감지된 와이파이 중 선택된 와이파이
		 * @return 연결 결과값
		 */
		public static int add(Context context, WifiConfiguration wifiConfig) {
			setEnable(context, true);
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			int netId = wifi.addNetwork(wifiConfig);
			wifi.saveConfiguration();
			return netId;
		}

		/**
		 * 해당 id를 가지는 와이파이를 삭제
		 * 
		 * @param context
		 * @param networkid
		 *            삭제할 와이파이의 id
		 */
		public static void remove(Context context, int networkid) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifi.removeNetwork(networkid);
			wifi.saveConfiguration();
		}

		/**
		 * 해당 ssid를 가지는 와이파이를 삭제
		 * 
		 * @param context
		 * @param ssid
		 */
		public static void remove(Context context, String ssid) {
			remove(context, ssid, null);
		}

		/**
		 * 해당 ssid, bssid를 가지는 와이파이를 삭제
		 * 
		 * @param context
		 * @param ssid
		 * @param bssid
		 */
		public static void remove(Context context, String ssid, String bssid) {
			if (ssid != null)
				ssid = ssid.replaceAll("\"", "");
			if (bssid != null)
				bssid = bssid.replaceAll("\"", "");
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			List<WifiConfiguration> configrations = getConf(context);
			for (int i = 0; i < configrations.size(); i++) {
				WifiConfiguration config = configrations.get(i);
				if ((config.SSID != null && config.SSID.replaceAll("\"", "")
						.equals(ssid))
						|| (config.BSSID != null && config.BSSID.replaceAll(
								"\"", "").equals(bssid))) {
					remove(context, config.networkId);
				}
			}
			wifi.saveConfiguration();
		}

		/**
		 * 지정된 와이파이에 연결한다
		 * 
		 * @param context
		 * @param wifiConfig
		 *            해당 와이파이 정보
		 * @param ntind
		 */
		public static void connect(Context context,
				WifiConfiguration wifiConfig, int ntind) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifi.updateNetwork(wifiConfig);
			wifi.enableNetwork(ntind, true);
		}

		/**
		 * 와이파이의 온오프 제어
		 * 
		 * @param context
		 * @param sw
		 * @return 성공여부
		 */
		public static boolean setEnable(Context context, boolean sw) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			return wifiManager.setWifiEnabled(sw);
		}

		/**
		 * 와이파이의 활성여부 확인
		 * 
		 * @param context
		 * @param sw
		 * @return
		 */
		public static boolean getEnableState(Context context, boolean sw) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			boolean enabled = wifiManager.isWifiEnabled();
			return enabled;
		}
	}

	public static class Bluetooth {

		/**
		 * 블루투스 온오프 제어
		 * 
		 * @param sw
		 * @return 성공여부
		 */
		public static boolean setEnable(boolean sw) {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

			if (sw) {
				if (adapter.getState() == BluetoothAdapter.STATE_TURNING_OFF
						|| adapter.getState() == BluetoothAdapter.STATE_OFF) {
					return adapter.enable();
				}
			} else {
				if (adapter.getState() == BluetoothAdapter.STATE_TURNING_ON
						|| adapter.getState() == BluetoothAdapter.STATE_ON) {
					return adapter.disable();
				}
			}

			return true;
		}

		/**
		 * 블루투스 활성여부 확인
		 * 
		 * @return
		 */
		public static int getStatus() {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			return adapter.getState();
		}
	}

	public static class GPS {
		/**
		 * GPS 설정 페이지 열기
		 * 
		 * @param context
		 */
		public static void goSetting(Context context) {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			context.startActivity(intent);
		}
	}

	@SuppressLint("DefaultLocale")
	public static class Time {
		/**
		 * 시간정보를 00:00:00 의 형식으로 변환한다.
		 * 
		 * @param timeMs
		 * @return
		 */
		public static String stringForTime(int timeMs) {
			Formatter mFormatter = null;
			try {
				int totalSeconds = timeMs / 1000;

				int seconds = totalSeconds % 60;
				int minutes = (totalSeconds / 60) % 60;
				int hours = totalSeconds / 3600;

				StringBuilder mFormatBuilder = new StringBuilder();
				mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
				mFormatBuilder.setLength(0);
				if (hours > 0) {
					return mFormatter.format("%02d:%02d:%02d", hours, minutes,
							seconds).toString();
				} else {
					return mFormatter.format("00:%02d:%02d", minutes, seconds)
							.toString();
				}
			} catch (ArithmeticException e) {
				return "00:00:00";
			} finally {
				if (mFormatter != null)
					mFormatter.close();
			}

		}

		/**
		 * 시간정보를 00:00:00 의 형식으로 변환한다.
		 * 
		 * @param timeMs
		 * @return
		 */
		public static String stringForTime(String timeMs) {
			return stringForTime(Integer.valueOf(timeMs));
		}

		/**
		 * 시간정보를 00:00:00 의 형식으로 변환한다.
		 * 
		 * @param timeMs
		 * @return
		 */
		public static String stringForTime(long timeMs) {
			return stringForTime((int) timeMs);
		}
	}

	public static class Lang {
		/**
		 * 언어 설정 페이지 열기
		 * 
		 * @param context
		 */
		public static void goSetting(Context context) {
			context.startActivity(new Intent(
					android.provider.Settings.ACTION_LOCALE_SETTINGS));
		}
	}

	public static class App {

		/**
		 * 앱의 현재 버전(versionName)을 실수형으로 리턴 (버전에 알파벳이 있을시 0.0으로 리턴됨)
		 * 
		 * @param ctx
		 * @return
		 */
		public static Float getVersionNum(Context ctx) {
			try {
				return Float
						.valueOf(ctx.getPackageManager().getPackageInfo(
								ctx.getPackageName(),
								PackageManager.GET_META_DATA).versionName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return 0.0f;
			}
		}

		/**
		 * 터미널 콘솔에 지정된 명령어를 수행하고 그 결과의 가장 마지막 줄을 리턴
		 * 
		 * @param shell
		 * @return
		 */
		public static String executeShell(String shell) {
			java.lang.Process p = null;
			BufferedReader in = null;
			String returnString = null;
			try {
				p = Runtime.getRuntime().exec(shell);
				in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while (returnString == null || returnString.contentEquals("")) {
					returnString = in.readLine();
				}
			} catch (IOException e) {
				l.e("error in getting first line of top");
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
					if (p != null)
						p.destroy();
				} catch (IOException e) {
					l.e("error in closing and destroying top process");
					e.printStackTrace();
				}
			}
			return returnString;
		}

		public static void goMarket(Context ctx) {
			ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + ctx.getPackageName())));
		}

		public static void goMarket(Context ctx, String pkgname) {
			ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + pkgname)));
		}

		public static void goTstore(Context ctx, String appid) {
			try {

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("tstore://PRODUCT_VIEW/" + appid
						+ "/0"));
				ctx.startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(ctx, "티스토어가 설치되어 있지 않습니다.", Toast.LENGTH_LONG)
						.show();
			}
		}

		public static void goOZstore(Context ctx, String appid) {
			try {
				Intent intent = new Intent();
				intent.setClassName("android.lgt.appstore",
						"android.lgt.appstore.Store");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("payload", "PID=" + appid);
				ctx.startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(ctx, "오즈스토어가 설치되어 있지 않습니다.", Toast.LENGTH_LONG)
						.show();
			}
		}

		/**
		 * 다른 어플리케이션 실행
		 * 
		 * @param context
		 * @param pkg
		 *            해당 어플리케이션 패키지 네임
		 * @param fullclasspath
		 *            실행할 클래스명
		 */
		public static void go(Context context, String pkg, String fullclasspath) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(new ComponentName(pkg, fullclasspath));
			context.startActivity(intent);
		}

		/**
		 * 어떤 프로세스를 바로 종료시킴
		 * 
		 * @param context
		 * @param packageName
		 *            종료시킬 패키지 네임
		 */
		public static void kill(Context context, String packageName) {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			mActivityManager.killBackgroundProcesses(packageName);
		}

		public static void kill(Context ctx, int pid) {
			android.os.Process.killProcess(pid);
		}

		public static void kill(Context ctx) {
			ActivityManager mActivityManager = (ActivityManager) ctx
					.getSystemService(Context.ACTIVITY_SERVICE);
			mActivityManager.killBackgroundProcesses(ctx.getPackageName());
		}

		public static boolean isRunningApp(Context ctx, String pkgName) {
			return getRunningProcess(ctx).contains(pkgName);
		}

		/**
		 * 실행중인 어플리케이션의 목록을 가져옴
		 * 
		 * @param ctx
		 *            해당 액티비티
		 * @return 실행중인 어플리케이션 목록
		 */
		public static ArrayList<String> getRunningProcess(Context ctx) {
			ArrayList<String> list = new ArrayList<String>();
			List<RunningAppProcessInfo> applist = getRunningProcessInfo(ctx);
			for (RunningAppProcessInfo r : applist)
				list.add(r.processName);
			return list;
		}

		public static boolean isInstalled(Context ctx, String pkgname) {
			try {
				ctx.getPackageManager().getApplicationInfo(pkgname,
						PackageManager.GET_META_DATA);
				return true;
			} catch (NameNotFoundException e) {
				return false;
			}
		}

		public static List<RunningAppProcessInfo> getRunningProcessInfo(
				Context ctx) {
			ActivityManager am = (ActivityManager) ctx
					.getSystemService(Context.ACTIVITY_SERVICE);
			return am.getRunningAppProcesses();
		}
	}

	public static class Noti {
		/**
		 * 
		 * 상단바 알림을 띄움
		 * 
		 * @param context
		 * @param title
		 * @param message
		 * @param alertmessage
		 *            뜰때 메시지
		 * @param icon
		 * @param notiid
		 *            알림 식별 코드 숫자
		 * @param requestcode
		 *            인텐트 리퀘스트 코드 숫자
		 * @param forwardIntent
		 *            뭘 실행할지에 대한 인텐트
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static NotificationManager show(Context context, String title,
				String message, String alertmessage, int icon, int notiid,
				int requestcode, Intent forwardIntent) {

			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			PendingIntent contentIntent = PendingIntent.getActivity(context,
					requestcode, forwardIntent, 0);
			Notification notif = new Notification(icon, alertmessage,
					System.currentTimeMillis());
			notif.setLatestEventInfo(context, title, message, contentIntent);
			notif.vibrate = new long[] { 200, 200, 200 };
			nm.notify(notiid, notif);

			return nm;
		}
	}

	public static class Loc {
		/**
		 * 현재 위치 정보를 반환
		 * 
		 * @param context
		 * @return
		 */
		public static Locale get(Context context) {
			Locale locale = context.getResources().getConfiguration().locale;
			return locale;
		}

		/**
		 * 두 지점 사이의 거리를 반환
		 * 
		 * @param startlat
		 *            시작지점 위도
		 * @param startlng
		 *            시작지점 경도
		 * @param endlat
		 *            끝지점 위도
		 * @param endlng
		 *            끝지점 경도
		 * @return
		 */
		public static float getDistanceBetween(double startlat,
				double startlng, double endlat, double endlng) {
			float[] results = new float[] {};
			Location.distanceBetween(startlat, startlng, endlat, endlng,
					results);
			return results[0];
		}

		/**
		 * 현재 위치서비스가 활성화되어 있는지의 여부를 반환. 네트워크와 GPS 둘다 비활성시 FALSE 반환.
		 * 
		 * @param mContext
		 * @return
		 */
		public static boolean chkLocService(Context mContext) {
			LocationManager locManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
			boolean isEnabled = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) ? locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)
					: locManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			return isEnabled;
		}
	}

	public static class Phone {

		/**
		 * 현재 배터리 상태를 백분율 정수로 반환
		 * 
		 * @param ctx
		 *            호출하는 액티비티
		 * @return 현재 배터리 상태 0~100 int
		 */
		public static int getBattStat(Context ctx) {
			Intent bat = ctx.registerReceiver(null, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			return Math.getPercent(
					bat.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
					bat.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
		}

		/**
		 * 디바이스의 고유한 번호를 반환 Simid --> (없으면) deviceId --> (없으면) AndroidID
		 * 
		 * @param ctx
		 *            호출한 액티비티
		 * @return Simid 또는 deviceId 또는 AndroidID
		 */
		public static String getDeviceId(Context ctx) {
			TelephonyManager mTelManager = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			String clientID = mTelManager.getSimSerialNumber();
			if (clientID == null || clientID.length() < 4)
				clientID = mTelManager.getDeviceId();
			if (clientID == null || clientID.length() < 4)
				clientID = android.provider.Settings.Secure.getString(
						ctx.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			return clientID;
		}

		/**
		 * 
		 * <PRE>
		 * 1. MethodName : setVolume
		 * 2. ClassName  : Phone
		 * 3. Comment   : 지정된 스트림의 볼륨을 설정함
		 * 4. 작성자    : 박찬우
		 * 5. 작성일    : 2012. 10. 15. 오후 8:08:52
		 * </PRE>
		 * 
		 * @return void
		 * @param ctx
		 * @param stream
		 * @param volume
		 * @param flags
		 */
		public static void setVolume(Context ctx, int stream, int volume,
				int flags) {
			AudioManager audioMng = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			audioMng.setStreamVolume(stream, volume, flags);
		}

		/**
		 * 
		 * <PRE>
		 * 1. MethodName : getVolume
		 * 2. ClassName  : Phone
		 * 3. Comment   : 지정된 스트림의 볼륨을 가져옴
		 * 4. 작성자    : 박찬우
		 * 5. 작성일    : 2012. 10. 15. 오후 8:09:08
		 * </PRE>
		 * 
		 * @return int
		 * @param ctx
		 * @param stream
		 * @return
		 */
		public static int getVolume(Context ctx, int stream) {
			AudioManager audioMng = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			return audioMng.getStreamVolume(stream);
		}

		/**
		 * 
		 * <PRE>
		 * 1. MethodName : getMaxVolume
		 * 2. ClassName  : Phone
		 * 3. Comment   : 지정된 스트림의 최대 볼륨을 가져옴
		 * 4. 작성자    : 박찬우
		 * 5. 작성일    : 2012. 10. 15. 오후 8:09:27
		 * </PRE>
		 * 
		 * @return int
		 * @param ctx
		 * @param stream
		 * @return
		 */
		public static int getMaxVolume(Context ctx, int stream) {
			AudioManager audioMng = (AudioManager) ctx
					.getSystemService(Context.AUDIO_SERVICE);
			return audioMng.getStreamMaxVolume(stream);
		}

		/**
		 * 지정된 주소로 이메일 보내기
		 * 
		 * @param ctx
		 * @param mail
		 */
		public static void sendMail(Context ctx, String mail) {
			Uri uri = Uri.parse("mailto:" + mail);
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			ctx.startActivity(it);
		}

		/**
		 * 해당 휴대폰의 휴대폰 번호를 가져옴. 대한민국 010 형식으로 변환하여 가져온다. "READ_PHONE_STATE" 퍼미션
		 * 필요.
		 * 
		 * @param ctx
		 *            호출한 액티비티
		 * 
		 * @return 휴대폰 번호
		 * 
		 * @throws NullPointerException
		 *             휴대폰이 아닌 장비에서 발생할 수 있음.
		 */
		public static String getNum(Context ctx) throws NullPointerException {
			TelephonyManager mTelephonyMgr = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			try {
				return mTelephonyMgr.getLine1Number().replace("+82", "0")
						.replace("-", "");
			} catch (NullPointerException e) {
				return null;
			}
		}

		/**
		 * 지정된 번호로 "다이얼" (전화를 걸지 않음)
		 * 
		 * @param ctx
		 *            호출하는 액티비티
		 * 
		 * @param phoneno
		 *            다이얼할 번호
		 */
		public static void autoDial(Context ctx, String phoneno) {
			Intent localIntent = new Intent(Intent.ACTION_DIAL);
			localIntent.setData(Uri.parse("tel:" + phoneno));
			ctx.startActivity(localIntent);
		}

		/**
		 * 현재 단말기에 등록된 통신사를 가져옴 휴대폰이 아닌 경우 null 값이 생성될 수 있으므로 적절한 예외처리 필요
		 * "READ_PHONE_STATE" 퍼미션 필요.
		 * 
		 * @param ctx
		 * @return operator 휴대폰이 아닌 장비에서 사용시 주의
		 */
		public static String getOper(Context ctx) {
			TelephonyManager tm = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getNetworkOperatorName();
		}

	}

	public static class Math {

		public static long avr(ArrayList<Long> array) {
			long value = 0;
			for (long a : array) {
				value += a;
			}
			return value / array.size();
		}

		/**
		 * 정수의 각 자리를 쪼개어 배열로 리턴
		 * 
		 * @param val
		 * @return
		 */
		public static int[] split(int val) {
			int[] retval = new int[getIntegerLength(val)];
			int len = getIntegerLength(val);
			for (int i = 0; i < len; i++) {
				int result = (int) (val / java.lang.Math.pow(10, (len - 1) - i) % 10);
				retval[i] = result;
			}
			return retval;
		}

		/**
		 * 숫자의 자리수를 구함
		 * 
		 * @param i
		 * @return
		 */
		public static int getIntegerLength(int i) {
			int len = 1;
			int div;
			while (true) {
				div = i / 10;
				if (div == 0)
					break;
				len++;
				i = div;
			}
			return len;
		}

		/**
		 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
		 * 
		 * @param level
		 * @param scale
		 * @return
		 */
		public static int getPercent(int level, int scale) {
			return getPercent((double) level, (double) scale);
		}

		/**
		 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
		 * 
		 * @param level
		 * @param scale
		 * @return
		 */
		public static int getPercent(float level, float scale) {
			return getPercent((double) level, (double) scale);
		}

		/**
		 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
		 * 
		 * @param level
		 * @param scale
		 * @return
		 */
		public static int getPercent(double level, double scale) {
			return (int) (level / scale * 100);
		}

		/**
		 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
		 * 
		 * @param level
		 * @param scale
		 * @return
		 */
		public static int getPercent(long level, long scale) {
			return getPercent((double) level, (double) scale);
		}

		/**
		 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
		 * 
		 * @param level
		 * @param scale
		 * @return
		 */
		public static int getPercent(short level, short scale) {
			return getPercent((double) level, (double) scale);
		}

		/**
		 * 정해진 범위의 숫자들을 랜덤하게 섞어서 배열로 리턴
		 * 
		 * @param arraysize
		 *            배열 크기
		 * @param startNum
		 *            섞을 숫자 범위의 가장 작은 수
		 * @param lastNum
		 *            섞을 숫자 범위의 가장 큰 수
		 * 
		 * @return 정해진 범위의 숫자들이 랜덤하게 섞인 배열
		 */
		public static ArrayList<Integer> getRandomIntArray(int arraysize,
				int startNum, int lastNum) {
			ArrayList<Integer> rnumList = new ArrayList<Integer>();
			int[] chk = new int[arraysize];
			for (int i = 0; i < arraysize; i++) {
				chk[i] = (int) (startNum + java.lang.Math.random()
						* (lastNum - startNum));
				for (int j = 0; j < i; j++) {
					if (chk[i] == chk[j]) {
						i--;
						break;
					}
				}
			}
			for (int i = 0; i < chk.length; i++) {
				rnumList.add(chk[i]);
			}
			return rnumList;
		}

		/**
		 * 주민등록번호를 입력받아 나이를 구함 이때 나이는 대한민국 기준이 아닌 "만" 나이
		 * 
		 * @param idNum
		 *            주민등록번호
		 * 
		 * @return "만" 나이
		 */
		public static int getMyAge(String idNum) {
			String year = "";
			String month = "";
			String day = "";
			String myYear = "";
			int myAge = 0;

			SimpleDateFormat formatY = new SimpleDateFormat("yyyy",
					Locale.KOREA);
			SimpleDateFormat formatM = new SimpleDateFormat("MM", Locale.KOREA);
			SimpleDateFormat formatD = new SimpleDateFormat("dd", Locale.KOREA);
			year = formatY.format(new Date());
			month = formatM.format(new Date());
			day = formatD.format(new Date());

			if (idNum.charAt(6) == '1' || idNum.charAt(6) == '2') {
				myYear = "19" + idNum.substring(0, 2);
			} else {
				myYear = "20" + idNum.substring(0, 2);
			}

			if (Integer.parseInt(month) > Integer.parseInt(idNum
					.substring(2, 4))) {
				myAge = Integer.parseInt(year) - Integer.parseInt(myYear);
			} else if (Integer.parseInt(month) == Integer.parseInt(idNum
					.substring(2, 4))) {
				if (Integer.parseInt(day) > Integer.parseInt(idNum.substring(4,
						6))) {
					myAge = Integer.parseInt(year) - Integer.parseInt(myYear);
				} else {
					myAge = Integer.parseInt(year)
							- (Integer.parseInt(myYear) + 1);
				}
			} else {
				myAge = Integer.parseInt(year) - (Integer.parseInt(myYear) + 1);
			}
			myYear = "00";
			return myAge;
		}

		/**
		 * 주민등록번호 유효성 검사 이론상 외국인도 가능하지만 검증되지는 않음
		 * 
		 * @param jumin
		 *            주민등록번호
		 * 
		 * @return 유효성 여부
		 */
		public static boolean juminCheck(String jumin) {
			boolean isKorean = true;
			int check = 0;
			if (jumin == null || jumin.length() != 13)
				return false;
			if (Character.getNumericValue(jumin.charAt(6)) > 4
					&& Character.getNumericValue(jumin.charAt(6)) < 9) {
				isKorean = false;
			}
			for (int i = 0; i < 12; i++) {
				if (isKorean)
					check += ((i % 8 + 2) * Character.getNumericValue(jumin
							.charAt(i)));
				else
					check += ((9 - i % 8) * Character.getNumericValue(jumin
							.charAt(i)));
			}
			if (isKorean) {
				check = 11 - (check % 11);
				check %= 10;
			} else {
				int remainder = check % 11;
				if (remainder == 0)
					check = 1;
				else if (remainder == 10)
					check = 0;
				else
					check = remainder;
				int check2 = check + 2;
				if (check2 > 9)
					check = check2 - 10;
				else
					check = check2;
			}
			if (check == Character.getNumericValue(jumin.charAt(12)))
				return true;
			else
				return false;
		}
	}

	public static class Stream {

		public static InputStream openAsset(Context ctx, String fileName)
				throws IOException {
			return ctx.getAssets().open(fileName);
		}

		/**
		 * html을 파싱하기 편한 형태로 정리. script, style css, blank, 개행문자 등 제거
		 * 
		 * @param str
		 * @return
		 */
		public static String cleanHtml(String str) {
			final Pattern SCRIPTS = Pattern.compile(
					"<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
			final Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>",
					Pattern.DOTALL);
			final Pattern Blank = Pattern.compile("\n\n", Pattern.DOTALL);
			if (str == null) {
				return null;
			}
			Matcher mat;
			mat = SCRIPTS.matcher(str);
			str = mat.replaceAll("");
			mat = STYLE.matcher(str);
			str = mat.replaceAll("");
			mat = Blank.matcher(str);
			str = mat.replaceAll("");
			return str;
		}

		/**
		 * 파일 다운로드. sdk 버전에 따라 다르게 동작.
		 * 
		 * @param ctx
		 * @param uri
		 * @return 다운로드 리퀘스트 아이디. api level 8 이하에서는 0이 리턴됨
		 */
		public static long download(Context ctx, Uri uri) {
			if (Build.VERSION.SDK_INT > 8) {
				List<String> segment = uri.getPathSegments();
				DownloadManager manager = (DownloadManager) ctx
						.getSystemService(Context.DOWNLOAD_SERVICE);
				DownloadManager.Request request = new DownloadManager.Request(
						uri);
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS,
						segment.get(segment.size() - 1));
				Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DOWNLOADS).mkdirs();
				return manager.enqueue(request);
			} else {
				ctx.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return 0L;
			}
		}

		/**
		 * 앱 다운로드 후 설치. api level 9 미만에서는 설치창으로 이동하지 않음.
		 * 
		 * @param ctx
		 * @param uri
		 */
		public static void downloadAndInstall(final Context ctx, Uri uri) {
			if (Build.VERSION.SDK_INT < 9) {
				download(ctx, uri);
				return;
			}
			final List<String> segment = uri.getPathSegments();
			DownloadManager manager = (DownloadManager) ctx
					.getSystemService(Context.DOWNLOAD_SERVICE);
			DownloadManager.Request request = new DownloadManager.Request(uri);
			request.setDestinationInExternalPublicDir(
					Environment.DIRECTORY_DOWNLOADS,
					segment.get(segment.size() - 1));
			Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).mkdirs();
			manager.enqueue(request);
			BroadcastReceiver broad = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Intent.ACTION_VIEW);
					java.io.File file = new java.io.File(
							Environment.getExternalStorageDirectory() + "/"
									+ Environment.DIRECTORY_DOWNLOADS,
							segment.get(segment.size() - 1));
					i.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					ctx.startActivity(i);
					ctx.unregisterReceiver(this);
				}
			};
			ctx.registerReceiver(broad, new IntentFilter(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		}

		/**
		 * 뷰를 비트맵 파일로 저장
		 * 
		 * @param v
		 *            캡쳐할 뷰(레이아웃)
		 * @param path
		 *            파일 저장 경로 및 파일명
		 * @return 파일이 저장된 경로
		 */
		public static String saveView(android.view.View v, String path) {
			if (!v.isDrawingCacheEnabled())
				v.setDrawingCacheEnabled(true);
			android.graphics.Bitmap c = v.getDrawingCache();
			java.io.File file = new java.io.File(path);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				c.compress(CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				v.setDrawingCacheEnabled(false);
				if (!c.isRecycled()) {
					c.recycle();
				}
			}
			return file.getAbsolutePath();
		}

		/**
		 * 지정된 url의 스트림을 가져옴
		 * 
		 * @param url
		 *            스트림을 가져올 url
		 * @return 지정된 url의 스트림
		 * @throws IllegalStateException
		 * @throws IOException
		 */
		public static InputStream inStreamFromURL(String url)
				throws IllegalStateException, IOException {
			return inStreamFromURLbyPOST(url, null);
		}

		/**
		 * 지정된 url의 스트림을 가져옴
		 * 
		 * @param url
		 *            스트림을 가져올 url
		 * @param params
		 *            url에 보낼 post data
		 * @return 지정된 url의 스트림
		 * @throws IllegalStateException
		 * @throws IOException
		 */
		public static InputStream inStreamFromURLbyPOST(String url,
				ArrayList<NameValuePair> params) throws IllegalStateException,
				IOException {

			StringBuilder paramBuilder = new StringBuilder();
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");

			if (params != null) {
				conn.setDoOutput(true); // 이거 호출하면 자동으로 POST로 변경됨
				for (NameValuePair p : params)
					paramBuilder.append(p.getName() + "=" + p.getValue() + "&");
				OutputStream paramWriter = conn.getOutputStream();
				paramWriter.write(paramBuilder.toString().getBytes());
				paramWriter.flush();
				paramWriter.close();
			}

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
				return conn.getInputStream();
			else
				return null;

		}

		/**
		 * 
		 * <PRE>
		 * 1. MethodName : stringFromThrowable
		 * 2. ClassName  : Stream
		 * 3. Comment   : 해당 에러에 대한 stack trace를 리턴 
		 * 4. 작성자    : 박찬우
		 * 5. 작성일    : 2012. 10. 30. 오후 5:24:30
		 * </PRE>
		 * 
		 * @return String
		 * @param t
		 * @return
		 */
		public static String stringFromThrowable(Throwable t) {
			Writer writer = new StringWriter();
			PrintWriter print = new PrintWriter(writer);
			t.printStackTrace(print);
			return writer.toString();
		}

		/**
		 * 지정된 url의 내용을 가져옴
		 * 
		 * @param url
		 *            내용이 있는 url
		 * @return 해당 url에 있는 내용
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
		public static String stringFromURL(String url)
				throws ClientProtocolException, IOException {
			return stringFromURLbyPOST(url, null);
		}

		/**
		 * 지정된 url의 내용을 가져옴
		 * 
		 * @param url
		 *            내용이 있는 url
		 * @param params
		 *            url에 보낼 post data
		 * @return 해당 url에 있는 내용
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
		public static String stringFromURLbyPOST(String url,
				ArrayList<NameValuePair> params)
				throws ClientProtocolException, IOException {
			StringBuilder sb = new StringBuilder();
			InputStream in = inStreamFromURLbyPOST(url, params);
			if (in == null) {
				return null;
			} else {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				in.close();
				return sb.toString();
			}
		}

		/**
		 * 지정된 url의 json문서를 파싱
		 * 
		 * @param url
		 *            파싱할 json 문서의 url
		 * 
		 * @return 파싱이 완료되어 생성된 JSONObject
		 * 
		 * @throws ClientProtocolException
		 * @throws IOException
		 * @throws JSONException
		 */
		public static JSONObject jsonFromURL(String url)
				throws ClientProtocolException, IOException, JSONException {
			return new JSONObject(stringFromURL(url));
		}

		/**
		 * 지정된 url의 json문서를 파싱
		 * 
		 * @param url
		 *            파싱할 json 문서의 url
		 * @param params
		 *            url에 보낼 post data
		 * 
		 * @return 파싱이 완료되어 생성된 JSONObject
		 * 
		 * @throws ClientProtocolException
		 * @throws IOException
		 * @throws JSONException
		 */
		public static JSONObject jsonFromURLbyPOST(String url,
				ArrayList<NameValuePair> params)
				throws ClientProtocolException, JSONException, IOException {
			return new JSONObject(stringFromURLbyPOST(url, params));
		}

		public static JSONObject xmlTojson(String xmlString)
				throws IllegalArgumentException, IllegalStateException,
				IOException, JSONException {
			XMLSerializer serializer = new XMLSerializer();
			JSON json = serializer.read(xmlString);
			return new JSONObject(json.toString(2));
		}

		public static String jsonToxml(JSONObject json) {
			return new XMLSerializer().write(JSONSerializer.toJSON(json
					.toString()));
		}

		/**
		 * 지정된 url의 xml문서를 파싱할 parser을 준비
		 * 
		 * @param urls
		 *            파싱할 문서의 url
		 * @param characterset
		 *            해당 url 페이지의 문자셋
		 * 
		 * @return 해당 url의 xml문서가 들어있는 parser
		 * 
		 * @throws XmlPullParserException
		 * @throws IOException
		 */
		public static XmlPullParser xmlFromURL(String url, String characterset)
				throws XmlPullParserException, IOException {
			return xmlFromURLbyPOST(url, characterset, null);
		}

		/**
		 * 지정된 url의 xml문서를 파싱할 parser을 준비
		 * 
		 * @param urls
		 *            파싱할 문서의 url
		 * @param characterset
		 *            해당 url 페이지의 문자셋
		 * 
		 * @return 해당 url의 xml문서가 들어있는 parser
		 * 
		 * @throws XmlPullParserException
		 * @throws IOException
		 */
		public static XmlPullParser xmlFromURLbyPOST(String url,
				String characterset, ArrayList<NameValuePair> params)
				throws XmlPullParserException, IOException {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStreamFromURLbyPOST(url, params), characterset);
			return parser;
		}

		/**
		 * 지정된 url에서 이미지를 가져옴
		 * 
		 * @param urls
		 *            가져올 이미지의 url
		 * 
		 * @return 가져온 이미지
		 * 
		 * @throws IOException
		 */
		public static android.graphics.Bitmap bitmapFromURL(String urls)
				throws IOException {
			URL url = new URL(urls);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream bis = new DataInputStream(conn.getInputStream());
			android.graphics.Bitmap b;
			if (android.os.Build.VERSION.SDK_INT > 8)
				b = BitmapFactory.decodeStream(bis);
			else
				b = BitmapFactory.decodeStream(new FlushedInputStream(bis));
			bis.close();
			return b;
		}
	}

	public static class Bitmap {
		/**
		 * 비트맵의 모서리를 둥글게 처리
		 * 
		 * @param x
		 *            둥글게 처리할 비트맵
		 * @param xradius
		 * @param yradius
		 * 
		 * @return 둥글게 처리된 비트맵
		 */
		public static android.graphics.Bitmap gerRoundBitmap(
				android.graphics.Bitmap x, int xradius, int yradius) {
			android.graphics.Bitmap output = android.graphics.Bitmap
					.createBitmap(x.getWidth(), x.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, x.getWidth(), x.getHeight());
			final RectF rectF = new RectF(rect);
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, xradius, yradius, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(x, rect, rect, paint);
			x.recycle();
			return output;
		}

		/**
		 * 원본 비트맵 위에 다른 비트맵을 겹침 두 비트맵의 크기가 같거나 비슷할 것을 권장 두 비트맵의 크기가 다를 경우 왼쪽 위를
		 * 기준으로 겹쳐짐
		 * 
		 * @param bmp1
		 *            원본 비트맵
		 * @param bmp2
		 *            원본 위에 겹쳐질 비트맵
		 * 
		 * @return 겹쳐진 비트맵
		 */
		public static android.graphics.Bitmap overlay(
				android.graphics.Bitmap bmp1, android.graphics.Bitmap bmp2) {
			android.graphics.Bitmap bmOverlay = android.graphics.Bitmap
					.createBitmap(bmp1.getWidth(), bmp1.getHeight(),
							bmp1.getConfig());
			android.graphics.Bitmap bmOrign = android.graphics.Bitmap
					.createScaledBitmap(bmp2, bmp1.getWidth(),
							bmp1.getHeight(), true);
			Canvas canvas = new Canvas(bmOverlay);
			canvas.drawBitmap(bmp1, 0, 0, null);
			canvas.drawBitmap(bmOrign, 0, 0, null);
			bmp1.recycle();
			bmp2.recycle();
			bmOrign.recycle();
			return bmOverlay;
		}
	}

	public static class Display {

		public static boolean isTablet(Context context) {
			Configuration config = context.getResources().getConfiguration();
			boolean isXlarge = (config.screenLayout & 4) == 4;
			boolean isLarge = (config.screenLayout & 3) == 3;

			boolean mask = true;
			if (Build.DEVICE.contains("SHV-E160")) // 노트1
				mask = false;
			else if (Build.DEVICE.contains("SHW-M180")) // 탭7
				mask = true;
			else if (getScreenInch(context) < 6.d) // 6인치 미만은 폰으로 취급
				mask = false;

			return (isXlarge || isLarge) & mask;
		}

		public static double getScreenInch(Context ctx) {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager) ctx
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(dm);
			double x = java.lang.Math.pow(dm.widthPixels / dm.xdpi, 2);
			double y = java.lang.Math.pow(dm.heightPixels / dm.ydpi, 2);
			return java.lang.Math.sqrt(x + y);
		}

		/**
		 * 상단바의 사이즈를 반환
		 * 
		 * @return 상단바 사이즈
		 */
		public static int getNotiBarSize(Context ctx) {
			return (int) Views.applyDimension(25, ctx);
		}

		/**
		 * 화면 전체 사이즈를 반환
		 * 
		 * @param ctx
		 *            호출한 액티비티
		 * @return 0 - 넓이, 1 - 높이
		 */
		public static int[] getWindowSize(Context ctx) {
			int[] result = new int[2];
			DisplayMetrics displayMetrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) ctx
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(displayMetrics);
			result[0] = displayMetrics.widthPixels;
			result[1] = displayMetrics.heightPixels;
			return result;
		}

		/**
		 * 현재 액티비티의 밝기를 조절
		 * 
		 * @param ctx
		 *            호출하는 액티비티
		 * @param bright
		 *            조절할 밝기 값. 0~1 (float)
		 */
		public static void setBrightness(Activity ctx, float bright) {
			WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
			lp.screenBrightness = bright;
			ctx.getWindow().setAttributes(lp);
		}

		public static float getBrightness(Activity ctx) {
			WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
			return lp.screenBrightness;
		}
	}

	public static class Views {
		/**
		 * 별 기능없이 메시지만 있는 간단한 알림창을 하나 띄움 (캔슬 됨)
		 * 
		 * @param ctx
		 *            띄울 액티비티
		 * @param title
		 *            타이틀
		 * @param message
		 *            메시지
		 * @param click
		 *            클릭 리스너
		 * 
		 * @return 여러번 사용할 필요가 있는 경우 레퍼런스를 반환
		 */
		public static AlertDialog showAlert(Context ctx, String title,
				String message, String btntext,
				DialogInterface.OnClickListener click, boolean cancelable) {
			AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
			alert.setCancelable(cancelable);
			if (title != null)
				alert.setTitle(title);
			if (message != null)
				alert.setMessage(message);
			if (btntext != null)
				alert.setPositiveButton(btntext, click);
			return alert.show();
		}

		/**
		 * 별 기능 없이 뺑뺑이만 도는 로딩창을 하나 띄움 (캔슬 안됨)
		 * 
		 * @param ctx
		 *            띄울 액티비티
		 * @param title
		 *            타이틀
		 * @param message
		 *            메시지
		 * 
		 * @return 여러번 사용할 필요가 있는 경우 레퍼런스를 반환
		 */
		public static ProgressDialog showLoading(Context ctx, String title,
				String message) {
			ProgressDialog loading = new ProgressDialog(ctx);
			if (title != null)
				loading.setTitle(title);
			if (message != null)
				loading.setMessage(message);
			loading.setCancelable(false);
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loading.show();
			return loading;
		}

		/**
		 * 해당 뷰와 그 하위뷰들을 모두 버림 (AdapterView 및 그를 상속한 뷰에서는 정상적으로 작동하지 않을 수 있음)
		 * 
		 * @param root
		 *            반환할 뷰
		 */
		@SuppressWarnings("deprecation")
		public static void Recycle(android.view.View root) {
			if (root == null)
				return;
			if (root instanceof android.view.ViewGroup) {
				ViewGroup group = (ViewGroup) root;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++) {
					Recycle(group.getChildAt(i));
				}
			}
			if (root instanceof ImageView) {
				((ImageView) root).setImageDrawable(null);
				((ImageView) root).setImageBitmap(null);
			}
			if (root instanceof AdapterView) {
				((AdapterView<?>) root).setAdapter(null);
			}
			if (Build.VERSION.SDK_INT >= 16)
				root.setBackground(null);
			else
				root.setBackgroundDrawable(null);
			root = null;
			return;
		}

		/**
		 * 레이아웃 내의 레이아웃이 아닌 모든 뷰들의 클릭리스너를 한꺼번에 지정 AdapterView 종류는 사용금지
		 * 
		 * @param v
		 * @param l
		 */
		public static void setClickListeners(View v, OnClickListener l) {
			if (v instanceof AdapterView<?>) {
				return;
			} else if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					setClickListeners(vg.getChildAt(i), l);
				}
			} else {
				v.setOnClickListener(l);
			}
		}

		/**
		 * 코드상에서 레이아웃의 dip를 지정할 수 없으므로 dip를 픽셀로 변환시켜 준다.
		 * 
		 * @param value
		 * @param ctx
		 * @return
		 */
		public static float applyDimension(int value, Context ctx) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					value, ctx.getResources().getDisplayMetrics());
		}

		/**
		 * 코드상에서 텍스트 사이즈(sp) 를 지정할 수 없으므로 sp를 포인트로 변환시켜 준다.
		 * 
		 * @param value
		 * @param ctx
		 * @return
		 */
		public static float applyScale(int value, Context ctx) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
					ctx.getResources().getDisplayMetrics());
		}

	}

	public static class File {

		/**
		 * 파일 이름이나 경로에서 파일 확장자를 구함
		 * 
		 * @param path
		 *            파일 이름이나 경로
		 * @return 확장자명
		 */
		public static String getExtension(String path) {
			int pos = path.lastIndexOf(".");
			return path.substring(pos + 1);
		}

		public static String changeExtension(String path, String changeExtension) {
			String[] filePathsplit = path.split("/");
			String fileName = filePathsplit[filePathsplit.length - 1];
			path = path.replace(fileName, "");
			String realName = fileName.split("\\.")[0];
			String extension = "smi";
			return path + realName + "." + extension;
		}

		public static String clearName(String name) {
			return name.replace(">", "").replace("<", "").replace("?", "")
					.replace(":", "").replace("/", "").replace("|", "");
		}

		/**
		 * CP를 통해 이미지의 Uri를 얻어온 경우 이를 이용해 실제 파일 경로 반환
		 * 
		 * @param context
		 *            호출한 액티비티
		 * @param uri
		 *            CP를 통해 반환받은 Uri
		 * @return
		 */
		public static String getMediaPathfromUri(Context context, Uri uri) {
			Cursor c = context.getContentResolver().query(uri, null, null,
					null, null);
			if (c.moveToNext())
				return c.getString(c
						.getColumnIndex(MediaStore.MediaColumns.DATA));
			return null;
		}

		/**
		 * 폴더 내부 파일들의 경로를 가져옴
		 * 
		 * @param path
		 *            폴더 경로
		 * @return 파일 경로
		 */

		public static ArrayList<String> getFileList(String path) {
			java.io.File file = new java.io.File(path);
			if (file != null && file.isDirectory() == true) {
				String[] fileList = file.list();
				int length = fileList.length;
				ArrayList<String> list = new ArrayList<String>(length);
				for (String fileName : fileList) {
					list.add(path + fileName);
				}
				return list;
			}
			return null;
		}
	}

	/**
	 * 지정된 url을 인터넷 창으로 띄운다.
	 * 
	 * @param ctx
	 * @param url
	 */
	public static void showInternet(Context ctx, String url) {
		ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	/**
	 * 확실한 기능이 뭔진 모르겠으나 화면의 방향을 기반으로 계산하는 것을 보아 중력센서 정보를 가져오지 않을까 합니다
	 * 
	 * @param context
	 * @param x_heading
	 * @param y_pitch
	 * @param z_roll
	 * @return
	 */
	public static float[] orientationSensor(Activity context, float x_heading,
			float y_pitch, float z_roll) {
		int rotation = context.getWindowManager().getDefaultDisplay()
				.getRotation();
		float temp = 0;

		if (y_pitch <= 0)
			temp = y_pitch * -1;
		else
			temp = 360 - (y_pitch);
		y_pitch = temp;

		if (y_pitch > 100) {
			if (z_roll < 0)
				z_roll = -90 - (z_roll + 90);
			else
				z_roll = 90 + (90 - z_roll);
		}

		if (z_roll <= 0)
			temp = z_roll * -1;
		else
			temp = 360 - (z_roll);
		z_roll = temp;

		if (rotation == Surface.ROTATION_0) {
		} else if (rotation == Surface.ROTATION_90) {
			if (x_heading > 270)
				x_heading = x_heading - 270;
			else
				x_heading = 360 - 270 + x_heading;
			temp = z_roll;
			z_roll = y_pitch;
			y_pitch = temp;
			y_pitch = 360 - y_pitch;
		} else if (rotation == Surface.ROTATION_180) {
		} else if (rotation == Surface.ROTATION_270) {
			if (x_heading > 90)
				x_heading = x_heading - 90;
			else
				x_heading = 360 - 90 + x_heading;
			temp = z_roll;
			z_roll = y_pitch;
			y_pitch = temp;
			z_roll = 360 - z_roll;
		}
		return new float[] { x_heading, y_pitch, z_roll };
	}

	/**
	 * 문자열을 php방식의 md5로 해쉬
	 * 
	 * @param key
	 * @return
	 */
	public static String MD5(String key) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String eip;
		byte[] bip;
		String temp = "";
		String tst = key;

		bip = md5.digest(tst.getBytes());
		for (int i = 0; i < bip.length; i++) {
			eip = "" + Integer.toHexString((int) bip[i] & 0x000000ff);
			if (eip.length() < 2)
				eip = "0" + eip;
			temp = temp + eip;
		}
		return temp;
	}

	public static String EUCKRToUTF8(String string) {
		try {
			return new String(string.getBytes("EUC-KR"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String UTF8ToEUCKR(String string) {
		try {
			return new String(string.getBytes(), "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static JSONObject jsonEncode(Object o) throws JSONException {
		return new JSONObject(new Gson().toJson(o));
	}

}
