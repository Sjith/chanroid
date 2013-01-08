package utils;

import android.annotation.SuppressLint;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint("DefaultLocale")
public class TimeUtils {

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getCurrentDateTimeString
	 * 2. ClassName  : Time
	 * 3. Comment   : 현재 날짜, 시간을 00-00-00 00:00:00 의 형식으로 반환 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 30. 오후 5:43:53
	 * </PRE>
	 * 
	 * @return void
	 */
	public static String getCurrentDateTimeString() {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		String time = String.format("%04d-%02d-%02d %02d:%02d:%02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		return time;
	}

	/**
	 * <PRE>
	 * 1. MethodName : getCurrentDateString
	 * 2. ClassName  : Time
	 * 3. Comment   : 현재 날짜를 00-00-00 의 형식으로 반환 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 11. 5. 오후 7:43:53
	 * </PRE>
	 * 
	 * @return
	 */
	public static String getCurrentDateString() {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		String time = String.format("%04d-%02d-%02d", c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		return time;
	}

	/**
	 * <PRE>
	 * 1. MethodName : getCurrentTimeString
	 * 2. ClassName  : Time
	 * 3. Comment   : 현재 시간을 00:00:00 의 형식으로 반환 
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 11. 5. 오후 7:44:16
	 * </PRE>
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		String time = String.format("%02d:%02d:%02d",
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));
		return time;
	}

}
