package kr.co.vipsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingTab extends Activity {

	final static int DEFAULT_FONT_SIZE = 20;
	
	String LOG_TAG = "Settings";
	
	RadioPlayer player = RadioPlayer.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		SeekBar skFontSize = (SeekBar) findViewById(R.id.sk_fontsize);		
		skFontSize.setProgress(player.getScriptFontSize() - DEFAULT_FONT_SIZE);
		skFontSize.setOnSeekBarChangeListener(skChangeListener);
		
		ImageView ivBtnInfo = (ImageView) findViewById(R.id.info_img);
		ivBtnInfo.setOnClickListener(btnControlClickListener);
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.chk_autologin);
		
		checkbox.setOnClickListener(onCheckedChanged);

		
	}

	private OnClickListener onCheckedChanged = new OnClickListener(){
//		@Override
		public void onClick(View v) {
			final CheckBox checkbox = (CheckBox) findViewById(R.id.chk_autologin);
			
			SharedPreferences pref = getSharedPreferences("Prefs",0);
			SharedPreferences.Editor edit = pref.edit();

			if(checkbox.isChecked()){
				edit.putString("autoLogin", "t");
			}else{
				edit.putString("autoLogin", "f");
			}		
			edit.commit();			
		}

	};

	
	private OnClickListener btnControlClickListener = new OnClickListener(){

//		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SettingTab.this, BusanEFMInfoActivity.class);
			startActivity(intent);
		}		
	};	
	
	private OnSeekBarChangeListener skChangeListener = new OnSeekBarChangeListener(){

//		@Override
		public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {

			TextView tvSample = (TextView) findViewById(R.id.tv_sample);
			
			tvSample.setTextSize(progress + DEFAULT_FONT_SIZE); 	
			
			player.setScriptFontSize(progress + DEFAULT_FONT_SIZE);
		}

//		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

//		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
		
	};
	
    ////////////////////////////////////////////////////////////////////////////
    // 메인 키 버튼 처리
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(LOG_TAG, "[onKeyDown] keyCode:"+Integer.toString(keyCode));
//		int nCurrentVolumn =0;
		
		switch(keyCode){
//			case KeyEvent.KEYCODE_VOLUME_UP :				
//				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//				nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//				seekVolumn.setProgress(nCurrentVolumn);
//				
//				return true;
//				
//			case KeyEvent.KEYCODE_VOLUME_DOWN:
//				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//				nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//				seekVolumn.setProgress(nCurrentVolumn);
//				return true;
//				
			case KeyEvent.KEYCODE_BACK:
//				new AlertDialog.Builder( SettingTab.this )
//				.setTitle("Alert!")						
//				.setMessage("Are you sure exit?")
//				.setPositiveButton( "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick( DialogInterface dialog, int id) {
//                        //dialog.cancel();
//                    	moveTaskToBack(true);
//                    	finish();
//                    }
//                }
//             )
//             .setNegativeButton("No", null)
//            .show();
				((eBFMView)getParent()).setTabIndex(0);
	            return true;

		}
		//return super.onKeyDown(keyCode, event);
		return false;
	}
    	

//	@Override
//	protected void onPause() {
//		super.onPause();
//		
//		final CheckBox checkbox = (CheckBox) findViewById(R.id.chk_autologin);
//		
//		SharedPreferences pref = getSharedPreferences("Prefs",0);
//		SharedPreferences.Editor edit = pref.edit();
//
//		if(checkbox.isChecked()){
//			edit.putString("autoLogin", "t");
//		}else{
//			edit.putString("autoLogin", "f");
//		}		
//		edit.commit();
//	}

}
