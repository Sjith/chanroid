package kr.co.drdesign.parmtree.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import kr.co.drdesign.parmtree.database.CouponController;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.database.EventController;
import kr.co.drdesign.parmtree.database.FavoriteController;
import kr.co.drdesign.parmtree.database.HotelController;
import kr.co.drdesign.parmtree.database.ReceivedMsgController;
import kr.co.drdesign.parmtree.database.SendedMsgController;
import kr.co.drdesign.parmtree.database.WorkerController;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class ParmUtil {

	/*
	 * static variable
	 */

	static ParmUtil util;
	public static Context ctx;

	static SharedPreferences mPref;
	static Editor mEditor;

	static String PHONENO;
	static String CLIENTID;

	/*
	 * database controller. these all static class. need allocate on start.
	 */

	CouponController cpCtrl;
	EstablishController estCtrl;
	FavoriteController favoCtrl;
	ReceivedMsgController rmgCtrl;
	SendedMsgController smgCtrl;
	WorkerController workCtrl;
	HotelController hotelCtrl;
	EventController eventCtrl;

	/*
	 * final variable
	 */

	public static final String GPSTYPE = "GPSType";
	public static final String AR = "AutoRefreshLocation";
	public static final String GPSSET = "GPSSetting";
	public static final String SATELLITE = "SatelliteSetting";
	public static final String PUSH = "EnabledPush";
	public static final String REGION = "FavoriteRegion";

	public static final String REGISTRATION = "Registration";
	public static final String ACCOUNT = "Account";
	public static final String ACCOUNTID = "Account_id";
	public static final String PASSWORD = "Password";

	public static final String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/.ParmTree/";
	public static final String TEMP = PATH + "temp/";

	/*
	 * constructor
	 */

	private ParmUtil(Context c) {
		ctx = c;
		mPref = PreferenceManager.getDefaultSharedPreferences(c);
		mEditor = mPref.edit();

		// 스태틱 클래스들을 죄다 올려버린다.
		// 최초 실행시 db 파일 생성 작업을 거치므로
		// 성능상 문제가 발생할때는 Thread로 처리해주는 구문이 필요하다.
		cpCtrl = CouponController.getInstance(c);
		estCtrl = EstablishController.getInstance(c);
		favoCtrl = FavoriteController.getInstance(c);
		rmgCtrl = ReceivedMsgController.getInstance(c);
		smgCtrl = SendedMsgController.getInstance(c);
		workCtrl = WorkerController.getInstance(c);
		hotelCtrl = HotelController.getInstance(c);
		eventCtrl = EventController.getInstance(c);

		// 임시파일이 저장될 폴더를 생성하고, Provider에서 검색되지 않도록 .nomedia 파일을 생성한다.
		// 단말기에 따라 파일 생성 속도가 느릴 수 있다.
		// 성능상 문제가 발생할 때는 Thread로 처리해 주는 구문이 필요하다.
		File tempDir = new File(TEMP);
		if (!tempDir.isDirectory()) {
			tempDir.mkdirs();
			File nomedia = new File(TEMP, ".nomedia");
			if (!nomedia.exists())
				try {
					nomedia.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					l.s('e', "IOException in ParmUtil constructor.");
				}
		}
	}

	public static ParmUtil getInstance(Context c) {
		if (util == null)
			util = new ParmUtil(c);
		return util;
	}

	public static Bitmap getImageURL(String path) {
		try {
			URL url = new URL(path);
			FlushedInputStream is = new FlushedInputStream(url.openStream());
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
			is.close();
			return bm;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * setters
	 */

	void commit() {
		mEditor.commit();
	}

	public void setPassWord(String pw) {
		// 넣기 전에 값 체크를 해야 한다.
		mEditor.putString(PASSWORD, pw);
		commit();
	}

	public void setAccountName(String id) {
		// 넣기 전에 값 체크를 해야 한다.
		mEditor.putString(ACCOUNT, id);
		commit();
	}

	public void setAccountId(int mid) {
		mEditor.putInt(ACCOUNTID, mid);
		commit();
	}

	public void setPhoneNo(String phone) {
		// 넣기 전에 값 체크를 해야 한다.
		mEditor.putString(c.PHONENO, phone);
		commit();
	}

	/*
	 * getters (static)
	 */

	public String getPhoneNo() {
		try {
			TelephonyManager tel = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			PHONENO = (tel != null) ? tel.getLine1Number() : "Default Value"
					.replace("+82", "0");
		} catch (NullPointerException e) {
			return mPref.getString(c.PHONENO, null);
		}
		l.s("PhoneNo : " + PHONENO);
		return PHONENO;
	}
	
	public static float getDistanceBetween(double startlat, double startlng, double endlat, double endlng) {
		float[] results = new float[]{};
		Location.distanceBetween(startlat, startlng, endlat, endlng, results);
		return results[0];
	}

	public String getDeviceId() {

		TelephonyManager tel = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		CLIENTID = tel.getSimSerialNumber();
		if (CLIENTID == null || CLIENTID.length() < 4)
			CLIENTID = tel.getDeviceId();
		if (CLIENTID == null || CLIENTID.length() < 4)
			CLIENTID = Secure.getString(ctx.getContentResolver(),
					Secure.ANDROID_ID);

		return CLIENTID;
	}

	/*
	 * preference check methods (static, getter)
	 */

	public String getPassWord() {
		return mPref.getString(PASSWORD, null);
	}

	public String getAccountName() {
		return mPref.getString(ACCOUNT, null);
	}

	public String getAccountID() {
		return mPref.getString(ACCOUNTID, "0");
	}

	public String getGPSSensitive() {
		return mPref.getString(GPSTYPE, "1");
	}

	public String getFavoriteRegion() {
		return mPref.getString(REGION, "서울");
	}

	public boolean isRegistration() {
		return mPref.getBoolean(REGISTRATION, false);
	}

	public boolean isCheckGPS() {
		return mPref.getBoolean(GPSSET, false);
	}

	public boolean isActivePush() {
		return mPref.getBoolean(PUSH, false);
	}

	public boolean isAR() {
		return mPref.getBoolean(AR, false);
	}

	public boolean isSatellite() {
		return mPref.getBoolean(SATELLITE, false);
	}

}
