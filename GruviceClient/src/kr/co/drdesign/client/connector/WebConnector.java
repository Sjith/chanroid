package kr.co.drdesign.client.connector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import kr.co.drdesign.client.aria.ARIAHelper;
import kr.co.drdesign.client.aria.ARIAInputStreamHelper;
import kr.co.drdesign.util.Loggable;
import android.util.Log;

public abstract class WebConnector implements Loggable{

	public static final String CHARACTER_SET = "EUC-KR";
//	public static final String CHARACTER_SET = "UTF-8";
	public static final int TIME_OUT = 10000;

	protected static ARIAHelper aria;
	public static final String keycode = "halftale_jiyong_";

	protected  static String ARIADecode(String text){
		if ( aria == null ) aria = new ARIAHelper(keycode);
		return aria.decryptToString( text.getBytes() );	
	}
	//	protected  static byte[] ARIADecode(byte[] text){
	protected  static String ARIADecode(byte[] text){
		if ( aria == null ) aria = new ARIAHelper(keycode);
		//		return aria.decrypt( text );	
		return aria.decryptToString( text );
	}

	public abstract BufferedReader getBufferedReaderFromURL( String URL, String id, String password, Map<String, String> post ) throws IOException;
	public abstract CharSequence readFromURL( String URL, String id, String password, Map<String, String> post) throws IOException;
	public abstract CharSequence decodeFromURL( String URL, String id, String password, Map<String, String> post) throws IOException, IllegalArgumentException;

	public abstract BufferedInputStream getBufferedInputStreamFromURL( String URL, String id, String password, Map<String, String> post ) throws IOException;
	public abstract BufferedInputStream getBufferedInputStreamFromURLwithoutSession(String url,String id, String password, Map<String, String> post) throws IOException;
	public ARIAInputStreamHelper getARIAInputStreamFromURL(String URL, String id,
			String password, Map<String, String> post, long length) 
	 throws IOException {
		InputStream is = getBufferedInputStreamFromURL( URL, id, password, post );

		ARIAInputStreamHelper ai =
			new ARIAInputStreamHelper(is, length);

		return ai;
	}

	public void L(char logLevel, String text){
		//I consider this code using reflectioin.
		//but, Log method need high performance.
		//so I use "switch" 

		if( IS_DEBUG_MODE )
			switch (logLevel){
			case 'i' :
				android.util.Log.i(DEBUG_TAG, text);
				break;
			case 'e' :
				android.util.Log.e(DEBUG_TAG, text);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}
