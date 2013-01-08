package utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class DisplayUtils {

	/**
	 * 코드상에서 레이아웃의 dip를 지정할 수 없으므로 dip를 픽셀로 변환시켜 준다.
	 * 
	 * @param value
	 * @param ctx
	 * @return
	 */
	public static float applyDimension(int value, Context ctx) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				value, ctx.getResources().getDisplayMetrics());
	}

	/**
	 * 코드상에서 텍스트 사이즈(sp) 를 지정할 수 없으므로 sp를 포인트로 변환시켜 준다.
	 * 
	 * @param value
	 * @param ctx
	 * @return
	 */
	public static float applyScale(int value, Context ctx) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,
				ctx.getResources().getDisplayMetrics());
	}
	
	public static boolean isTablet(Context context) {
		Configuration config = context.getResources().getConfiguration();
		boolean isXlarge = (config.screenLayout & 4) == 4;
		boolean isLarge = (config.screenLayout & 3) == 3;

		boolean mask = true;
		if (Build.DEVICE.contains("SHV-E160")) // 노트1
			mask = false;
		else if (Build.DEVICE.contains("SHW-M180")) // 탭7
			mask = true;
		else if (getScreenInch(context) < 6.d) // 6인치 미만은 폰으로 취급
			mask = false;

		return (isXlarge || isLarge) & mask;
	}

	public static double getScreenInch(Context ctx) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		double x = java.lang.Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = java.lang.Math.pow(dm.heightPixels / dm.ydpi, 2);
		return java.lang.Math.sqrt(x + y);
	}

	/**
	 * 상단바의 사이즈를 반환
	 * 
	 * @return 상단바 사이즈
	 */
	public static int getNotiBarSize(Context ctx) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
				ctx.getResources().getDisplayMetrics()); // 25dp
	}

	/**
	 * 화면 전체 사이즈를 반환
	 * 
	 * @param ctx
	 *            호출한 액티비티
	 * @return 0 - 넓이, 1 - 높이
	 */
	public static int[] getWindowSize(Context ctx) {
		int[] result = new int[2];
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		result[0] = displayMetrics.widthPixels;
		result[1] = displayMetrics.heightPixels;
		return result;
	}

	/**
	 * 현재 액티비티의 밝기를 조절
	 * 
	 * @param ctx
	 *            호출하는 액티비티
	 * @param bright
	 *            조절할 밝기 값. 0~1 (float)
	 */
	public static void setBrightness(Activity ctx, float bright) {
		WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
		lp.screenBrightness = bright;
		ctx.getWindow().setAttributes(lp);
	}

	public static float getBrightness(Activity ctx) {
		WindowManager.LayoutParams lp = ctx.getWindow().getAttributes();
		return lp.screenBrightness;
	}
}
