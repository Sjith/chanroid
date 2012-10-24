package kr.co.vipsapp.util;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlParserMain {
	final String RADIO_MAIN = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=main";
//	final String RADIO_MAIN = "http://www.befm.or.kr/app/LinkageAction.do?cmd=main";
	final String LOG_TAG = "Xml Parser - Main";
	
	private String titleImg;
	private String bannerImg;
	private String prgmId;
	
	public XmlParserMain(){
		
	}
	
	public void parser(){
		try{
			
			URL url = new URL(RADIO_MAIN);
			
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("result");
			Log.i(LOG_TAG, "Result: "+nodeList.getLength());
			
			// schedule 노드 중 첫번째 요소를 찾는다.
			Node node = nodeList.item(0);				
			Element fstElmnt = (Element) node;

			// prgmId
			NodeList iList = fstElmnt.getElementsByTagName("prgmId");
			Log.i(LOG_TAG,"prgmId: "+iList.getLength());
			
			Element nameElement = (Element) iList.item(0);
			iList = nameElement.getChildNodes();
			prgmId = ((Node)iList.item(0)).getNodeValue();
			Log.i(LOG_TAG,"prgmId: "+prgmId);
			
			NodeList sList = fstElmnt.getElementsByTagName("prgmImg");
			Log.i(LOG_TAG,"prgmId: "+sList.getLength());
			
			Element sElement = (Element) sList.item(0);
			sList = sElement.getChildNodes();
			titleImg = ((Node)sList.item(0)).getNodeValue();
			Log.i(LOG_TAG,"prgmImg: "+titleImg);
			
			NodeList bList = fstElmnt.getElementsByTagName("banImg");
			Log.i(LOG_TAG,"titleImg: "+bList.getLength());
			
			
			// WMA Live, MP3 Live
			
			Element bElement = (Element) bList.item(0);
			bList = bElement.getChildNodes();
			bannerImg = ((Node)bList.item(0)).getNodeValue();
			Log.i(LOG_TAG,"bannerImg: "+bannerImg);
			
		}catch(Exception e){
			Log.e(LOG_TAG , e.getMessage());
		}		
	}
	
	
	public String getTitleImg() {
		return titleImg;
	}
	public String getBannerImg() {
		return bannerImg;
	}
	public String getPrgmId() {
		return prgmId;
	}
	
	
}
