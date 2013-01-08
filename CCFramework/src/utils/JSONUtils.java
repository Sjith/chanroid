package utils;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

	public static JSONObject xmlTojson(String xmlString)
			throws IllegalArgumentException, IllegalStateException,
			IOException, JSONException {
		XMLSerializer serializer = new XMLSerializer();
		JSON json = serializer.read(xmlString);
		return new JSONObject(json.toString(2));
	}
	
	/**
	 * 지정된 url의 json문서를 파싱
	 * 
	 * @param url
	 *            파싱할 json 문서의 url
	 * 
	 * @return 파싱이 완료되어 생성된 JSONObject
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject jsonFromURL(String url)
			throws ClientProtocolException, IOException, JSONException {
		return new JSONObject(StringUtils.stringFromURL(url, null));
	}

	/**
	 * 지정된 url의 json문서를 파싱
	 * 
	 * @param url
	 *            파싱할 json 문서의 url
	 * @param params
	 *            url에 보낼 post data
	 * 
	 * @return 파싱이 완료되어 생성된 JSONObject
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject jsonFromURL(String url,
			ArrayList<NameValuePair> params)
			throws ClientProtocolException, JSONException, IOException {
		return new JSONObject(StringUtils.stringFromURL(url, params));
	}
}
