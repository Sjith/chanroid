package kr.co.drdesign.util;
/***
 * �α׸� ��� ���� �������̽�
 * @author drdesign
 *
 */
public interface Loggable {

	public static final String DEBUG_TAG = "Gruvice";
	public static final Boolean IS_DEBUG_MODE = true;
	
	public void L(char i , String log);
	public void L(String log);
}
