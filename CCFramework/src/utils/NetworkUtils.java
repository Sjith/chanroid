package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {

	/**
	 * 단말기가 어떤 종류의 네트워크에 연결되어 있는지 확인
	 * 
	 * @param context
	 *            호출한 액티비티든 뭐든
	 * @return 네트워크 종류(ConnectivityManager 상수)
	 */
	public static int getNetworkType(Context context) {
		try {
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
		} catch (Exception e) {
			return ConnectivityManager.TYPE_DUMMY;
		}
	}

	/**
	 * 와이파이의 온오프 제어
	 * 
	 * @param context
	 * @param sw
	 * @return 성공여부
	 */
	public static boolean setWifiEnabled(Context context, boolean sw) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.setWifiEnabled(sw);
	}

	/**
	 * 와이파이의 활성여부 확인
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiEndbled(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean enabled = wifiManager.isWifiEnabled();
		return enabled;
	}
}
