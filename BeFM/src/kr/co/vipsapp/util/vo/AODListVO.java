package kr.co.vipsapp.util.vo;

import android.util.Log;

public class AODListVO {
	private String date;
	private String pDate;
	
	String LOG_TAG = "AODListVO";
	
	public AODListVO(){
		
	}
	
	public AODListVO(String date){
		this.date = date;
		this.pDate = parseDate();
	}
	
	private String parseDate(){
		String pdate;
		
		pdate = date.substring(0, 4)+date.substring(5,7)+date.substring(8);
		Log.i(LOG_TAG, "Date: "+pdate);
		
		return pdate;
	}
	
	public String getDate() {
		return date;
	}
	public String getpDate() {
		return pDate;
	}
	
	
}
