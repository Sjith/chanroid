package utils;

import java.io.IOException;
import java.util.ArrayList;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.http.NameValuePair;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XMLUtils {

	public static String jsonToxml(JSONObject json) {
		return new XMLSerializer()
				.write(JSONSerializer.toJSON(json.toString()));
	}

	/**
	 * 지정된 url의 xml문서를 파싱할 parser을 준비
	 * 
	 * @param urls
	 *            파싱할 문서의 url
	 * @param characterset
	 *            해당 url 페이지의 문자셋
	 * 
	 * @return 해당 url의 xml문서가 들어있는 parser
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static XmlPullParser xmlFromURL(String url, String characterset)
			throws XmlPullParserException, IOException {
		return xmlFromURLbyPOST(url, characterset, null);
	}

	/**
	 * 지정된 url의 xml문서를 파싱할 parser을 준비
	 * 
	 * @param urls
	 *            파싱할 문서의 url
	 * @param characterset
	 *            해당 url 페이지의 문자셋
	 * 
	 * @return 해당 url의 xml문서가 들어있는 parser
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static XmlPullParser xmlFromURLbyPOST(String url,
			String characterset, ArrayList<NameValuePair> params)
			throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(StreamUtils.inStreamFromURL(url, params), characterset);
		return parser;
	}

}
