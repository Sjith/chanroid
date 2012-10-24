package kr.co.chunhoshop;

import java.util.Map;

import kr.co.chunhoshop.util.ChunhoUtil;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener,
		ParserTag {

	SharedPreferences mPref;
	Editor mEditor;

	LoginThread lThread = new LoginThread();
	LoginHandler lHandler = new LoginHandler();
	ItemParser iParser = new ItemParser();

	TextView loginnameText;
	EditText memberIdEdit;
	EditText memberPwEdit;
	ProgressDialog dialog;

	Button loginbtn;

	LinearLayout loginagent;
	TextView loginagentText;
	ImageView loginagentPic;

	RelativeLayout loginForm;

	CheckedTextView auto;

	String id;
	String pw;
	String autoLogin;
	String phoneNo;
	String agent;

	Map<String, CharSequence> result;

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
		mEditor = mPref.edit();
		setContentView(R.layout.setting);
		loginbtn = (Button) findViewById(R.id.loginbtn);
		memberIdEdit = (EditText) findViewById(R.id.settingidedit);
		memberIdEdit.setHintTextColor(Color.LTGRAY);
		memberPwEdit = (EditText) findViewById(R.id.settingpwedit);
		memberPwEdit.setHintTextColor(Color.LTGRAY);
		auto = (CheckedTextView) findViewById(R.id.autologincheck);
		auto.setOnClickListener(this);
		loginForm = (RelativeLayout) findViewById(R.id.loginform);
		loginnameText = (TextView) findViewById(R.id.loginnametext);
		loginagent = (LinearLayout) findViewById(R.id.loginagent);
		loginagentPic = (ImageView) findViewById(R.id.loginagentpic);
		loginagentText = (TextView) findViewById(R.id.loginagenttext);

	}

	@Override
	public void onResume() {
		super.onResume();
		if (mPref.getString(ANAME, "").equalsIgnoreCase("")) {
			// 로그인 안된 상태
			loginagent.setVisibility(View.GONE);
			loginnameText.setVisibility(View.GONE);
			loginForm.setVisibility(View.VISIBLE);
			memberIdEdit.setEnabled(true);
			memberPwEdit.setEnabled(true);
			((TextView)findViewById(R.id.loginmembonustxt)).setText(R.string.membonus1);
			findViewById(R.id.loginnamelayout).setVisibility(View.GONE);
			findViewById(R.id.logininfo1).setVisibility(View.VISIBLE);
			findViewById(R.id.logininfo2).setVisibility(View.VISIBLE);
			loginbtn.setText("로그인");
		} else {
			// 로그인 된 상태.
			loginagent.setVisibility(View.VISIBLE);
			findViewById(R.id.loginnamelayout).setVisibility(View.VISIBLE);
			loginForm.setVisibility(View.GONE);
			loginnameText.setVisibility(View.VISIBLE);
			loginnameText.setText(mPref.getString(ANAME,
					mPref.getString("id", ""))
					+ " 님 환영합니다!");
			((TextView)findViewById(R.id.loginmembonustxt)).setText(R.string.membonus2);
			findViewById(R.id.logininfo1).setVisibility(View.GONE);
			findViewById(R.id.logininfo2).setVisibility(View.GONE);
			memberIdEdit.setEnabled(false);
			memberPwEdit.setEnabled(false);
			loginbtn.setText("로그아웃");
		}

		if (mPref.getString(AGENT, "").equalsIgnoreCase("")) {
			// 상담원이 없는 상태
			loginagentText.setVisibility(View.GONE);
		} else if(mPref.getString(AGENT, "").equalsIgnoreCase("천호쇼핑")) {
			// 상담원이 천호쇼핑인 상태
			StringBuilder sb = new StringBuilder("");
			sb.append(mPref.getString(ANAME, mPref.getString("id", "")));
			sb.append(" 회원님의 건강을 위한\n");
			sb.append("천호쇼핑 고객센터 입니다.\n");
			sb.append("\n정성껏 친절히 상담하겠습니다.");
			loginagentText.setText(sb.toString());			
		} else { 
			// 상담원이 있는 상태
			StringBuilder sb = new StringBuilder("");
			sb.append(mPref.getString(ANAME, mPref.getString("id", "")));
			sb.append(" 님의 건강담당자\n");
			sb.append("헬스플래너 " + mPref.getString(AGENT, "") + " 입니다.\n");
			sb.append("\n정성껏 친절히 상담하겠습니다.");
			loginagentText.setText(sb.toString());
		}

		if (mPref.getString(AGENTPIC, "").equalsIgnoreCase("")) {
			loginagentPic.setVisibility(View.GONE);
		} else {
			new PicSync().execute(mPref.getString(AGENTPIC, ""));
		}

		ChunhoUtil.setOnClickListener((ViewGroup) getWindow().getDecorView(),
				this);

		autoLogin = mPref.getString("autologin", "Y");
		if (autoLogin.equals("N"))
			auto.setChecked(false);
		else {
			id = mPref.getString("id", "");
			pw = mPref.getString("pw", "");
			memberIdEdit.setText(id);
			memberPwEdit.setText(pw);
			auto.setChecked(true);
		}
	}

	class PicSync extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap b = ParserUtil.getImageURLAbsolutePath(params[0]);
			return b;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result == null)
				return;
			loginagentPic.setImageBitmap(result);
		}

	}

	void loading() {
		id = memberIdEdit.getText().toString();
		pw = memberPwEdit.getText().toString();
		if (id.length() < 1) {
			Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		} else if (pw.length() < 1) {
			Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		dialog = new ProgressDialog(this);
		dialog.setMessage("로그인 중입니다...");
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		lThread = new LoginThread();
		lThread.start();
	}

	void loadComplete() {
		dialog.dismiss();
		result = iParser.get();

		if (result.get(ASTATE) == null) {
			Toast.makeText(this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		if (result.get(ASTATE).equals("N")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("로그인 실패")
					.setMessage(
							"없는 아이디이거나 비밀번호가 맞지 않습니다. "
									+ "계정 정보를 분실하신 경우 홈페이지에서 아이디/비밀번호 찾기 기능을"
									+ " 이용해 주시기 바랍니다.")
					.setPositiveButton(R.string.done, null).show();
		} else {
			loginbtn.setText("로그인");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("로그인")
					.setMessage("로그인에 성공하였습니다!")
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									mEditor.putString("id", id);
									mEditor.putString("pw", pw);
									mEditor.putString(ANAME,
											(String) result.get(ANAME));
									// loginnameText.setText(result.get(ANAME)
									// + " 님 환영합니다!");
									// memberIdEdit.setEnabled(false);
									// memberPwEdit.setEnabled(false);
									mEditor.putString(ATEL,
											(String) result.get(ATEL));
									mEditor.putString(AGENT, result.get(AGENT)
											.toString());
									mEditor.putString(AGENTPIC,
											result.get(AGENTPIC).toString());
									mEditor.commit();
									InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									mgr.hideSoftInputFromWindow(
											memberIdEdit.getWindowToken(), 0);
									mgr.hideSoftInputFromWindow(
											memberPwEdit.getWindowToken(), 0);
									SettingActivity.this.onBackPressed();
								}
							}).show();
		}
	}

	class LoginThread extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			iParser.load(LOGINPAGE + "id=" + id + "&pw=" + pw + "&auto="
					+ autoLogin + "&mobile=" + phoneNo);
			// iParser.load(LOGINPAGE
			// + "id=wisetail&pw=vkdrms11&auto=y&mobile=010-4787-5718");
			// 테스트용 데이터.
			lHandler.sendEmptyMessage(0);
		}

	}

	class LoginHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			loadComplete();
		}

	}

	@Override
	public void onBackPressed() {
		try {
			((ChunhoTabActivity) getParent()).setTab();
		} catch (Exception e) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (!(v instanceof EditText)) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(memberIdEdit.getWindowToken(), 0);
			mgr.hideSoftInputFromWindow(memberPwEdit.getWindowToken(), 0);
		}

		switch (v.getId()) {
		case R.id.settingbackbtn:
			try {
				((ChunhoTabActivity) getParent()).setTab();
			} catch(Exception e) {
				finish();
			}
			break;
		case R.id.autologincheck:
			auto.setChecked(!auto.isChecked());
			if (auto.isChecked())
				autoLogin = "Y";
			else
				autoLogin = "N";
			mEditor.putString("autologin", autoLogin);
			mEditor.commit();
			break;
		case R.id.loginnamebtn:

			AlertDialog.Builder alert1 = new AlertDialog.Builder(this);
			alert1.setTitle("로그아웃");
			alert1.setMessage("로그아웃 하시겠습니까?");
			alert1.setNegativeButton("취소",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			alert1.setPositiveButton(R.string.done,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							mEditor.putString("id", "");
							mEditor.putString("pw", "");
							mEditor.putString(ANAME, "");
							mEditor.putString(ATEL, "");
							mEditor.commit();
							AlertDialog.Builder builder = new AlertDialog.Builder(
									SettingActivity.this);
							builder.setTitle("로그아웃")
									.setMessage("로그아웃 되었습니다.")
									.setPositiveButton(
											R.string.done,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													dialog.dismiss();
													SettingActivity.this
															.onBackPressed();
												}
											}).show();
						}
					}).show();
			break;
		case R.id.loginbtn:
			if (mPref.getString("id", "").equalsIgnoreCase("")) {
				loading();
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("로그아웃");
				alert.setMessage("로그아웃 하시겠습니까?");
				alert.setNegativeButton("취소",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				alert.setPositiveButton(R.string.done,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								mEditor.putString("id", "");
								mEditor.putString("pw", "");
								mEditor.putString(ANAME, "");
								mEditor.commit();
								AlertDialog.Builder builder = new AlertDialog.Builder(
										SettingActivity.this);
								builder.setTitle("로그아웃")
										.setMessage("로그아웃 되었습니다.")
										.setPositiveButton(
												R.string.done,
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														// TODO Auto-generated
														// method stub
														dialog.dismiss();
														SettingActivity.this
																.onBackPressed();
													}
												}).show();
							}
						}).show();
			}

			break;
		case R.id.memberbonusbtn:
			startActivity(new Intent(this, BonusActivity.class));
			break;
		}
	}
}
