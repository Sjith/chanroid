package kr.co.chunhoshop.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class ParserUtil implements ParserTag {

	public static Bitmap getImageURL(String path) {

		try {
			URL url = new URL(PAGE + path);
			Log.i("Chunho", url.toString());
			URLConnection conn = url.openConnection();
			conn.connect();
			FlushedInputStream bis = new FlushedInputStream(
					conn.getInputStream());
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 2;
			Bitmap b = BitmapFactory.decodeStream(bis, null, opt);
			Bitmap bm = ChunhoUtil.gerRoundBitmap(b);
			bis.close();
			b.recycle();
			return bm;
		} catch (Exception e) {
			return null;
		}
	}
	

	public static Bitmap getImageURLAbsolutePath(String path) {
		try {
			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			conn.connect();
			FlushedInputStream bis = new FlushedInputStream(
					conn.getInputStream());
			Bitmap b = BitmapFactory.decodeStream(bis);
			Bitmap bm = ChunhoUtil.gerRoundBitmap(b);
			bis.close();
			b.recycle();
			return bm;
		} catch (Exception e) {
			return null;
		}
	}

	public static Bitmap getImageURLNonSampling(String path) {
		try {
			URL url = new URL(PAGE + path);
			URLConnection conn = url.openConnection();
			conn.connect();
			FlushedInputStream bis = new FlushedInputStream(
					conn.getInputStream());
			Bitmap b = BitmapFactory.decodeStream(bis);
			Bitmap bm = ChunhoUtil.gerRoundBitmap(b);
			bis.close();
			b.recycle();
			return bm;
		} catch (Exception e) {
			return null;
		}
	}

	public static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);

				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break;
					} else {
						bytesSkipped = 1;
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static Bitmap overlayMark(Bitmap bmp1, Bitmap bmp2) {
		if (bmp1 == null) return null;
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Bitmap bmOrign = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth(),
				bmp1.getHeight(), true);
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, 0, 0, null);
		canvas.drawBitmap(bmOrign, 0, 0, null);
		bmp1.recycle();
		bmp2.recycle();
		bmOrign.recycle();
		return bmOverlay;
	}

}
