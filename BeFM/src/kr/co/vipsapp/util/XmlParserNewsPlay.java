package kr.co.vipsapp.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class XmlParserNewsPlay {
	
//	String NEWS_PLAYER = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodDetail&prgmId=";
	String NEWS_PLAYER = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodDetail&prgmId=";
	
	String LOG_TAG="eFM BUSAN";
	
	private String script;
	private String fileURL;
		
	public XmlParserNewsPlay(){
		
	}

	
	public void parser(String prgmId, String playDt){
		try{
			
			URL url = new URL(NEWS_PLAYER+prgmId+"&playDt="+playDt);
			Log.i(LOG_TAG,"URL: "+url.toString());

			XmlPullParserFactory xpp = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xpp.newPullParser();
			parser.setInput(url.openStream(), "UTF-8");

			String tag = "", text = "";

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
				
				case XmlPullParser.START_TAG :
					tag = parser.getName();
					break;
				case XmlPullParser.TEXT :
					text = parser.getText();
					Log.i("eFM BUSAN", "tag : " + tag + ", text : " + text);
					if (tag.equalsIgnoreCase("script")) {
						script = text;
					} else if (tag.equalsIgnoreCase("wma_url")) {
						fileURL = text;
					}
					
					if (script == null || fileURL == null) fileURL = "NO DATA";
					
					break;
				case XmlPullParser.END_TAG :
					tag = "";
					text = "";
					break;
				
				}
			}
			
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			
//			Document doc = db.parse(new InputSource(url.openStream()));
//			
//			NodeList nodeList = doc.getElementsByTagName("aod_detail");
//			
//			Log.i(LOG_TAG, "Length: "+nodeList.getLength());
//			Node node = nodeList.item(0);
//
//			Element fstElmnt = (Element) node;
//			NodeList pidList = fstElmnt.getElementsByTagName("script");			
//			Element pidElement = (Element) pidList.item(0);
//			pidList = pidElement.getChildNodes();
//			
//			if(pidList == null){
//				this.script = "NO DATA";
//				NodeList imgList = fstElmnt.getElementsByTagName("wma_url");
//				
//				Element imgElement = (Element) imgList.item(0);
//				imgList = imgElement.getChildNodes();
//				if(imgList == null){ 
//					this.fileURL = "NO DATA";
//				}else{
//					Node nw = (Node)imgList.item(0);
//					if(nw == null){
//						this.fileURL = "NO DATA";
//					}else{
//						this.fileURL = nw.getNodeValue();
//					}
//				}
//				Log.i(LOG_TAG, "script : "+script);
//				Log.i(LOG_TAG, "File URL : "+fileURL);	
//			}else{
//				this.script = ((Node)pidList.item(0)).getNodeValue();
//				NodeList imgList = fstElmnt.getElementsByTagName("wma_url");
//				
//				Element imgElement = (Element) imgList.item(0);
//				imgList = imgElement.getChildNodes();
//				if(imgList == null){
//					this.fileURL = "NO DATA";
//				}else{
//					Node ni = (Node)imgList.item(0);
//					if(ni == null){						
//						this.fileURL = "NO DATA";
//					}else{
//						this.fileURL = ni.getNodeValue();
//					}
//				}
//				Log.i(LOG_TAG, "script : "+script);
//				Log.i(LOG_TAG, "File URL : "+fileURL);				
//			}
//			
//			this.script = ((Node)pidList.item(0)).getNodeValue();
//			if(this.script.equals("NO DATA")){
//				NodeList imgList = fstElmnt.getElementsByTagName("wma_url");
//			
//				Element imgElement = (Element) imgList.item(0);
//				imgList = imgElement.getChildNodes();
//				
//				this.fileURL = "NO DATA";
//				Log.i(LOG_TAG, "File URL : "+fileURL);
//			}else{
//				NodeList imgList = fstElmnt.getElementsByTagName("wma_url");
//			
//				Element imgElement = (Element) imgList.item(0);
//				imgList = imgElement.getChildNodes();
//
//				this.fileURL = ((Node)imgList.item(0)).getNodeValue();
//				Log.i(LOG_TAG, "File URL : "+fileURL);			
//			}
			
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public String getScript() {
		return script;
	}


	public String getFileURL() {
		return fileURL;
	}
	
}

