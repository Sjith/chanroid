package utils;

import java.util.Arrays;

public class HangulUtils {

	public static final char HANGUL_BEGIN_UNICODE = 44032;
	public static final char HANGUL_END_UNICODE = 55203;
	public static final char HANGUL_BASE_UNIT = 588;

	public static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
			'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

	private static char getInitalSound(char hanChar) {
		int hanBegin = (hanChar - HANGUL_BEGIN_UNICODE);
		int index = hanBegin / HANGUL_BASE_UNIT;
		return INITIAL_SOUND[index];
	}

	private static boolean isHangul(char unicode) {
		return HANGUL_BEGIN_UNICODE <= unicode && unicode <= HANGUL_END_UNICODE;
	}

	private static boolean containInitialChar(char c) {
		return Arrays.binarySearch(INITIAL_SOUND, c) != -1;
	}

	public static boolean initialCheck(String name, String keyword) {
		name = name.replaceAll("\\p{Space}", "");
		keyword = keyword.replaceAll("\\p{Space}", "");
		String iniName = getHangulInitialSound(name, keyword);
		if (iniName.indexOf(keyword) >= 0) {
			return true;
		} else if (name.contains(keyword)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getHangulInitialSound(String value, String search) {
		StringBuilder sb = new StringBuilder();
		int minLen = Math.min(value.length(), search.length());
		for (int i = 0; i < minLen; i++) {
			char ch = value.charAt(i);
			if (isHangul(ch) && containInitialChar(search.charAt(i))) {
				sb.append(getInitalSound(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static boolean isHangulInitialSound(String value, String sarch) {
		if (value == null || sarch == null) {
			if (value != null)
				return true;
			return sarch == value;
		}
		return sarch.equals(getHangulInitialSound(value, sarch));
	}
}