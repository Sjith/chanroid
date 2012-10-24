package kr.co.vipsapp.util;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlResult {
//	final String BUSAN_EFM_COMMENT = "http://www.befm.or.kr/app/LinkageAction.do?cmd=cmtReg&prgmId=";
	final String BUSAN_EFM_COMMENT = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=cmtReg&prgmId=";
	
	// login 처리 결과
	private String result;
	
	public XmlResult(){
		
	}
	
	/**
	 * XML 파싱
	 */
	public void parsingResult(String prgmId, String id, String content){
		
		try{
			
			
			content= new String(content.getBytes("euc-kr"),"utf-8");  

			URL url = new URL(BUSAN_EFM_COMMENT+prgmId+"&userId="+id+"&content="+content);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(new InputSource(url.openStream()));
			
			// root node
			NodeList nodeList = doc.getElementsByTagName("result");
			
			Log.i("INFO", "Length: "+nodeList.getLength());
			Node node = nodeList.item(0);
			
			Node text = node.getFirstChild();
			
			result = text.getNodeValue();
			Log.i("eFM_BUSAN", "Login Comment : "+result);
			
			
		} catch(Exception e){
			System.out.println("XML Pasing Excpetion = " + e);

		}		
		
	}

	public String getResult() {
		return result;
	}
	
	
}
