package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import model.CCSetting;
import android.util.Log;

public class LogUtils {
	
	/**
	 * 로그를 편하게 찍기 위한 래퍼 클래스
	 * 
	 * @author CINEPOX
	 *
	 */
	public static class l {
		public static void i(String message) {
			if (CCSetting.isTestMode)
				Log.i(CCSetting.DEBUG_TAG, message);
		}

		public static void e(String message) {
			if (CCSetting.isTestMode)
				Log.e(CCSetting.DEBUG_TAG, message);
		}

		public static void v(String message) {
			if (CCSetting.isTestMode)
				Log.v(CCSetting.DEBUG_TAG, message);
		}

		public static void d(String message) {
			if (CCSetting.isTestMode)
				Log.d(CCSetting.DEBUG_TAG, message);
		}

		public static void w(String message) {
			if (CCSetting.isTestMode)
				Log.w(CCSetting.DEBUG_TAG, message);
		}

	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getStackTrace
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
	public static String getStackTrace(Throwable t) {
		Writer writer = new StringWriter();
		PrintWriter print = new PrintWriter(writer);
		t.printStackTrace(print);
		return writer.toString();
	}

}
