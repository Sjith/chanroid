package kr.co.drdesign.client.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import kr.co.drdesign.client.connector.HttpConnector;
import kr.co.drdesign.client.connector.HttpsConnector;
import kr.co.drdesign.client.connector.WebConnector;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.util.Loggable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

/**
 * 
 * 2011-05-30
 * @author brake-_-
 * 하암 졸려
 * 우왕 재미난 개발
 * 거의 다 끝나 갑니다
 * 
 * 2011-06-23
 * @author brake-_-
 * 귀찮아서 목록 가져오는거랑
 * 추가, 삭제하는 기능까지 다 때려 넣었습니다.
 * 차후 수정이 필요하면 분리하기로 합니다.
 *
 */

public class GrpReceiver implements Loggable {


	private String URL = "http://121.254.228.178:8080/xmlmsgsys/devicemsg/";
	private String buffer;
	private String password;
	
	private boolean useSSL;
	
	private GroupController grpCtrl;
	

	public static final String GROUP_ID = "GROUP_ID";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String GROUP_KNAME = "GROUP_KNAME";
	public static final String GROUP_DESC = "GROUP_DESC";
	public static final String GROUP_ETC = "GROUP_ETC";
	
	private static String GRPLIST_PAGE_NAME = "getGrplist.jsp";
	private static String ALL_GRPLIST_PAGE_NAME = "getAllGrplist.jsp";
	private static String GRP_DELETE_PAGE_NAME = "delGrpMem.jsp";
	private static String GRP_ADD_PAGE_NAME = "addGrpMem.jsp";
	private static String GRP_ADD_GROUP_NAME = "addGrp.jsp";
	private static String GRP_DEL_GROUP_NAME = "delGrp.jsp";
	
	private Context context;
	
	private WebConnector connector;
	
	
	
	public GrpReceiver(Context context, String id, String password) {
		super();
		this.context = context;
		this.password = password;
		this.buffer = id;
		
		grpCtrl = GroupController.getInstance(this.context);
		
		if( useSSL ) {
			connector = new HttpsConnector();
			L("use HttpsConnector"); }
		else {
			connector = new HttpConnector();
			L("use HttpConnector"); }
	}

	public void getGrplist() {

		String url = URL + GRPLIST_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);


		CharSequence text = "";
		try{
			text = connector.decodeFromURL(url, buffer, password, parameter);
//			text = connector.readFromURL(url, buffer, password, parameter);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		L(text+"");
		StringReader sr = new StringReader( text.toString() );

		ArrayList<Map<String, String>> msg = parsingGrpList(sr);
		
		if( msg == null || msg.size() < 1 ) {
			L("msg null");
			grpCtrl.insert();
		} else {		
			L("msg size : " + msg.size());
			for (int i = 0; i < msg.size(); i++) {
				grpCtrl.insert(msg.get(i));
			}
		}
		
	}
	// 2011-06-20 그룹 탈퇴 메서드. 서버와 비교해가며 디버깅 해줘야함.
	public boolean delGrpMem(int gid) {

		String url = URL + GRP_DELETE_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		parameter.put("groupid", Integer.toString(gid));
		// mid는 서버에서 생성해 준다.

		String text = "";
		try{
//			text = connector.decodeFromURL(url, buffer, password, parameter);
			text = connector.readFromURL(url, buffer, password, parameter).toString();

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		L(text+"");
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.contains("succeed")) {
			Log.i("Gruvice", "group delete succeed.");
			return true;
			// 그룹삭제 성공
			// 내부 db의 해당 그룹 정보를 삭제한다.
		} else {
			Log.e("Gruvice", "group delete failed.");
			return false;
			// 그룹삭제 실패
		}
		
	}
	// 2011-06-21 그룹 가입 메서드. 서버작업 해줘야 함.
	public boolean addGrpMem(int gid) {

		String url = URL + GRP_ADD_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		parameter.put("groupid", Integer.toString(gid));
		// mid는 서버에서 생성해 준다.

		CharSequence text = "";
		try{
//			text = connector.decodeFromURL(url, buffer, password, parameter);
			text = connector.readFromURL(url, buffer, password, parameter);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		L(text+"");
		
		
		// text로 가져온 정보를 파싱해서 db에 넣어줘야 한다.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
			// 그룹삭제 성공
		} else {
			return false;
			// 그룹삭제 실패
		}
	}
	
