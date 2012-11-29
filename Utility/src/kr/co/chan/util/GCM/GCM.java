package kr.co.chan.util.GCM;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gcm.GCMRegistrar;

/**
 * 
 * GCM 관리 클래스
 * 각종 url과 path 정보를 멤버변수에 세팅
 * 
 * @author CINEPOX
 *
 */
public class GCM {

	private static GCM gcm;

	public static String BASE_URL = "";
	public static String REGISTER_PATH = "";
	public static String SEND_PATH = "";
	public static String UNREGISTER_PATH = "";

	public static GCM getManager(Context ctx, String senderId) {
		if (gcm == null)
			gcm = new GCM(ctx, senderId);
		return gcm;
	}

	public static void checkManager() {
		if (gcm == null)
			throw new NullPointerException("GCM 객체를 생성하고 사용하세요");
	}

	private GCM(Context ctx, String sender) {
		mContext = ctx;
		mSenderID = sender;
	}

	private Context mContext;
	private String mSenderID;

	/**
	 * 등록, 해제 관리
	 * (무조건 post로 요청)
	 * 
	 * @author CINEPOX
	 *
	 */
	public static class Registrar {

		private Callback callback;

		public void setCallback(Callback cb) {
			callback = cb;
		}

		public interface Callback {
			public void onRegistered();

			public void onRegisterFailed();

			public void onUnregistered();

			public void onUnregisterFailed();
		}

		public Registrar() {
			checkManager();
		}

		/**
		 * @param param null만 안 들어가면 됨. final이라서 안에서 생성할 수가 음슴
		 */
		public synchronized void register(final ArrayList<NameValuePair> param) {
			final String regId = GCMRegistrar.getRegistrationId(gcm.mContext);
			if (regId.equals("")) {
				GCMRegistrar.register(gcm.mContext, gcm.mSenderID);
			} else {
				if (GCMRegistrar.isRegisteredOnServer(gcm.mContext)) {
					if (callback != null)
						callback.onRegistered();
				} else {
					new AsyncTask<Void, Void, Boolean>() {

						@Override
						protected Boolean doInBackground(Void... p) {
							String serverUrl = BASE_URL + REGISTER_PATH;
							param.add(new BasicNameValuePair("registration_id",
									regId));
							for (int i = 1; i <= 5; i++) {
								l.d("Attempt #" + i + " to register");
								try {
									Util.Stream.inStreamFromURLbyPOST(
											serverUrl, param);
									GCMRegistrar.setRegisteredOnServer(
											gcm.mContext, true);
									return true;
								} catch (IOException e) {
									l.e("Failed to register on attempt " + i
											+ ":" + e);
									if (i == 5)
										break;
									try {
										l.d("Sleeping for " + 2000
												+ " ms before retry");
										Thread.sleep(2000);
									} catch (InterruptedException e1) {
										// Activity finished before we complete
										// - exit.
										l.d("Thread interrupted: abort remaining retries!");
										Thread.currentThread().interrupt();
										return false;
									}
								}
							}
							return false;
						}
						

						@Override
						protected void onPostExecute(Boolean result) {
							if (result && callback != null)
								callback.onRegistered();
							else
								callback.onRegisterFailed();
						}

					}.execute();
				}
			}
		}

		public synchronized void unregister() {
			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... p) {
					final String regId = GCMRegistrar
							.getRegistrationId(gcm.mContext);
					if ("".equals(regId))
						return false;
					l.i("unregistering device (regId = " + regId + ")");
					String serverUrl = BASE_URL + UNREGISTER_PATH;
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("registration_id", regId));
					try {
						Util.Stream.inStreamFromURLbyPOST(serverUrl, params);
						GCMRegistrar.unregister(gcm.mContext);
						setRegisteredOnServer(false);
						return true;
					} catch (IOException e) {
						return false;
					}
				}

				@Override
				protected void onPostExecute(Boolean result) {
					if (result)
						callback.onUnregistered();
					else
						callback.onUnregisterFailed();
				}

			}.execute();
		}

		public boolean isRegisteredOnGCM() {
			return GCMRegistrar.isRegistered(gcm.mContext);
		}

		public void setRegisteredOnServer(boolean flag) {
			GCMRegistrar.setRegisteredOnServer(gcm.mContext, flag);
		}

		public boolean isRegisteredOnServer() {
			return GCMRegistrar.isRegisteredOnServer(gcm.mContext);
		}
	}

	/**
	 * 메시지 객체
	 * 
	 * @author CINEPOX
	 *
	 */
	public static class Message {

		private ArrayList<NameValuePair> messageBody = new ArrayList<NameValuePair>();

		public void addData(String key, String value) {
			messageBody.add(new BasicNameValuePair(key, value));
		}
		
		public void clearData() {
			messageBody.clear();
		}

		public Message() {
			checkManager();
		}

	}

	/**
	 * 메시지 전송 객체
	 * (무조건 post로 전송됨)
	 * 
	 * @author CINEPOX
	 *
	 */
	public static class Sender {

		private Callback callback;

		public void setCallback(Callback cb) {
			callback = cb;
		}

		public interface Callback {
			public void onSendSuccessed();

			public void onSendFailed();
		}

		private String sendUrl = BASE_URL + SEND_PATH;

		public void send(Message msg) {
			try {
				Util.Stream.inStreamFromURLbyPOST(sendUrl, msg.messageBody);
				if (callback != null)
					callback.onSendSuccessed();
			} catch (Exception e) {
				if (callback != null)
					callback.onSendFailed();
				e.printStackTrace();
			}
		}

		public Sender() {
			checkManager();
		}
	}

}
