package com.kr.rulubla.VideoUploader.Uploader;


// 썸네일 추출 확인 콜백 인터페이스
public interface ThumbCallBack {
	// 썸네일 추출 시작시 호출
	public void onThumbStart();
	// 썸네일 추출중 오류 발생시 호출. 인자값으로 오류 메시지 출력
	public void onThumbError(String result);
	// 썸네일 추출 완료시 호출.
	public void onThumbCompleted(ThumbResult result);
}
