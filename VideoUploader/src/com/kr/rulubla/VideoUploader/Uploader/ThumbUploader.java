package com.kr.rulubla.VideoUploader.Uploader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;


public class ThumbUploader extends AsyncTask<String, Integer, String> implements TAGS {

	public static final int SELECT_MOVIE_CODE = 1988;
	public static final int SELECT_THUMB_CODE = 1977;
	public static final String UPLOAD_COMPLETE = "upload complete. will start conversion.";
	public static final String UPLOAD_ERROR = "upload error. - ";

	private String CHARACTER_SET = "EUC-KR"; 

	private String UPLOADED_URL = "http://110.93.136.115/interfaces/thumbUploader.asp";

	private String cid;
	private String filePath;
	private String message;
	private String imgurl;
	private String imgnum;
	
	private ThumbUploaderCallBack callback;
	
	public void setFilePath(String path) {
		filePath = path;
	}
	
	public String getImgPath() {
		return imgurl;
	}
	
	public void setImgPath(String path) {
		this.imgurl = path;
	}
	
	public String getImgNum() {
		return imgnum;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getcID() {
		return cid;
	}
	
	public void setcID(String cid) {
		this.cid = cid;
	}
	
	public void setUploadCallBack(ThumbUploaderCallBack callback) {
		this.callback = callback;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		if (cid == null || imgurl == null)
			throw new NullPointerException("need setting cid, url, name");

		String result = "";
		try {
			URL url = new URL(UPLOADED_URL + "?cID=" + cid + "&uri=" + imgurl + "&name=" + "0000000008");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();  
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" 
					+ boundary);

			StringBuilder sb = new StringBuilder();
			sb.append(HYPEN + boundary + CRLF);
			sb.append("Content-Disposition: " +
					"form-data; name=\"Filename\"" + CRLF + CRLF);
			sb.append("0000000008" + CRLF + HYPEN + boundary + CRLF);
			sb.append("Content-Disposition: " +
					"form-data; name=\"thumbUploadForm\"; filename=\"" + "0000000008" + "\"" + CRLF);
			sb.append("Content-Type: application/octet-stream" + CRLF + CRLF);
			
			DataOutputStream dos = 
				new DataOutputStream(con.getOutputStream());
			dos.writeBytes(sb.toString());
			FileInputStream fileInputStream = new FileInputStream(filePath);
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			int fileSize = Integer.parseInt(String.valueOf(new File(filePath).length()));
			int transByte = 0;
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				transByte += bufferSize;
				int percent = Math.round(((float) transByte / fileSize) * 100.0f);
//				Log.i(TAG, "fileSize : " + fileSize + ", transBytes : " 
//						+ transByte + ", percent : " + percent);
				onProgressUpdate(percent);
			}
			dos.writeBytes(CRLF + HYPEN + boundary + HYPEN + CRLF);
			dos.flush();
			fileInputStream.close();
			dos.close();
			
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
//			Log.i(TAG, "getInputStream start");
			parser.setInput(con.getInputStream(), CHARACTER_SET);
//			Log.i(TAG, "getInputStream end");
			
			String tag = "", text = "";
			
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
//					Log.i(TAG, "TAG : " + tag + ", TEXT : " + text);
					if (tag.equalsIgnoreCase(IMGUPLOAD_CID))
						cid = text;
					else if (tag.equalsIgnoreCase(IMGUPLOAD_MESSAGE))
						message = text;
					else if (tag.equalsIgnoreCase(IMGUPLOAD_RESULT))
						result = text;
					else if (tag.equalsIgnoreCase(IMGUPLOAD_URL))
						imgurl = text;
					else if (tag.equalsIgnoreCase(IMGUPLOAD_NUM))
						imgnum = text;
					break;
				case XmlPullParser.END_TAG:
					tag = "";
					text = "";
					break;
				}
			}
		} catch (Exception e) {
			return UPLOAD_ERROR + e.getMessage();
		}
		
		if (result.equalsIgnoreCase("1"))
			return UPLOAD_COMPLETE;
		else return UPLOAD_ERROR + message;
		
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result.contains(UPLOAD_COMPLETE)) {
			if (callback != null) callback.onThumbUploadComplete();
		} else {
			if (callback != null) callback.onThumbUploadError(result);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (callback != null) callback.onThumbUploadStart();
	}
}
