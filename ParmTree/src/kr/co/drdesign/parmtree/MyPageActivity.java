package kr.co.drdesign.parmtree;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MyPageActivity extends ExTabActivity {


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_tabhost);

		tabHost = getTabHost();
		TabSpec spec;
		Intent tabIntent;
		
		tabIntent = new Intent(this, CouponActivity.class);
		spec = tabHost.newTabSpec("coupon")
		.setIndicator(getView(R.drawable.menu_top_coupon))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		// 2011-07-19 친구목록
		tabIntent = new Intent(this, FriendListActivity.class);
		spec = tabHost.newTabSpec("friend")
		.setIndicator(getView(R.drawable.menu_top_friend))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		// 2011-07-19 즐겨찾기의 개념이 아닌, 가입한 그룹목록을 보는 개념의 액티비티로 변경.
		tabIntent = new Intent(this, EstListActivity.class);
		spec = tabHost.newTabSpec("estlist")
		.setIndicator(getView(R.drawable.menu_top_favorite))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, NoticeActivity.class);
		spec = tabHost.newTabSpec("parmtree")
		.setIndicator(getView(R.drawable.menu_top_notice))
		.setContent(tabIntent);
		tabHost.addTab(spec);
	}

	/**
	 * 
	 * @param id
	 * @return TextView
	 * 탭에 깔리는 뷰를 리턴해준다.
	 * 
	 */
	protected TextView getView(int id) {
		TextView tv = new TextView(this);
//		tv.setText(id);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(id);				
		return tv;		
	}
}
