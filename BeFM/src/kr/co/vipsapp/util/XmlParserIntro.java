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


public class XmlParserIntro {

	final String RADIO_INTRODUCTION = "http://www.befm.or.kr/adm/ProgramAction.do?cmd=OnAir&mode=curInfo";
	
	private String pid = "";
	private String imgURL = "";
	private String introText = "";
	private String isLocal = "";
	
	
	String LOG_TAG="Xml Parser Intro";
	
	public XmlParserIntro(){
		
	}
	
	public void parsingIntro(){
		
		try{
		
			URL url = new URL(RADIO_INTRODUCTION);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			Document doc = db.parse(new InputSource(url.openStream()));
			
			// root node
			NodeList nodeList = doc.getElementsByTagName("prgm");
			
			Log.i("INFO", "Length: "+nodeList.getLength());
			Node node = nodeList.item(0);

			Element fstElmnt = (Element) node;
			// pid
			
			NodeList pidList = fstElmnt.getElementsByTagName("pid");			
			Element pidElement = (Element) pidList.item(0);
			pidList = pidElement.getChildNodes();
			if(pidList ==null){
				pid= "NO DATA";
			}else{
				pid = ((Node)pidList.item(0)).getNodeValue();
				Log.i("eFM_BUSAN", "PID : "+pid);
			
				//Node nd = (Node)pidList.item(0);
				//NamedNodeMap Attrs = nd.getAttributes();
				isLocal = pidElement.getAttribute("isLocal");
				if(isLocal == null){
					isLocal = "NO DATA";
				} //else{
					//Node attr = Attrs.item(0);
					//isLocal = attr.getNodeValue();
				//}
				Log.i("INFO", "isLocal: "+isLocal);
			}
			
			
			/* img URL*/
			NodeList imgList = fstElmnt.getElementsByTagName("img");
			
			Element imgElement = (Element) imgList.item(0);
			imgList = imgElement.getChildNodes();

			imgURL = ((Node)imgList.item(0)).getNodeValue();
			Log.i("eFM_BUSAN", "IMG URL : "+imgURL);			
			
			// intro
			NodeList introList = fstElmnt.getElementsByTagName("intro");
			
			Element introElement = (Element) introList.item(0);
			introList = introElement.getChildNodes();

			introText = ((Node)introList.item(0)).getNodeValue();
			Log.i("eFM_BUSAN", "Intro : "+introText);				
			
		} catch(Exception e){
			Log.e(LOG_TAG, e.getMessage());

		}
	}
	
	
	public String getImgURL() {
		return imgURL;
	}
	
	public String getIntroText() {
		return introText;
	}

	public String getPid() {
		return pid;
	}

	public String getIsLocal() {
		return isLocal;
	}
	
	
}
