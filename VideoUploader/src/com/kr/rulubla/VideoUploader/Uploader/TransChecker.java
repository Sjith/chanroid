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


public class TransChecker extends AsyncTask<String, Integer, String> implements
		TAGS {

	private String TRANS_CHECK_URL = "http://110.93.136.115/interfaces/transCheck.asp?cid=";

	public static final String TRANS_COMPLETE = "video transfer complete.";
	public static final String TRANS_ERROR = "video transfer error. - ";

	public String CHARACTER_SET = "EUC-KR";
	
	private TransResult trresult = new TransResult();
	private TransCallBack callback;
	
	private String cid;

	public TransChecker (String cid) {
		this.cid = cid;
	}
	
	public void setcID(String cid) {
		this.cid = cid;
	}
	
	public String getcID() {
		return cid;
	}
	
	public TransResult getResult() {
		return trresult;
	}

	public void setEncoding(String enc) throws UnsupportedEncodingException {
		URLEncoder.encode(enc, CHARACTER_SET);
		this.CHARACTER_SET = enc;
	}
	
	public void setTransferCallBack(TransCallBack callback) {
		this.callback = callback;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

		if (cid == null) {
			throw new NullPointerException("need setting cid");
		}
		
		
		String percent = "0";
		
		try {
			while (!percent.equalsIgnoreCase("100")) {

				URL url = new URL(TRANS_CHECK_URL + URLEncoder.encode(cid, CHARACTER_SET));
				XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
				XmlPullParser parser = xpp.newPullParser();
				parser.setInput(url.openStream(), CHARACTER_SET);
				String tag = "", text = "";
				
				while (parser.next() != XmlPullParser.END_DOCUMENT) {
					switch (parser.getEventType()) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						break;
					case XmlPullParser.TEXT:
						text = parser.getText();
//						Log.i(TAG, "TAG : " + tag + ", TEXT : " + text);
						if (tag.equalsIgnoreCase(TRANS_RESULT)) {
							trresult.setResult(text);
							if (trresult.getResult().equalsIgnoreCase("0"))
								return TRANS_ERROR + "trans failed.";
						} else if (tag.equalsIgnoreCase(TRANS_CID))
							trresult.setcID(text);
						else if (tag.equalsIgnoreCase(TRANS_PROGRESS)) {
							percent = text;
							callback.onTransProgress(Integer.parseInt(percent));
						} else if (tag.equalsIgnoreCase(TRANS_LENGTH))
							trresult.setDuration(text);
						else if (tag.equalsIgnoreCase(TRANS_URL))
							trresult.setVideoURL(text);
						else if (tag.equalsIgnoreCase(TRANS_THUMB_URL))
							trresult.setThumbURL(text);
						break;
					case XmlPullParser.END_TAG:
						tag = "";
						text = "";
						break;
					}
				}
				Thread.sleep(3000);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return TRANS_ERROR + e.getMessage();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			return TRANS_ERROR + e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return TRANS_ERROR + e.getMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return TRANS_ERROR + e.getMessage();
		}

		if (trresult.getResult().equalsIgnoreCase("1")) {
			return TRANS_COMPLETE;
		} else
			return TRANS_ERROR + "extracted failed.";
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result.contains(TRANS_COMPLETE))
			if (callback != null) callback.onTransCompleted(trresult);
		else
			if (callback != null) callback.onTransError(result);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (callback != null) callback.onTransStart();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if (callback != null) callback.onTransProgress(values[0]);
	}

}
