package kr.co.drdesign.client.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kr.co.drdesign.client.R;
import kr.co.drdesign.client.ReceivedMsgActivity;
import kr.co.drdesign.client.connector.HttpConnector;
import kr.co.drdesign.client.connector.HttpsConnector;
import kr.co.drdesign.client.connector.WebConnector;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

// ���������� Msg�� �޾Ƽ� ó���Ѵ�.
// Notification � ���⼭ ��� ������.
// DRMService�� ȣ���ϸ� �������� ȯ�漳�� �Ŀ� ȣ��ȴ�.
// 2011-05-24 �����忡�� ���ư��� Ŭ�����ϱ�. �ִ��� ������.
public class MsgReceiver implements Runnable, Loggable {

//	private static final String CLASS_NAME = "MsgReceiver";
	public static final Boolean IS_DEBUG_MODE = true;
	public static String uid = "0";

	private String title;
	private String URL;
	private String buffer;
	private String password;

	private boolean useSSL;
	private WebConnector connector;

	// Parameter IS Not NECESSARY. so, you can delete This code.
	private String cNewMsgList 	= "isNewMsg";
	private String cGetMsg 		= "getMsg";

	private Context context = Communicator.context;
	private SharedPreferences mPref;

	private final String RECEIVE_PAGE_NAME 		= "getMsg.jsp";
	private final String CERTIFICATION_PAGE_NAME = "certMsg.jsp";
	private final String ISNEWMSG_PAGE_NAME 	= "newMsg.jsp";

	private final String NOTIFY_SOUND	= "MsgNotifywithSound";
	private final String NOTIFY_VIBRATE	= "MsgNotifywithVibrate";

	public MsgReceiver(String URL, String id, String password, boolean useSSl)
	{
		this.URL 	= URL;
		this.buffer = "";
		this.password = password;
		this.useSSL = useSSl;

		if( useSSL ) {
			connector = new HttpsConnector();
			L("use HttpsConnector"); }
		else {
			connector = new HttpConnector();
			L("use HttpConnector"); }

		if ( mPref == null ) mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	// ���ο� �޽�������� �о� �´�.
	public List<String> isNewMsg()
	{
		String newMsgURL = URL + ISNEWMSG_PAGE_NAME; 
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("command", cNewMsgList);

		CharSequence text = "";
		try{
				text = connector.readFromURL(newMsgURL, buffer, password, parameter);
				// 2011-04-30 ����ó������ �ʹ� ���� ���๮�� ���� Ȯ�εȰ� �� ��.
		}catch( Exception ioe)
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "isNewMessage API didn't excute. Maybe IOException.." );
			// halftale 100816 ���߿�  retry �ϴ� �κ��� �ʿ��ϴ�.
			// 2011-05-24 ���񽺴ܿ��� retry�ϴ� ������ �߰��Ͽ���.
		}

		//halftale 100806 Connection Loss..
		// 2011-05-24 ���񽺴ܿ��� Connection�� �ٽ� ����ִ� ������ �߰��Ͽ���.
		if ( text == null ) return null;

		StringReader br = new StringReader(text.toString());
		
		Log.i("DR", text.toString());
		// Parsing XML of MessageList.
		Map <String, String> listMap = parsingMsgList(br);
		List<String> msgList = new ArrayList<String>();
		//if( listMap == null || listMap.size() == 0 ) return null;

		// Process Message List ( Get Message from Server. )
		Iterator<String> it = listMap.keySet().iterator();
		
