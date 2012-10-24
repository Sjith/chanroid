package com.kr.rulubla.VideoUploader.Uploader;



public interface UploaderCallBack {
	
	public void onUploadStart();
	public void onUploadProgress(int progress);
	public void onUploadError(String message);
	public void onUploadCanceled();
	public void onUploadCompleted(UploadResult result);
	
}
