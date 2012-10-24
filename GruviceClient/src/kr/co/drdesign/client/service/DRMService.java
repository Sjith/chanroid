package kr.co.drdesign.client.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DRMService extends Service
{ 

	
	private Intent i;
	private int startid;
	
	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		
		this.i = intent;
		this.startid = startId;
		
		// 2011-05-24 쓸모없는 테스트 로그, 코드 삭제.
		// 서비스는 최대한 가볍게. 가볍게
		new Thread( new Communicator( getApplicationContext() )).start();
		stopSelf();
	}


	public static void actionStart(Context ctx)
	{
		// BR이나 다른 서비스 등의 액티비티 외부에서 호출할 때 사용하는 메서드.
		Log.i("mqtt", "DRMService Start");
		Intent i = new Intent(ctx, DRMService.class);
		ctx.startService(i);
	}
	
	public static void actionStop(Context ctx)
	{
		// BR이나 다른 서비스 등의 액티비티 외부에서 호출할 때 사용하는 메서드.
		Log.i("mqtt", "DRMService Stop");
		Intent i = new Intent(ctx, DRMService.class);
		ctx.stopService(i);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void restart() {
		this.onStart(i, startid); 
	}
}