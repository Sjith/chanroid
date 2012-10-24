package kr.co.drdesign.parmtree;

import kr.co.drdesign.parmtree.util.l;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class ExTabActivity extends TabActivity implements OnClickListener {


	protected TabHost tabHost;
	protected GestureDetector flingDetector;
	
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_tabhost);
		flingDetector = new GestureDetector(this, new LinearGestureListener());
		flingDetector.setIsLongpressEnabled(true);
		Button tomainBtn;
		tomainBtn = (Button) findViewById(R.id.searchtabtomain);
		tomainBtn.setOnClickListener(this);
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
				tabHost.setCurrentTab(3);
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
				tabHost.setCurrentTab(3);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(1);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		case 3 : 
			if (direction) {
				tabHost.setCurrentTab(0);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
			} else {
				tabHost.setCurrentTab(2);
				tabHost.getCurrentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
			}
			break;
		default :
			break;
		}
		// 오른쪽으로 이동 true, 왼쪽으로 이동 false
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		l.s("onTouchEvent");
		return flingDetector.onTouchEvent(event);
	}

	protected class LinearGestureListener extends SimpleOnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		public LinearGestureListener() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			l.s("onFling()");
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				moveTab(true);
			}
			// left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				moveTab(false);
			}   
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			l.s("onDown()");
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			l.s("onLongPress()");			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			l.s("onScroll()");
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			l.s("onSingleTabUp()");
			return true;
		}

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
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getParent().onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
		startActivity(new Intent(this, MainListActivity.class));
	}
}
