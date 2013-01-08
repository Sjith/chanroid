package utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;

public class PhoneUtils {

	/**
	 * 현재 배터리 상태를 백분율 정수로 반환
	 * 
	 * @param ctx
	 *            호출하는 액티비티
	 * @return 현재 배터리 상태 0~100 int
	 */
	public static int getBatteryLevel(Context ctx) {
		Intent bat = ctx.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		return MathUtils.getPercent(
				bat.getIntExtra(BatteryManager.EXTRA_LEVEL, 0),
				bat.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
	}

	/**
	 * 디바이스의 고유한 번호를 반환 Simid --> (없으면) deviceId --> (없으면) AndroidID
	 * 
	 * @param ctx
	 *            호출한 액티비티
	 * @return Simid 또는 deviceId 또는 AndroidID
	 */
	public static String getDeviceId(Context ctx) {
		TelephonyManager mTelManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String clientID = mTelManager.getSimSerialNumber();
		if (clientID == null || clientID.length() < 4)
			clientID = mTelManager.getDeviceId();
		if (clientID == null || clientID.length() < 4)
			clientID = android.provider.Settings.Secure.getString(
					ctx.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);
		return clientID;
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : setVolume
	 * 2. ClassName  : Phone
	 * 3. Comment   : 지정된 스트림의 볼륨을 설정함
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:08:52
	 * </PRE>
	 * 
	 * @return void
	 * @param ctx
	 * @param stream
	 * @param volume
	 * @param flags
	 */
	public static void setVolume(Context ctx, int stream, int volume,
			int flags) {
		AudioManager audioMng = (AudioManager) ctx
				.getSystemService(Context.AUDIO_SERVICE);
		audioMng.setStreamVolume(stream, volume, flags);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getVolume
	 * 2. ClassName  : Phone
	 * 3. Comment   : 지정된 스트림의 볼륨을 가져옴
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:09:08
	 * </PRE>
	 * 
	 * @return int
	 * @param ctx
	 * @param stream
	 * @return
	 */
	public static int getVolume(Context ctx, int stream) {
		AudioManager audioMng = (AudioManager) ctx
				.getSystemService(Context.AUDIO_SERVICE);
		return audioMng.getStreamVolume(stream);
	}

	/**
	 * 
	 * <PRE>
	 * 1. MethodName : getMaxVolume
	 * 2. ClassName  : Phone
	 * 3. Comment   : 지정된 스트림의 최대 볼륨을 가져옴
	 * 4. 작성자    : 박찬우
	 * 5. 작성일    : 2012. 10. 15. 오후 8:09:27
	 * </PRE>
	 * 
	 * @return int
	 * @param ctx
	 * @param stream
	 * @return
	 */
	public static int getMaxVolume(Context ctx, int stream) {
		AudioManager audioMng = (AudioManager) ctx
				.getSystemService(Context.AUDIO_SERVICE);
		return audioMng.getStreamMaxVolume(stream);
	}

	/**
	 * 해당 휴대폰의 휴대폰 번호를 가져옴. 대한민국 010 형식으로 변환하여 가져온다. "READ_PHONE_STATE" 퍼미션
	 * 필요.
	 * 
	 * @param ctx
	 *            호출한 액티비티
	 * 
	 * @return 휴대폰 번호
	 * 
	 * @throws NullPointerException
	 *             휴대폰이 아닌 장비에서 발생할 수 있음.
	 */
	public static String getPhoneNum(Context ctx) throws NullPointerException {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			return mTelephonyMgr.getLine1Number().replace("+82", "0")
					.replace("-", "");
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 현재 단말기에 등록된 통신사를 가져옴 휴대폰이 아닌 경우 null 값이 생성될 수 있으므로 적절한 예외처리 필요
	 * "READ_PHONE_STATE" 퍼미션 필요.
	 * 
	 * @param ctx
	 * @return operator 휴대폰이 아닌 장비에서 사용시 주의
	 */
	public static String getOperatorName(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();
	}
}
