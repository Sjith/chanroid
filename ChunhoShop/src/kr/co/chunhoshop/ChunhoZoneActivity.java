package kr.co.chunhoshop;

import java.util.ArrayList;
import java.util.Map;

import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class ChunhoZoneActivity extends TabActivity implements ParserTag,
		OnClickListener, OnTabChangeListener {

	public static String pnum;
	public static int tabindex;
	
	boolean first = true;

	TabHost tabHost;
	TabWidget tabWidget;

	ListParser parser = new ListParser();
	ZoneThread zThread = new ZoneThread();
	ZoneHandler zHandler = new ZoneHandler();

	ProgressDialog dialog;
	TextView zoneTitle;

	ArrayList<Map<String, CharSequence>> category = new ArrayList<Map<String, CharSequence>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialog = new ProgressDialog(this);
		dialog.setMessage("카테고리 정보를 가져오는 중입니다.");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog.setCancelable(false);
		loading();

		// 숨김처리된 5번째 탭. 제품 상세정보.
		// tabIntent = new Intent(this, ProductActivity.class);
		// spec[4].setContent(tabIntent).setIndicator(getView(R.string.ginseng));
		// tabHost.addTab(spec[4]);
		// tabHost.getTabWidget().getChildAt(4).setVisibility(View.GONE);

	}
	
	TextView getView(String title, int resid) {
		TextView tv = new TextView(this);
		tv.setText(title);
		tv.setTextColor(Color.BLACK);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(resid);
		return tv;
	}

	void loading() {
		if (!dialog.isShowing())
			dialog.show();
		zThread = new ZoneThread();
		zThread.start();
	}

	void loadComplete() {
		if (dialog.isShowing())
			dialog.dismiss();
		category = parser.get();
		if (category.size() < 1)
			return;

		setContentView(R.layout.chunho_zone);
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		tabWidget = tabHost.getTabWidget();
		Intent tabIntent;
		TabSpec[] spec = { tabHost.newTabSpec("1"), tabHost.newTabSpec("2"),
				tabHost.newTabSpec("3"), tabHost.newTabSpec("4") };

		zoneTitle = (TextView) findViewById(R.id.zonetitle);
		zoneTitle.setText("천호ZONE - " + category.get(0).get(CATEGORYNAME).toString());
		tabIntent = new Intent(this, ZoneActivity.class).setAction(category
				.get(0).get(CATEGORYURL).toString());
		spec[0].setContent(tabIntent).setIndicator(
				getView(category.get(0).get(CATEGORYNAME).toString(),
						R.drawable.zone_01));
		tabHost.addTab(spec[0]);

		tabIntent = new Intent(this, ZoneActivity.class).setAction(category
				.get(1).get(CATEGORYURL).toString());
		spec[1].setContent(tabIntent).setIndicator(
				getView(category.get(1).get(CATEGORYNAME).toString(),
						R.drawable.zone_02));
		tabHost.addTab(spec[1]);

		tabIntent = new Intent(this, ZoneActivity.class).setAction(category
				.get(2).get(CATEGORYURL).toString());
		spec[2].setContent(tabIntent).setIndicator(
				getView(category.get(2).get(CATEGORYNAME).toString(),
						R.drawable.zone_03));
		tabHost.addTab(spec[2]);

		tabIntent = new Intent(this, ZoneActivity.class).setAction(category
				.get(3).get(CATEGORYURL).toString());
		spec[3].setContent(tabIntent).setIndicator(
				getView(category.get(3).get(CATEGORYNAME).toString(),
						R.drawable.zone_04));
		tabHost.addTab(spec[3]);
	}

	class ZoneThread extends Thread {
		@Override
		public void run() {
			parser.load(ZONECATEGORY);
			zHandler.sendEmptyMessage(0);
		}
	}

	class ZoneHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadComplete();
				break;
			case 1:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.zonebackbtn:
			if (tabHost.getCurrentTab() != 4) {
				getParent().onBackPressed();
			} else {
				tabHost.setCurrentTab(tabindex);
				showTab();
			}
			break;
		case R.id.chunhozonesettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {}
			break;
		}
	}

	public void showProductDesc() {
		tabHost.setCurrentTab(4);
	}

	public void hideTab() {
		tabWidget.setVisibility(View.GONE);
	}

	public void showTab() {
		tabWidget.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if (first) {
			((TextView)tabWidget.getChildAt(0)).setTextColor(Color.WHITE);
			first = false;
			return;
		}
		switch (Integer.valueOf(tabId)) {
		case 1:
			zoneTitle.setText("천호ZONE - " + category.get(0).get(CATEGORYNAME).toString());
			((TextView)tabWidget.getChildAt(0)).setTextColor(Color.WHITE);
			((TextView)tabWidget.getChildAt(1)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(2)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(3)).setTextColor(Color.BLACK);
			break;
		case 2:
			zoneTitle.setText("천호ZONE - " + category.get(1).get(CATEGORYNAME).toString());
			((TextView)tabWidget.getChildAt(0)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(1)).setTextColor(Color.WHITE);
			((TextView)tabWidget.getChildAt(2)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(3)).setTextColor(Color.BLACK);
			break;
		case 3:
			zoneTitle.setText("천호ZONE - " + category.get(2).get(CATEGORYNAME).toString());
			((TextView)tabWidget.getChildAt(0)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(1)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(2)).setTextColor(Color.WHITE);
			((TextView)tabWidget.getChildAt(3)).setTextColor(Color.BLACK);
			break;
		case 4:
			zoneTitle.setText("천호ZONE - " + category.get(3).get(CATEGORYNAME).toString());
			((TextView)tabWidget.getChildAt(0)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(1)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(2)).setTextColor(Color.BLACK);
			((TextView)tabWidget.getChildAt(3)).setTextColor(Color.WHITE);
			break;
		}
	}

}