		while( it.hasNext() )
		{
			uid = it.next();
			title = listMap.get( uid ).toString();
			L('i', "ListMap contain : UID : " + uid + ", TITLE : " + title);
			//�������̼� üũ�� ���� �ϴ� �κ�..

			// DB�� �ִ� ������ �߿��ϴٸ� ���⼭ ������ �ؾ��Ѵ�.
			msgList.add(uid);
		}
		return msgList;
	}

	// ���� �޽����� �޾ƿ´�. 
	public void getMsg(String uid){
		String url = URL + RECEIVE_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("command", cGetMsg );
		parameter.put("uid", uid );

		//halftaleTestPage
		parameter.put("number", GruviceUtillity.getInstance(context).getClientId());

		CharSequence text = "";
		try{
			text = connector.decodeFromURL(url, buffer, password, parameter);
			//text = connector.readFromURL(url, buffer, password, parameter);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IOException.." );
			// halftale 100816 ���߿�  retry �ϴ� �κ��� �ʿ��ϴ�.
			getMsg(uid);
			// 2011-05-24 ����� ���� �쿬�� �����̹Ƿ� ���� retry �Ѵ�.
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-24 �߸��� �����͸� ���� ����̱� ������ ó������ �ʿ䰡 ����.
		}catch( OutOfMemoryError ooe ) {
			notify(null, 1);
		}
		
		// 2011-04-29 ������ϱ� ���� �׳� ������ ���´µ� �� ������ ����.
		StringReader sr = new StringReader( text.toString() );

		Map<String, String> msg = parsingMsg( sr );
		
		if( msg == null )
			return;
		if( msg.get("UID") == null || msg.get("UID").equals(uid) == false )
			return;
		
		ReceiveMsgController msgController = Communicator.msgController;
		long _id = msgController.insert(msg);

//		GroupController grpController = Communicator.grpController;
//		grpController.insert(msg);
		// 2011-05-30 �׷������� �ѹ��� �� �޾ƿ����� ����.
		
		msg.put(ReceiveMsgController._id, String.valueOf(_id));
		notify(msg, 0);
		
	}

	// ������ �޽����� ���� ��Ƽ�� ����ִ� �κ�.
	private void notify( Map<String, String> msg, int msgUID ){

		boolean isNotifywithSound 	= mPref.getBoolean(NOTIFY_SOUND, false);
		boolean isNotifywithVibrate = mPref.getBoolean(NOTIFY_VIBRATE, false);
		NotificationManager notifier = 
			(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 

		String title = null;
		String msgId = null;
		String contentText = null;
		// 2011-05-24 �޽��� ���ſ� ������ case������ �˸� ���� �߰�.
		if (msgUID == 0) {
			title = msg.get(ReceiveMsgController.TITLE);
			msgId = msg.get(ReceiveMsgController._id);
			contentText = msg.get(ReceiveMsgController.SENDER);
		}
		int iMsgId = msgUID;
		try{
			iMsgId = Integer.valueOf(msgId);
		}catch( NumberFormatException nfe )
		{
			Log.e(DEBUG_TAG, nfe.getMessage());
		}

		Notification notify = new Notification(R.drawable.icon, title, System.currentTimeMillis());

		// Notification set Sound & Vibrate. 
		if( isNotifywithSound ) notify.defaults |= Notification.DEFAULT_SOUND;
		if( isNotifywithVibrate ) notify.defaults |= Notification.DEFAULT_VIBRATE;

		notify.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent toLaunch = new Intent( context, ReceivedMsgActivity.class );
		toLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		toLaunch.putExtra(ReceiveMsgController._id, msgId);
		if (msgUID != 0) toLaunch = null;

		PendingIntent intentBack = PendingIntent.getActivity(context, iMsgId, toLaunch, 0 );
		if ( contentText == null || contentText.length() == 0 ) 
			contentText = context.getString(R.string.txtNotiText);
		notify.setLatestEventInfo( context, title, contentText, intentBack);

		notifier.notify(iMsgId, notify);
		
	}

	public void getAllNewMsg()
	{
		
	}

	// Web���� �ѱ��� ������ ���������� SAX�ļ��� PULL�ļ��� ������.
	// SAX �ļ��� ���Ͽ��� �д� ���� ������ ����.
	// �ѱ��� ������ Character Set�� ���� Ȯ���� �� ��, ���ʿ����� �����ؼ� ������ ��찡 ����.
	public Map<String, String> parsingMsgList( Reader reader ){
		Map<String, String> listMap = new TreeMap<String, String>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 

			xpp.setInput(reader); 

			String tag=null, id = "", title= "";
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					tag = (xpp.getName()+"").trim();
					break;
				case XmlPullParser.TEXT :
					if("uid".equalsIgnoreCase(tag)) 
					{
						id =  (id + xpp.getText()).trim();
					}
					else if("title".equalsIgnoreCase(tag)) 
					{
						title = (title + xpp.getText()).trim();
					}
					break;
				case XmlPullParser.END_TAG :
					if( "title".equalsIgnoreCase(tag) )
					{
						if( id != null && id.length() > 0)
						{
							listMap.put(id, title);
							L( "!id = " + id + ", title = " + title);
						}
						id = ""; title = "";
					}
					break;
				default :
					break;
				}
				eventType = xpp.next(); 
			}
			return listMap;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return listMap;			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public Map<String, String> parsingMsg( Reader reader ){

		Map<String, String> msg = new HashMap<String, String>();

		String attachName = null;
		String attachURL = null;
		String attachLength = null;
		String attachType = null;
		String uID = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 

			xpp.setInput(reader); 

			String tag="", buffer ="", nameOfApp = "";

			boolean isAttachments = false;
			boolean isETC = false;
			boolean isAPP = false;
			StringBuilder etcBuffer = new StringBuilder();
			StringBuilder contentsBuffer = new StringBuilder();
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
					
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					L( "xpp.getName() = " + xpp.getName());
					// 2011-04-29 �̰�... �� ������ �ϳ��� �о������????
					if( "attachments".equalsIgnoreCase(xpp.getName()))
					{
						isAttachments = true;
					}else if ( "etc".equalsIgnoreCase(xpp.getName()))
					{
						isETC = true;
					}else if ( "app".equalsIgnoreCase(xpp.getName()))
					{
						isAPP = true;
					}
					break;
				case XmlPullParser.TEXT :
					buffer = ("" + xpp.getText()).trim();
					break;
				case XmlPullParser.END_TAG :
					tag = (xpp.getName()).trim();
					L("parsing tag : " + tag );
					
					
					if( "file".equalsIgnoreCase(tag) )
					{
						L( "DB insert" + " uid : " + uID);
						L( "DB insert" + " url : " + attachURL);
						L( "DB insert" + " name : " + attachName);
						L( "DB insert" + " length : " + attachLength);
						L( "DB insert" + " type : " + attachType);
						/// DB�� �ִ� �κ�..att;
						if (!attachName.contains("filenull")) {
							Communicator.attController.insert(uID, attachName, attachURL, null, attachType, attachLength);
							msg.put(ReceiveMsgController.ATTACHMENTS, attachName);
						}
						
					}

					//if(�̹��� �ý�Ʈ �϶�,) �̹����� �����ϰ� �ý�Ʈ�� ���� �����ϴ�..�κ��� ��.
					// Contents�� �ش��ϴ� �κ��� ��ȯ�ؼ� �־�� �ϰ�, �ʿ� �׳� ������ �ȵ�.
					if( (buffer == null || buffer.length() == 0) 
							&& (contentsBuffer == null || contentsBuffer.length() == 0) ) 
						break;

					if( "groupid".equalsIgnoreCase(tag))
					{
						msg.put(GroupController.GROUP_ID, buffer);
												L("GROUP_ID : " + buffer);
					}
					if( "groupname".equalsIgnoreCase(tag))
					{
						msg.put(GroupController.GROUP_NAME, buffer);
												L( "GROUP_NAME : " + buffer);
					}
					if( "uid".equalsIgnoreCase(tag))
					{
						msg.put("UID", buffer);
						uID = buffer;
					}
					else if( "title".equalsIgnoreCase(tag))
					{
						msg.put("TITLE", buffer);
					}
					else if( "body".equalsIgnoreCase(tag))
					{
						msg.put("CONTENTS", contentsBuffer.toString().trim());
						contentsBuffer = null;
						L("CONTENTS" + " : " + contentsBuffer);
					}
					else if( "img".equalsIgnoreCase(tag))
					{
						// 2011-04-29 �̹����� ������ ��ȣȭ �� ������ Ư�� ��ο� �����ϴ°� ����.
						// DB�� ���°� ���⼭ �ϴ°� �ƴѵ�.. �ƿ� DB�� �Ⱦ��� ���������� �ְ�....
						String imgFileName = System.currentTimeMillis()+".png";
						// 2011-06-14 ��Ʈ������ ���°ű� ������ ���ϸ��� �ݵ�� ��������� �ϴµ�
						// �Ľ����� ����� �̹��� ������ �̸��� �� �� ���� ���� ����.
						File file = Communicator.fileHelper.saveBase64File(null, imgFileName, buffer);
						//���� ���� ����
						contentsBuffer.append("img_\n");
						contentsBuffer.append(file.getAbsolutePath() + "\n");
						Log.i("Gruvice", imgFileName);
					}
					else if( "text".equalsIgnoreCase(tag))
					{
						contentsBuffer.append("text_\n");
						contentsBuffer.append(buffer + "\n");
					}
					else if( "mov".equalsIgnoreCase(tag))
					{
						contentsBuffer.append("mov_\n");
						contentsBuffer.append(buffer + "\n");
					}
					else if( "name".equalsIgnoreCase(tag) && isAPP == true )
					{
						nameOfApp += buffer.trim();
					}
					else if( "enter".equalsIgnoreCase(tag))
					{
						msg.put("ETC", buffer);
					}
					//APK, ATTACHMENT�� ����� �� ó���ϴ� ���.
					else if( "apk".equalsIgnoreCase(tag))
					{
						File file = Communicator.fileHelper.saveBase64File(null, nameOfApp, buffer);
						//���� ���� ����
						buffer = file.getAbsolutePath();
						msg.put("APPPATH", buffer);
					}
					else if( "url".equalsIgnoreCase(tag) )
					{
						attachURL = buffer;
					}
					else if( "name".equalsIgnoreCase(tag) && isAttachments == true )
					{
						attachName = buffer;
					}
					//���ۻ���� ��������.
					else if( "length".equalsIgnoreCase(tag) )
					{
						attachLength = buffer;
					}
					else if( "type".equalsIgnoreCase(tag) )
					{
						attachType = buffer;
					}
					else if( "from".equalsIgnoreCase(tag) )
					{
						msg.put("SENDER", buffer);
					}
					else if( "to".equalsIgnoreCase(tag) )
					{
						
					}
					else if( "create-date".equalsIgnoreCase(tag) )
					{
						attachType = buffer;
						msg.put("CREATEDATE", buffer);
					}else if( "code".equalsIgnoreCase(tag) && isETC == true){
						etcBuffer.append("code_\n" + buffer + "\n");
					}else if( "price".equalsIgnoreCase(tag) && isETC == true){
						etcBuffer.append("price_\n" + buffer + "\n");
					}else if( "name".equalsIgnoreCase(tag) && isETC == true){
						etcBuffer.append("name_\n" + buffer + "\n");
					}else if( "detail".equalsIgnoreCase(tag) && isETC == true){
						etcBuffer.append("detail_\n" + buffer + "\n");
					}
					else{
						
					}
					buffer = "";
					tag="";
					break;
				default :
					break;
				}
				
				try {
				eventType = xpp.next();
				} catch (XmlPullParserException xpe) {
					L("parse error : " + xpe.getMessage());
				}
			}
			if( isETC == true )
			{
				msg.put("ETC", etcBuffer.toString());
			}
			msg.put("RECEIVEDATE", System.currentTimeMillis()+"");
			L("!!!RECEIVEDATE " + " : " + System.currentTimeMillis());
			return msg;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public void run() { 

		try{
			certicateServer();
			List<String> msgList = isNewMsg();

			//halftale 100806 Connection�� ���� ���� ��.. ���� Reconnect�� �ǵ��� �����ؾ� ��
			if( msgList == null || msgList.size() == 0)
			{
				L("Connection Loss Msg Receive Process Stop..");
				return;
			}
			Iterator<String> it = msgList.iterator();
			while( it.hasNext() )
			{
				getMsg(it.next());
			}
		}
		catch ( Exception iae ){
//			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IllegalArgumentException.." );
		}
	}

	//halftale �����ؾ� ��. 100816
	//�α��� �ؼ� ������ �����ϴ� �κ�.
	private void certicateServer() 
	{
		//connector.readFromURL(���˿�, ��ȭ��ȣ, "", �� );
		Map<String, String> map = new HashMap<String, String>();
		try {
			connector.readFromURL( URL + CERTIFICATION_PAGE_NAME, GruviceUtillity.getInstance(context).getClientId(), "",  map);
		} catch (IOException e) {
			Log.e(DEBUG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void L(char i, String log) 
	{
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}
