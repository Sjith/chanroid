package kr.co.chunhoshop.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class OrderParser implements ParserTag {

	ArrayList<Map<String, String>> orders = new ArrayList<Map<String, String>>();
	ArrayList<ArrayList<Map<String, String>>> product = new ArrayList<ArrayList<Map<String, String>>>();

	public void load(String path) {
		// TODO Auto-generated method stub
		try {
			URL url = new URL(PAGE + path);
//			Log.i("Chunho", url.toString());
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);

			String tag = "", text = "";

			Map<String, String> product = new HashMap<String, String>();
			ArrayList<Map<String, String>> products = new ArrayList<Map<String, String>>();
			Map<String, String> order = new HashMap<String, String>();

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (ITEM.equalsIgnoreCase(tag)) {
						order = new HashMap<String, String>();
						products = new ArrayList<Map<String, String>>();
					} else if (OSUBITEM.equalsIgnoreCase(tag)) {
						product = new HashMap<String, String>();
					}
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();

					for (int i = 0; i < ORDERITEMS.length; i++) {
						if (ORDERITEMS[i].equalsIgnoreCase(tag)) {
							order.put(ORDERITEMS[i], text);
						}
					}

					for (int i = 0; i < TAGS.length; i++) {
						if (TAGS[i].equalsIgnoreCase(tag)) {
							product.put(TAGS[i], text);
						}
					}
					
					if ("p_option_flag".equalsIgnoreCase(tag)) {
						product.put(tag, text);
					}
					
//					Log.i("Chunho", "tag : " + tag + ", text : " + text);

					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (ITEM.equalsIgnoreCase(tag)) {
						orders.add(order);
						this.product.add(products);
					} else if (OSUBITEM.equalsIgnoreCase(tag)) {
						products.add(product);
					}
					tag = "";
					text = "";
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public ArrayList<Map<String, String>> getOrders() {
		// TODO Auto-generated method stub
		return orders;
	}

	public ArrayList<ArrayList<Map<String, String>>> getProducts() {
		return product;
	}

}
