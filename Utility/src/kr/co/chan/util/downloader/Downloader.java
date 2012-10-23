package kr.co.chan.util.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.chan.util.Util;
import kr.co.chan.util.l;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

@SuppressWarnings("deprecation")
public class Downloader extends AsyncTask<String, Integer, String> {

	private int mTimeout = 30000;
	// private int mBufferSize = 1024;
	private int mId = Integer.MAX_VALUE;
	private int mProgress = 101;
	private int mIcon;

	private String mPath;
	private String mUrl;
	private String mTitle;
	private Context mContext;

	private OnProgressUpdateListener listen;
	private NotificationManager mNotimanager;

	/**
	 * DownManager를 통해서만 생성할 수 있음.
	 */
	public Downloader(Context ctx, String url, String path, int id, int icon,
			String title) {
		this();
		mContext = ctx.getApplicationContext();
		mPath = path;
		mUrl = url;
		mId = id;
		mIcon = icon;
		mTitle = title;
		mNotimanager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 기본생성자 사용불가
	 */
	private Downloader() {
		super();
	}

	protected interface OnProgressUpdateListener {
		public void OnProgressUpdate(Downloader d, int progress);
	}

	public void setId(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener l) {
		listen = l;
	}

	public void start() throws IOException {
		notifyDownloadStart();
		long fileSize, remains, lenghtOfFile = 0;
		File file = new File(mPath);
		if (!file.exists())
			file.createNewFile();

		RandomAccessFile output = new RandomAccessFile(file.getAbsolutePath(),
				"rw");
		output.seek(fileSize = output.length());

		// HttpClient client = new DefaultHttpClient();
		// HttpPost post = new HttpPost(mUrl);
		// post.addHeader("Range", "bytes=" + String.valueOf(fileSize) + "-");
		// HttpResponse response = client.execute(post);
		// remains = response.getEntity().getContentLength();

		HttpURLConnection conn = (HttpURLConnection) new URL(mUrl)
				.openConnection();
		conn.addRequestProperty("Range", "bytes=" + String.valueOf(fileSize)
				+ '-');
		conn.setReadTimeout(mTimeout);
		conn.setConnectTimeout(mTimeout);
		conn.connect();
		remains = conn.getContentLength();

		lenghtOfFile = remains + fileSize;

//		l.i("filesize : " + fileSize + ", " + "remain : " + remains + ", "
//				+ "length : " + lenghtOfFile);

		if (remains < 0) {
			notifyDownloaded();
			output.close();
			conn.disconnect();
			return;
		}
		
		// InputStream input = response.getEntity().getContent();
		InputStream input = conn.getInputStream();

		if (fileSize < lenghtOfFile) {
			int readBytes = 0;
			byte data[] = new byte[1024];
			while ((readBytes = input.read(data)) != -1) {
				try {
					output.write(data, 0, readBytes);
					fileSize += readBytes;
					if (mProgress != Util.Math.getPercent(fileSize,
							lenghtOfFile))
						publishProgress(Util.Math.getPercent(fileSize,
								lenghtOfFile));
				} catch (IOException e) {
					continue;
				}
			}
		}
		
		input.close();
		output.close();
		conn.disconnect();
		input = null;
		output = null;
		conn = null;
	}

	private void notifyDownloadStart() {
		// TODO Auto-generated method stub
		h.sendEmptyMessage(1);
	}

	Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			PendingIntent intent = PendingIntent.getActivity(mContext, 0,
					new Intent(Intent.ACTION_VIEW, Uri.parse(mPath)),
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification mNoti;
			switch (msg.what) {
			case 0:
				if (Build.VERSION.SDK_INT > 13) {
					Notification.Builder mBuilder = new Notification.Builder(
							mContext);
					mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
					mBuilder.setContentTitle(mTitle);
					mBuilder.setAutoCancel(true);
					mBuilder.setSmallIcon(mIcon);
					mBuilder.setContentText("이미 다운로드가 완료된 파일입니다.");
					mBuilder.setTicker("이미 다운로드가 완료된 파일입니다.");
					mBuilder.setContentIntent(intent);
					mNoti = mBuilder.getNotification();
				} else {
					mNoti = new Notification(mIcon, "이미 다운로드가 완료된 파일입니다.",
							System.currentTimeMillis());
					mNoti.vibrate = new long[] { 0, 200, 300, 200 };
					mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
					mNoti.setLatestEventInfo(mContext, mTitle,
							"이미 다운로드가 완료된 파일입니다.", intent);
				}

				mNotimanager.notify(mId, mNoti);
				break;
			case 1:
				if (Build.VERSION.SDK_INT > 13) {
					Notification.Builder mBuilder = new Notification.Builder(
							mContext);
					mBuilder.setContentTitle(mTitle);
					mBuilder.setSmallIcon(mIcon);
					mBuilder.setContentText("다운로드를 시작하는 중입니다.");
					mBuilder.setTicker("다운로드를 시작합니다.");
					mBuilder.setContentIntent(intent);
					mNoti = mBuilder.getNotification();
				} else {
					mNoti = new Notification(mIcon, "다운로드를 시작하는 중입니다.",
							System.currentTimeMillis());
					mNoti.vibrate = new long[] { 0, 200, 300, 200 };
					mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
					mNoti.setLatestEventInfo(mContext, "다운로드를 시작하는 중입니다.",
							"다운로드를 시작합니다.", intent);
				}

				mNotimanager.notify(mId, mNoti);
				break;
			}
		}
	};

