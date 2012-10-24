package kr.co.drdesign.parmtree;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener {

	EditText idText;
	EditText passwordText;
	EditText confirmText;
	EditText emailText;
	Spinner regionSpinner;
	
	Button regBtn;
	Button initBtn;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_page);
		
		idText = (EditText) findViewById(R.id.regidtext);
		passwordText = (EditText) findViewById(R.id.regpasswordtext);
		confirmText = (EditText) findViewById(R.id.regconfirmtext);
		emailText = (EditText) findViewById(R.id.regmailtext);
		regBtn = (Button) findViewById(R.id.regokbtn);
		initBtn = (Button) findViewById(R.id.regclearbtn);
		regionSpinner = (Spinner) findViewById(R.id.regregionspinner);
		
		String regionItems[] = getResources().getStringArray(R.array.RegionTypeValues);
		ArrayAdapter<String> regionAdapter = 
			new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regionItems);
		regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		regionSpinner.setPrompt(getString(R.string.searchTxtSelectRegion));
		regionSpinner.setAdapter(regionAdapter);
		regBtn.setOnClickListener(this);
		initBtn.setOnClickListener(this);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.regokbtn :
			registration();
			break;
		case R.id.regclearbtn :
			Toast.makeText(this, R.string.msgTxtInit, Toast.LENGTH_SHORT).show();
			init();
			break;
		}
	}
	

	void registration() {
		if (idText.length() < 5) {
			idText.setText("");
			Toast.makeText(this, R.string.loginTxtid, Toast.LENGTH_SHORT).show();
			return; 
		}
		if (passwordText.length() < 6) {
			passwordText.setText("");
			confirmText.setText("");
			Toast.makeText(this, R.string.loginTxtpw, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!passwordText.getText().toString().equals(confirmText.getText().toString())) {
			passwordText.setText("");
			confirmText.setText("");
			Toast.makeText(this, R.string.loginTxtpwnotsame, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!emailText.getText().toString().contains("@")) {
			Toast.makeText(this, R.string.loginTxtmail, Toast.LENGTH_SHORT).show();
			return;
		}
		finish();
	}
	
	void init() {
		idText.setText("");
		passwordText.setText("");
		confirmText.setText("");
		emailText.setText("");
		regionSpinner.setSelection(0, true);
	}

}
