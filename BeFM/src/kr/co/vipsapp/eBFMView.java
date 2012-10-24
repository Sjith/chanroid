package kr.co.vipsapp;



import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class eBFMView extends TabActivity {

	RadioPlayer player = RadioPlayer.getInstance();
	String LOG_TAG = "eBFM Main";

	//private NotificationManager nm;

	TabHost tabHost;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// splash 화면, 3초 동안 유지
		//startActivity(new Intent(this, SplashActivity.class));

		// /////////////////////////////////////////////////////////////
		// do something
		// 화면 하단에 Tab 생성, Radio, News, AOD, Setting

		tabHost = getTabHost();
		Intent intentRadio, intentNews, intentAOD, intentSettings;
		//

		// Radio Tab
		intentRadio = new Intent(this, RadioTab.class);
		tabHost.addTab(tabHost.newTabSpec("Radio")
				.setIndicator(new MyTabView(this, R.drawable.radio, "Radio"))
				.setContent(intentRadio));

		// News Tab
		intentNews = new Intent(this, NewsTab.class);
		tabHost.addTab(tabHost.newTabSpec("News")
				.setIndicator(new MyTabView(this, R.drawable.news, "News"))
				.setContent(intentNews));

		// AOD Tab
		intentAOD = new Intent(this, AODTab.class);
		tabHost.addTab(tabHost.newTabSpec("AOD")
				.setIndicator(new MyTabView(this, R.drawable.aod, "AOD"))
				.setContent(intentAOD));

		// Setting Tab
		intentSettings = new Intent(this, SettingTab.class);
		tabHost.addTab(tabHost.newTabSpec("Settings")
				.setIndicator(
						new MyTabView(this, R.drawable.settings, "Settings"))
						.setContent(intentSettings));
		//
		tabHost.setCurrentTab(0);


		//		nm =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//		PendingIntent intent = PendingIntent.getActivity(eBFMView.this, 0, intentRadio, 0);
		//		Notification notification = new Notification(R.drawable.icon, "Busan eFM", System.currentTimeMillis());
		//		notification.setLatestEventInfo(eBFMView.this, "Busan eFM 9.05", "", intent);
		//		nm.notify(player.NOTIFICATION_ID, notification);

		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			//			@Override
			public void onTabChanged(String tabId) {

				//				Log.i(LOG_TAG, "현재 Tab : "+tabHost.getCurrentTab()+", Play 상태: "+player.getPlayFeathers());
				// 현재 tab의 위치
				player.setTab(tabHost.getCurrentTab());

				//if(tabId.equals("Radio")){
				if(player.getPlayFeathers() != player.PLAY_RADIO){
					player.setPlayFeathers(player.PLAY_OFF);
					stop();
					player.setPlaying(false);						
				}
				//}
			}
		});	// listener



	}

	public void setTabIndex(int index) {
		if (index > -1 && index < 5) tabHost.setCurrentTab(index);
	}

	private void stop() {
		if (player.getAacPlayer() != null) { 
			player.getAacPlayer().stop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Process.killProcess(Process.myPid());
	}

	private class MyTabView extends LinearLayout {
		public MyTabView(Context c, int drawable, String label) {
			super(c);
			ImageView iv = new ImageView(c);
			iv.setImageResource(drawable);

			TextView tv = new TextView(c);
			tv.setText(label);
			tv.setGravity(0x01); /* Center */
			setOrientation(LinearLayout.VERTICAL);
			addView(iv);
			addView(tv);	

			this.setBackgroundResource(R.drawable.tab_radio);
			this.setPadding(5, 5, 5, 5);
		}


	}



}
