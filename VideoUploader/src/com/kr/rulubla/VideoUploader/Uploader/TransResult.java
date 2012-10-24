package com.kr.rulubla.VideoUploader.Uploader;

public class TransResult {

	private String result;
	private String cid;
	private String duration;

	private String videoURL;
	private String thumbURL;
	
	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setcID(String cid) {
		this.cid = cid;
	}
	
	public String getcID() {
		return cid;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String getDuration() {
		return duration;
	}
}
