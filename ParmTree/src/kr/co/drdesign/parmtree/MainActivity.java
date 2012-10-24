package kr.co.drdesign.parmtree;

import kr.co.drdesign.parmtree.location.LocationActivity;
import kr.co.drdesign.parmtree.location.SearchTabActivity;
import kr.co.drdesign.parmtree.message.MessageActivity;
import kr.co.drdesign.parmtree.util.l;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity 
	implements OnTabChangeListener {

	private TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		TabSpec spec;
		Intent tabIntent;
		
		tabIntent = new Intent(this, LocationActivity.class);
		spec = tabHost.newTabSpec("map")
		.setIndicator(getView(R.drawable.menu_btm_map))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, SearchTabActivity.class);
		spec = tabHost.newTabSpec("search")
		.setIndicator(getView(R.drawable.menu_btm_search))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, EventActivity.class);
		spec = tabHost.newTabSpec("event")
		.setIndicator(getView(R.drawable.menu_btm_event))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		// 2011-07-19 "메시지" 탭.
		tabIntent = new Intent(this, MessageActivity.class);
		spec = tabHost.newTabSpec("message")
		.setIndicator(getView(R.drawable.menu_btm_mess))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, MyPageActivity.class);
		spec = tabHost.newTabSpec("mypage")
		.setIndicator(getView(R.drawable.menu_btm_my))
		.setContent(tabIntent);
		tabHost.addTab(spec);

		tabIntent = getIntent();
		if (tabIntent == null) {
			tabHost.setCurrentTab(0);
		} else tabHost.setCurrentTab(tabIntent.getIntExtra("TAG", 1));		
		
		
	}

	/**
	 * 
	 * @param id - string resources id.
	 * @return TextView - appear on tab.
	 * 탭에 깔리는 뷰를 리턴해준다.
	 * 
	 */
	private TextView getView(int id) {
		TextView tv = new TextView(this);
//		tv.setText(id);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(id);				
		return tv;		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		l.s("onBackPressed");
		Intent i = new Intent(this, MainListActivity.class);
		finish();
		startActivity(i);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mn_menu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.btnSetting :
			startActivity(new Intent(this, SettingActivity.class)); // 설정창 호출
			break;
		case R.id.btnAbout :
			Toast.makeText(this, "짱 좋은 야자수", Toast.LENGTH_SHORT).show();
			break;
		default :
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		tabHost.getCurrentView().startAnimation(
				AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_up));		
	}


}
