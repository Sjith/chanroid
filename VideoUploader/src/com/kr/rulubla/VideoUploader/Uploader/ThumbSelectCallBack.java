package com.kr.rulubla.VideoUploader.Uploader;

// 썸네일 선택 콜백 인터페이스
public interface ThumbSelectCallBack {
	// 썸네일 선택 요청을 보낼시 호출
	public void onSelectStart();
	// 썸네일 선택이 완료되면 호출
	public void onSelectComplete();
	// 썸네일 선택 중 에러가 발생하면 호출
	public void onSelectError(String message);
}
