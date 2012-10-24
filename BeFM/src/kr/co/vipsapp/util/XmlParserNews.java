package kr.co.vipsapp.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.co.vipsapp.util.vo.AODVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XmlParserNews {
//	final String NEWS_MAIN = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodList&prgmId=news";
	final String NEWS_MAIN = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodList&prgmId=news";
	//String NEWS_PLAYER = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodDetail&prgmId=";
//	String NEWS_SELECT_DATE = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodList&prgmId=news&pageNo=1&playDt=";
	String NEWS_SELECT_DATE = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodList&prgmId=news&pageNo=1&playDt=";
	
	String LOG_TAG = "Xml Parser News";
	ArrayList<AODVO> aod = new ArrayList<AODVO>();
	
	public XmlParserNews() {
		
	}
	
	public void parse(){
		//String AOD_URL = AODList[prgmId]+day;
		URL url;
		try {
			url = new URL(NEWS_MAIN);
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));	
			getDocument(doc);
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (SAXException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG,e.getMessage());
		}
		
	
	}
	
	public void parse(String day){
		URL url;
		try {
			url = new URL(NEWS_SELECT_DATE+day);
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));	
			
			getDocument(doc);
			
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (SAXException e) {
			Log.e(LOG_TAG,e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG,e.getMessage());
		}
		

	}
	
	public void getDocument(Document doc){
		try{
			
	
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("aod");
			Log.i("INFO", "Length: "+nodeList.getLength());
			
				
			AODVO vo = null;
			for ( int j=0; j< nodeList.getLength(); j++){
				
				Node pnode = nodeList.item(j);									
				Element pElement = (Element) pnode;

				// date
				NodeList dateList = pElement.getElementsByTagName("date");
				Element dateElement = (Element) dateList.item(0);
				dateList = dateElement.getChildNodes();
				String date = ((Node)dateList.item(0)).getNodeValue();
				Log.i("INFO","date: "+date);
				
				// title
				NodeList titleList = pElement.getElementsByTagName("title");
				Element titleElement = (Element) titleList.item(0);
				titleList = titleElement.getChildNodes();
				String title = ((Node)titleList.item(0)).getNodeValue();
				Log.i("INFO","title: "+title);
				
				// prgmId
				NodeList prgmList = pElement.getElementsByTagName("prgmId");
				Element prgmElement = (Element) prgmList.item(0);
				prgmList = prgmElement.getChildNodes();
				String prgm = ((Node)prgmList.item(0)).getNodeValue();
				Log.i("INFO","prgmId: "+prgm);
				
				// stime
				NodeList sTimeList = pElement.getElementsByTagName("sTime");
				Element sTimeElement = (Element) sTimeList.item(0);
				sTimeList = sTimeElement.getChildNodes();
				String sTime = ((Node)sTimeList.item(0)).getNodeValue();
				Log.i("INFO","sTime: "+sTime);
				
				NodeList eTimeList = pElement.getElementsByTagName("eTime");
				Element eTimeElement = (Element) eTimeList.item(0);
				eTimeList = eTimeElement.getChildNodes();
				String eTime = ((Node)eTimeList.item(0)).getNodeValue();
				Log.i("INFO","eTime: "+eTime);
				
				vo = new AODVO(date, title, prgm, sTime, eTime);
				aod.add(vo);
			}
			
		}catch(Exception e){
			Log.e(LOG_TAG,e.getMessage());
		}						
	}

	public ArrayList<AODVO> getAod() {
		return aod;
	}
	

}
