package kr.co.drdesign.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.client.connector.HttpConnector;
import kr.co.drdesign.client.connector.WebConnector;
import kr.co.drdesign.client.service.ArgosService;
import kr.co.drdesign.client.service.Communicator;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

// 화원가입이후 기기등록을 해주는 Class
public class RegistrationActivity extends Activity implements Loggable{

	private static final String CLASS_NAME = "RegistrationActivity";
	
	private EditText 	rsEtEmail;
	private EditText 	rsEtPw;
	private EditText 	rsEtPw2;
	private EditText 	rsEtName;
	private EditText	rsEtPhoneNo;
	private Button 		rsBtnRegistration;
	private CheckBox 	rsCbWebRegistration;
	private LinearLayout rsMain;
	
	private String 		lineNumber;
	private boolean		rsisRegisted = false;
	private WebConnector mConnector;
	private GruviceUtillity gUtil;
	
	private RegistrationThread rsThread;
	private RegistrationHandler rsHandler;
	
	private ProgressDialog rsdlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L(CLASS_NAME + " onCreate()");
		
		setContentView(R.layout.registration_view);

		mConnector 	= new HttpConnector();
		gUtil = GruviceUtillity.getInstance(getApplicationContext());
		rsEtEmail 	= (EditText) findViewById(R.id.rsEtEmail);
		rsEtPw 		= (EditText) findViewById(R.id.rsEtPw);
		rsEtPw2 	= (EditText) findViewById(R.id.rsEtPw2);
		rsEtName 	= (EditText) findViewById(R.id.rsEtName);
		rsEtPhoneNo = (EditText) findViewById(R.id.rsEtPhoneNo);
		rsBtnRegistration 	= (Button) findViewById(R.id.rsBtnRegistration);
		rsCbWebRegistration = (CheckBox) findViewById(R.id.rsCbWebRegist);
		
		
		if (gUtil.getPhoneNumber() != "") {
			rsEtPhoneNo.setText(gUtil.getPhoneNumber());
			rsEtPhoneNo.setEnabled(false);
		}
		
		// 2011-06-01 등록화면 배경 설정
		rsMain = (LinearLayout) findViewById(R.id.regMain);
		setBackGround();
		
