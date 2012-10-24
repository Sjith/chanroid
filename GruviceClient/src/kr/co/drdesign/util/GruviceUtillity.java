package kr.co.drdesign.util;

import kr.co.drdesign.client.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GruviceUtillity {

	private static GruviceUtillity gUtil;
	private static Context ctx;

	private static SharedPreferences mPref;
	private static Editor mEditor;

	// Array of SKIN TYPE
	private static String[] SKIN_TYPES ;
	// DEFAULT SKIN TYPE = Now, "Gruvice2"
	private static String DEF_SKIN;
	
	private static String[] CONNECTION_TYPES;
	public static final String SKIN_TYPE 	= "SkinType";
	public static final String CONNECTION_TYPE = "ConnectionType";
	public static final String LINE_NUMBER 	= "LINE_NUMBER";
	public static final String CLIENT_ID 	= "CLIENT_ID";
	public static final String REGISTRATION = "REGISTRATION";
	public static final String Email = "Email";
	public static final String PASSWORD = "PASSWORD";
	public static final String CONNECTION_NOTI = "ConnectionNoti";

	public static final String DATA_STORAGE	= android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/.dataStorage/";
	public static final String TEMP_STORAGE = DATA_STORAGE + "temp/";
	
	private static String lineNumber;
	private static String clientID;
	
	private static Boolean isRegistration;

	// 자신의 생성자 호출
	private GruviceUtillity(Context context)
	{
		// 저장된 설정값을 불러오고 편집 가능한 상태로 재생성
		ctx = context.getApplicationContext();
		mPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		mEditor = mPref.edit();

		SKIN_TYPES = ctx.getResources().getStringArray(R.array.SkinTypeValues);
		if( SKIN_TYPES.length >= 2 )DEF_SKIN = SKIN_TYPES[1];
		else DEF_SKIN = SKIN_TYPES[0];
		
		CONNECTION_TYPES = ctx.getResources().getStringArray(R.array.ConnectionTypeValues);
		if ( CONNECTION_TYPES.length >= 3 ) {
		}
		
	}

	// 싱글톤 패턴으로 구현(한 객체만 생성)
	// 추상클래스화하여 이 메서드로만 클래스를 생성함
	public static GruviceUtillity getInstance( Context context )
	{
		if ( gUtil == null ) 
			gUtil = new GruviceUtillity(context);
		return gUtil;
	}

	// number of Phone, National Code is replaced '0';
	public String getPhoneNumber()
	{
		if(lineNumber != null ) return lineNumber;

		lineNumber = mPref.getString(LINE_NUMBER, "");
		if(lineNumber.equals("") == false ) return lineNumber;

		// 해당 액티비티에서 사용될 telephonymanager 를 생성
		try {
			TelephonyManager telephony = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			lineNumber = (telephony != null)? telephony.getLine1Number():"Default Value";
			lineNumber = lineNumber.replace("+82", "0");
			Log.e("Gruvice", "lineNumber : " + lineNumber);
		} catch (NullPointerException e){
			// 2011-06-08 폰이 아닌 기기에서는 익셉션이 발생하므로 다른 방식으로 아이디를 가져와야 한다.
			// 아니면 서버에서 요청하여 아이디에 맞는 전화번호를 가져오던지, 사용자가 직접 입력하게 해야 한다.
			return mPref.getString(LINE_NUMBER, "");
		}
		// line_number 으로 지정된 속성값을 폰번호로 변경함
		mEditor.putString(LINE_NUMBER, lineNumber);
		Log.i("number", lineNumber);
		mEditor.commit();

		return lineNumber;
	}
	
	public void setPhoneNumber(String phone) {
		if (phone.length() > 10) {
			Log.i("Gruvice", "setPhoneNum");
			mEditor.putString(LINE_NUMBER, phone);
			mEditor.commit();
		}
	}

	//return 하는 순서는 Simid --> deviceId --> AndroidID
	public String getClientId()
	{
		if(clientID != null ) return clientID;

		clientID = mPref.getString(CLIENT_ID, "");
		if(clientID.equals("") == false ) return clientID;

		// 해당 액티비티에서 사용될 telephonymanager 를 생성
		TelephonyManager mTelManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

		// 단말기의 sim number 로드
		clientID	= mTelManager.getSimSerialNumber();
		if( clientID == null || clientID.length() < 4)	clientID	= mTelManager.getDeviceId();
		if( clientID == null || clientID.length() < 4)	clientID 	= android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		// client_id 로 지정된 속성값을 sim number 로 변경
		mEditor.putString(CLIENT_ID, clientID);
		mEditor.commit();

		return clientID;
	}

	// skin_type 로 지정된 속성값 리턴
	public String getSkinType(){
		return mPref.getString(SKIN_TYPE, DEF_SKIN);
	}

	// 스킨 타입 종류 리턴
	public String[] getSKIN_TYPES()
	{
		return SKIN_TYPES;
	}
	
	public String getConnectionType() {
		return mPref.getString(CONNECTION_TYPE, "3G / WI-FI");
	}
	
	public String[] getConnectionTypes() {
		return CONNECTION_TYPES;
	}

	// REGISTRATION 으로 지정된 속성값 리턴
	public boolean isRegistration(){
		if(isRegistration != null ) return isRegistration;
		isRegistration = mPref.getBoolean(REGISTRATION, false);
		return isRegistration;
	}
	
	// REGISTRATION 의 설정값 입력
	public void setRegistration(boolean registration){
		isRegistration = registration;
		mEditor.putBoolean(REGISTRATION, registration);
		mEditor.commit();
	}
	
	public void setEmail(String email) {
		mEditor.putString(Email, email);
		mEditor.commit();
	}
	
	public String getEmail() {		
		return mPref.getString(Email, "");		
	}
	
	public void setPassword(String password) {
		mEditor.putString(PASSWORD, password);
		mEditor.commit();
	}
	
	public String getPassword() {		
		return mPref.getString(PASSWORD, "");		
	}
	
	// 연결상태 알림 설정값 입력
	public boolean isActiveConnectionNoti()
	{
		mPref.getBoolean(CONNECTION_NOTI, false);
		return false;
	}
}
