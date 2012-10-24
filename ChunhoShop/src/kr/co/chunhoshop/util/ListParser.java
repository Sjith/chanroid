package kr.co.chunhoshop.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ListParser implements ParserTag {

	ArrayList<Map<String, CharSequence>> map = new ArrayList<Map<String, CharSequence>>();

	public void load(String path) {
		try {
			URL url = new URL(PAGE + path);
//			Log.i("Chunho", url.toString());
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);

			String tag = "", text = "";

			if (map.size() > 0) map.clear();

			Map<String, CharSequence> product = new HashMap<String, CharSequence>();
			Map<String, CharSequence> category = new HashMap<String, CharSequence>();

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (ITEM.equalsIgnoreCase(tag)) {
						product = new HashMap<String, CharSequence>();
					} else if (ITEMCONTENT1.equalsIgnoreCase(tag)) {
						category = new HashMap<String, CharSequence>();
					} else if (ITEMCONTENT2.equalsIgnoreCase(tag)) {
						category = new HashMap<String, CharSequence>();
					} else if (ITEMCONTENT3.equalsIgnoreCase(tag)) {
						category = new HashMap<String, CharSequence>();
					} else if (ITEMCONTENT4.equalsIgnoreCase(tag)) {
						category = new HashMap<String, CharSequence>();
					}
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					for (int i = 0; i < TAGS.length; i++) {
						if (TAGS[i].equalsIgnoreCase(tag)) {
							product.put(TAGS[i], text);
//							Log.i("Chunho", "put : " + tag + ", " + text);
						}
					}
					for (int i = 0; i < CONTENT.length; i++) {
						if (CONTENT[i].equalsIgnoreCase(tag)) {
							category.put(CONTENT[i], text);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (ITEM.equalsIgnoreCase(tag)) {
						map.add(product);
//						Log.i("Chunho", "add map");
						product = null;
					} else if (ITEMCONTENT1.equalsIgnoreCase(tag)) {
						map.add(category);
						category = null;
					} else if (ITEMCONTENT2.equalsIgnoreCase(tag)) {
						map.add(category);
						category = null;
					} else if (ITEMCONTENT3.equalsIgnoreCase(tag)) {
						map.add(category);
						category = null;
					} else if (ITEMCONTENT4.equalsIgnoreCase(tag)) {
						map.add(category);
						category = null;
					}
					tag = "";
					text = "";
					break;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}
	
	public void clear() {
		if (map.size() > 0) {
			map.clear();
		}
	}

	public ArrayList<Map<String, CharSequence>> get() {
		return map;
	}

}
