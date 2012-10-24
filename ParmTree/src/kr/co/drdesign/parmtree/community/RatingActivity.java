package kr.co.drdesign.parmtree.community;

import android.app.Activity;

public class RatingActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.getParent().onBackPressed();
	}

}
