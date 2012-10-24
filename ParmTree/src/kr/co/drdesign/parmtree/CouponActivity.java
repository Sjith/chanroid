package kr.co.drdesign.parmtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.database.CouponController;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CouponActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, c {

	ProgressDialog loading;
	CheckedTextView alignCheck;
	ListView couponList;

	ArrayList<Map<String, String>> couponArray;

	EstablishController estCtrl;
	CouponController couponCtrl;
	CouponThread couponThread;
	CouponHandler couponHandler;
	CouponAdapter couponAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupon_list);

		alignCheck = (CheckedTextView) findViewById(R.id.couponaligncheck);
		alignCheck.setOnClickListener(this);

		couponList = (ListView) findViewById(R.id.couponlistView1);
		couponList.setCacheColorHint(0);
		couponArray = new ArrayList<Map<String, String>>();
		couponThread = new CouponThread();
		couponHandler = new CouponHandler();
		couponCtrl = CouponController.getInstance(getApplicationContext());
		estCtrl = EstablishController.getInstance(getApplicationContext());

		load();
	}

	void showLoadingDialog() {
		loading = null;
		loading = new ProgressDialog(getParent().getParent());
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.setMessage(getString(R.string.searchTxtResponseToServer));
		loading.show();
	}

	void dismiss() {
		if (loading.isShowing()) loading.dismiss();
	}

	void load() {
		showLoadingDialog();
		couponThread = new CouponThread();
		couponThread.start();
	}

	void getList() {
		couponArray = couponCtrl.get();
	}

	void fillList() {
		// 생성된 ArrayList로 어댑터를 생성해서 화면에 뿌려준다.
		couponAdapter = new CouponAdapter(this, couponArray,
				R.layout.coupon_item, null, null);
		couponList.setAdapter(couponAdapter);
	}

	class CouponThread extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getList();
			couponHandler.sendEmptyMessage(0);
		}
	}

	class CouponHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				fillList();
				dismiss();
				break;
			}
		}

	}

	class CouponAdapter extends SimpleAdapter {

		public CouponAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 아주 무거울 것으로 예상되므로 뷰 관련 로직 말고는 되도록이면 쓰지 말자.
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.coupon_item, null);
			}

			ImageView couponBanner = (ImageView) view
					.findViewById(R.id.couponBanner);
			TextView estName = (TextView) view.findViewById(R.id.couponEstname);
			TextView enableDate = (TextView) view
					.findViewById(R.id.couponEnabledate);

			String name = estCtrl.get(
					Integer.valueOf(couponArray.get(position).get(ESTID)))
					.get(ESTNAME);
			String enabledate = couponArray.get(position).get(ENABLEDATE);
			String couponid = couponArray.get(position).get(COUPONID);

			estName.setText(name);
			enableDate.setText(enabledate);

			return view;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.getParent().onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.couponaligncheck:
			if (((CheckedTextView) v).isChecked()) {
				((CheckedTextView) v).setChecked(false);
			} else {
				((CheckedTextView) v).setChecked(true);
			}
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

}
