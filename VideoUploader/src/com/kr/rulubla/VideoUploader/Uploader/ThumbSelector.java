package com.kr.rulubla.VideoUploader.Uploader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;

// 썸네일 선택 동작을 수행하는 클래스
public class ThumbSelector extends AsyncTask<String, Integer, String> implements
		TAGS {

	private String SELECT_THUMB_URL = "http://110.93.136.115/interfaces/thumbSelect.asp?";

	public static final String THUMB_COMPLETE = "thumbnail select complete.";
	public static final String THUMB_ERROR = "thumbnail select error. - ";

	public String CHARACTER_SET = "EUC-KR";
	
	private ThumbSelectCallBack callback;
	
	private String cid;
	private String seq;
	
	// cid와 몇번째 썸네일을 선택할 지의 정보로 생성 가능.
	public ThumbSelector (String cid, String seq) {
		this.cid = cid;
		this.seq = seq;
	}
	
	public ThumbSelector() {}
	
	// 몇 번째 썸네일을 선택할지 설정한다.
	public void setNum(String num) {
		this.seq = num;
	}
	
	public void setNum(int num) {
		this.seq = String.valueOf(num);
	}
	
	public String getNum() {
		return seq;
	}
	
	// cid를 설정한다.
	public void setcID(String cid) {
		this.cid = cid;
	}
	
	public String getcID() {
		return cid;
	}
	
	// 콜백 인터페이스를 설정한다.
	public void setThumbCallBack(ThumbSelectCallBack callback) {
		this.callback = callback;
	}
	
	// url 문자열 인코딩을 설정한다.
	public void setEncoding(String enc) throws UnsupportedEncodingException {
		URLEncoder.encode(enc, CHARACTER_SET);
		this.CHARACTER_SET = enc;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (callback != null) callback.onSelectStart();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		if (cid == null || seq == null) {
			cid = params[0];
			seq = params[1];
			if (cid == null || seq == null)
				if (callback != null) {
					callback.onSelectError("need setting cid and seq");
				} else throw new NullPointerException("need setting cid and seq");
		}
		
		String result = "0";
		
		try {
			URL url = new URL(SELECT_THUMB_URL + "cid=" + 
					URLEncoder.encode(cid, CHARACTER_SET) + 
					"&" + "seq=" + URLEncoder.encode(seq, CHARACTER_SET));
			String tag = "", text = "";
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
//					Log.i(TAG, "TAG : " + tag + ", TEXT : " + text);
					if (THUMB_RESULT.equalsIgnoreCase(tag)) {
						if (text.equalsIgnoreCase("1"))
							result = text;
							return THUMB_COMPLETE;
					}
					if (THUMB_MESSAGE.equalsIgnoreCase(tag)) {
						result = text;
						return THUMB_ERROR + text;
					}
					break;
				case XmlPullParser.END_TAG:
					tag = "";
					text = "";
					break;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return THUMB_ERROR + e.getMessage();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return THUMB_ERROR + e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return THUMB_ERROR + e.getMessage();
		}

		if (result.equalsIgnoreCase("1")) {
			return THUMB_COMPLETE;
		} else
			return THUMB_ERROR + "extracted failed.";
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result.contains(THUMB_COMPLETE))
			if (callback != null) callback.onSelectComplete();
		else
			if (callback != null) callback.onSelectError(result);
	}
}
