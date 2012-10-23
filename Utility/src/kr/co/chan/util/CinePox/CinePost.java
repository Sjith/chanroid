package kr.co.chan.util.CinePox;

import java.io.IOException;
import java.util.ArrayList;

import kr.co.chan.util.Util;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public final class CinePost extends AsyncTask<String, Integer, String> {

	private StringBuilder sb;
	private ArrayList<NameValuePair> param;

	private OnStartListener start;
	private OnProgressListener progress;
	private OnCanceledListener cancel;
	private OnCompleteListener complete;

	public final void setParam(ArrayList<NameValuePair> param) {
		this.param = param;
	}

	public final ArrayList<NameValuePair> getParam() {
		return param;
	}

	private void clearInfo() {
		sb = null;
	}

	public final StringBuilder getJSONText() {
		return sb;
	}

	public final JSONObject getJSONObject() {
		try {
			return new JSONObject(sb.toString());
		} catch (JSONException e) {
			// new LogPost(e).appendMessage("Error String : " +
			// sb.toString()).start();
			return new JSONObject();
		}
	}

	public final void setOnStartListener(OnStartListener listen) {
		start = listen;
	}

	public final void setOnCompleteListener(OnCompleteListener listen) {
		complete = listen;
	}

	public final void setOnCanceledListener(OnCanceledListener listen) {
		cancel = listen;
	}

	public final void setOnProgressListener(OnProgressListener listen) {
		progress = listen;
	}

	public interface OnStartListener {
		public void OnStart(CinePost cp);
	}

	public interface OnCompleteListener {
		public void OnComplete(CinePost cp, StringBuilder sb);
	}

	public interface OnCanceledListener {
		public void onCancel(CinePost cp);

		public void onCancel(CinePost cp, String result);
	}

	public interface OnProgressListener {
		public void onProgress(CinePost cp, int progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (start != null)
			start.OnStart(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... baseurl) {
		// TODO Auto-generated method stub
		if (baseurl[0] == null)
			return null;
		try {
			this.sb = new StringBuilder(Util.Stream.stringFromURLbyPOST(
					baseurl[0], param));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if (progress != null)
			progress.onProgress(this, values[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		if (cancel != null) {
			cancel.onCancel(this);
			clearInfo();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		if (cancel != null) {
			cancel.onCancel(this, result);
			clearInfo();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (result == null) {
			try {
				if (sb == null)
					return;
				if ("N".equalsIgnoreCase(getJSONObject().getString("result"))) {
					if (cancel != null)
						cancel.onCancel(this, getJSONObject().getString("msg"));
					return;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (cancel != null)
					cancel.onCancel(this, e.getMessage());
				return;
			}
			if (complete != null)
				complete.OnComplete(this, sb);
		} else {
			if (cancel != null)
				cancel.onCancel(this, result);
		}

	}

}
