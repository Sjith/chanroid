package kr.co.drdesign.parmtree;

import android.app.Activity;

public class NoticeActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getParent().onBackPressed();
	}

}
