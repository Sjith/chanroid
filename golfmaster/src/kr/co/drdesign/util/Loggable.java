package kr.co.drdesign.util;

/***
 * �α׸� ��� ���� �������̽�
 * @author drdesign
 *
 */
public interface Loggable {

	public static final String DEBUG_TAG = "DR";
	public static final Boolean IS_DEBUG_MODE = false;
	
	public void L(String log);
}
