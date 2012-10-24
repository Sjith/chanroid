package kr.co.vipsapp.util;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlLogin {
	

	/* 로그인 결과 */
	// http://www.befm.or.kr/app/LinkageAction.do?cmd=login&id=ID&pwd=PASSWORD
	
//	public static String RADIO_LOGIN = "http://www.befm.or.kr/app/LinkageAction.do?cmd=login&";
	public static String RADIO_LOGIN = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=login&";
	
	// login 처리 결과
	private String result;
	// id
	private String id;
	// password
	private String pwd;
	
	/**
	 * 
	 */
	public XmlLogin(){
		
	}
	
	/**
	 * 파라메터 생성자
	 * @param id
	 * @param pwd
	 */
	public XmlLogin(String id, String pwd){
		this.id = id;
		this.pwd = pwd;
	}
	
	/**
	 * XML 파싱
	 */
	public void parsingLogin(){
		
		try{
			
			
			
			URL url = new URL(RADIO_LOGIN+"&id="+id+"&pwd="+pwd);
			
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document doc = db.parse(new InputSource(url.openStream()));
			
			// root node
			NodeList nodeList = doc.getElementsByTagName("result");
			
			Log.i("INFO", "Length: "+nodeList.getLength());
			Node node = nodeList.item(0);
			
			Node text = node.getFirstChild();
			
			result = text.getNodeValue();
			Log.i("eFM_BUSAN", "Login Result : "+result);
			
			
		} catch(Exception e){
			System.out.println("XML Pasing Excpetion = " + e);

		}		
		
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}


	
}
