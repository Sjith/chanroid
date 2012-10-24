package kr.co.chunhoshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class WorldActivity extends Activity implements OnClickListener {

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.worldbackbtn:
			finish();
			break;
		case R.id.worldbtn1:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("전화연결")
			.setMessage("누를수록 건강해지는 번호\n[080-607-1005]\n전화 연결 하시겠습니까?")
			.setNegativeButton("취소", null)
			.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Intent.ACTION_CALL);
					i.setData(Uri.parse("tel:0806071005"));
					startActivity(i);
				}
			}).show();
			break;
		case R.id.worldsettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {
				Intent setting = new Intent(this, ChunhoTabActivity.class);
				setting.setAction("setting");
				startActivity(setting);
			}
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.world);
	}

}
