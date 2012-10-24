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
 * �Ͼ� ����
 * ��� ��̳� ����
 * ���� �� ���� ���ϴ�
 * 
 * 2011-06-23
 * @author brake-_-
 * �����Ƽ� ��� �������°Ŷ�
 * �߰�, �����ϴ� ��ɱ��� �� ���� �־����ϴ�.
 * ���� ������ �ʿ��ϸ� �и��ϱ�� �մϴ�.
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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
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
	// 2011-06-20 �׷� Ż�� �޼���. ������ ���ذ��� ����� �������.
	public boolean delGrpMem(int gid) {

		String url = URL + GRP_DELETE_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		parameter.put("groupid", Integer.toString(gid));
		// mid�� �������� ������ �ش�.

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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
		}
		L(text+"");
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.contains("succeed")) {
			Log.i("Gruvice", "group delete succeed.");
			return true;
			// �׷���� ����
			// ���� db�� �ش� �׷� ������ �����Ѵ�.
		} else {
			Log.e("Gruvice", "group delete failed.");
			return false;
			// �׷���� ����
		}
		
	}
	// 2011-06-21 �׷� ���� �޼���. �����۾� ����� ��.
	public boolean addGrpMem(int gid) {

		String url = URL + GRP_ADD_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("clientid", buffer);
		parameter.put("groupid", Integer.toString(gid));
		// mid�� �������� ������ �ش�.

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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
		}
		L(text+"");
		
		
		// text�� ������ ������ �Ľ��ؼ� db�� �־���� �Ѵ�.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
			// �׷���� ����
		} else {
			return false;
			// �׷���� ����
		}
	}
	
	public boolean addGroup(Map<String,String> map) {

		String url = URL + GRP_ADD_GROUP_NAME;

		// mid�� �������� ������ �ش�.

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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
		}
		L(text+"");
		
		
		// text�� ������ ������ �Ľ��ؼ� db�� �־���� �Ѵ�.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeGroup(int gid) {

		String url = URL + GRP_DEL_GROUP_NAME;

		// mid�� �������� ������ �ش�.


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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
		}
		L(text+"");
		
		
		// text�� ������ ������ �Ľ��ؼ� db�� �־���� �Ѵ�.
		
		Log.i("Gruvice", "text : " + text.toString());
		if (text.toString().contains("succeed")) {
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<Map<String,String>> getAllGrplist() {
		// �׷�񽺿� �����ϴ� ��� �׷��� �����´�.
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
			// 2011-05-30 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
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
		// 2011-06-20 �� ����� �޾ƿ��⸸ �ϰ� DB�� ���������� ������,
		// �޾ƿ� ����� ����ڰ� �����Ͽ� �����ϴ� ����� ����ؾ� �Ѵ�.
		// �� �ڵ�� ���� �ʿ��� ��� �ּ��� �����Ͽ� ����� �� �ִ�.
	}
	
	// Web���� �ѱ��� ������ ���������� SAX�ļ��� PULL�ļ��� ������.
	// SAX �ļ��� ���Ͽ��� �д� ���� ������ ����.
	// �ѱ��� ������ Character Set�� ���� Ȯ���� �� ��, ���ʿ����� �����ؼ� ������ ��찡 ����.
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
					// �±��� ������.
					name = xpp.getName();
					contents = true;
					if (name.contains("contents")) {
						L("GrpList parse line start.");
					}
					break;
				case XmlPullParser.TEXT :
					// ���۰� �� �±� ������ �ؽ�Ʈ.
					tag = xpp.getText();
					if (contents) {
						// ���ǹ����� ���� �д� ��ġ�� �� �ľ��Ͽ� ���� �����ؾ� �Ѵ�.
						if (name.contains("gid")) gID = tag;
						if (name.contains("gname")) gName = tag;
						if (name.contains("gkname")) gKname = tag;
						if (name.contains("gdesc")) gDesc = tag;
					}
					break;
				case XmlPullParser.END_TAG :
					// �±װ� ��������.
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
