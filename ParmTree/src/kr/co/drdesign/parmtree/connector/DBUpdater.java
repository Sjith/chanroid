package kr.co.drdesign.parmtree.connector;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.database.CouponController;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.database.EventController;
import kr.co.drdesign.parmtree.util.ParmUtil;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * 
 * @author brake-_-
 * ���� �ð����� Ȥ��
 * ������ ��û�� ������ �����ͺ��̽��� ����ȭ�Ѵ�.
 *
 *
 */
public class DBUpdater extends Thread implements c {

	ParmUtil util;
	Context ctx = ParmUtil.ctx;

	String UpdateUrl; // ������ url. ���ͷ� �޾ƾ� �Ѵ�.
	String userid;
	String password;
	int work; // �۾� ���й�ȣ
	int id; // �۾��� �ʿ��� ���̵�
	
	// �޽��� ������ ������ �����尡 ������.
	public static final int LOAD_COUPON = 2222;
	public static final int LOAD_EVENT = 3333;
	public static final int LOAD_ESTLIST = 5555;
	public static final int LOAD_LASER = 6666;
	public static final int LOAD_HOTEL = 7777;
	public static final int LOAD_USER = 8888;
	public static final int UPDATE_ALL = 9999;
	
	boolean useSSL = false;
	WebConnector connector;
	SharedPreferences mPref;
	
	CouponController cpCtrl;
	EstablishController estCtrl;
	EventController eventCtrl;
	// ���� ��������.


	public DBUpdater() {
		this.userid = util.getAccountID();
		this.password = util.getPassWord();
		util = ParmUtil.getInstance(ctx);

		if ( mPref == null ) mPref = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	void setUrl(String url) {
		if (url != null) this.UpdateUrl 	= url;	
		else return;
		if (url.contains("https")) setSSL(true);
		if( useSSL ) {
			connector = new HttpsConnector();
		} else {
			connector = new HttpConnector();
		}
	}

	void setSSL(boolean usessl) {
		this.useSSL = usessl;
		if( useSSL ) {
			connector = new HttpsConnector();
		} else {
			connector = new HttpConnector();
		}
	}

	void setWork(int work, int id) {
		this.work = work;
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// �Ľ�
		switch (work) {
		case LOAD_COUPON :
			loadCoupon(id);
			break;
		case LOAD_EVENT :
			loadEvent(id);
			break;
		case LOAD_ESTLIST :
			loadEst();
			break;
		case LOAD_LASER :
			loadLaser();
			break;
		case LOAD_HOTEL :
			loadHotel();
			break;
		case LOAD_USER :
			loadUser(id); 
			// �����ϸ� �ȵǰ� �׶��׶� �����;� �ɰ� ������ ���� �������� �̻��ϴ�.
		case UPDATE_ALL :
			loadEst();
			loadLaser();
			loadHotel();
		default :
			break;
		}
	}
	
	void loadCoupon(int cid) {
		// �޽����� �����͸� ����´�.
	}
	
	void loadEvent(int eid) {
		eventCtrl.insert(eid);
		// �� ���� �̺�Ʈ�� �ִ����� �־���.
	}
	
	void loadUser(int uid) {
		// ������ ������ ������ ���ο� �����´�.
	}
	
	void loadEst() {
		// 1�� - ��ü������ �α׸� ������Ʈ.
		// 2�� - ��ü ���Կ��� ���̺� ������Ʈ.
	}
	
	void loadLaser() {
		// ���� ������ �α׸� ������Ʈ.
	}
	
	void loadHotel() {
		// ���ھ��� ������ �α׸� ������Ʈ.
	}

	private void insert() {

		String newMsgURL = UpdateUrl;
		Map<String, String> parameter = new HashMap<String, String>();

		CharSequence text = "";
		try{
			text = connector.readFromURL(newMsgURL, userid, password, parameter);
			// ����� ����Ʈ���� ���ִ� xml�� ���´�.
		}catch( Exception ioe)
		{
			return;
		}
		if ( text == null ) return;

		StringReader br = new StringReader(text.toString());
		ArrayList<Map<String,String>> msg = parse(br);
		
		for (int i = 0; i < msg.size(); i++) {
			Map<String,String> map = msg.get(i);
			
			switch (work) {
			case LOAD_ESTLIST :
				estCtrl.insert(map);
			case LOAD_COUPON :
				cpCtrl.insert(map);
				break;
			case LOAD_EVENT :
				eventCtrl.insert(id);
				break;
			case LOAD_HOTEL :
//				hotelCtrl.insert(map);
				break;
			case LOAD_LASER :
//				laserCtrl.insert(map);
				break;
			}
			
		}
		
	}
	
	private ArrayList<Map<String,String>> parse(Reader r) {
		ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true); 
			XmlPullParser xpp = factory.newPullParser(); 
			xpp.setInput(r); 
			

			String tag = null, contents = "";
			int eventType = xpp.getEventType();
			
			while ( eventType != XmlPullParser.END_DOCUMENT ) {
				switch ( eventType ) {
				case XmlPullParser.START_DOCUMENT :
					break;
				case XmlPullParser.START_TAG :
					tag = xpp.getName().trim();
					l.s("xml tag : " + tag);
					break;
				case XmlPullParser.TEXT :
					contents = xpp.getText();
					l.s("xml text : " + contents);
					break;
				case XmlPullParser.END_TAG :
					Map<String,String> map = new HashMap<String,String>();
					if (tag == "sender") map.put(SENDER, contents);
					if (tag == "receiver") map.put(RECEIVER, contents);
					if (tag == "contents") map.put(CONTENTS, contents);
					if (tag == "image") map.put(IMAGE, contents);
					result.add(map);
					tag = ""; contents = "";
					break;
				case XmlPullParser.END_DOCUMENT :
					break;
				default :
					break;
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		
		return null;		
	}


}
