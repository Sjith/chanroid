package kr.co.drdesign.parmtree;

import kr.co.drdesign.parmtree.util.ParmUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends Activity {
	
	ParmUtil util;
	ImageView mainLogo;
    IntroRunnable r = new IntroRunnable();
    Handler handler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        
        mainLogo = (ImageView) findViewById(R.id.imageView1);
        Animation introAni = AnimationUtils.loadAnimation(this, R.anim.intro);
        
		util = ParmUtil.getInstance(getApplicationContext());
		// 스태틱 클래스들을 미리 메모리에 다 적재시켜 둔다.
        mainLogo.startAnimation(introAni);
        handler.postDelayed(r, 4000);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// 백키가 먹히면 안된다.
		Intent i;
		if (util.isRegistration()) i = new Intent(IntroActivity.this, MainListActivity.class);
		else i = new Intent(IntroActivity.this, LoginActivity.class);
		startActivity(i);
		finish();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		handler.removeCallbacks(r);
		Intent i;
		if (util.isRegistration()) i = new Intent(IntroActivity.this, MainListActivity.class);
		else i = new Intent(IntroActivity.this, LoginActivity.class);
		startActivity(i);
		finish();		
		return true;
	}

	class IntroRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent i;
			if (util.isRegistration()) i = new Intent(IntroActivity.this, MainListActivity.class);
			else i = new Intent(IntroActivity.this, LoginActivity.class);
			startActivity(i);
			finish();
		}
		
	}
	
}