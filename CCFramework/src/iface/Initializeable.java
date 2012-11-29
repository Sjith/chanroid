package iface;

public interface Initializeable {
	/**
	 * 변수나 객체의 상태를 초기화하는 내용을 구현
	 * 
	 * @param action 초기화 수행시 액션 구분값
	 * 		(참고 : 0번은 생성자에 예약되어 있음)
	 */
	public void init(int action);
}
