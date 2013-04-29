package utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class ManifestUtils {

	/**
	 * 앱의 현재 버전(versionName)을 실수형으로 리턴 (버전에 알파벳이 있을시 0.0으로 리턴됨)
	 * 
	 * @param ctx
	 * @return
	 */
	public static Float getVersionName(Context ctx) {
		try {
			return Float
					.valueOf(ctx.getPackageManager().getPackageInfo(
							ctx.getPackageName(), PackageManager.GET_META_DATA).versionName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0.0f;
		}
	}

	public static String getVersionNameText(Context ctx) {
		try {
			return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_META_DATA).versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * 앱의 현재 버전(versionCode)을 정수형으로 리턴
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getVersionCode(Context ctx) {
		try {
			return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_META_DATA).versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}
}
