package kr.co.drdesign.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Utillity {

	private static Utillity gUtil;
	private static Context ctx;

	private static SharedPreferences mPref;
	private static Editor mEditor;

	private Utillity(Context context)
	{
		ctx = context.getApplicationContext();
		mPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		mEditor = mPref.edit();
	}

	// ΩÃ±€≈Ê ∆–≈œ¿∏∑Œ ±∏«ˆ
	public static Utillity getInstance( Context context )
	{
		if ( gUtil == null ) 
			gUtil = new Utillity(context);
		return gUtil;
	}

	//	// number of Phone, National Code is replaced '0';
	//	public String getPhoneNumber()
	//	{
	//		if(lineNumber != null ) return lineNumber;
	//
	//		lineNumber = mPref.getString(LINE_NUMBER, "");
	//		if(lineNumber.equals("") == false ) return lineNumber;
	//
	//		TelephonyManager telephony = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
	//		lineNumber = (telephony != null)?telephony.getLine1Number():"Default Value";
	//		lineNumber = lineNumber.replace("+82", "0");
	//
	//		mEditor.putString(LINE_NUMBER, lineNumber);
	//		mEditor.commit();
	//
	//		return lineNumber;
	//	}
	
	public static boolean searchGCActivity = false;
}
