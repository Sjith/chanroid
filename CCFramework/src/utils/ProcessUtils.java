package utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessUtils {

	/**
	 * 실행중인 어플리케이션의 이름들을 가져옴
	 * 
	 * @param ctx
	 *            해당 액티비티
	 * @return 실행중인 어플리케이션 이름 목록
	 */
	public static ArrayList<String> getRunningProcess(Context ctx) {
		ArrayList<String> list = new ArrayList<String>();
		List<RunningAppProcessInfo> applist = getRunningProcessInfo(ctx);
		for (RunningAppProcessInfo r : applist)
			list.add(r.processName);
		return list;
	}

	public static boolean isRunningApp(Context ctx, String pkgName) {
		return getRunningProcess(ctx).contains(pkgName);
	}

	/**
	 * 실행중인 어플리케이션의 정보 목록을 가져옴
	 * 
	 * @param ctx
	 * @return
	 */
	public static List<RunningAppProcessInfo> getRunningProcessInfo(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses();
	}
}
