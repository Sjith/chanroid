package kr.co.drdesign.parmtree.message;

import kr.co.drdesign.parmtree.ExTabActivity;
import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.util.l;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MessageActivity extends ExTabActivity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_tabhost);
		
		TabSpec spec;
		Intent tabIntent;
		tabHost = getTabHost();
		
		tabIntent = new Intent(this, ReceivedMsgListActivity.class);
		spec = tabHost.newTabSpec("received")
		.setIndicator(getView(R.drawable.menu_top_receivedmsg))
		.setContent(tabIntent);
		tabHost.addTab(spec);

		// 2011-07-19 "���ڽü�" ��.
		tabIntent = new Intent(this, SendedMsgListActivity.class);
		spec = tabHost.newTabSpec("sended")
		.setIndicator(getView(R.drawable.menu_top_sendedmsg))
		.setContent(tabIntent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
	}

	/**
	 * 
	 * @param id
	 * @return TextView
	 * �ǿ� �򸮴� �並 �������ش�.
	 * 
	 */
	protected TextView getView(int id) {
		TextView tv = new TextView(this);
//		tv.setText(id);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(id);				
		return tv;		
	}

	/**
	 * @param index
	 * @see android.widget.TabHost#setCurrentTab(int)
	 * �׽�Ʈ��. �̰� UX�� �󸶳� ������ �Ƿ���
	 */
	protected void moveTab(boolean direction) {
		l.s("moveTab()");
		switch (tabHost.getCurrentTab()) {
		case 0 : 
			if (direction) {
				tabHost.setCurrentTab(1);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(1);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		case 1 : 
			if (direction) {
				tabHost.setCurrentTab(0);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(0);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		default :
			break;
		}
		// ���������� �̵� true, �������� �̵� false
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getParent().onBackPressed();
	}




}
