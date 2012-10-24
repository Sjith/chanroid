package kr.co.drdesign.parmtree.est;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.util.c;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class EstablishActivity extends TabActivity implements c {

	TabHost tabHost;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.est_info);
		tabHost = getTabHost();
		TabSpec tabSpec;
		Intent tabIntent;
		Intent getIntent = getIntent();

		tabIntent = new Intent(this, EstIntroduceActivity.class);
		tabIntent.putExtra(ESTID, getIntent.getStringExtra(ESTID));
		tabSpec = tabHost.newTabSpec("introduce").setContent(tabIntent)
				.setIndicator(getView(R.string.introduceest));
		tabHost.addTab(tabSpec);

		tabIntent = new Intent(this, EstPicActivity.class);
		tabIntent.putExtra(ESTID, getIntent.getStringExtra(ESTID));
		tabSpec = tabHost.newTabSpec("pic").setContent(tabIntent)
				.setIndicator(getView(R.string.picest));
		tabHost.addTab(tabSpec);

		tabIntent = new Intent(this, EstRatingActivity.class);
		tabIntent.putExtra(ESTID, getIntent.getStringExtra(ESTID));
		tabSpec = tabHost.newTabSpec("rating").setContent(tabIntent)
				.setIndicator(getView(R.string.ratingest));
		tabHost.addTab(tabSpec);

		tabIntent = new Intent(this, EstNoticeActivity.class);
		tabIntent.putExtra(ESTID, getIntent.getStringExtra(ESTID));
		tabSpec = tabHost.newTabSpec("notice").setContent(tabIntent)
				.setIndicator(getView(R.string.noticeest));
		tabHost.addTab(tabSpec);
	}

	/**
	 * 
	 * @param id
	 * @return TextView ≈«ø° ±Ú∏Æ¥¬ ∫‰∏¶ ∏Æ≈œ«ÿ¡ÿ¥Ÿ.
	 * 
	 */
	private TextView getView(int id) {
		TextView tv = new TextView(this);
		tv.setText(id);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(R.drawable.btn);
		return tv;
	}
}
