package kr.co.chunhoshop;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MyCartActivity extends TabActivity 
implements OnClickListener, OnTabChangeListener {

	TextView cartTitle;
	static TabHost tabHost;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycart_tab);
		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		TabSpec tabSpec;
		Intent tabIntent;
		cartTitle = (TextView) findViewById(R.id.mycarttitle);
		
		tabIntent = new Intent(this, CartActivity.class);
		tabSpec = tabHost.newTabSpec("mycart")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.cart_cart));
		tabHost.addTab(tabSpec);
		
		tabIntent = new Intent(this, BuyListActivity.class);
		tabSpec = tabHost.newTabSpec("buylist")
		.setContent(tabIntent)
		.setIndicator(getView(R.drawable.cart_order_btn));
		tabHost.addTab(tabSpec);
		
		tabHost.getTabWidget().getLayoutParams().height = 55;
		
	}
	
	TextView getView(int resid) {
		TextView tv = new TextView(this);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(resid);
		return tv;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("Chunho", "RESULT : " + resultCode);		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mycartbackbtn :
			finish();
			break;
		case R.id.cartsettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {}
			break;
		}
		
	}
	
	public static void showCart() throws Exception{
		tabHost.setCurrentTab(0);
	}
	
	public static void showBuyList() throws Exception {
		tabHost.setCurrentTab(1);
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if (tabId.equalsIgnoreCase("mycart")) {
			cartTitle.setText("장바구니");
		} else if (tabId.equalsIgnoreCase("buylist")) {
			cartTitle.setText("주문내역");
		}
	}

}
