package kr.co.drdesign.parmtree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	EditText tvId;
	EditText tvPassword;
	
	Button loginBtn;
	Button registrationBtn;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_view);
		
		tvId = (EditText) findViewById(R.id.loginiddtext);
		tvPassword = (EditText) findViewById(R.id.loginpwtext);
		
		loginBtn = (Button) findViewById(R.id.loginbtn);
		registrationBtn = (Button) findViewById(R.id.joinbtn);
		
		loginBtn.setOnClickListener(this);
		registrationBtn.setOnClickListener(this);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginbtn :
			login();
			// 로그인 관련 서버 요청
			break;
		case R.id.joinbtn :
			startActivity(new Intent(this, NameCheckActivity.class));
			// 회원가입창 호출
			break;
		}
	}
	
	void login() {
//		if (tvId.length() < 5) {
//			Toast.makeText(this, R.string.loginTxtid, Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if (tvPassword.length() < 6) {
//			Toast.makeText(this, R.string.loginTxtpw, Toast.LENGTH_SHORT).show();
//			return;
//		}
		finish();
		startActivity(new Intent(this, MainListActivity.class));
	}

	
}
