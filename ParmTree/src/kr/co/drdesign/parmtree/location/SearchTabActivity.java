package kr.co.drdesign.parmtree.location;

import kr.co.drdesign.parmtree.ExTabActivity;
import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.util.l;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TabHost.TabSpec;

public class SearchTabActivity extends ExTabActivity {

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		TabSpec spec;
		Intent tabIntent;
		tabHost = getTabHost();
		
		tabIntent = new Intent(this, SearchActivity.class);
		spec = tabHost.newTabSpec("search")
		.setIndicator(getView(R.drawable.menu_top_search))
		.setContent(tabIntent);
		tabHost.addTab(spec);

		tabIntent = new Intent(this, PremiumActivity.class);
		spec = tabHost.newTabSpec("premium")
		.setIndicator(getView(R.drawable.menu_top_store))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		// 2011-07-19 "숙박시설" 탭.
		tabIntent = new Intent(this, HotelActivity.class);
		spec = tabHost.newTabSpec("hotel")
		.setIndicator(getView(R.drawable.menu_top_stay))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		
		tabHost.setCurrentTab(0);
	}

	/**
	 * @param index
	 * @see android.widget.TabHost#setCurrentTab(int)
	 * 테스트용. 이게 UX에 얼마나 도움이 되려나
	 */
	protected void moveTab(boolean direction) {
		l.s("moveTab()");
		switch (tabHost.getCurrentTab()) {
		case 0 : 
			if (direction) {
				tabHost.setCurrentTab(1);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(2);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		case 1 : 
			if (direction) {
				tabHost.setCurrentTab(2);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(0);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		case 2 : 
			if (direction) {
				tabHost.setCurrentTab(0);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(1);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		default :
			break;
		}
		// 오른쪽으로 이동 true, 왼쪽으로 이동 false
	}

}
