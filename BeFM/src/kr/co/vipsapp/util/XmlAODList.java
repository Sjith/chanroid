package kr.co.vipsapp.util;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kr.co.vipsapp.util.vo.AODListVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlAODList {
	
//	String AOD_PRGM = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodCafeList&prgmId=";
	String AOD_PRGM = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodCafeList&prgmId=";
//	String AOD_PRGM_DATE = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodDetail&prgmId=";
	String AOD_PRGM_DATE = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=aodDetail&prgmId=";
	
	String LOG_TAG="eFM BUSAN";
	
	private String script = "NO DATA";
	private String fileURL = "NO DATA";
	
	
	private ArrayList<AODListVO> aodList = new ArrayList<AODListVO>();
	
	public XmlAODList(){
		
	}
	
	public void parsingAOD(String prgmId){
		try{
			
			URL url = new URL(AOD_PRGM+prgmId);
			
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("aod_lists");
			Log.i(LOG_TAG, "Length: "+nodeList.getLength());
			
			Node pnode = nodeList.item(0);									
			Element pElement = (Element) pnode;

			// date
			NodeList aList = pElement.getElementsByTagName("aod");
			//Element aodElement = (Element) aodList.item(0);
			//aodList = aodElement.getChildNodes();
			//String date = ((Node)aodList.item(0)).getNodeValue();
			Log.i(LOG_TAG,"aod Length: "+aList.getLength());
			
			AODListVO vo;
			
			for ( int j=0; j< aList.getLength(); j++){
				
				Node dnode = aList.item(j);									
				Element dElement = (Element) dnode;

				// date
				NodeList dateList = dElement.getElementsByTagName("date");
				Element dateElement = (Element) dateList.item(0);
				dateList = dateElement.getChildNodes();
				String date = ((Node)dateList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"date: "+date);		
				vo = new AODListVO(date);
				aodList.add(vo);
			}
			
		}catch(Exception e){
			
		}				
	}
	

	public ArrayList<AODListVO> getAodList() {
		return aodList;
	}
	
	public void parsingAOD(String prgmId, String playDt){
		try{
			
			
			URL url = new URL(AOD_PRGM_DATE+prgmId+"&playDt="+playDt);
			Log.i(LOG_TAG,"URL: "+AOD_PRGM_DATE+prgmId+"&playDt="+playDt);
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("aod_detail");
			Log.i("INFO", "Length: "+nodeList.getLength());
			
			Node pnode = nodeList.item(0);									
			Element pElement = (Element) pnode;

			// date
			NodeList scriptList = pElement.getElementsByTagName("script");
			Element scriptElement = (Element) scriptList.item(0);
			scriptList = scriptElement.getChildNodes();
			script  = ((Node)scriptList.item(0)).getNodeValue();
			if(script.equals("NO DATA")){
				fileURL  = "NO DATA";
				Log.i(LOG_TAG,"script : "+script);				

			}else{
//				NodeList urlList = pElement.getElementsByTagName("file_url");
				NodeList urlList = pElement.getElementsByTagName("wma_url");
				Element urlElement = (Element) urlList.item(0);
				urlList = urlElement.getChildNodes();
				fileURL  = ((Node)urlList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"wmaurl : "+fileURL);
			}
			
		}catch(Exception e){
			
		}		
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}


	
	
}
