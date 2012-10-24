package kr.co.chunhoshop;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kr.co.chunhoshop.util.ChunhoUtil;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class SampleActivity extends Activity implements OnClickListener,
		ParserTag {

	SharedPreferences mPref;
	SharedPreferences.Editor mEdit;

	String name;
	String phone;
	String product;
	String pnum;

	EditText sName;
	EditText sPhone;
	EditText sProduct;
	EditText sProduct2;

	ItemParser iParser = new ItemParser();
	SampleThread sThread = new SampleThread();
	SampleHandler sHandler = new SampleHandler();

	ProgressDialog loading;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPref = getSharedPreferences(AAUTO, MODE_PRIVATE);
		mEdit = mPref.edit();
		setContentView(R.layout.sample);
		Intent i = getIntent();
		product = i.getStringExtra(PNAME);
		pnum = i.getStringExtra(PNUM);

		sName = (EditText) findViewById(R.id.samplenametext);
		sPhone = (EditText) findViewById(R.id.samplecalltext);
		sProduct = (EditText) findViewById(R.id.sampleproducttext);
		sProduct2 = (EditText) findViewById(R.id.sampleproducttext2);

		sName.setText(mPref.getString(ANAME, ""));
		sPhone.setText(mPref.getString(ATEL, ""));

		loading = new ProgressDialog(this);
		loading.setMessage("신청 중입니다. 잠시만 기다려주세요.");
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		ChunhoUtil.setOnClickListener((ViewGroup) getWindow().getDecorView(), this);
	}

	void sending() {
		if (sName.length() < 2) {
			Toast.makeText(this, "이름을 두글자 이상 입력해주세요.", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (sPhone.length() < 10) {
			Toast.makeText(this, "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
			return;
		} else if (sProduct.length() < 2) {
			Toast.makeText(this, "제품명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!loading.isShowing())
			loading.show();
		sThread = new SampleThread();
		sThread.start();
	}

	class SampleThread extends Thread {
		@Override
		public void run() {
			try {
				String request = SAMPLESEND
						+ "p_num="
						+ pnum
						+ "&"
						+ "u_id="
						+ mPref.getString("id", "")
						+ "&"
						+ "u_name="
						+ URLEncoder.encode(sName.getText().toString(),
								CHARACTER_SET)
						+ "&"
						+ "u_mobile="
						+ sPhone.getText().toString()
						+ "&"
						+ "u_memo="
						+ URLEncoder.encode(sProduct.getText().toString(),
								CHARACTER_SET)
						+ ","
						+ URLEncoder.encode(sProduct2.getText().toString(),
								CHARACTER_SET);
				iParser.load(request);
				if (iParser.getResult().contains("true")) {
					sHandler.sendEmptyMessage(0);
				} else {
					sHandler.sendEmptyMessage(1);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SampleHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				AlertDialog.Builder builder = new AlertDialog.Builder(SampleActivity.this);
				builder.setTitle(R.string.alert);
				builder.setMessage(R.string.samplecomplete);
				builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
					}
				}).show();
				break;
			case 1:
				Toast.makeText(SampleActivity.this,
						"샘플 신청에 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (!(v instanceof EditText)) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(sName.getWindowToken(), 0);
			mgr.hideSoftInputFromWindow(sPhone.getWindowToken(), 0);
			mgr.hideSoftInputFromWindow(sProduct.getWindowToken(), 0);
			mgr.hideSoftInputFromWindow(sProduct2.getWindowToken(), 0);
		}
		
		switch (v.getId()) {
		case R.id.samplebackbtn:
			finish();
			break;
		case R.id.samplecommitbtn:
			sending();
			break;
		}
	}

}
