package com.kr.rulubla.VideoUploader.Uploader;


public class UploadResult {
	
	private String resultText;
	private String cID;
	private String message;
	
	public String getResult() {
		return resultText;
	}
	
	public String getcID() {
		return cID;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setResult(String text) {
		this.resultText = text;
	}
	
	public void setcID(String text) {
		this.cID = text;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
