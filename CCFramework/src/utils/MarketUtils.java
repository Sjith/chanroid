package utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MarketUtils {

	/**
	 * 앱 자신의 마켓 정보로 이동
	 * 
	 * @param ctx
	 */
	public static void goMarket(Context ctx) {
		ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri
				.parse("market://details?id=" + ctx.getPackageName())));
	}

	/**
	 * 해당 패키지의 마켓 정보로 이동
	 * 
	 * @param ctx
	 * @param pkgname
	 */
	public static void goMarket(Context ctx, String pkgname) {
		ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri
				.parse("market://details?id=" + pkgname)));
	}

	/**
	 * 해당 appId를 가진 티스토어 정보로 이동
	 * 
	 * @param ctx
	 * @param appid
	 * @return 실행되었으면 true, 아니면 false
	 */
	public static boolean goTStore(Context ctx, String appid) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("tstore://PRODUCT_VIEW/" + appid + "/0"));
			ctx.startActivity(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 해당 appId를 가진 오즈스토어 정보로 이동
	 * 
	 * @param ctx
	 * @param appid
	 * @return 실행되었으면 true, 아니면 false
	 */
	public static boolean goOZstore(Context ctx, String appid) {
		try {
			Intent intent = new Intent();
			intent.setClassName("android.lgt.appstore",
					"android.lgt.appstore.Store");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("payload", "PID=" + appid);
			ctx.startActivity(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
