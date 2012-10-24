package kr.co.chunhoshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class IntroActivity extends Activity implements OnTouchListener {

	HorizontalScrollView bottomscroll;
	Runnable r;
	Handler go;
	Runnable top3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		RelativeLayout touch = (RelativeLayout) findViewById(R.id.intromain);
		touch.setOnTouchListener(this);

		go = new Handler();
		r = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				startActivity(new Intent(IntroActivity.this, MainActivity.class));
			}
		};

		go.postDelayed(r, 6000);
		// start activity post

		Runnable top2 = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Animation ani = AnimationUtils.loadAnimation(
						IntroActivity.this, R.anim.intro_in);
				findViewById(R.id.introtop2).setVisibility(View.VISIBLE);
				findViewById(R.id.introtop2).startAnimation(ani);
			}
		};
		go.postDelayed(top2, 2000);

		top3 = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ImageView iv = (ImageView) findViewById(R.id.introtop2);
				iv.setImageResource(R.drawable.intro_03_r);
//				MediaPlayer mp = MediaPlayer.create(IntroActivity.this,
//						R.raw.fx);
//				mp.setLooping(false);
//				mp.start();
			}
		};
		go.postDelayed(top3, 4000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		go.removeCallbacks(r);
		go.removeCallbacks(top3);
		finish();
		// overridePendingTransition(R.anim.intro_in, R.anim.intro_out);
		startActivity(new Intent(IntroActivity.this, MainActivity.class));
		return false;
	}

}