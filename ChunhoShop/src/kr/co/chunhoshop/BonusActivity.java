package kr.co.chunhoshop;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BonusActivity extends TabActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.memberbonus);
		TabHost tabHost = getTabHost();

		Intent tabIntent;
		TabSpec spec;

		tabIntent = new Intent(this, GiftInfoActivity.class);
		spec = tabHost.newTabSpec("1");
		spec.setContent(tabIntent).setIndicator(
				getView(R.drawable.gift_giftinfo_btn));
		tabHost.addTab(spec);

		tabIntent = new Intent(this, MemberBonusActivity.class);
		spec = tabHost.newTabSpec("2");
		spec.setContent(tabIntent).setIndicator(
				getView(R.drawable.gift_member_btn));
		tabHost.addTab(spec);

	}

	TextView getView(int resid) {
		TextView tv = new TextView(this);
		tv.setBackgroundResource(resid);
		return tv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bonusbackbtn:
			finish();
			break;
		}
	}
}
