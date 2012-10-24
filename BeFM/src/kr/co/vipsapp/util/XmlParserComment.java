package kr.co.vipsapp.util;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kr.co.vipsapp.util.vo.CommentVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlParserComment {
	
//	String RADIO_COMMENT = "http://www.befm.or.kr/app/LinkageAction.do?cmd=lstCmt&prgmId=";
	String RADIO_COMMENT = "http://www.befm.or.kr/app2/LinkageAction.do?cmd=lstCmt&prgmId=";
	
	String LOG_TAG="Xml Parser Comment";
	
	ArrayList<CommentVO> comments = new ArrayList<CommentVO>();
	//private int size;
	private String prgmId;
	
	public XmlParserComment(){
		//this.prgmId = prgmId;
	}
	
	public void parsing(String prgmId){
		
		try{
			CommentVO vo;
			URL url = new URL(RADIO_COMMENT+prgmId);
			
			// DOM parser를 이용해 XML 문서 파싱
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();									
			Document doc = db.parse(new InputSource(url.openStream()));			
			
			// root node program_schedule 자식의 week 노드를 찾는다. 
			NodeList nodeList = doc.getElementsByTagName("comments");
			//Log.i(LOG_TAG, "Comment Length: "+nodeList.getLength());
			
			if( nodeList.getLength() < 1){
				//size = 0;
				vo = new CommentVO("","","", "NO DATA", "");
				comments.add(vo);
				
				return;
			}
			
			Node pnode = nodeList.item(0);									
			Element pElement = (Element) pnode;

			// date
			NodeList aList = pElement.getElementsByTagName("comment");

			Log.i(LOG_TAG,"comment Length: "+aList.getLength());
			
			
			
			for ( int j=0; j< aList.getLength(); j++){
				
				Node dnode = aList.item(j);									
				Element dElement = (Element) dnode;

				// seq
				NodeList seqList = dElement.getElementsByTagName("seq");
				Element seqElement = (Element) seqList.item(0);
				seqList = seqElement.getChildNodes();
				String seq = ((Node)seqList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"seq: "+seq);		

				// id
				NodeList idList = dElement.getElementsByTagName("user_id");
				Element idElement = (Element) idList.item(0);
				idList = idElement.getChildNodes();
				String id = ((Node)idList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"id: "+id);
				
				// name
				NodeList nameList = dElement.getElementsByTagName("user_name");
				Element nameElement = (Element) nameList.item(0);
				nameList = nameElement.getChildNodes();
				String name = ((Node)nameList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"name: "+name);				
				
				// contents
				NodeList contentList = dElement.getElementsByTagName("content");
				Element contentElement = (Element) contentList.item(0);
				contentList = contentElement.getChildNodes();
				String content = ((Node)contentList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"content: "+content);

				// input date
				NodeList dateList = dElement.getElementsByTagName("inp_dt");
				Element dateElement = (Element) dateList.item(0);
				dateList = dateElement.getChildNodes();
				String date = ((Node)dateList.item(0)).getNodeValue();
				Log.i(LOG_TAG,"date: "+date);
				
				vo = new CommentVO(seq,id,name, content, date);
				comments.add(vo);
			}
			
			//size = comments.size();
		}catch(Exception e){
			
		}				
		
	}

	public ArrayList<CommentVO> getComments() {
		return comments;
	}



	public String getPrgmId() {
		return prgmId;
	}
	
	
}
