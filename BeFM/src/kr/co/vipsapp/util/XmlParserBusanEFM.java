package kr.co.vipsapp.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;


public class XmlParserBusanEFM {
	
//	final String BUSAN_EFM = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodPrgmDetail&prgmId=";
	final String BUSAN_EFM = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodPrgmDetail&prgmId=";
	
//	String BUSAN_EFM_DATE = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodPrgmDetail&prgmId=";
	String BUSAN_EFM_DATE = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodPrgmDetail&prgmId=";
	
	String LOG_TAG = "eFM BUSAN";
	

	private String prgmId = "NO DATA";
	private String date = "NO DATA";
	private String prgmImg = "NO DATA";
	private String fileURL = "NO DATA";
	private String script = "NO DATA";
	
	public void parsing(String prgmId, String playDt){
		try{
			
			URL url = new URL(BUSAN_EFM_DATE+prgmId+"&playDt="+playDt);
			Log.i(LOG_TAG, "URL :"+BUSAN_EFM+prgmId+"&playDt="+playDt);
			

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
					Log.i(LOG_TAG, "tag : " + tag + ", text : " + text);
					
					if (text == null) text = "NO DATA";
					
					if (tag.equalsIgnoreCase("script")) {
						script = text;
					} else if (tag.equalsIgnoreCase("prgmId")) {
						prgmId = text;
					} else if (tag.equalsIgnoreCase("date")) {
						date = text;
					} else if (tag.equalsIgnoreCase("prgmImg")) {
						prgmImg = text;
//					} else if (tag.equalsIgnoreCase("file_url")) {
					} else if (tag.equalsIgnoreCase("wma_url")) {
						fileURL = text;
					}
					
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
			
			// root node
//			NodeList nodeList = doc.getElementsByTagName("aod_detail");			
//			Log.i(LOG_TAG, "Length: "+nodeList.getLength());			
//			
//			
//			Node node = nodeList.item(0);
//			Element fstElmnt = (Element) node;
			
			// intro
//			NodeList scriptList = fstElmnt.getElementsByTagName("script");
//						
//			Element scriptElement = (Element) scriptList.item(0);
//			scriptList = scriptElement.getChildNodes();
//			if(scriptList == null){
//				script = "NO DATA";
//				this.prgmId =prgmId;
//				date = "NO DATA";
//				prgmImg="NO DATA";
//				fileURL="NO DATA";
//				Log.i(LOG_TAG, "script : "+script);		
//			}else{
//			if(scriptList.item(0) == null){
//				script = "NO DATA";
//				this.prgmId =prgmId;
//				date = "NO DATA";
//				prgmImg="NO DATA";
//				fileURL="NO DATA";
//			}else{
//			script = ((Node)scriptList.item(0)).getNodeValue();
//			if(script == null || script.equals("NO DATA")){
//				script = "NO DATA";
//				this.prgmId =prgmId;
//				date = "NO DATA";
//				prgmImg="NO DATA";
//				fileURL="NO DATA";				
//			}else{
			

			// pid
			
//			NodeList pidList = fstElmnt.getElementsByTagName("prgmId");				
//			Element pidElement = (Element) pidList.item(0);
//			if(pidElement == null || !pidElement.hasChildNodes() ){
//				if(prgmId == null) this.prgmId = "NO DATA";	
//			}else{
//				pidList = pidElement.getChildNodes();
//				this.prgmId = ((Node)pidList.item(0)).getNodeValue();			
//			}
//			
//			Log.i(LOG_TAG, "prgmId : "+this.prgmId);
			
			/* img URL*/
//			NodeList dateList = fstElmnt.getElementsByTagName("date");			
//			Element dateElement = (Element) dateList.item(0);
//			dateList = dateElement.getChildNodes();
//			date = ((Node)dateList.item(0)).getNodeValue();
//			if(date == null) date = "NO DATA";
//			Log.i(LOG_TAG, "date : "+date);			
			
			// intro
//			NodeList imgList = fstElmnt.getElementsByTagName("prgmImg");
//			
//			Element imgElement = (Element) imgList.item(0);
//			imgList = imgElement.getChildNodes();
//
//			prgmImg = ((Node)imgList.item(0)).getNodeValue();
//			if(prgmImg == null) prgmImg = "NO DATA";
//			Log.i(LOG_TAG, "prgmImg : "+prgmImg);
			
			// intro
//			NodeList urlList = fstElmnt.getElementsByTagName("file_url");
//			
//			Element urlElement = (Element) urlList.item(0);
//			urlList = urlElement.getChildNodes();
//
//			fileURL = ((Node)urlList.item(0)).getNodeValue();
//			if(fileURL == null) fileURL = "NO DATA";
//			Log.i(LOG_TAG, "file_url : "+fileURL);
//			}		
//			}	
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
	
	
	public void parsing(String prgmId){
		try{
			
			URL url = new URL(BUSAN_EFM+prgmId);
			Log.i(LOG_TAG, "URL :"+BUSAN_EFM+prgmId);
			
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
					Log.i(LOG_TAG, "tag : " + tag + ", text : " + text);
					
					if (tag.equalsIgnoreCase("script")) {
						script = text;
					} else if (tag.equalsIgnoreCase("prgmId")) {
						prgmId = text;
					} else if (tag.equalsIgnoreCase("date")) {
						date = text;
					} else if (tag.equalsIgnoreCase("prgmImg")) {
						prgmImg = text;
//					} else if (tag.equalsIgnoreCase("file_url")) {
					} else if (tag.equalsIgnoreCase("wma_url")) {
						fileURL = text;
					}
					
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
			
			// root node
//			NodeList nodeList = doc.getElementsByTagName("aod_detail");			
//			Log.i(LOG_TAG, "Length: "+nodeList.getLength());			
//			
//			
//			Node node = nodeList.item(0);
//			Element fstElmnt = (Element) node;
			
			// intro
//			NodeList scriptList = fstElmnt.getElementsByTagName("script");
//			
//			Element scriptElement = (Element) scriptList.item(0);
//			scriptList = scriptElement.getChildNodes();
//
//			if((Node)scriptList.item(0) == null || ((Node)scriptList.item(0)).getNodeValue().length() == 0){
//				script = "NO DATA";
//			}else{
//				script = ((Node)scriptList.item(0)).getNodeValue();	
//			}
//			
//			Log.i(LOG_TAG, "script : "+script);
			
			// pid
			
//			NodeList pidList = fstElmnt.getElementsByTagName("prgmId");			
//			Element pidElement = (Element) pidList.item(0);
//			pidList = pidElement.getChildNodes();
//			this.prgmId = ((Node)pidList.item(0)).getNodeValue();
//			if(prgmId == null) this.prgmId = "NO DATA";
//			Log.i(LOG_TAG, "prgmId : "+this.prgmId);
			
			/* img URL*/
//			NodeList dateList = fstElmnt.getElementsByTagName("date");			
//			Element dateElement = (Element) dateList.item(0);
//			dateList = dateElement.getChildNodes();
//			date = ((Node)dateList.item(0)).getNodeValue();
//			if(date == null) date = "NO DATA";
//			Log.i(LOG_TAG, "date : "+date);			
			
			// intro
//			NodeList imgList = fstElmnt.getElementsByTagName("prgmImg");
//			
//			Element imgElement = (Element) imgList.item(0);
//			imgList = imgElement.getChildNodes();
//
//			prgmImg = ((Node)imgList.item(0)).getNodeValue();
//			if(prgmImg == null) prgmImg = "NO DATA";
//			Log.i(LOG_TAG, "prgmImg : "+prgmImg);
			
			// intro
//			NodeList urlList = fstElmnt.getElementsByTagName("file_url");
//			
//			Element urlElement = (Element) urlList.item(0);
//			urlList = urlElement.getChildNodes();
//
//			fileURL = ((Node)urlList.item(0)).getNodeValue();
//			if(fileURL == null) fileURL = "NO DATA";
//			Log.i(LOG_TAG, "file_url : "+fileURL);
						
			
			
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPrgmId() {
		return prgmId;
	}

	public String getDate() {
		return date;
	}

	public String getPrgmImg() {
		return prgmImg;
	}

	public String getFileURL() {
		return fileURL;
	}

	public String getScript() {
		return script;
	}
	
	
	
}
