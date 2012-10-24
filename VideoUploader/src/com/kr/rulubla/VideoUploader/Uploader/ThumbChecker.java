package com.kr.rulubla.VideoUploader.Uploader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.os.AsyncTask;

// 썸네일 추출 여부를 확인하는 클래스.
public class ThumbChecker extends AsyncTask<String, Integer, String> 
	implements TAGS {


	private String EXT_THUMB_URL = "http://110.93.136.115/interfaces/thumbCheck.asp?cid=";

	public static final String THUMB_COMPLETE = "thumbnail extract complete.";
	public static final String THUMB_ERROR = "thumbnail extract error. - ";

	public String CHARACTER_SET = "EUC-KR";
	
	private ThumbResult thresult = new ThumbResult();
	private ThumbCallBack callback;
	
	private String cid;
	
	
	// 생성자로 업로드시 받은 cid를 사용한다.
	public ThumbChecker (String cid) {
		this.cid = cid;
	}
	
	// 생성자 없이 생성할 수도 있다.
	public ThumbChecker() {}
	
	// 생성자 없이 생성한 경우 cid를 설정
	public void setcID(String cid) {
		this.cid = cid;
	}
	
	// 설정된 cid를 가져온다.
	public String getcID() {
		return cid;
	}
	
	// 썸네일 추출 결과 객체를 가져온다.
	public ThumbResult getResult() {
		return thresult;
	}

	// url 인코딩을 설정한다.
	public void setEncoding(String enc) throws UnsupportedEncodingException {
		URLEncoder.encode(enc, CHARACTER_SET);
		this.CHARACTER_SET = enc;
	}
	
	// 콜백 인터페이스를 설정한다.
	public void setThumbCallBack(ThumbCallBack callback) {
		this.callback = callback;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (callback != null) callback.onThumbStart();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		if (cid == null) {
			if (callback != null) callback.onThumbError("need setting cid");
			throw new NullPointerException("need setting cid");
		}
		
		String result = "0";
		ArrayList<String> thumburl = new ArrayList<String>();
		ArrayList<Bitmap> bitArray = new ArrayList<Bitmap>();
		
		try {
			URL url = new URL(EXT_THUMB_URL + URLEncoder.encode(cid, CHARACTER_SET));
			String tag = "", text = "";
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			while (!(result.equalsIgnoreCase("1") || result.equalsIgnoreCase("-1"))) {

				XmlPullParser parser = xpp.newPullParser();
				parser.setInput(url.openStream(), CHARACTER_SET);
				
				while (parser.next() != XmlPullParser.END_DOCUMENT) {
					switch (parser.getEventType()) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						break;
					case XmlPullParser.TEXT:
						text = parser.getText();
//						Log.i(TAG, "TAG : " + tag + ", TEXT : " + text);
						break;
					case XmlPullParser.END_TAG:
						if (tag.equalsIgnoreCase(IMAGES_RESULT))
							thresult.setResult(text);
						if (thresult.getResult().equalsIgnoreCase("1")) {
							result = "1";
							if (tag.equalsIgnoreCase(IMAGES_URL))
								thresult.setImgPath(text);
							else if (tag.equalsIgnoreCase(IMAGES_NAME))
								thresult.setImgName(text);
							else if (tag.equalsIgnoreCase(IMAGES))
								thumburl.add(text);
								Bitmap b = UploaderUtil.getImageURL(text);
								bitArray.add(b);							
						} else if (thresult.getResult().equalsIgnoreCase("-1")) {
							result = "-1";
							return THUMB_ERROR + "extracted failed.";
						}
						tag = "";
						text = "";
						break;
					}
				}
				Thread.sleep(3000);
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return THUMB_ERROR + e.getMessage();
		}

		if (result.equalsIgnoreCase("1")) {
			thresult.setImgArray(thumburl);
			thresult.setBitmapArray(bitArray);
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
			if (callback != null) callback.onThumbCompleted(thresult);
		else
			if (callback != null) callback.onThumbError(result);
	}

}
