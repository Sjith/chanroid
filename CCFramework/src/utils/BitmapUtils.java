package utils;

import java.io.DataInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	/**
	 * 지정된 url에서 이미지를 가져옴
	 * 
	 * @param urls
	 *            가져올 이미지의 url
	 * 
	 * @return 가져온 이미지
	 * 
	 * @throws IOException
	 */
	public static Bitmap bitmapFromURL(String bitmapUrl,
			BitmapFactory.Options opt) throws IOException {
		DataInputStream bis = new DataInputStream(StreamUtils.inStreamFromURL(
				bitmapUrl, null));
		Bitmap b;
		if (opt != null)
			b = BitmapFactory.decodeStream(bis, null, opt);
		else
			b = BitmapFactory.decodeStream(bis);
		bis.close();
		return b;
	}

	/**
	 * 원본 비트맵 위에 다른 비트맵을 겹침 두 비트맵의 크기가 같거나 비슷할 것을 권장 두 비트맵의 크기가 다를 경우 원본 비트맵에
	 * 크기가 맞춰짐
	 * 
	 * @param bmp1
	 *            원본 비트맵
	 * @param bmp2
	 *            원본 위에 겹쳐질 비트맵
	 * 
	 * @return 겹쳐진 비트맵
	 */
	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
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
}
