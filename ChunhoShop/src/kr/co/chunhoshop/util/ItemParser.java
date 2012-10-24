package kr.co.chunhoshop.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class ItemParser implements ParserTag {
	Map<String, CharSequence> product = new HashMap<String, CharSequence>();
	ArrayList<String> options = new ArrayList<String>();
	ArrayList<String> optval = new ArrayList<String>();
	// option_area 태그 내의 내용들을 해시맵 리스트로 저장.
	ArrayList<String> imgs = new ArrayList<String>();
	ArrayList<String> txts = new ArrayList<String>();
	// p_view 태그 내의 내용들을 해시맵 리스트로 저장. 해시맵 하나당 이미지, 텍스트 하나씩.

	String result;

	public void load(String path) {

		try {
			URL url = new URL(PAGE + path);
//			Log.i("Chunho", url.toString());
			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), CHARACTER_SET);

			String tag = "", text = "";

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (ITEM.equalsIgnoreCase(tag)) {
						product = new HashMap<String, CharSequence>();
					}
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();

					for (int i = 0; i < TAGS.length; i++) {
						if (TAGS[i].equalsIgnoreCase(tag)) {
							product.put(TAGS[i], text);
						}
					}

					if (POPTIONNAME.equalsIgnoreCase(tag)) {
						options.add(text);
					}
					
					if (POPTIONVALUE.equalsIgnoreCase(tag)) {
						optval.add(text);
					}

					for (int i = 0; i < PIMGS.length; i++) {
						if (PIMGS[i].equalsIgnoreCase(tag)) {
							imgs.add(text);
						}
					}

					for (int i = 0; i < PTXTS.length; i++) {
						if (PTXTS[i].equalsIgnoreCase(tag)) {
							txts.add(text);
						}
					}

					if ("".equalsIgnoreCase(tag)) {
						result = text;
					}

//					Log.i("Chunho", "tag : " + tag + ", text : " + text);
					
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

	public String getResult() {
//		Log.i("Chunho", "result : " + result);
		return result;
	}

	public ArrayList<String> getImgs() {
		return imgs;
	}

	public ArrayList<String> getOptval() {
		return optval;
	}
	
	public ArrayList<String> getTxts() {
		return txts;
	}

	public ArrayList<String> getOptions() {
		return options;
	}
}
