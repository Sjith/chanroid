package kr.co.drdesign.client;

import java.util.Iterator;
import java.util.List;

import kr.co.drdesign.client.service.DRMService;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author brake-_-
 * 말그래도 테스트용 앸티비티.
 * 혹시 몰라서 남겨두지만... 너무 거슬린다 ㅠㅠ
 * 
 * 
 */

public class TestActivity extends Activity implements Loggable{
	
	private static final String CLASS_NAME = "TestActivity";
	
	private TextView tvArgosSatus;
	private EditText repeatMsgInterval;

	public TestActivity(){
		super();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L( CLASS_NAME + " onCreate()");
		
		setContentView(R.layout.test_page_view);
		
		tvArgosSatus = (TextView) findViewById(R.id.tv_argosStatus);
		repeatMsgInterval = (EditText) findViewById(R.id.etInterval);
		
		Button btnRepeatMsg = (Button) findViewById(R.id.BtnRepeatSnm);
		Button btnStopRepeatMsg = (Button) findViewById(R.id.BtnStopSnm);
		
		btnRepeatMsg.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(TestActivity.this, DRMService.class);
				i.setAction("Action_Repeat_SendMessage");
				try{
					int interval = Integer.parseInt( repeatMsgInterval.getText().toString() );
					i.putExtra("interval", interval);
				}catch( NumberFormatException nfe){
					Log.e(DEBUG_TAG, nfe.getMessage());
					nfe.printStackTrace();
				}
				startService(i);
			}
		});
		btnStopRepeatMsg.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(TestActivity.this, DRMService.class);
				i.setAction("Action_Stop_SendMessage");
				startService(i);
			}
		});


		Button btnSvc = (Button) findViewById(R.id.btnGetMsg);
		btnSvc.setOnClickListener( new View.OnClickListener() {
			public void onClick(View arg0) {
				//테스트코드
				//repeatAlram();
				startService(new Intent(getApplicationContext(), DRMService.class));
				Toast.makeText(TestActivity.this, "메시지를 가져옵니다." , Toast.LENGTH_SHORT).show();
			}
		});
		showArgosStatus();
		//원래는 버튼을 클릭못하도록 막아야 하지만 일단 놔두었다.
		Button btnArgosStart = (Button) findViewById(R.id.BtnArgosStart);
		btnArgosStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
//				DisableArgosService.actionStart(getApplicationContext());
//				L("ArgosService.actionStart(getApplicationContext());");
			}
		});
		Button btnArgosStop = (Button) findViewById(R.id.BtnArgosStop);
		btnArgosStop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
//				DisableArgosService.actionStop(getApplicationContext());
				//bindService(new Intent("kr.co.drdesign.argos.ArgosControllService"), new ArgosSleep(), Context.BIND_AUTO_CREATE);
//				L("ArgosService.actionStop(getApplicationContext());");
			}
		});

	}
	
	private void showArgosStatus(){
		if( isRunningArgos() )
		{
			tvArgosSatus.setText("Argos Wake up");	
		}else{
			tvArgosSatus.setText("Argos sleep");
		}
		L( "isRunningArgos = " + isRunningArgos());
	}

	private boolean isRunningArgos()
	{
		ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
		List <ActivityManager.RunningServiceInfo> list = actvityManager.getRunningServices(100);

		Iterator <ActivityManager.RunningServiceInfo> it = list.iterator();
		ActivityManager.RunningServiceInfo rs;

		while( it.hasNext()){
			rs = it.next();
			if( "kr.co.drdesign.client.argos".equals(rs.process) ) 
				return true;
		}
		return false;
	}
	public void L(char i, String log) {
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}