package kr.co.drdesign.parmtree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameCheckActivity extends Activity implements OnClickListener {

	EditText nameText;
	EditText personnumText1;
	EditText personnumText2;
	Button checkBtn;
	Button initBtn;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.namecheck_view);
		
		nameText = (EditText) findViewById(R.id.checknametext);
		personnumText1 = (EditText) findViewById(R.id.checknumtext1);
		personnumText2 = (EditText) findViewById(R.id.checknumtext2);
		checkBtn = (Button) findViewById(R.id.checkbtn);
		initBtn = (Button) findViewById(R.id.checkinitbtn);
		
		checkBtn.setOnClickListener(this);
		initBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.checkbtn :
			checkName();
			break;
		case R.id.checkinitbtn :
			init();
			break;
		}
	}
	
	void checkName() {
		if (nameText.length() < 2) {
			Toast.makeText(this, R.string.msgTxtUsername, Toast.LENGTH_SHORT).show();
			return;
		}
		if (personnumText1.length() < 6) {
			Toast.makeText(this, R.string.loginTxtpernum, Toast.LENGTH_SHORT).show();
			return;
		}
		if (personnumText2.length() < 7) {
			Toast.makeText(this, R.string.loginTxtpernum, Toast.LENGTH_SHORT).show();
			return;
		}
		finish();
		startActivity(new Intent(this, RegistrationActivity.class));
	}
	
	void init() {
		nameText.setText("");
		personnumText1.setText("");
		personnumText2.setText("");
		Toast.makeText(this, R.string.msgTxtInit, Toast.LENGTH_SHORT).show();
	}

}
