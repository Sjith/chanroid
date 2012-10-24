package kr.co.vipsapp.util.vo;

import java.io.Serializable;

import android.util.Log;

public class AODVO implements Serializable {
	
	String LOG_TAG = "AOD VO";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** */
	private String date;
	/**  */
	private String title;
	/** */
	private String prgmId;
	/** */
	private String sTime;
	/** */
	private String eTime;
	
	private String pDate;
		
	
	public AODVO(String date, String title, String prgmId, String sTime, String eTime){
		this.date = date;
		this.title = title;
		this.prgmId = prgmId;
		this.sTime = sTime;
		this.eTime = eTime;
		
		this.pDate = parseDate();
		
	}
	
	private String parseDate(){
		
		String pdate = "";
		
		pdate = date.substring(0, 4)+date.substring(5,7)+date.substring(8);
		Log.i(LOG_TAG, "Date: "+pdate);
		
		return pdate;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPrgmId() {
		return prgmId;
	}

	public void setPrgmId(String prgmId) {
		this.prgmId = prgmId;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String geteTime() {
		return eTime;
	}

	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	public String getpDate() {
		return pDate;
	}

	
}
