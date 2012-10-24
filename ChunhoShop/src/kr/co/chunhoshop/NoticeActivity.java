package kr.co.chunhoshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeActivity extends Activity implements OnItemClickListener,
		ParserTag {

	static ListView pListView;
	ProgressBar loading;
	ListParser parser;
	ArrayList<Map<String, CharSequence>> pList = new ArrayList<Map<String, CharSequence>>();

	NoticeThread pThread = new NoticeThread();
	NoticeHandler pHandler = new NoticeHandler();
	NoticeAdapter pAdapter;

	static FrameLayout desc;

	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.notice);
//		if (pListView == null) {
			pListView = (ListView) findViewById(R.id.noticelist);
			pListView.setOnItemClickListener(this);
			pListView.setDividerHeight(3);
			pListView.addFooterView(new View(this));
//		}
//		if (desc == null) {
			desc = (FrameLayout) findViewById(R.id.noticedesc);
//		}
		loading = (ProgressBar) findViewById(R.id.noticeprogress);
		parser = new ListParser();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.gc();
		super.onPause();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (desc.getVisibility() != View.GONE) {
			desc.setVisibility(View.GONE);
		}
		if (pList.size() > 0) {
			pList.clear();
			pAdapter.notifyDataSetChanged();
		}
		loading();
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		pThread = new NoticeThread();
		pThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		pList = parser.get();
		
		if (pList.size() == 0) {
			loading();
			return;
		}
		
		pAdapter = new NoticeAdapter(this, pList, R.layout.notice_item, null,
				null);
		pListView.setAdapter(pAdapter);
	}

	class NoticeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(NOTICELIST);
			pHandler.sendEmptyMessage(0);
		}
	}

	class NoticeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}

	}

	class NoticeAdapter extends SimpleAdapter {

		public NoticeAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto generated method stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.notice_item, null);
			}

			if (position % 2 == 0)
				view.setBackgroundColor(Color.WHITE);

			TextView title = (TextView) view.findViewById(R.id.noticelisttitle);
			title.setText(pList.get(position).get(NSUBJECT));

			String day = pList.get(position).get(NDATE).toString().split(" ")[0];
			
			TextView date = (TextView) view.findViewById(R.id.noticelistdate);
			date.setText(day);

			return view;
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (pThread.isAlive()) {
			pThread.interrupt();
		}
	}

	@Override
	public void onBackPressed() {
		if (desc.getVisibility() != View.GONE) {
			desc.setVisibility(View.GONE);
			pListView.setVisibility(View.VISIBLE);
		} else {
			super.onBackPressed();
		}
	}
	
	public static void closeDesc() {
		desc.setVisibility(View.GONE);		
		pListView.setVisibility(View.VISIBLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if (pList.get(position).get(NNUM) != null) {
			pListView.setVisibility(View.GONE);
			Intent i = new Intent(this, NoticeDescActivity.class);
			i.setAction(pList.get(position).get(NNUM).toString());
			Window w = ((ActivityGroup) getParent()).getLocalActivityManager()
					.startActivity("aaa", i);
			View contentView = w.getDecorView();
			if (desc.getChildCount() > 0) {
				desc.removeAllViewsInLayout();
			}
			desc.addView(contentView);
			desc.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(getParent().getParent(), "서버에 파라미터가 존재하지 않습니다.",
					Toast.LENGTH_SHORT).show();
		}
	}
}
