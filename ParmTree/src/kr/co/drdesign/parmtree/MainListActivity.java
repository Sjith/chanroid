package kr.co.drdesign.parmtree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainListActivity extends Activity implements OnClickListener {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list);
		
		Button locationBtn;
		Button searchBtn;
		Button eventBtn;
		Button messageBtn;
		Button mypageBtn;
		
		locationBtn = (Button) findViewById(R.id.mainbutton2);
		searchBtn = (Button) findViewById(R.id.mainbutton3);
		eventBtn = (Button) findViewById(R.id.mainbutton4);
		messageBtn = (Button) findViewById(R.id.mainbutton5);
		mypageBtn = (Button) findViewById(R.id.mainbutton6);
		
		locationBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		eventBtn.setOnClickListener(this);
		messageBtn.setOnClickListener(this);
		mypageBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, MainActivity.class);
		switch (v.getId()) {
		case R.id.mainbutton2 :
			i.putExtra("TAG", 0);
			break;
		case R.id.mainbutton3 :
			i.putExtra("TAG", 1);
			break;
		case R.id.mainbutton4 :
			i.putExtra("TAG", 2);
			break;
		case R.id.mainbutton5 :
			i.putExtra("TAG", 3);
			break;
		case R.id.mainbutton6 :
			i.putExtra("TAG", 4);
			break;
		default :
			break;
		}
		startActivity(i);
		finish();
	}

}
