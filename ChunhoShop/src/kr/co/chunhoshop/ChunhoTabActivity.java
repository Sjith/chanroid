package kr.co.chunhoshop;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class ChunhoTabActivity extends TabActivity implements
		OnTabChangeListener {

	Integer tabindex = null;

	static TabHost tabHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		TabSpec spec;
		Intent tabIntent;

		tabIntent = new Intent(this, SearchActivity.class);
		spec = tabHost.newTabSpec("search")
				.setIndicator(getView(R.drawable.tab_search))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		// 검색

		tabIntent = new Intent(this, EventActivity.class);
		spec = tabHost.newTabSpec("event")
				.setIndicator(getView(R.drawable.tab_event))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		// 이벤트

		tabIntent = new Intent(this, ChunhoZoneActivity.class);
		spec = tabHost.newTabSpec("chunhozone")
				.setIndicator(getView(R.drawable.tab_zone))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		// 천호존

		tabIntent = new Intent(this, MyCartActivity.class);
		spec = tabHost.newTabSpec("mycart")
				.setIndicator(getView(R.drawable.tab_cart))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		// 카트

		tabIntent = new Intent(this, MyChunhoActivity.class);
		spec = tabHost.newTabSpec("mychunho")
				.setIndicator(getView(R.drawable.tab_call))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		// 고갱센터

		tabIntent = new Intent(this, SettingActivity.class);
		spec = tabHost.newTabSpec("setting")
				.setIndicator(getView(R.drawable.tab_call))
				.setContent(tabIntent);
		tabHost.addTab(spec);
		tabHost.getTabWidget().getChildAt(5).setVisibility(View.GONE);

		for (int tab = 0; tab < tabHost.getTabWidget().getChildCount(); tab++) {
			tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = 80;
		}

		tabIntent = getIntent();
		if (tabIntent == null) {
			tabHost.setCurrentTab(0);
		} else {
			tabHost.setCurrentTab(tabIntent.getIntExtra("TAG", 0));
			if (tabIntent.getAction() != null) {
				if (tabIntent.getAction().equalsIgnoreCase("cart")) {
					tabHost.setCurrentTab(3);
					try {
						MyCartActivity.showCart();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (tabIntent.getAction().equalsIgnoreCase("event")) {
					tabHost.setCurrentTab(4);
					try {
						MyChunhoActivity.showEvent();
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				}
				if (tabIntent.getAction().equalsIgnoreCase("setting")) {
					tabHost.setCurrentTab(5);
				}
				tabindex = 0;
			}
		}

	}

	public static void showCart() throws Exception {
		if (tabHost == null)
			throw new NullPointerException("dd");
		tabHost.setCurrentTab(3);
		MyCartActivity.showCart();
	}

	public static void showEvent() throws Exception {
		if (tabHost == null)
			throw new NullPointerException("dd");
		tabHost.setCurrentTab(4);
		MyChunhoActivity.showEvent();
	}

	public static void showSetting() throws Exception {
		if (tabHost == null)
			throw new NullPointerException("dd");
		tabHost.setCurrentTab(5);
	}

	ImageView getView(int resid) {
		ImageView iv = new ImageView(this);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setBackgroundResource(resid);
		return iv;
	}

	public void setTab() {
		if (tabindex == (Integer) null)
			throw new NullPointerException("dd");
		else
			tabHost.setCurrentTab(tabindex);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		if (arg0.equalsIgnoreCase("search"))
			tabindex = 0;
		if (arg0.equalsIgnoreCase("event"))
			tabindex = 1;
		if (arg0.equalsIgnoreCase("chunhozone"))
			tabindex = 2;
		if (arg0.equalsIgnoreCase("mycart"))
			tabindex = 3;
		if (arg0.equalsIgnoreCase("mychunho"))
			tabindex = 4;
	}

}
