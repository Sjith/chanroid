package kr.co.vipsapp.util;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class XmlParserWrite {
//	final String BUSAN_EFM_COMMENT = "http://www.befm.or.kr/app/LinkageAction.do";
	final String BUSAN_EFM_COMMENT = "http://www.befm.or.kr/app2/LinkageAction.do";
	String LOG_TAG = "Xml Parser Comment write ";
	// login 처리 결과
	private String result="true";
	
	public XmlParserWrite(){
		
	}
	
	/**
	 * XML 파싱
	 */
	public void parsingResult(String prgmId, String id, String content){

		 
		try{
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(BUSAN_EFM_COMMENT);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cmd","cmtReg"));
			params.add(new BasicNameValuePair("prgmId",prgmId));
			params.add(new BasicNameValuePair("userId",id));
			params.add(new BasicNameValuePair("content",content));
			
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "euc-kr");
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();

			String response = EntityUtils.toString(resEntity);
			Log.w("RESPONSE", response);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource( new StringReader(response ) );
			
			Document doc = db.parse(is);
				
			NodeList nodeList = doc.getElementsByTagName("result");
				
			Log.i("INFO", "Length: "+nodeList.getLength());
			Node node = nodeList.item(0);
				
			Node text = node.getFirstChild();
				
			result = text.getNodeValue();
			Log.i("eFM_BUSAN", "Login Comment : "+result);

		} catch(Exception e){
			Log.e(LOG_TAG, e.getMessage());

		}		
		
	}
	

	
	public String getResult() {
		return result;
	}
	
  	
}