		rsCbWebRegistration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				rsEtPw2.setEnabled(!(isChecked));
				rsEtName.setEnabled(!(isChecked));
				if (isChecked) rsisRegisted = true;
				else rsisRegisted = false;
			}
		});

		showRegistrationDialog();
		rsBtnRegistration.setOnClickListener(new BtnRegistrationListener());
	}
	
	private void setBackGround() {
		rsMain.setBackgroundResource(R.drawable.v2_ml_bg);
	}

	private void showRegistrationDialog() {
		if (gUtil.isRegistration()) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this)
			.setTitle(R.string.txtAlret)
			.setMessage(R.string.rsTxtAlreadyRegisted)
			.setCancelable(false)
			.setPositiveButton(R.string.mnBtnRegistration, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					rsEtEmail.setText("");
					rsEtPw.setText("");
				}
			})
			.setNegativeButton(R.string.txtCancel, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			ab.show();
		}
	}
	
	class BtnRegistrationListener implements View.OnClickListener
	{
		public void onClick(View v) {
			String email = rsEtEmail.getText().toString();
			String pw = rsEtPw.getText().toString();
			String name = rsEtName.getText().toString();

			String clientId = gUtil.getClientId();
			lineNumber = rsEtPhoneNo.getText().toString();

			//조건 검사 알림창
			if( email.length() < 3 )
			{
				Toast.makeText(getApplicationContext(), R.string.rsTxtMailtooshort, Toast.LENGTH_SHORT).show();
				return;
			}
			if (lineNumber.length() < 10) {
				Toast.makeText(getApplicationContext(), R.string.rsTxtPhoneNotooshort, Toast.LENGTH_SHORT).show();
				rsEtPhoneNo.setText(""); // initialize text. for rewrite password.
				return;
			}
			if( rsCbWebRegistration.isChecked() == false  
					&& pw.equals(rsEtPw2.getText().toString()) == false )
			{
				Toast.makeText(getApplicationContext(), R.string.rsTxtPasswordtooshort, Toast.LENGTH_SHORT).show();
				rsEtPw.setText(""); rsEtPw2.setText(""); // initialize text. for rewrite password.
				return;
			}
			if ( rsCbWebRegistration.isChecked() == false 
					&& name.length() < 2  )
				// 2011-05-24 3글자 제한으로 하면 이름 외자인사람은 어쩔?
			{
				Toast.makeText(getApplicationContext(), R.string.rsTxtNameisempty, Toast.LENGTH_SHORT).show(); 
				return;
			}

			Map<String, String> post = new HashMap<String, String>();
			post.put("mname", name);
			post.put("memail", email);
			post.put("mpassword", pw);
			post.put("mphoneid", clientId);
			post.put("mphoneno", lineNumber);
			// 2011-05-13 폰 종류 확인을 위한 값.
			// 안드로이드-1, 윈모-2, 아이폰-3
			post.put("mwhatphone", "1");

			rsHandler = new RegistrationHandler(post);
			rsThread = new RegistrationThread(post);
			showLoadingDialog();
			rsThread.start();
			
		}
	}
	

	// 2011-06-01 그룹정보 로딩 다이얼로그
	public void showLoadingDialog() {
		rsdlg = null;
		rsdlg = new ProgressDialog(this);
		rsdlg.setCancelable(true);
		rsdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		rsdlg.setMessage("요청을 기다리는 중입니다.");
		// 이부분은 int형의 인자값을 받는 메서드가 없음. 일단 int값으로 문자 객체를 만들어야함.
		rsdlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				rsHandler.sendEmptyMessage(1);
			}
		});
		rsdlg.show();
	}
	
	class RegistrationThread extends Thread {
		
		Map<String, String> post;

		RegistrationThread (Map<String, String> post) {
			this.post = post;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String URL;
			if (rsisRegisted) URL = Communicator.SERVERURL + "verifyMem.jsp";
			else URL = Communicator.SERVERURL + "addMem.jsp";
			try {
				String result = mConnector.readFromURL(URL, "", "", post).toString();
//				String result = mConnector.decodeFromURL(URL, "", "", post).toString();
				Log.i("Gruvice", result);
				//알림창
				if( result.contains("succeed")) 
				{
					// 2011-05-13 세팅은 건들지 말고 어플 켤때마다 로그인 확인을 해야할 것 같다.
					// 2011-06-07 remove "hard coding" strings.
					gUtil.setRegistration(true);
					gUtil.setEmail(post.get("memail"));
					gUtil.setPassword(post.get("mpassword"));				
					gUtil.setPhoneNumber(lineNumber);
					rsHandler.sendEmptyMessage(0);
				} else if (result.contains("failed")) {
					rsHandler.sendEmptyMessage(1);
				}
			} catch (IOException e) {
				rsHandler.sendEmptyMessage(1);
				Log.e(DEBUG_TAG, e.getMessage());
			}
		}
	}
	
	class RegistrationHandler extends Handler {
		
		Map<String,String> post;
		
		RegistrationHandler (Map<String,String> post) {
			this.post = post;
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0 : 
				// 성공
				Toast.makeText(getApplicationContext(), R.string.rsTxtRegistrationSucess, Toast.LENGTH_SHORT).show();
				rsThread.interrupt();
				rsdlg.dismiss();
				ArgosService.actionStart(getApplicationContext());
				setResult(RESULT_OK);
				finish(); 
				break;
			case 1 :
				Toast.makeText(getApplicationContext(), R.string.rsTxtRegistrationFailure, Toast.LENGTH_SHORT).show();
				rsThread.interrupt();
				rsThread = new RegistrationThread(post);
				rsdlg.dismiss();
				break;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!gUtil.isRegistration()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(RegistrationActivity.this);
			dialog.setTitle(R.string.txtAlret);
			dialog.setMessage(R.string.rsTxtReallyNotRegistration);
			dialog.setPositiveButton(R.string.txtYES, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					setResult(RESULT_CANCELED);
					dialog.dismiss();
					Log.i("Gruvice", "result Cancel");
					finish();
				}
			});
			dialog.setNegativeButton(R.string.txtNO, null);
			dialog.show();
		} else {
			super.onBackPressed();
		}
	}

	public void L(char i, String log) {
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}