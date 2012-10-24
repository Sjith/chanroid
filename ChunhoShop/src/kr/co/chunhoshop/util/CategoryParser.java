package kr.co.chunhoshop.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class CategoryParser implements ParserTag {

	ArrayList<ArrayList<Map<String, String>>> map = new ArrayList<ArrayList<Map<String, String>>>();
	ArrayList<String> category = new ArrayList<String>();

	boolean first = true;

	public void load() {
		try {
			URL url = new URL(PAGE + CATEGORYPAGE);
//			Log.i("Chunho", url.toString());
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);

			String tag = "", text = "";

			Map<String, String> c1 = new HashMap<String, String>();
			ArrayList<Map<String, String>> c2 = new ArrayList<Map<String, String>>();

			if (map.size() > 0)
				map.clear();

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					// 카테고리 태그가 시작되면 컨텐츠 해시맵 리스트를 멤버의 리스트에 추가한다.
					for (int i = 0; i < CATEGORYS.length; i++) {
						if (CATEGORYS[i].equalsIgnoreCase(tag)) {
							if (first) {
								first = false;
							} else {
								map.add(c2);
								c2 = new ArrayList<Map<String, String>>();
//								Log.i("Chunho", "add and create item list");
							}
						}
					}
					// 컨텐츠 태그가 시작되면 새로운 해시맵을 생성한다.
					for (int i = 0; i < CCONTENTS.length; i++) {
						if (CCONTENTS[i].equalsIgnoreCase(tag)) {
							c1 = new HashMap<String, String>();
//							Log.i("Chunho", "create hashmap");
						}
					}
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					// 컨텐츠 태그가 끝나면 해시맵을 리스트에 저장한다.
					for (int i = 0; i < CCONTENTS.length; i++) {
						if (CCONTENTS[i].equalsIgnoreCase(tag)) {
							c2.add(c1);
//							Log.i("Chunho", "save hashmap");
						}
					}
					// 카테고리 태그의 텍스트를 카테고리 리스트에 추가한다.
					for (int i = 0; i < CATEGORYS.length; i++) {
						if (CATEGORYS[i].equalsIgnoreCase(tag)) {
							category.add(category.size(), text);
//							Log.i("Chunho", "add category : " + text
//									+ " size : " + category.size());
						}
					}
					// 인덱스의 각 아이템에 대한 텍스트를 해시맵에 저장한다.
					for (int i = 0; i < CITEMS.length; i++) {
						if (CITEMS[i].equalsIgnoreCase(tag)) {
							c1.put(tag, text);
//							Log.i("Chunho", "index item : " + text);
						}
					}
					if (CCONTENT6.equalsIgnoreCase(tag)) {
						map.add(c2);
//						Log.i("Chunho", "add and create item list");						
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

	public ArrayList<ArrayList<Map<String, String>>> get() {
		return map;
	}

	public ArrayList<String> getCategory() {
		return category;
	}

}