	public boolean addGroup(Map<String,String> map) {

		String url = URL + GRP_ADD_GROUP_NAME;

		// mid는 서버에서 생성해 준다.

		CharSequence text = "";
		try{
//			text = connector.decodeFromURL(url, buffer, password, parameter);
			text = connector.readFromURL(url, buffer, password, map);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		L(text+"");
		
		
		// text로 가져온 정보를 파싱해서 db에 넣어줘야 한다.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeGroup(int gid) {

		String url = URL + GRP_DEL_GROUP_NAME;

		// mid는 서버에서 생성해 준다.


		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		parameter.put("groupid", Integer.toString(gid));
		CharSequence text = "";
		try{
//			text = connector.decodeFromURL(url, buffer, password, parameter);
			text = connector.readFromURL(url, buffer, password, parameter);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		L(text+"");
		
		
		// text로 가져온 정보를 파싱해서 db에 넣어줘야 한다.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<Map<String,String>> getAllGrplist() {
		// 그루비스에 존재하는 모든 그룹을 가져온다.
		String url = URL + ALL_GRPLIST_PAGE_NAME;
		
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		
		CharSequence text = "";
		try {
			text = connector.decodeFromURL(url, buffer, password, parameter);
//			text = connector.readFromURL(url, buffer, password, parameter);
		} catch ( IOException ioe )	{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IOException.." );
		} catch ( IllegalArgumentException iae ) {
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getGrp API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-30 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}
		
		L(text+"");
		StringReader sr = new StringReader(text.toString());
		ArrayList<Map<String, String>> msg = parsingGrpList(sr);
		
		if (msg == null) {
			L("msg null");
			return null;
		}

		L("msg size : " + msg.size());
		return msg;
		// 2011-06-20 이 목록은 받아오기만 하고 DB에 저장하지는 않으며,
		// 받아온 목록을 사용자가 선택하여 저장하는 방법을 사용해야 한다.
		// 이 코드는 차후 필요한 경우 주석을 해제하여 사용할 수 있다.
	}
	
	// Web에서 한글이 깨지는 문제때문에 SAX파서를 PULL파서로 변경함.
	// SAX 파서도 파일에서 읽는 경우는 문제가 없음.
	// 한글이 깨지면 Character Set을 먼저 확인해 볼 것, 한쪽에서만 설정해서 깨지는 경우가 많음.
	public ArrayList<Map<String, String>> parsingGrpList( Reader reader ){
		ArrayList<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 

			xpp.setInput(reader); 

			String tag="", name="", gID = "", gName = "", gKname = "", gDesc = "";
			boolean contents = false;
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
				switch( eventType ){
				case XmlPullParser.START_TAG :
					// 태그의 시작점.
					name = xpp.getName();
					contents = true;
					if (name.contains("contents")) {
						L("GrpList parse line start.");
					}
					break;
				case XmlPullParser.TEXT :
					// 시작과 끝 태그 사이의 텍스트.
					tag = xpp.getText();
					if (contents) {
						// 조건문으로 현재 읽는 위치를 잘 파악하여 값을 대입해야 한다.
						if (name.contains("gid")) gID = tag;
						if (name.contains("gname")) gName = tag;
						if (name.contains("gkname")) gKname = tag;
						if (name.contains("gdesc")) gDesc = tag;
					}
					break;
				case XmlPullParser.END_TAG :
					// 태그가 끝나는점.
					name = xpp.getName();
					contents = false;
					if (name.contains("contents")) { 
						map = new TreeMap<String, String>();
						map.put(GROUP_ID, gID);
						map.put(GROUP_NAME, gName);
						map.put(GROUP_KNAME, gKname);
						map.put(GROUP_DESC, gDesc);
						listMap.add(map);
						L("GrpList parse line end.");
					}
					break;
				default :
					break;
				}
				eventType = xpp.next(); 
			}
			return listMap;
		} catch (XmlPullParserException e) {
			L(e.getMessage());
			e.printStackTrace();
			return listMap;			
		} catch (IOException e) {
			L(e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}
	
	@Override
	public void L(char i, String log) {
		// TODO Auto-generated method stub
		Log.i(DEBUG_TAG, log);		
	}

	@Override
	public void L(String log) {
		// TODO Auto-generated method stub
		Log.i(DEBUG_TAG, log);
	}

}
