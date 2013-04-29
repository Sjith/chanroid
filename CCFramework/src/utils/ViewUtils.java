package utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ViewUtils {

	/**
	 * 별 기능없이 메시지만 있는 간단한 알림창을 하나 띄움 (캔슬 됨)
	 * 
	 * @param ctx
	 *            띄울 액티비티
	 * @param title
	 *            타이틀
	 * @param message
	 *            메시지
	 * @param click
	 *            클릭 리스너
	 * 
	 * @return 여러번 사용할 필요가 있는 경우 레퍼런스를 반환
	 */
	public static AlertDialog showAlert(Context ctx, String title,
			String message, String btntext,
			DialogInterface.OnClickListener click, boolean cancelable) {
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.setCancelable(cancelable);
		if (title != null)
			alert.setTitle(title);
		if (message != null)
			alert.setMessage(message);
		if (btntext != null)
			alert.setPositiveButton(btntext, click);
		return alert.show();
	}

	/**
	 * 별 기능없이 메시지만 있는 간단한 알림창을 하나 띄움 (캔슬 됨)
	 * 
	 * @param ctx
	 *            띄울 액티비티
	 * @param title
	 *            타이틀
	 * @param message
	 *            메시지
	 * @param click
	 *            클릭 리스너
	 * 
	 * @return 여러번 사용할 필요가 있는 경우 레퍼런스를 반환
	 */
	public static AlertDialog showAlert(Context ctx, int title, int message,
			int btntext, DialogInterface.OnClickListener click,
			boolean cancelable) {
		String titleText = ctx.getString(title);
		String messageText = ctx.getString(message);
		String btnText = ctx.getString(btntext);
		return showAlert(ctx, titleText, messageText, btnText, click,
				cancelable);
	}

	/**
	 * 별 기능 없이 뺑뺑이만 도는 로딩창을 하나 띄움 (캔슬 안됨)
	 * 
	 * @param ctx
	 *            띄울 액티비티
	 * @param title
	 *            타이틀
	 * @param message
	 *            메시지
	 * 
	 * @return 여러번 사용할 필요가 있는 경우 레퍼런스를 반환
	 */
	public static ProgressDialog showLoading(Context ctx, String title,
			String message) {
		ProgressDialog loading = new ProgressDialog(ctx);
		if (title != null)
			loading.setTitle(title);
		if (message != null)
			loading.setMessage(message);
		loading.setCancelable(false);
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.show();
		return loading;
	}
}
