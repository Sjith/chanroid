package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import kr.co.chan.util.Util;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;

public class CCModel {

	private Context mContext;

	protected CCModel(Context ctx) {
		mContext = ctx;
	}

	public Context getContext() {
		return mContext;
	}

	public String executeShell(String shell) {
		return Util.App.executeShell(shell);
	}

	public final String getDeviceId() {
		return Util.Phone.getDeviceId(mContext);
	}

	public String getPhoneNum() {
		return Util.Phone.getNum(mContext);
	}

	public String getOperatorName() {
		return Util.Phone.getOper(mContext);
	}

	public String getCleanHTML(String html) {
		return Util.Stream.cleanHtml(html);
	}

	public void addPostParam(ArrayList<NameValuePair> param, String key,
			String value) {
		param.add(new BasicNameValuePair(key, value));
	}

	public InputStream openRemoteInputStream(String url)
			throws IllegalStateException, IOException {
		return Util.Stream.inStreamFromURL(url);
	}

	public InputStream openRemoteInputStream(String url,
			ArrayList<NameValuePair> param) throws IllegalStateException,
			IOException {
		return Util.Stream.inStreamFromURLbyPOST(url, param);
	}

	public InputStream openLocalInputStream(String path)
			throws FileNotFoundException {
		File file = new File(path);
		return new FileInputStream(file);
	}

	public InputStream openAsset(String fileName) throws IOException {
		return mContext.getAssets().open(fileName);
	}

	public JSONObject jsonEncode(Object o) throws JSONException {
		return new JSONObject(new Gson().toJson(o));
	}

	public JSONObject xmlTojson(String xmlString)
			throws IllegalArgumentException, IllegalStateException,
			IOException, JSONException {
		XMLSerializer serializer = new XMLSerializer();
		JSON json = serializer.read(xmlString);
		return new JSONObject(json.toString(2));
	}

	public String jsonToxml(JSONObject json) {
		return new XMLSerializer()
				.write(JSONSerializer.toJSON(json.toString()));
	}

	public String md5(String key) {
		return Util.MD5(key);
	}

	public String euckrToUTF8(String string) {
		try {
			return new String(string.getBytes("EUC-KR"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String UTF8Toeuckr(String string) {
		try {
			return new String(string.getBytes(), "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
