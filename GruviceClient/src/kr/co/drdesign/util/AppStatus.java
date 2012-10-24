package kr.co.drdesign.util;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class AppStatus {

	public static boolean isInstalledPackage( String packageName , Context context ){
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		boolean rtn = false;
		int size = infos.size();
		for( int i = 0; i < size ; i++)
		{
			if( infos.get(i).packageName.equalsIgnoreCase(packageName) ) 
				rtn = true;
		}
		return rtn;
	}
}