package utils;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
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

	public static ArrayList<Object> jsonArrayToArrayList(JSONArray array)
			throws JSONException {
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			result.add(array.get(i));
		}
		return result;
	}

	public static int wrapJsonInteger(JSONObject o, String name) {
		try {
			return o.getInt(name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}
	
	public static String[] jsonArrayToArray(JSONArray array)
			throws JSONException {
		String[] result = new String[array.length()];
		for (int i = 0; i < array.length(); i++) {
			result[i] = array.getString(i);
		}
		return result;
	}

	/**
	 * 지정된 url의 json문서를 파싱하여 jsonarray형식으로 리턴
	 * 
	 * @param url
	 *            파싱할 json 문서의 url
	 * 
	 * @return 파싱이 완료되어 생성된 JSONArray
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 *             array형식이 아닌 json을 파싱할 경우
	 */
	public static JSONArray jsonArrayFromURL(String url)
			throws ClientProtocolException, IOException, JSONException {
		return jsonFromURL(url).optJSONArray("list");
	}

	/**
	 * 지정된 url의 json문서를 파싱하여 jsonarray형식으로 리턴
	 * 
	 * @param url
	 *            파싱할 json 문서의 url
	 * 
	 * @return 파싱이 완료되어 생성된 JSONArray
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 *             array형식이 아닌 json을 파싱할 경우
	 */
	public static JSONArray jsonArrayFromURL(String url,
			ArrayList<NameValuePair> params) throws ClientProtocolException,
			JSONException, IOException {
		return jsonFromURL(url, params).optJSONArray("list");
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
			ArrayList<NameValuePair> params) throws ClientProtocolException,
			JSONException, IOException {
		return new JSONObject(StringUtils.stringFromURL(url, params));
	}
}
