package kr.co.chunhoshop.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ChunhoUtil {

	/**
	 * thanks to 그냥가자 (in AndroidPub)
	 * 
	 * @param x
	 *            (Bitmap)
	 * @return rounded Bitmap
	 */
	public static Bitmap gerRoundBitmap(Bitmap x) {
		
		if (x == null) return null;

		Bitmap output = Bitmap.createBitmap(x.getWidth(), x.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, x.getWidth(), x.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, 5, 5, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(x, rect, rect, paint);
		x.recycle();

		return output;
	}

	public static void recursiveRecycle(View root) {
		if (root == null)
			return;

		if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				recursiveRecycle(group.getChildAt(i));
			}
		}

		if (root instanceof ImageView) {
			((ImageView) root).setImageDrawable(null);
			((ImageView) root).setImageBitmap(null);
		}

		root.setBackgroundDrawable(null);

		root = null;
		
		return;
	}

	public static void setAppPreferences(Activity context, String key, String value1,
			boolean value2) {
		SharedPreferences pref = null;
		pref = context.getSharedPreferences("FacebookCon", 0);
		SharedPreferences.Editor prefEditor = pref.edit();
		prefEditor.putString(key, value1);
		prefEditor.putBoolean("fbauthrized", value2);
		prefEditor.commit();
	}

	public static String getAppPreferences(Activity context, String key) {
		String returnValue = null;

		SharedPreferences pref = null;
		pref = context.getSharedPreferences("FacebookCon", 0);

		returnValue = pref.getString(key, "");

		return returnValue;
	}
	
	public static String getPhoneNum(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			return mTelephonyMgr.getLine1Number().replace("+82", "0").replace("-", "");
		} catch (Exception e) {
			return "";
		}
	}
	
	public static void setOnClickListener(ViewGroup group, OnClickListener click) {
		View[] childViews = getChildViews(group);
		for (View view : childViews) { 
		     if (view instanceof Button)
		           view.setOnClickListener(click);
		     else 
		    	 if (view instanceof ViewGroup)
		          setOnClickListener( (ViewGroup)view, click );
		}
	}

    private static View[] getChildViews(ViewGroup group) {
        int childCount = group.getChildCount();
        final View[] childViews = new View[childCount];
        for (int index = 0; index < childCount; index++) {
            childViews[index] = group.getChildAt(index);
        }
        return childViews;
    }
	
}
