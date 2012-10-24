package kr.co.vipsapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BusanEFMInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.efm_info);

		
		ImageView ivBtnClose = (ImageView) findViewById(R.id.info_btn_close);
		ivBtnClose.setOnClickListener(btnControlClickListener);
		

		
	}
	
	private OnClickListener btnControlClickListener = new OnClickListener(){

//		@Override
		public void onClick(View v) {
			finish();
		}		
	};
}
