package kr.co.drdesign.util;
/***
 * 로그를 찍기 위한 인터페이스
 * @author drdesign
 *
 */
public interface Loggable {

	public static final String DEBUG_TAG = "Gruvice";
	public static final Boolean IS_DEBUG_MODE = true;
	
	public void L(char i , String log);
	public void L(String log);
}
