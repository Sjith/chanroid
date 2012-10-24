package kr.co.drdesign.parmtree.community;

import kr.co.drdesign.parmtree.ExTabActivity;
import kr.co.drdesign.parmtree.NoticeActivity;
import kr.co.drdesign.parmtree.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.TabSpec;

public class CommunityActivity extends ExTabActivity {


	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_tabhost);

		tabHost = getTabHost();
		TabSpec spec;
		Intent tabIntent;
		
		tabIntent = new Intent(this, FreeBoardActivity.class);
		spec = tabHost.newTabSpec("freeboard")
		.setIndicator(getView(R.string.comTxtFreeBoard))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, RatingActivity.class);
		spec = tabHost.newTabSpec("rating")
		.setIndicator(getView(R.string.ratingest))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, AdvertiseActivity.class);
		spec = tabHost.newTabSpec("adver")
		.setIndicator(getView(R.string.comTxtAdver))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabIntent = new Intent(this, NoticeActivity.class);
		spec = tabHost.newTabSpec("notice")
		.setIndicator(getView(R.string.comTxtNotice))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
	}


}
