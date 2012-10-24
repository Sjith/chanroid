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

public class XmlParserSchedule {

//	final String RADIO_SCHEDULE = "http://www.befm.or.kr/app/LinkageAction.do?cmd=prgmSchedule&prgmDay=";
	final String RADIO_SCHEDULE = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=prgmSchedule&prgmDay=";
	
	final String LOG_TAG = "XML Parsing - Schedule";
	
	//private ArrayList<ProgramVO> programList = new ArrayList<ProgramVO>();
	private String radioScheduleImage;
	
	
	public XmlParserSchedule(){
		
	}
	
	/**
	 * 해당 요일에 대한 프로그램 목록을 가져온다.
	 * 
	 * @param idx 요일 인덱스(0-평일, 1-토요일, 2-일요일)
	 */
	public void parsingProgram(String day){
		
		try{
			
			URL url = new URL(RADIO_SCHEDULE+day);
			
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("result");
			Log.i("INFO", "Result: "+nodeList.getLength());
			
			// schedule 노드 중 첫번째 요소를 찾는다.
			Node node = nodeList.item(0);				
			Element fstElmnt = (Element) node;

			NodeList sList = fstElmnt.getElementsByTagName("schedule");
			Log.i("INFO","schedule: "+sList.getLength());
			
			Element nameElement = (Element) sList.item(0);
			sList = nameElement.getChildNodes();
			radioScheduleImage = ((Node)sList.item(0)).getNodeValue();
			Log.i("INFO","radioScheduleImage: "+radioScheduleImage);
			
		}catch(Exception e){
			Log.e(LOG_TAG , e.getMessage());
		}
		
	}

	public String getRadioScheduleImage() {
		return radioScheduleImage;
	}


	/**
	 * 프로그램 정보를 파싱한다. name, s_time, product_type
	 * 
	 * @param pElement
	 * @return ProgramInfo 클래스
	 */
//	private ProgramVO getProgramDetail(Element pElement) {
//	
//		NodeList nameList = pElement.getElementsByTagName("name");
//		Element nameElement = (Element) nameList.item(0);
//		nameList = nameElement.getChildNodes();
//		String name = ((Node)nameList.item(0)).getNodeValue();
//		Log.i("INFO","Name: "+name);
//		
//		// s_time
//		NodeList stimeList = pElement.getElementsByTagName("s_time");
//		Element stimeElement = (Element) stimeList.item(0);
//		stimeList = stimeElement.getChildNodes();
//		String stime = ((Node)stimeList.item(0)).getNodeValue();
//		Log.i("INFO","s_time: "+stime);
//		
//		// product type
//		NodeList typeList = pElement.getElementsByTagName("product_type");
//		Element typeElement = (Element) typeList.item(0);
//		typeList = typeElement.getChildNodes();
//		String type = ((Node)typeList.item(0)).getNodeValue();
//		Log.i("INFO","type: "+type);
//		
//		// 프로그램 정보를 담은 클래스 생성
//		ProgramVO info = new ProgramVO(name, stime, type);
//		
//		return info;
//	}



	
	
}
