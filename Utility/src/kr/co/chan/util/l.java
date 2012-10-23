package kr.co.chan.util;

import android.util.Log;

public class l {

	private static boolean debug = true;
	private static String TAG = "DEBUG_TAG";
	
	public static void setEnabled(boolean enabled) {
		debug = enabled;
	}
	
	public static void setTag(String tag) {
		if (tag != null) TAG = tag;
	}
	
	public static void i(String message) {
		if (debug) Log.i(TAG, message);
	}
	
	public static void e(String message) {
		if (debug) Log.e(TAG, message);
	}
	
	public static void e(String message, Throwable e) {
		if (debug) Log.e(TAG, message, e);
	}
	
	public static void v(String message) {
		if (debug) Log.v(TAG, message);
	}
	
	public static void d(String message) {
		if (debug) Log.d(TAG, message);
	}
	
	public static void w(String message) {
		if (debug) Log.w(TAG, message);
	}
	
}
