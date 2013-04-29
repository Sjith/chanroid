package com.fedorvlasov.lazylist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public abstract class AbsImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context ctx;
	ImageLoaderCallback callback;
	private Options bitmapOption;
	private int roundRadius = 0;

	public void setCallback(ImageLoaderCallback callback) {
		this.callback = callback;
	}

	public AbsImageLoader(Context context) {
		ctx = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		bitmapOption = new BitmapFactory.Options();
	}

	/**
	 * 비트맵의 모서리를 둥글게 처리
	 * 
	 * @param x
	 *            둥글게 처리할 비트맵
	 * @param xradius
	 * @param yradius
	 * 
	 * @return 둥글게 처리된 비트맵
	 */
	public static Bitmap round(Bitmap x, int xradius, int yradius) {
		Bitmap output = Bitmap.createBitmap(x.getWidth(), x.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242; // 투명
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, x.getWidth(), x.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, xradius, yradius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(x, rect, rect, paint);
		x.recycle();
		return output;
	}

	public void DisplayImage(String url, ImageView imageView) {
		if (callback != null)
			callback.onStart(this);
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			if (callback != null)
				callback.onLoad(this);
		} else {
			imageView.setImageBitmap(null);
			queuePhoto(url, imageView);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void setBitmapOption(BitmapFactory.Options opt) {
		bitmapOption = opt;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			return BitmapFactory.decodeStream(new FileInputStream(f), null,
					bitmapOption);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);

			// 비트맵 조작 처리
			if (roundRadius > 0)
				bmp = round(bmp, roundRadius, roundRadius);

			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				if (callback != null)
					callback.onLoad(AbsImageLoader.this);
				photoToLoad.imageView.setImageBitmap(bitmap);
				try {
					if (getLoadAnimationResources() != 0)
						photoToLoad.imageView
								.startAnimation(AnimationUtils.loadAnimation(
										ctx, getLoadAnimationResources()));
				} catch (Exception e) {
				}
			} else {
				if (callback != null)
					callback.onError(AbsImageLoader.this, "");
				try {
					if (getLoadAnimationResources() != 0)
						photoToLoad.imageView
								.startAnimation(AnimationUtils.loadAnimation(
										ctx, getOutAnimationResources()));
				} catch (Exception e) {
				}
				photoToLoad.imageView.setImageBitmap(null);
			}
		}
	}

	public void clearCache() {
		fileCache.clear();
	}

	public void clearMemory() {
		memoryCache.clear();
	}

	public abstract int getLoadAnimationResources();

	public abstract int getOutAnimationResources();

	public void setScale(int scale) {
		bitmapOption.inSampleSize = scale;
	}

	public int getScale() {
		return bitmapOption.inSampleSize;
	}

	public void setRoundRadius(int radius) {
		roundRadius = radius;
	}

}
