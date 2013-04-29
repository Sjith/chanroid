package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.text.InputFilter;
import android.text.Spanned;

public class StringUtils {

	public static InputFilter getEditTextFilter(final String pattern) {
		return new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// TODO Auto-generated method stub
				Pattern p = Pattern.compile(pattern);
				if (!p.matcher(source).matches())
					return "";
				return null;
			}
		};
	}

	public static String comma(int value) {
		DecimalFormat df = new DecimalFormat("#,##0");
		return df.format(value);
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
	public static String stringFromURL(String url,
			ArrayList<NameValuePair> params) throws ClientProtocolException,
			IOException {
		InputStream in = StreamUtils.inStreamFromURL(url, params);
		if (in == null) {
			return null;
		} else {
			return stringFromStream(in);
		}
	}

	public static String stringFromStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
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
