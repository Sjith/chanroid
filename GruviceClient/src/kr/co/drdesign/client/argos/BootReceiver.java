package kr.co.drdesign.client.argos;

import kr.co.drdesign.client.service.ArgosService;
import kr.co.drdesign.client.service.DRMService;
import kr.co.drdesign.util.GruviceUtillity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author JY
 * Auto Starting DRMService by this Class
 * and Setting for DRMClass Repeated interval. 
 */
public class BootReceiver extends BroadcastReceiver{

	private static final String TAG = "DR";

	@Override
	public void onReceive(Context context, Intent intent) {

		//test halftale 2010-08-27 수정해야 하는 부분
		String mDeviceID = GruviceUtillity.getInstance(context).getClientId();
		Log.i(TAG, "mDeviceID is " + mDeviceID);

		Editor editor = context.getSharedPreferences(ArgosService.TAG, Context.MODE_PRIVATE).edit();
		editor.putString(ArgosService.clientID, mDeviceID);
		editor.commit();
		
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			context.startService(new Intent(context, DRMService.class));
			context.startService(new Intent(context, ArgosService.class));
		}

		Log.i("DR", "BOOT Receiver Complete.");
	}

}