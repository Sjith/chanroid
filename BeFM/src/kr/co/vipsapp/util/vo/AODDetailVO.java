package kr.co.vipsapp.util.vo;

public class AODDetailVO {
	private String prgmId;
	private String date;
	private String prgmImg;
	private String fileURL;
	private String script;
	
	public AODDetailVO(String prgmId, String date, String prgmImg, String fileURL, String script){
		this.prgmId = prgmId;
		this.date = date;
		this.prgmImg = prgmImg;
		this.fileURL = fileURL;
		this.script = script;
	}
	
	public String getPrgmId() {
		return prgmId;
	}
	public void setPrgmId(String prgmId) {
		this.prgmId = prgmId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPrgmImg() {
		return prgmImg;
	}
	public void setPrgmImg(String prgmImg) {
		this.prgmImg = prgmImg;
	}
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	
	
	
}
