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

	// �ڽ��� ������ ȣ��
	private GruviceUtillity(Context context)
	{
		// ����� �������� �ҷ����� ���� ������ ���·� �����
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

	// �̱��� �������� ����(�� ��ü�� ����)
	// �߻�Ŭ����ȭ�Ͽ� �� �޼���θ� Ŭ������ ������
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

		// �ش� ��Ƽ��Ƽ���� ���� telephonymanager �� ����
		try {
			TelephonyManager telephony = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			lineNumber = (telephony != null)? telephony.getLine1Number():"Default Value";
			lineNumber = lineNumber.replace("+82", "0");
			Log.e("Gruvice", "lineNumber : " + lineNumber);
		} catch (NullPointerException e){
			// 2011-06-08 ���� �ƴ� ��⿡���� �ͼ����� �߻��ϹǷ� �ٸ� ������� ���̵� �����;� �Ѵ�.
			// �ƴϸ� �������� ��û�Ͽ� ���̵� �´� ��ȭ��ȣ�� ����������, ����ڰ� ���� �Է��ϰ� �ؾ� �Ѵ�.
			return mPref.getString(LINE_NUMBER, "");
		}
		// line_number ���� ������ �Ӽ����� ����ȣ�� ������
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

	//return �ϴ� ������ Simid --> deviceId --> AndroidID
	public String getClientId()
	{
		if(clientID != null ) return clientID;

		clientID = mPref.getString(CLIENT_ID, "");
		if(clientID.equals("") == false ) return clientID;

		// �ش� ��Ƽ��Ƽ���� ���� telephonymanager �� ����
		TelephonyManager mTelManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

		// �ܸ����� sim number �ε�
		clientID	= mTelManager.getSimSerialNumber();
		if( clientID == null || clientID.length() < 4)	clientID	= mTelManager.getDeviceId();
		if( clientID == null || clientID.length() < 4)	clientID 	= android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		// client_id �� ������ �Ӽ����� sim number �� ����
		mEditor.putString(CLIENT_ID, clientID);
		mEditor.commit();

		return clientID;
	}

	// skin_type �� ������ �Ӽ��� ����
	public String getSkinType(){
		return mPref.getString(SKIN_TYPE, DEF_SKIN);
	}

	// ��Ų Ÿ�� ���� ����
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

	// REGISTRATION ���� ������ �Ӽ��� ����
	public boolean isRegistration(){
		if(isRegistration != null ) return isRegistration;
		isRegistration = mPref.getBoolean(REGISTRATION, false);
		return isRegistration;
	}
	
	// REGISTRATION �� ������ �Է�
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
	
	// ������� �˸� ������ �Է�
	public boolean isActiveConnectionNoti()
	{
		mPref.getBoolean(CONNECTION_NOTI, false);
		return false;
	}
}
