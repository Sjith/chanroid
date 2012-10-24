package kr.co.chunhoshop;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MyChunhoActivity extends TabActivity implements OnClickListener, OnTabChangeListener {
	
	public static int tabindex;
	public static String nnum;

	static TabHost tabHost;
	TabWidget tabWidget;
	TextView centerTitle;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mychunho);
		centerTitle = (TextView) findViewById(R.id.mychunhotitle);
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		tabWidget = tabHost.getTabWidget();
		TabSpec tabSpec;
		Intent tabIntent;
		
		tabIntent = new Intent(this, NoticeActivity.class);
		tabSpec = tabHost.newTabSpec("notice")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.center_notice));
		tabHost.addTab(tabSpec);
		
		tabIntent = new Intent(this, FAQActivity.class);
		tabSpec = tabHost.newTabSpec("faq")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.center_faq));
		tabHost.addTab(tabSpec);
		
		tabIntent = new Intent(this, GetEventActivity.class);
		tabSpec = tabHost.newTabSpec("getevent")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.center_event));
		tabHost.addTab(tabSpec);
		
		tabIntent = new Intent(this, ChunhoWayActivity.class);
		tabSpec = tabHost.newTabSpec("way")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.center_map));
		tabHost.addTab(tabSpec);
		
	}
	
	public static void showEvent() throws Exception{
		tabHost.setCurrentTab(2);
	}
	
	TextView getView(int resid) {
		TextView tv = new TextView(this);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(resid);
		return tv;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mychunhobackbtn :
			switch (tabHost.getCurrentTab()) {
			case 0 :
				if (getCurrentActivity() instanceof NoticeActivity) {
					((NoticeActivity) getCurrentActivity()).onBackPressed();
				} else if (getCurrentActivity() instanceof NoticeDescActivity) {
					NoticeActivity.closeDesc();
				}
				break;
			case 1 :
				((FAQActivity) getCurrentActivity()).onBackPressed();
				break;
			case 2 : 
				((GetEventActivity) getCurrentActivity()).onBackPressed();
				break;
			case 3 :
				((ChunhoWayActivity) getCurrentActivity()).onBackPressed();
			}
			break;
		case R.id.mychunhosettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {}
			break;
		}
	}

	public void showProductDesc() {
		tabHost.setCurrentTab(4);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (tabHost.getCurrentTab() != 4) {
			getParent().onBackPressed();
		} else {
			setTabIndex(tabindex);
		}
	}

	public void hideTab() {
		tabWidget.setVisibility(View.GONE);
	}

	public void showTab() {
		tabWidget.setVisibility(View.VISIBLE);
	}

	public void setTabIndex(int i) {
		tabHost.setCurrentTab(i);
		if (i != 4) tabindex = i;
		// tabindex는 절대로 4가 되어서는 안된다.
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if (tabId.equals("notice")) {
			centerTitle.setText("공지사항");
		} else if (tabId.equals("faq")) {
			centerTitle.setText("FAQ");
		} else if (tabId.equals("getevent")) {
			centerTitle.setText("이벤트게시판");
		} else if (tabId.equals("way")) {
			centerTitle.setText("찾아오시는");
		}
	}

}
