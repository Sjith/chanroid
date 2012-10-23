package kr.co.chan.util.CinePox;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.co.chan.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.Build;
import android.os.Debug;

/**
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 * 
 * @author Chan-woo Park
 * 
 */
public class LogPost extends Thread {

	private ArrayList<NameValuePair> mParams = new ArrayList<NameValuePair>();
	private StringBuilder mMsg = new StringBuilder();
	private String SERVER_URL = "http://adappstore.co.kr/ADULT_APP_RESULT/";
	private Throwable mException;

	public LogPost(Context ctx, String appkey, Throwable t) {
		this(appkey, t);

		mMsg.append("Operator Name : " + Util.Phone.getOper(ctx) + "\n");
		mMsg.append("Network Type : " + Util.Connection.getType(ctx) + "\n");
		mMsg.append("running another APP : \n");
		for (String s : Util.App.getRunningProcess(ctx)) {
			mMsg.append("	" + s + "\n");
		}
		mMsg.append("\n");
	}

	public LogPost(String appkey, Throwable t) {
		mException = t;
		mParams.add(new BasicNameValuePair("app_name", appkey));
		mParams.add(new BasicNameValuePair("type", "exceptions"));

		mMsg.append("\n");
		mMsg.append("APP Name : " + appkey + "\n");
		appendBasicInfo();
	}

	public LogPost(Throwable t) {
		this("Utility", t);
	}

	/**
	 * 인자값 없이 넣을 수 있는 정보들
	 */
	public void appendBasicInfo() {
		mMsg.append("Time : " + getTimeString() + "\n");
		mMsg.append("Exception type : " + mException.getClass().getName()
				+ "\n");
		mMsg.append("Model Name : " + Build.MODEL + "\n");
		mMsg.append("Android Ver. : " + Build.VERSION.RELEASE + "\n");
		mMsg.append("SDK Ver. : " + Build.VERSION.SDK_INT + "\n");
		mMsg.append("aviliable Memory : " + Debug.getNativeHeapFreeSize()
				+ "\n\n");
	}

	public String getTimeString() {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		String time = String.format("%04d-%02d-%02d %02d:%02d:%02d",
				c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		return time;
	}

	public LogPost appendMessage(String msg) {
		mMsg.append(msg + "\n");
		return this;
	}

	public void setServerUrl(String url) {
		SERVER_URL = url;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (mException instanceof SocketTimeoutException) {
			return;
		} else if (mException instanceof SocketException) {
			return;
		}

		Writer writer = new StringWriter();
		PrintWriter print = new PrintWriter(writer);
		mException.printStackTrace(print);
		CinePost cp = new CinePost();
		appendMessage("----------------------- Error Message Start -------------------------");
		appendMessage(writer.toString());
		appendMessage("----------------------- Error Message End -------------------------");
		try {
			mParams.add(new BasicNameValuePair("msg", URLEncoder.encode(
					mMsg.toString(), HTTP.UTF_8)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			mParams.add(new BasicNameValuePair("msg", mMsg.toString()));
		}
		cp.setParam(mParams);
		cp.execute(SERVER_URL);
	}
}
