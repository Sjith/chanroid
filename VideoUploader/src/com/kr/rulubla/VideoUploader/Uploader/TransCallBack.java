package com.kr.rulubla.VideoUploader.Uploader;


public interface TransCallBack {
	public void onTransStart();
	public void onTransProgress(int progress);
	public void onTransCompleted(TransResult result);
	public void onTransError(String message);
}
