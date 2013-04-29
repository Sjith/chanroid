package utils;

import android.app.Activity;
import android.os.Build;

/**
 * 테마 설정시 super.onCreate 이전에 호출해야 정상적으로 적용됨
 * 
 * @author CINEPOX
 * 
 */
public class ThemeUtils {
	public static void lightNoTitle(Activity activity) {
		setTheme(activity, android.R.style.Theme_Light_NoTitleBar,
				android.R.style.Theme_Holo_Light_NoActionBar);
	}

	public static void darkNoTitle(Activity activity) {
		setTheme(activity, android.R.style.Theme_NoTitleBar,
				android.R.style.Theme_Holo_NoActionBar);
	}

	public static void lightNoTitleFull(Activity activity) {
		setTheme(activity, android.R.style.Theme_Light_NoTitleBar_Fullscreen,
				android.R.style.Theme_Holo_Light_NoActionBar);
	}

	public static void darkNoTitleFull(Activity activity) {
		setTheme(activity, android.R.style.Theme_NoTitleBar_Fullscreen,
				android.R.style.Theme_Holo_NoActionBar);
	}

	public static void transParent(Activity activity) {
		setTheme(activity, android.R.style.Theme_Translucent_NoTitleBar,
				android.R.style.Theme_Translucent_NoTitleBar);
	}

	public static void lightDialog(Activity activity) {
		setTheme(activity, android.R.style.Theme_Dialog,
				android.R.style.Theme_Holo_Dialog);
	}

	public static void lightDialogNoTitle(Activity activity) {
		setTheme(activity, android.R.style.Theme_Dialog,
				android.R.style.Theme_Holo_Dialog_NoActionBar);
	}

	private static void setTheme(Activity activity, int noHolo, int holo) {
		if (Build.VERSION.SDK_INT > 11)
			activity.setTheme(holo);
		else
			activity.setTheme(noHolo);
	}
}