	private void notifyDownloaded() {
		// TODO Auto-generated method stub
		h.sendEmptyMessage(0);
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cancel(true);
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		mNotimanager.cancel(mId);
		showError();
	}

	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
		mNotimanager.cancel(mId);
		showError();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result == null) {
			showComplete();
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		mProgress = values[0];
		if (listen != null)
			listen.OnProgressUpdate(this, mProgress);
		if (mProgress < 100 && mProgress > 0) {
			l.i("progress : " + mProgress);
			showProgress(mProgress);
		} else if (mProgress < 0) {
			mNotimanager.cancel(mId);
			showError("이미 존재하는 파일입니다.");
		}
	}

	private void showProgress(int progress) {
		Notification mNoti;
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setProgress(100, mProgress, false);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setOngoing(true);
			mBuilder.setSmallIcon(mIcon);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification();
			mNoti.setLatestEventInfo(mContext, mTitle,
					progress + "% 다운로드 중...", PendingIntent.getActivity(
							mContext, 0,
							new Intent(Intent.ACTION_VIEW, Uri.parse(mPath)),
							PendingIntent.FLAG_UPDATE_CURRENT));
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.icon = mIcon;
			mNoti.when = System.currentTimeMillis();
		}

		mNotimanager.notify(mId, mNoti);
	}

	private void showError() {
		// TODO Auto-generated method stub
		Notification mNoti;
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentText("다운로드 중에 오류가 발생했습니다.");
			mBuilder.setTicker("다운로드 중에 오류가 발생했습니다.");
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon, "다운로드 중에 오류가 발생했습니다.",
					System.currentTimeMillis());
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.flags |= Notification.FLAG_SHOW_LIGHTS;
			mNoti.ledARGB = 0xffff0000;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.tickerText = "다운로드 중에 오류가 발생했습니다.";
			mNoti.setLatestEventInfo(mContext, mTitle, "다운로드 중에 오류가 발생했습니다.",
					PendingIntent.getActivity(mContext, 0, new Intent(
							Intent.ACTION_VIEW, Uri.parse(mPath)),
							PendingIntent.FLAG_UPDATE_CURRENT));
		}

		mNotimanager.notify(mId, mNoti);
	}

	private void showError(String e) {
		Notification mNoti;
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 200, 300, 200 });
			mBuilder.setLights(0xffff0000, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setAutoCancel(true);
			mBuilder.setContentText("다운로드 중에 오류가 발생했습니다. : " + e);
			mBuilder.setTicker("다운로드 중에 오류가 발생했습니다. : " + e);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon, "다운로드 중에 오류가 발생했습니다. : " + e,
					System.currentTimeMillis());
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.flags |= Notification.FLAG_SHOW_LIGHTS;
			mNoti.ledARGB = 0xffff0000;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.tickerText = "다운로드 중에 오류가 발생했습니다. : " + e;
			mNoti.setLatestEventInfo(mContext, mTitle, "다운로드 중에 오류가 발생했습니다. : "
					+ e, PendingIntent.getActivity(mContext, 0, new Intent(
					Intent.ACTION_VIEW, Uri.parse(mPath)),
					PendingIntent.FLAG_UPDATE_CURRENT));
		}

		mNotimanager.notify(mId, mNoti);
	}

	private void showComplete() {
		// TODO Auto-generated method stub
		PendingIntent intent = PendingIntent.getActivity(mContext, 0,
				new Intent(Intent.ACTION_VIEW, Uri.parse(mPath)),
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification mNoti;
		if (Build.VERSION.SDK_INT > 13) {
			Notification.Builder mBuilder = new Notification.Builder(mContext);
			mBuilder.setVibrate(new long[] { 0, 1000 });
			mBuilder.setLights(0xff00ff00, 500, 500);
			mBuilder.setContentTitle(mTitle);
			mBuilder.setSmallIcon(mIcon);
			mBuilder.setContentText("다운로드가 완료되었습니다.");
			mBuilder.setTicker("다운로드가 완료되었습니다.");
			mBuilder.setAutoCancel(true);
			mBuilder.setContentIntent(intent);
			mNoti = mBuilder.getNotification();
		} else {
			mNoti = new Notification(mIcon, "다운로드가 완료되었습니다.",
					System.currentTimeMillis());
			mNoti.vibrate = new long[] { 0, 1000 };
			mNoti.ledARGB = 0xff00ff00;
			mNoti.ledOffMS = 500;
			mNoti.ledOnMS = 500;
			mNoti.flags |= Notification.FLAG_AUTO_CANCEL;
			mNoti.setLatestEventInfo(mContext, mTitle, "다운로드가 완료되었습니다.", intent);
		}

		mNotimanager.notify(mId, mNoti);
	}

}
