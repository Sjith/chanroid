package kr.co.drdesign.parmtree.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class Parser implements c {
	ArrayList<Map<String,CharSequence>> map = new ArrayList<Map<String,CharSequence>>();
	public void load(String path) {
			try {
				URL url = new URL(path);
				XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
				XmlPullParser parser = xpp.newPullParser();
				parser.setInput(url.openStream(), "EUC-KR");
				
				String tag = "", text = "";
				
				Map<String,CharSequence> product = new HashMap<String,CharSequence>();
				
				while (parser.next() != XmlPullParser.END_DOCUMENT){
					switch (parser.getEventType()) {
					case XmlPullParser.START_TAG :
						tag = parser.getName();
//						if (ITEM.equalsIgnoreCase(tag)) {
//							product = new HashMap<String,CharSequence>();
//						}
						break;						
					case XmlPullParser.TEXT:
						text = parser.getText();
						// ÇÑ¹ø¿¡ ÁË´Ù ÆÄ½ÌÇÏÀÚ.
//						for (int i = 0; i < tags.length; i++) {
//							if (tags[i].equalsIgnoreCase(tag)) {
//								product.put(tags[i], text);
//								break;
//							}
//						}
						break;
					case XmlPullParser.END_TAG :
						tag = parser.getName();
//						if (ITEM.equalsIgnoreCase(tag)) {
//							map.add(product);
//							product = null;
//						}
						tag = ""; text = "";
						break;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
	}
		
	public ArrayList<Map<String, CharSequence>> get() {
		return map;
	}
}
