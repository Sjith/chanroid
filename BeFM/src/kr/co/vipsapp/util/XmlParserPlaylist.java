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

public class XmlParserPlaylist {

//	final String RADIO_PLAYLIST = "http://www.befm.or.kr/app/LinkageAction.do?cmd=prgmTxt&prgmId=";
	final String RADIO_PLAYLIST = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=prgmTxt&prgmId=";
	
	final String LOG_TAG = "XML Parsing - Playlist";
	
	private String playList;
	
	private String prgmId;
	
	public XmlParserPlaylist(){
		
	}
	
	public XmlParserPlaylist(String prgmId){
		this.prgmId = prgmId;
	}
	
	public void parsing(){
		try{
			
			URL url = new URL(RADIO_PLAYLIST+prgmId);
			
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			NodeList sList = doc.getElementsByTagName("text");
			Log.i("INFO","playList: "+sList.getLength());
			
			Element nameElement = (Element) sList.item(0);
			sList = nameElement.getChildNodes();
			playList= ((Node)sList.item(0)).getNodeValue();
			Log.i("INFO","playList: "+playList);
			
		}catch(Exception e){
			Log.e(LOG_TAG , e.getMessage());
		}		
	}

	public String getPlayList() {
		return playList;
	}

	public void setPlayList(String playList) {
		this.playList = playList;
	}
	
	
}
