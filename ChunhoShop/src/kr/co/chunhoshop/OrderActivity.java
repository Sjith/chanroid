package kr.co.chunhoshop;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import kr.co.chunhoshop.util.CartDBController;
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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends Activity implements ParserTag,
		OnClickListener {

	SharedPreferences mPref;
	SharedPreferences.Editor mEdit;

	ArrayList<Map<String, String>> orderArray;
	AlertDialog.Builder dialog;
	ProgressDialog loading;
	String time;
	String gnum;
	String gname;

	CartDBController db;

	LinearLayout orderagent;
	TextView orderagentText;
	ImageView orderagentPic;

	// EditText memo;
	String memotext = "";


	EditText nameText;
	EditText phoneText;

	ItemParser iParser = new ItemParser();
	OrderThread oThread = new OrderThread();
	OrderHandler oHandler = new OrderHandler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPref = getSharedPreferences(AAUTO, MODE_PRIVATE);
		mEdit = mPref.edit();
		setContentView(R.layout.order);
		final String times[] = new String[] { "09:00~10:00", "10:00~11:00",
				"11:00~12:00", "12:00~13:00", "13:00~14:00", "14:00~15:00",
				"15:00~16:00", "16:00~17:00", "17:00~18:00", "확인즉시 연락요함" };
		orderArray = (ArrayList<Map<String, String>>) getIntent()
				.getSerializableExtra("data");
		gnum = getIntent().getStringExtra("gnum");
		gname = getIntent().getStringExtra("gname");
		orderagent = (LinearLayout) findViewById(R.id.orderagent);
		orderagentPic = (ImageView) findViewById(R.id.orderagentpic);
		orderagentText = (TextView) findViewById(R.id.orderagenttext);
		db = CartDBController.getInstance(getApplicationContext());

		nameText = (EditText) findViewById(R.id.oeditText1);
		phoneText = (EditText) findViewById(R.id.oeditText2);

		nameText.setText(mPref.getString(ANAME, ""));
		phoneText.setText(mPref.getString(ATEL, ""));

		loading = new ProgressDialog(this);
		loading.setMessage("서버의 응답을 기다리는 중입니다.");
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.setCancelable(false);

		if (mPref.getString(AGENT, "").equalsIgnoreCase("")) {
			orderagentText.setVisibility(View.GONE);
		} else if (mPref.getString(AGENT, "").equalsIgnoreCase("천호쇼핑")) {
			StringBuilder sb = new StringBuilder("");
			sb.append("( " + "상담시간 선택" + " )" + "\n");
			sb.append("천호쇼핑 헬스플래너가\n");
			sb.append("연락드리겠습니다. 감사합니다.");
			orderagentText.setText(sb);
		} else {
			StringBuilder sb = new StringBuilder("");
			sb.append("( " + "상담시간 선택" + " )" + "\n");
			sb.append("담당자 " + mPref.getString(AGENT, "") + " 헬스플래너가\n");
			sb.append("연락드리겠습니다. 감사합니다.");
			orderagentText.setText(sb);
		}

		if (mPref.getString(AGENTPIC, "").equalsIgnoreCase("")) {
			orderagentPic.setVisibility(View.GONE);
		} else {
			new PicSync().execute(mPref.getString(AGENTPIC, ""));
		}

		dialog = new AlertDialog.Builder(this);
		dialog.setTitle("상담시간 예약");
		dialog.setItems(times, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				time = times[which];
				String agent = mPref.getString(AGENT, "");
				if (which == 9) {
					StringBuilder sb = new StringBuilder("");
					if (agent.equalsIgnoreCase("")) {
						sb.append("(" + time + ")" + "\n");
						sb.append("천호쇼핑 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					} else if (agent.equalsIgnoreCase("천호쇼핑")) {
						sb.append("(" + time + ")" + "\n");
						sb.append("천호쇼핑 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					} else {
						sb.append("(" + time + ")" + "\n");
						sb.append("담당자 " + mPref.getString(AGENT, "")
								+ " 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					}

					orderagentText.setText(sb);
					orderagentText.setVisibility(View.VISIBLE);
					dialog.dismiss();
				} else {
					Toast.makeText(OrderActivity.this,
							"상담시간이 " + time + " (으)로 설정되었습니다.",
							Toast.LENGTH_SHORT).show();

					StringBuilder sb = new StringBuilder("");
					if (agent.equalsIgnoreCase("")) {
						sb.append(time + " 사이에\n");
						sb.append("천호쇼핑 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					} else if (agent.equalsIgnoreCase("천호쇼핑")) {
						sb.append(time + " 사이에\n");
						sb.append("천호쇼핑 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					} else {
						sb.append(time + " 사이에\n");
						sb.append("담당자 " + mPref.getString(AGENT, "")
								+ " 헬스플래너가\n");
						sb.append("연락드리겠습니다. 감사합니다.");
					}

					orderagentText.setText(sb);
					orderagentText.setVisibility(View.VISIBLE);
					dialog.dismiss();
				}
			}
		});

		ChunhoUtil.setOnClickListener((ViewGroup) getWindow().getDecorView(),
				this);

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
			orderagentPic.setImageBitmap(result);
		}

	}

	void order() {
		if (!loading.isShowing())
			loading.show();
		oThread = new OrderThread();
		oThread.start();
	}

	void orderComplete() {
		if (loading.isShowing())
			loading.dismiss();
		String result = iParser.getResult();
		try {
			if (result.contains("true")) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("알림");
				dialog.setMessage(R.string.ordercomplete);
				dialog.setPositiveButton(R.string.done,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								for (int i = 0; i < orderArray.size(); i++) {
									db.delete(Integer.parseInt(orderArray
											.get(i).get("_id")));
								}
								try {
									MyCartActivity.showBuyList();
								} catch (Exception e) {
								}
								dialog.dismiss();
								finish();
							}
						}).show();
			} else {
				Toast.makeText(this, "상품 주문에 실패했습니다. 잠시 후 다시 시도해 주세요.",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "상품 주문에 실패했습니다. 잠시 후 다시 시도해 주세요.",
					Toast.LENGTH_SHORT).show();
		}
	}

	class OrderThread extends Thread {
		@Override
		public void run() {
			StringBuilder builder = new StringBuilder(ORDERPAGE);
			builder.append("o_id=" + mPref.getString("id", "") + "&");
			builder.append("o_name=" + encode(nameText.getText().toString())
					+ "&");
			builder.append("o_phone=" + phoneText.getText().toString() + "&");
			builder.append("o_calltime=" + encode(time) + "&");
			builder.append("o_memo=" + encode(memotext) + "&");
			builder.append("g_num=" + gnum + "&");
			builder.append("g_name=" + encode(gname));

			builder.append("&o_product=");

			for (int i = 0; i < orderArray.size(); i++) {
				if (i == orderArray.size() - 1) {
					builder.append(orderArray.get(i).get(PNUM));
				} else {
					builder.append(orderArray.get(i).get(PNUM) + ",");
				}
			}

			builder.append("&o_count=");

			for (int i = 0; i < orderArray.size(); i++) {
				if (i == orderArray.size() - 1) {
					builder.append(orderArray.get(i).get(PCOUNT));
				} else {
					builder.append(orderArray.get(i).get(PCOUNT) + ",");
				}
			}

			builder.append("&o_option_name=");

			for (int i = 0; i < orderArray.size(); i++) {
				if (i == orderArray.size() - 1) {
					builder.append(encode(orderArray.get(i).get(POPTIONNAME)));
				} else {
					builder.append(encode(orderArray.get(i).get(POPTIONNAME))
							+ ",");
				}
			}

			builder.append("&o_option_code=");

			for (int i = 0; i < orderArray.size(); i++) {
				if (i == orderArray.size() - 1) {
					builder.append(encode(orderArray.get(i).get(POPTIONVALUE)));
				} else {
					builder.append(encode(orderArray.get(i).get(POPTIONVALUE))
							+ ",");
				}
			}

			iParser.load(builder.toString());
			oHandler.sendEmptyMessage(0);
		}
	}

	String encode(CharSequence c) {
		try {
			return URLEncoder.encode(c.toString(), CHARACTER_SET);
			// return URLEncoder.encode(c.toString(), "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return c.toString();
		}
	}

	class OrderHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				orderComplete();
				break;
			case 1:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (!(v instanceof EditText)) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(nameText.getWindowToken(), 0);
			mgr.hideSoftInputFromWindow(phoneText.getWindowToken(), 0);
		}

		switch (v.getId()) {
		case R.id.ordermemobtn:

			final EditText memo = new EditText(this);
			memo.setGravity(Gravity.NO_GRAVITY);
			memo.setLines(5);
			memo.setMaxLines(5);
			memo.setText(memotext);

			AlertDialog.Builder builder;
			builder = new AlertDialog.Builder(this);
			builder.setTitle("메모");
			builder.setView(memo);
			builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					memotext = memo.getText().toString();
					Toast.makeText(OrderActivity.this, "메모가 입력되었습니다.",
							Toast.LENGTH_SHORT).show();
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(memo.getWindowToken(), 0);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case R.id.orderbackbtn:
			finish();
			break;
		case R.id.ordersettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {
			}
			finish();
			break;
		case R.id.obonusbtn:
			startActivity(new Intent(this, BonusActivity.class));
			break;
		case R.id.otimebtn:
			dialog.show();
			break;
		case R.id.orderbtn:
			if (nameText.length() < 2) {
				Toast.makeText(this, "주문자명을 입력해 주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (phoneText.length() < 7) {
				Toast.makeText(this, "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (time == null) {
				Toast.makeText(this, "상담시간을 선택해 주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			} else {
				order();
			}
			break;
		}
	}

}
