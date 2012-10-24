package com.kr.rulubla.VideoUploader.Uploader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.util.Log;


public class Uploader extends AsyncTask<String, Integer, String> implements TAGS {

	public static final int SELECT_MOVIE_CODE = 1988;
	public static final int SELECT_THUMB_CODE = 1977;
	public static final String UPLOAD_COMPLETE = "upload complete. will start conversion.";
	public static final String UPLOAD_ERROR = "upload error. - ";
	private final String UPLOADED_URL = "http://110.93.136.115/interfaces/fileUploader.asp";

	private String CHARACTER_SET = "EUC-KR"; 

	private UploaderCallBack callback;
	private UploadResult upresult = new UploadResult();

	private String videoPath;

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (callback != null) callback.onUploadCanceled();
	}

	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
		if (callback != null) callback.onUploadCanceled();
	}

	public UploadResult getUploadResult() {
		return upresult;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (callback != null) callback.onUploadStart();
	}

	@Override
	protected String doInBackground(String... params) {

		if (videoPath == null)
			throw new NullPointerException("need setting upload video file.");

		try {
			URL url = new URL(UPLOADED_URL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();  
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(true);
			con.setChunkedStreamingMode(0);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" 
					+ boundary);

			String fileName = new File(videoPath).getName();
			
			StringBuilder sb = new StringBuilder();
			// 파일명 폼 입력 시작
			sb.append(HYPEN + boundary + CRLF);
			sb.append("Content-Disposition: " +
					"form-data; name=\"Filename\"" + CRLF + CRLF);
			sb.append(fileName + CRLF + HYPEN + boundary + CRLF);
			// 파일명 폼 입력 끝
			
			// 실제 전송할 파일 폼 입력 시작
			sb.append("Content-Disposition: " +
					"form-data; name=\"vodUploadForm\"; filename=\"" + fileName + "\"" + CRLF);
			sb.append("Content-Type: application/octet-stream" + CRLF + CRLF);
			
			BufferedOutputStream dos = new BufferedOutputStream(con.getOutputStream());
			dos.write(sb.toString().getBytes());
			dos.flush();
			BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(videoPath));

			int fileSize = Integer.parseInt(String.valueOf(new File(videoPath).length()));
			int transByte = 0;
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			while (fileInputStream.read(buffer) != -1) {
				dos.write(buffer);
				transByte += bufferSize;
				int percent = Math.round(((float) transByte / fileSize) * 100.0f);
				onProgressUpdate(percent);
				buffer = new byte[bufferSize];
			}
			dos.write((CRLF + HYPEN + boundary + HYPEN + CRLF).getBytes());
			dos.close();
			fileInputStream.close();
			// 파일 폼 입력 끝

			// 전송이 완료되었으니 결과값을 기다리라는 인터페이스를 구현하면 좋겠지만, 시간관계상 생략
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(con.getInputStream(), CHARACTER_SET);
			String tag = "", text = "";			
			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					Log.i(TAG, "TAG : " + tag + ", TEXT : " + text);
					// 내용 확인이 필요할시 주석 해제
					if (tag.equalsIgnoreCase(UPLOAD_CID)) {
						upresult.setcID(text);
					} else if (tag.equalsIgnoreCase(UPLOAD_RESULT)) 
						upresult.setResult(text);
					else if (tag.equalsIgnoreCase(UPLOAD_MESSAGE)) {
						upresult.setMessage(text);
					}
					break;
				case XmlPullParser.END_TAG:
					tag = "";
					text = "";
					break;
				}
			}
			
			if (upresult.getResult().equalsIgnoreCase("0"))
				return UPLOAD_ERROR + new String(upresult.getMessage().getBytes("EUC-KR"));
			else
				return UPLOAD_COMPLETE;
		} catch (IOException e) {
			e.fillInStackTrace();
			return UPLOAD_ERROR + e.getMessage();
		} catch (XmlPullParserException e) {
			e.fillInStackTrace();
			return UPLOAD_ERROR + e.getMessage();
		} 
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (callback != null) callback.onUploadProgress(values[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);	
		if (result.contains(UPLOAD_ERROR)) {
			if (callback != null) callback.onUploadError(result);
		} else if (result.contains(UPLOAD_COMPLETE)) {
			if (callback != null) callback.onUploadCompleted(upresult);
		}
	}
	
	public void setUploaderCallback(UploaderCallBack callback) {
		this.callback = callback;
	}

	public void setUploadVideo(File video) {
		this.videoPath = video.getPath();
	}

	public void setUploadVideo(String video) {
		this.videoPath = video;
	}
	
	public void setEncoding(String enc) throws UnsupportedEncodingException {
		URLEncoder.encode(videoPath, enc);
		this.CHARACTER_SET = enc;
	}

	public void Upload(String path) {
		File video = new File(path);
		Upload(video);
	}

	public void Upload(File file) {
		this.videoPath = file.getPath();
		Upload();
	}

	public void Upload() {
		try { 
			execute(URLEncoder.encode(videoPath, CHARACTER_SET));
		} catch (UnsupportedEncodingException e) {
			callback.onUploadError(UPLOAD_ERROR + "invaild text encoding.");
		}
	}


}
