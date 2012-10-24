package kr.co.chunhoshop.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainParser implements ParserTag {

	Map<String, CharSequence> product = new HashMap<String, CharSequence>();

	public void load(String path) {

		try {
			URL url = new URL(PAGE + path);
//			Log.i("Chunho", url.toString());
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);
			
			if (product.size() > 0) product.clear();

			String tag = "", text = "";

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					if (tag.equalsIgnoreCase(CATEGORYURL)) {
						product.put(tag, text);
					}
					break;
				case XmlPullParser.END_TAG:
					tag = "";
					text = "";
					break;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}

	public Map<String, CharSequence> get() {
		return product;
	}

}
