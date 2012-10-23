package kr.co.chan.util.Classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.net.Uri;

public class CaptionParser {

	private Map<String, ArrayList<CaptionData>> mCaptionArray = new HashMap<String, ArrayList<CaptionData>>();
	private ArrayList<String> mLangNames = new ArrayList<String>();
	private ArrayList<String> mLangKeys = new ArrayList<String>();

	private String mCurrentCC = "KRCC";
	private int mCurrentIndex = 0;

	public void setCCLang(int index) {
		mCurrentIndex = index;
		mCurrentCC = mLangKeys.get(index);
	}

	public ArrayList<String> getCCLanglist() {
		return mLangNames;
	}

	public int getCurrentLang() {
		return mCurrentIndex;
	}

	public String getCaption(long playTime) {
		if (!mCaptionArray.containsKey(mCurrentCC))
			return "";
		int l = 0, m, h = mCaptionArray.get(mCurrentCC).size();
		while (l <= h) {
			m = (l + h) / 2;
			if (mCaptionArray.get(mCurrentCC).get(m).getTime() <= playTime
					&& playTime < mCaptionArray.get(mCurrentCC).get(m + 1)
							.getTime()) {
				return mCaptionArray.get(mCurrentCC).get(m).getText();
			}
			if (playTime > mCaptionArray.get(mCurrentCC).get(m + 1).getTime()) {
				l = m + 1;
			} else {
				h = m - 1;
			}
		}
		return mCaptionArray.get(mCurrentCC).get(0).getText().trim();
	}

	public class InvaildCaptionException extends IOException {
		public InvaildCaptionException(String string) {
			// TODO Auto-generated constructor stub
			super(string);
		}

		public static final String ERROR_INVAILD_FILE = "invaild caption file.";
		public static final String ERROR_INVAILD_PATH = "invaild caption path.";
		private static final long serialVersionUID = 1L;
	}

	public void parse(Uri uri) throws IOException {
		parse(uri.getPath());
	}

	/**
	 * 자막 파싱. 현재는 http, https 프로토콜, smi 확장자, utf-8 인코딩만 지원됨
	 * 
	 * @param path
	 *            자막 파일이 존재하는 경로(로컬, 네트워크 모두 가능)
	 * @throws IOException
	 */
	public void parse(String path) throws IOException {
		if (path == null || "".equalsIgnoreCase(path))
			return;
		InputStream in;
		if (path.contains("http://") || path.contains("https://")) {
			URL url = new URL(path);
			in = url.openStream();
		} else {
			File file = new File(path);
			in = new FileInputStream(file);
		}
		
		if (in == null)
			throw new InvaildCaptionException(InvaildCaptionException.ERROR_INVAILD_PATH);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String buffer = "";
		long time = -1;
		String tag = null;
		String lang = null;
		String text = null;
		boolean smistart = false;

		mCaptionArray = new HashMap<String, ArrayList<CaptionData>>();
		mLangNames = new ArrayList<String>();
		mLangKeys = new ArrayList<String>();

		while ((buffer = reader.readLine()) != null) {
			if (buffer.contains("{") && buffer.contains("CC")) {
				// 자막 언어구분정보 파싱
				String[] info = buffer.split(" ");
				for (String s : info) {
					String name, classname;
					if (s.contains("Name")) {
						name = s.replace("Name:", "").trim().replace(";", "")
								.replace("{", "");
						mLangNames.add(name);
						// 자막 구분 이름
					} else if (s.contains(".")) {
						classname = s.replace(".", "").trim();
						mLangKeys.add(classname);
						mCurrentCC = mLangKeys.get(0);
						// 자막 구분 클래스 이름
					}
				}
				continue;
			}

			if (buffer.contains("<SYNC")) {
				smistart = true;
				if (time != -1) {
					Source s = new Source(tag);
					Element p = s.getFirstElement(HTMLElementName.P);
					if (p != null) {
						lang = p.getAttributeValue("Class");
						text = p.toString();
					} else {
						lang = "KRCC";
						text = tag;
					}

					for (String key : mLangKeys) {
						if (!mCaptionArray.containsKey(key)) {
							ArrayList<CaptionData> map = new ArrayList<CaptionData>();
							mCaptionArray.put(key, map);
						}
					}

					if (mCaptionArray.containsKey(lang)) {
						mCaptionArray.get(lang)
								.add(new CaptionData(time, text));
					}
				}
				time = Integer.parseInt(buffer.substring(
						buffer.indexOf("=") + 1, buffer.indexOf(">")));
				tag = buffer
						.substring(buffer.indexOf(">") + 1, buffer.length());
			} else {
				// 시작태그가 아닌 경우 이전 태그의 뒤에 덧붙임
				if (smistart == true) {
					tag += buffer;
				}
			}
		}

		if (mCaptionArray.size() == 0 || mLangKeys.size() == 0
				|| mLangNames.size() == 0)
			throw new InvaildCaptionException(
					InvaildCaptionException.ERROR_INVAILD_FILE);

		mLangNames.add("자막 숨김");
		mLangKeys.add("");
	}
}
