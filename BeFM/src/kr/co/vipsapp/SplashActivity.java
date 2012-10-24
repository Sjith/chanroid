package kr.co.vipsapp;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	/** Called when the activity is first created. */
	String LOG_TAG = "Splash";
	
    final int welcomeScreenDisplay = 10000;
    final HttpClient httpClient = new DefaultHttpClient();
    final HttpGet request = new HttpGet("http://www.befm.or.kr");
//	
    int status =0;
	Intent intent;
	Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);
	    
//	    /** set time to splash out */
/*	    final int welcomeScreenDisplay = 3000;
	    final HttpClient httpClient = new DefaultHttpClient();
	    final HttpGet request = new HttpGet("http://www.befm.or.kr");*/


        handler = new Handler () {
            @Override
            public void handleMessage(Message msg) {
            	//intent = new Intent(this, eBFMView.class);
            	//startActivity(intent);         
            	startActivity(new Intent(SplashActivity.this,eBFMView.class));
            	finish();
            }
           };
	    
	    handler.sendEmptyMessageDelayed(0,3000);
        // loading();
	}

/*	private void loading(){
	    *//** create a thread to show splash up to splash time *//*
	    Thread welcomeThread = new Thread() {

	    int wait = 0;
	    //boolean isOn =false;

	    @Override
	    	public void run() {
	    		try {
	    			super.run();
	    		*//**
	    		 * use while to get the splash time. Use sleep() to increase
	    		 * the wait variable for every 100L.
	    		 *//*
	    			while (wait < welcomeScreenDisplay) {
	    			//while (isOn) {	
	    				//int status = 0;
	    			    try {
	    					HttpResponse response = httpClient.execute(request);
	    					status = response.getStatusLine().getStatusCode();
	    					
	    				} catch (ClientProtocolException e1) {
	    					Log.i(LOG_TAG, "EXc=" + e1.getMessage());
	    				} catch (IOException e1) {
	    					Log.i(LOG_TAG, "EXc=" + e1.getMessage());
	    				}
	    				
//	    			    sleep(1000);
//    					wait += 1000;
    					
	    				if(status == HttpStatus.SC_OK){
	    					wait = welcomeScreenDisplay;
	    					//isOn = true;
	    					//handler.sendEmptyMessageDelayed(0,1000);
	    					//sleep(1000);
	    				}else{
	    					sleep(1000);
	    					wait += 1000;
	    					if( wait > welcomeScreenDisplay){
	    						new AlertDialog.Builder( SplashActivity.this )
	    						.setTitle("BeFM")						
	    						.setMessage("인터넷 상태가 좋지 않습니다. 잠시 후에 다시 시작하십시요.")
	    						.setNeutralButton("확인",new DialogInterface.OnClickListener() {
	    			                public void onClick( DialogInterface dialog, int id) {
	    			                    dialog.cancel();
	    			                }
	    			            }
	    			         )
	    			         .show();
	    					}
	    				}
	    			}
	    		} catch (Exception e) {
	    			//System.out.println("EXc=" + e);
	    			Log.i(LOG_TAG, "EXc=" + e);
	    		} finally {
	    			*//**
	    			 * Called after splash times up. Do some action after splash
	    			 * times up. Here we moved to another main activity class
	    			 *//*
	    			startActivity(new Intent(SplashActivity.this,eBFMView.class));
	    			finish();
//	    			new AlertDialog.Builder( SplashActivity.this )
//					.setTitle("알림")						
//					.setMessage("인터넷 상태가 좋지 않습니다. 프로그램을 종료합니다. ")
//					.setNeutralButton("확인" ,
//		            new DialogInterface.OnClickListener() {
//		                public void onClick( DialogInterface dialog, int id) {
//		                    dialog.cancel();
//		                }
//		            }
//		         )		         
//		        .show();
//	    		
//	    			finish();
	    		}
	    	}
	    };
	    welcomeThread.start();		
	}*/
	
	@Override
	public void onBackPressed() {
		
	}
	
//    @Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.i(LOG_TAG, "[onKeyDown] keyCode:"+Integer.toString(keyCode));
//	
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			new AlertDialog.Builder( SplashActivity.this )
//				.setTitle("경고")						
//				.setMessage("종료하시겠습니까?")
//				.setPositiveButton( "예",
//					new DialogInterface.OnClickListener() {
//						public void onClick( DialogInterface dialog, int id) {
//							//	dialog.cancel();
//							moveTaskToBack(true);
//							finish();
//						}
//					}
//				)
//				.setNegativeButton("아니오", null)
//				.show();
//			return true;
//		}
//		return false;
//    }
}
