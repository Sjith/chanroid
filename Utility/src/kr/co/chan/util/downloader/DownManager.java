package kr.co.chan.util.downloader;

import java.util.ArrayList;

import kr.co.chan.util.downloader.Downloader.OnProgressUpdateListener;
import android.content.Context;

public class DownManager implements OnProgressUpdateListener {
	
	private static DownManager instance;
	
	private Context mContext;
	
	private DownManager(Context ctx) {
		mContext = ctx.getApplicationContext();
	}
	
	public static DownManager getInstance(Context ctx) {
		if (instance == null)
			instance = new DownManager(ctx);
		return instance;
	}
	
	public interface OnProgressUpdateListener {
		/**
		 * @param id
		 *            Downloader Object's Unique id.
		 * @param progress
		 *            Download progress.
		 */
		public void OnProgressUpdate(int id, int progress);
	}
	
	private ArrayList<Downloader> mQueueStack = new ArrayList<Downloader>();
	private OnProgressUpdateListener listen;
	
	public void setCallBack(OnProgressUpdateListener l) {
		listen = l;
	}
	 
	public int queue(String url, String path, int icon, String title) {
		final int id = mQueueStack.size();
		Downloader down = new Downloader(mContext, url, path, id, icon, title);
		down.setOnProgressUpdateListener(this);
		mQueueStack.add(down);
		down.execute();
		return id;
	}
	
	public Downloader get(int id) {
		return mQueueStack.get(id);
	}

	@Override
	public void OnProgressUpdate(Downloader d, int progress) {
		// TODO Auto-generated method stub
		if (listen != null) {
			listen.OnProgressUpdate(d.getId(), progress);
		}
	}
	
}
