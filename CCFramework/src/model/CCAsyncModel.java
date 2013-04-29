package model;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.os.AsyncTask;

/**
 * 요청값 및 결과값으로 구조체를 사용하여<br>
 * 스레드로 동작하는 모델을 추상화한 클래스 <br>
 * <br>
 * 용도: Http, FTP 등의 네트워크 작업,<br>
 * SQLite 데이터베이스, 비트맵처리 등의 백그라운드 작업
 * 
 * @author chanroid
 * 
 */
public abstract class CCAsyncModel extends CCModel {

	private AsyncModelSync currentThread;
	private Result currentResult;

	/**
	 * <b>메인스레드에서 실행됨.</b> non-blocking 호출 <br>
	 * loadData 메서드를 실행하는 스레드를 시작시키는 메서드
	 * 
	 * @param param
	 *            구조체 또는 단일 객체
	 * @param callback
	 *            실행 경과를 통보받을 콜백
	 */
	public void loadDataAsync(Object param, Callback callback) {
		currentThread = new AsyncModelSync(callback);
		currentThread.execute(param);
	}

	/**
	 * <b>백그라운드 스레드</b>에서 실행되는 동작들을 기술하는 메서드<br>
	 * 호출한 스레드에서 실행되는 blocking 호출<br>
	 * loadDataAsync() 에서 호출하면 백그라운드에서 동작
	 * 
	 * @param param
	 *            구조체 또는 단일 객체
	 * @param callback
	 *            실행 경과를 통보받을 콜백
	 * 
	 * @return 결과값 구조체 또는 단일 객체
	 */
	public abstract Result loadData(Object param) throws MalformedURLException,
			IOException, JSONException;

	/**
	 * 해당 스레드의 동작을 취소함
	 */
	public void cancel() {
		if (currentThread != null
				&& currentThread.getStatus() == AsyncTask.Status.RUNNING)
			currentThread.cancel(true);
	}

	public BasicNameValuePair value(String key, String value) {
		return new BasicNameValuePair(key, value);
	}

	public BasicNameValuePair value(String key, int value) {
		return new BasicNameValuePair(key, String.valueOf(value));
	}

	public void publishProgress(int progress) {
		if (currentThread != null
				&& currentThread.getStatus() == AsyncTask.Status.RUNNING)
			currentThread.publishProgress(progress);
	}

	public Result getCurrentResult() {
		return currentResult;
	}

	public static class Result {
		/**
		 * 에러나 익셉션 발생시 여기다 그대로 세팅
		 * 
		 */
		public Exception error;

		/**
		 * 콜백으로 뭔가 넘겨야 할 객체가 있으면 여기다가 세팅
		 */
		public Object result;
	}

	public static class SimpleCallback implements Callback {

		@Override
		public void onPreExecute(CCAsyncModel model) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressUpdated(CCAsyncModel model, int progress) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPostExecute(CCAsyncModel model, Result result) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCanceled(CCAsyncModel model, Result result) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object preProcess(Object param) {
			// TODO Auto-generated method stub
			return param;
		}

		@Override
		public Result postProcess(Result result) {
			// TODO Auto-generated method stub
			return result;
		}

	}

	public static interface Callback {
		public void onPreExecute(CCAsyncModel model);

		/**
		 * 백그라운드 작업 중 이벤트 발생시<br>
		 * UI 스레드에서 필요한 처리 수행
		 * 
		 * @param model
		 * @param progress
		 */
		public void onProgressUpdated(CCAsyncModel model, int progress);

		/**
		 * 작업이 완료된 후<br>
		 * UI 스레드에서 필요한 처리 수행
		 * 
		 * @param model
		 * @param result
		 */
		public void onPostExecute(CCAsyncModel model, Result result);

		public void onCanceled(CCAsyncModel model, Result result);

		/**
		 * 백그라운드 동작을 수행하기 전에<br>
		 * 요청값에 필요한 처리 수행
		 * 
		 * @param param
		 *            loadData
		 * @return
		 */
		public Object preProcess(Object param);

		/**
		 * 결과값을 반환하기 전에<br>
		 * 백그라운드 스레드에서 필요한 처리 수행
		 * 
		 * @param result
		 * @return
		 */
		public Result postProcess(Result result);
	}

	private class AsyncModelSync extends AsyncTask<Object, Integer, Result> {

		private Callback callback;

		private AsyncModelSync(Callback callback) {
			this.callback = callback;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (callback != null)
				callback.onPreExecute(CCAsyncModel.this);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (callback != null)
				callback.onProgressUpdated(CCAsyncModel.this, values[0]);
		}

		public void publishProgress(int progress) {
			publishProgress(progress);
		}

		@Override
		protected Result doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Object param = params[0];
			if (callback != null)
				param = callback.preProcess(param);

			Result result = new Result();
			try {
				result = loadData(param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result.error = e;
			}

			if (callback != null)
				result = callback.postProcess(result);

			currentResult = result;
			return result;
		}

		@Override
		protected void onCancelled(Result result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if (callback != null)
				callback.onCanceled(CCAsyncModel.this, result);
		}

		@Override
		protected void onPostExecute(Result result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (callback != null)
				callback.onPostExecute(CCAsyncModel.this, result);
		}
	}
}
