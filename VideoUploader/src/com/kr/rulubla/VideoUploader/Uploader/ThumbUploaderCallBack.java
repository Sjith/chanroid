package com.kr.rulubla.VideoUploader.Uploader;

public interface ThumbUploaderCallBack {
	public void onThumbUploadStart();
	public void onThumbUploadError(String message);
	public void onThumbUploadProgress(int progress);
	public void onThumbUploadComplete();
}
