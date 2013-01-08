package utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {

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
	public static float getDistanceBetween(double startlat, double startlng,
			double endlat, double endlng) {
		float[] results = new float[] {};
		Location.distanceBetween(startlat, startlng, endlat, endlng, results);
		return results[0];
	}

	/**
	 * 현재 위치서비스가 활성화되어 있는지의 여부를 반환. 네트워크와 GPS 둘다 비활성시 FALSE 반환.
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isEnabled(Context mContext) {
		LocationManager locManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isEnabled = locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER) ? locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER) : locManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return isEnabled;
	}
}
