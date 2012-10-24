package kr.co.drdesign.parmtree.connector;

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

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.ReceivedMsgController;
import kr.co.drdesign.parmtree.message.ReceivedMsgActivity;
import kr.co.drdesign.parmtree.util.ParmUtil;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;

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


public class MsgReceiver extends Thread implements Runnable, c {

//	private static final String CLASS_NAME = "MsgReceiver";
	public static String uid = "0";
	private ParmUtil util;

	private String URL;
	private String buffer;
	private String password;

	private boolean useSSL;
	private WebConnector connector;

	private Context context = ParmUtil.ctx;
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
		util = ParmUtil.getInstance(context);

		if( useSSL ) {
			connector = new HttpsConnector();
		} else {
			connector = new HttpConnector();
		}
		if ( mPref == null ) mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}


	public void run() { 

		try{
			certicateServer();
			List<String> msgList = isNewMsg();

			if( msgList == null || msgList.size() == 0)
			{
				return;
			}
			Iterator<String> it = msgList.iterator();
			while( it.hasNext() )
			{
				getMsg(it.next());
			}
		}
		catch ( Exception iae ){
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IllegalArgumentException.." );
		}
	}

	
	// 새로운 메시지목록을 읽어 온다.
	public List<String> isNewMsg()
	{
		String newMsgURL = URL + ISNEWMSG_PAGE_NAME; 
		Map<String, String> parameter = new HashMap<String, String>();

		CharSequence text = "";
		try{
			text = connector.readFromURL(newMsgURL, buffer, password, parameter);
			// 2011-04-30 예외처리문에 너무 많은 실행문이 들어가서 확인된건 다 뺌.
		}catch( Exception ioe)
		{
			
		}
		
		if ( text == null ) return null;

		StringReader br = new StringReader(text.toString());
		
		
		// Parsing XML of MessageList.
		Map <String, String> listMap = parsingMsgList(br);
		List<String> msgList = new ArrayList<String>();
		if( listMap == null || listMap.size() == 0 ) return null;

		// Process Message List ( Get Message from Server. )
		Iterator<String> it = listMap.keySet().iterator();
		
		while( it.hasNext() )
		{
			uid = it.next();
			msgList.add(uid);
		}
		return msgList;
	}

	// 실제 메시지를 받아온다. 
	public void getMsg(String uid){
		String url = URL + RECEIVE_PAGE_NAME;

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(USERID, uid );

		//halftaleTestPage
		parameter.put("number", util.getAccountID());

		CharSequence text = "";
		try{
			text = connector.decodeFromURL(url, buffer, password, parameter);
			//text = connector.readFromURL(url, buffer, password, parameter);

		}catch( IOException ioe )
		{
			Log.e(DEBUG_TAG, ioe.getMessage());
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IOException.." );
			// halftale 100816 나중에  retry 하는 부분이 필요하다.
			getMsg(uid);
			// 2011-05-24 입출력 상의 우연한 오류이므로 필히 retry 한다.
		}catch ( IllegalArgumentException iae ){
			Log.e(DEBUG_TAG, iae.getMessage());
			Log.e(DEBUG_TAG, "getMsg API didn't excute. Maybe IllegalArgumentException.." );
			// 2011-05-24 잘못된 데이터를 넣은 경우이기 때문에 처리해줄 필요가 없다.
		}catch( OutOfMemoryError ooe ) {
			notify(null, 1);
		}
		
		// 2011-04-29 디버깅하기 힘들어서 그냥 밖으로 꺼냈는데 별 문제는 없음.
		StringReader sr = new StringReader( text.toString() );

		Map<String, String> msg = parsingMsg( sr );
		
		if( msg == null )
			return;
		if( msg.get("UID") == null || msg.get("UID").equals(uid) == false )
			return;
		
		ReceivedMsgController msgController = ReceivedMsgController.getInstance(context);
		long _id = msgController.insert(msg);

//		GroupController grpController = Communicator.grpController;
//		grpController.insert(msg);
		// 2011-05-30 그룹정보는 한번에 다 받아오도록 변경.
		
		msg.put(ID, String.valueOf(_id));
		notify(msg, 0);
		
	}

	// 실제로 메시지가 오면 노티를 띄워주는 부분.
	private void notify( Map<String, String> msg, int msgUID ){

		boolean isNotifywithSound 	= mPref.getBoolean(NOTIFY_SOUND, false);
		boolean isNotifywithVibrate = mPref.getBoolean(NOTIFY_VIBRATE, false);
		NotificationManager notifier = 
			(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 

		String title = null;
		String msgId = null;
		String contentText = null;
		if (msgUID == 0) {
			title = msg.get(TITLE);
			msgId = msg.get(ID);
			contentText = msg.get(CONTENTS);
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
		toLaunch.putExtra(ID, msgId);
		if (msgUID != 0) toLaunch = null;

		PendingIntent intentBack = PendingIntent.getActivity(context, iMsgId, toLaunch, 0 );
		if ( contentText == null || contentText.length() == 0 ) 
			contentText = context.getString(R.string.done);
		notify.setLatestEventInfo( context, title, contentText, intentBack);

		notifier.notify(iMsgId, notify);
		
	}

	public void getAllNewMsg()
	{
		
	}

	// Web에서 한글이 깨지는 문제때문에 SAX파서를 PULL파서로 변경함.
	// SAX 파서도 파일에서 읽는 경우는 문제가 없음.
	// 한글이 깨지면 Character Set을 먼저 확인해 볼 것, 한쪽에서만 설정해서 깨지는 경우가 많음.
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
					if(USERID.equalsIgnoreCase(tag)) 
					{
						id =  (id + xpp.getText()).trim();
					}
					else if(CONTENTS.equalsIgnoreCase(tag)) 
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

	public Map<String, String> parsingMsg( Reader reader ) {

		Map<String, String> msg = new HashMap<String, String>();

		String uID = null;
		String stID = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 

			xpp.setInput(reader); 

			String tag="", buffer ="";

			StringBuilder contentsBuffer = new StringBuilder();
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
				switch( eventType ){
				case XmlPullParser.START_DOCUMENT :
					break;
				case XmlPullParser.END_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					tag = xpp.getName();
					l.s("xml tag : " + tag);
					break;
				case XmlPullParser.TEXT :
					buffer = xpp.getText();
					l.s("xml text : " + buffer);
					break;
				case XmlPullParser.END_TAG :

					//if(이미지 택스트 일때,) 이미지는 저장하고 택스트는 따로 저장하는..부분이 들어감.
					// Contents에 해당하는 부분은 변환해서 넣어야 하고, 맵에 그냥 넣으면 안됨.
					if (buffer == null || buffer.length() == 0) break;

					if( ESTID.equalsIgnoreCase(tag))
					{
						msg.put(ESTID, buffer);
						uID = buffer;
						stID = "0"; // 업체에게 받은 메시지
					}
					if( USERID.equalsIgnoreCase(tag))
					{
						msg.put(USERID, buffer);
						uID = buffer;
						stID = "1"; // 사용자에게 받은 메시지
					}
					else if( CONTENTS.equalsIgnoreCase(tag))
					{
						msg.put(CONTENTS, buffer);
						contentsBuffer.append(buffer);
					}
					else if ( COUPONID.equalsIgnoreCase(tag)) {
						msg.put(COUPONID, buffer);
					}
					else if ( EVENTID.equalsIgnoreCase(tag)) {
						msg.put(EVENTID, buffer);
					}
					else if( IMAGE.equalsIgnoreCase(tag))
					{
						// 이미지를 받으면 복호화 한 다음에 특정 경로에 저장하는것 같다.
						// DB에 쓰는건 여기서 하는게 아닌듯.. 아예 DB에 안쓰게 돼있을수도 있고....
						String imgFileName = System.currentTimeMillis()+".png";
						// 스트림으로 쓰는거기 때문에 파일명을 반드시 설정해줘야 하는데
						// 파싱으로 갖고온 이미지 파일의 이름을 알 수 있을 턱이 없다.
						File file = FileHelper.saveBase64File(null, imgFileName, buffer);
						// 파일 생성 구문
						contentsBuffer.append("img_\n");
						contentsBuffer.append(file.getAbsolutePath() + "\n");
					}
					buffer = "";
					tag="";
					break;
				default :
					break;
				}
				msg.put(RECEIVEDATE, System.currentTimeMillis()+"");
				try {
					eventType = xpp.next();
				} catch (XmlPullParserException xpe) {
					xpe.getMessage();
				}
			}
			return msg;
		} catch(Exception e) {
			return null;
		}
	}
	
	void getCoupon(String cid) {
		
	}
	
	void getEvent(String eid) {
		
	}

	private void certicateServer() 
	{
		Map<String, String> map = new HashMap<String, String>();
		try {
			connector.readFromURL( URL + CERTIFICATION_PAGE_NAME, util.getAccountID(), "",  map);
		} catch (IOException e) {
			Log.e(DEBUG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
