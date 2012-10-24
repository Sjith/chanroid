package kr.co.drdesign.parmtree.util;

import android.util.Log;

/**
 * 
 * @author brake-_-
 * 
 * these methods is very important "perfomance"
 * so, i use "switch - case" and "char"
 * 
 * and, is that all "static" method. 
 * "L" is non-allocable class.
 * 
 * just, how to use it, l.s(msg); wow!
 *
 */

public class l {
	
	public static final boolean isDebugMode = true;
	
	public static void s(String msg) {
		if (isDebugMode) Log.i("ParmTree", msg);
	}
	
	public static void s(char level, String msg) {
		if (isDebugMode) {
			switch (level) {
			case 'i' :
				Log.i("ParmTree", msg);
				break;
			case 'e' :
				Log.e("ParmTree", msg);
				break;
			case 'd' :
				Log.d("ParmTree", msg);
				break;
			case 'v' :
				Log.v("ParmTree", msg);
				break;
			case 'w' :
				Log.w("ParmTree", msg);
				break;
			default :
				break;
			}
		}
	}
	
	public static void s(String tag, String msg) {
		if (isDebugMode) Log.i(tag, msg);
	}
	
}
