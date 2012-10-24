package kr.co.chunhoshop;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ChunhoUtil;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GetEventActivity extends Activity implements ParserTag,
		OnItemClickListener, OnClickListener {

	ArrayList<Map<String, CharSequence>> map = new ArrayList<Map<String,CharSequence>>();

	ListParser parser = new ListParser();
	EventThread eThread = new EventThread();
	EventHandler eHandler = new EventHandler();
	EventAdapter eAdapter;
	SharedPreferences mPref;

	ListView eventList;
	ProgressBar loading;

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
		setContentView(R.layout.m_event);
		mdesc = (RelativeLayout) findViewById(R.id.meventdesc);
		mbody = (RelativeLayout) findViewById(R.id.meventbody);
		eventList = (ListView) findViewById(R.id.meventlist);
		eventList.setDividerHeight(3);
		eventList.setOnItemClickListener(this);
		dialog = new ProgressDialog(getParent().getParent());
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("서버의 응답을 기다리는 중입니다.");
		loading = (ProgressBar) findViewById(R.id.meventdialog);
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
		if (mdesc.getVisibility() != View.GONE) {
			mdesc.setVisibility(View.GONE);
		}
		if (map.size() > 0) {
			map.clear();
			eAdapter.notifyDataSetChanged();
		}
		loading();
	}

	void loading() {
		loading.setVisibility(View.VISIBLE);
		eThread = new EventThread();
		eThread.start();
	}

	void loadComplete() {
		loading.setVisibility(View.GONE);
		map = parser.get();
		if (map.size() < 1) {
			Toast.makeText(this, "진행중인 이벤트가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		eAdapter = new EventAdapter(this, map, R.layout.m_event_item, null,
				null);
		eventList.setAdapter(eAdapter);
	}

	class EventThread extends Thread {
		@Override
		public void run() {
			parser.load(EVENTBOARD);
			eHandler.sendEmptyMessage(0);
		}
	}

	class EventHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			loadComplete();
		}
	}

	class EventAdapter extends SimpleAdapter {

		public Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		public Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public EventAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(final int position, View view, ViewGroup root) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.m_event_item, null);
			}

			TextView title = (TextView) view.findViewById(R.id.meventitemtitle);
			TextView sdate = (TextView) view.findViewById(R.id.meventitemstart);
			TextView edate = (TextView) view.findViewById(R.id.meventitemend);
			TextView econtent = (TextView) view.findViewById(R.id.meventitemcontent);
			ImageView icon = (ImageView) view.findViewById(R.id.eventitemicon);

			title.setText(map.get(position).get(ETITLE));
			econtent.setText(map.get(position).get(ECONTENT));
			sdate.setText("시작 : " + map.get(position).get(ESDATE));
			edate.setText("종료 : " + map.get(position).get(EEDATE));

			if (icons.containsKey(position)) {
				icon.setImageBitmap(icons.get(position));
			} else {
				if (syncs.containsKey(position)) {
					if (syncs.get(position).getStatus() == AsyncTask.Status.PENDING) {
						try {
							syncs.get(position).execute(position);
						} catch (Exception e) {
						}
					} else if (syncs.get(position).getStatus() == AsyncTask.Status.RUNNING) {
						icon.setImageResource(R.drawable.icon);
					}
				} else {
					ImgSync sync = new ImgSync(position, icon);
					syncs.put(position, sync);
					try {
						syncs.get(position).execute(position);
					} catch (Exception e) {
					}
				}
			}

			return view;
		}

		class ImgSync extends AsyncTask<Integer, Void, Bitmap> {

			ImageView iv;
			int position;

			public ImgSync(int position, ImageView v) {
				// TODO Auto-generated constructor stub
				iv = v;
				this.position = position;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
			@Override
			protected Bitmap doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				Bitmap b = icons.get(position);
				String url = map.get(position).get(EIMG).toString();

				if (b == null) {
					b = ParserUtil.getImageURL(url);
					icons.put(position, b);
				}

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
				if (result == null) return;
				iv.setImageBitmap(result);
				notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mdesc.getVisibility() != View.GONE) {
			mdesc.setVisibility(View.GONE);
			mbody.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	DescThread dThread = new DescThread();
	DescHandler dHandler = new DescHandler();
	ItemParser iParser = new ItemParser();
	String index;

	Map<String, CharSequence> event = new HashMap<String, CharSequence>();

	RelativeLayout mdesc;
	RelativeLayout mbody;
	ProgressDialog dialog;

	void loadDesc() {
		loading.setVisibility(View.VISIBLE);
		mdesc.setVisibility(View.VISIBLE);
		mbody.setVisibility(View.GONE);
		dThread = new DescThread();
		dThread.start();
	}

	void loadDescComplete() {
		loading.setVisibility(View.GONE);
		event = iParser.get();
		if (event.size() < 1) {
			Toast.makeText(this, "이벤트 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
					.show();
			mdesc.setVisibility(View.GONE);
			mbody.setVisibility(View.GONE);
			return;
		}

		TextView title = (TextView) findViewById(R.id.eventdesctitle);
		TextView content = (TextView) findViewById(R.id.eventdesccontent);
		ImageView icon = (ImageView) findViewById(R.id.eventdescicon);
		Button case1 = (Button) findViewById(R.id.eventcase1);
		Button case2 = (Button) findViewById(R.id.eventcase2);

		title.setText(event.get(ETITLE));
		content.setText(event.get(ECONTENT));
		
		if ("".equalsIgnoreCase(event.get(ECASE1).toString())) {
			case1.setVisibility(View.VISIBLE);
			case1.setText(event.get(ECASE1));
		} else {
			case1.setVisibility(View.GONE);
		}
		
		if ("".equalsIgnoreCase(event.get(ECASE2).toString())) {
			case2.setVisibility(View.VISIBLE);
			case2.setText(event.get(ECASE2));			
		} else {
			case2.setVisibility(View.GONE);
		}

		new DescSync(icon).execute(event.get(EIMG).toString());

		mdesc.setVisibility(View.VISIBLE);
		mbody.setVisibility(View.VISIBLE);
	}

	public class DescSync extends AsyncTask<String, Void, Bitmap> {

		ImageView iv;

		public DescSync(ImageView iv) {
			this.iv = iv;
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap b = ParserUtil.getImageURLNonSampling(params[0]);
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
			if (result == null) return;
			iv.setImageBitmap(result);
		}

	}

	class DescThread extends Thread {
		@Override
		public void run() {
			iParser.load(EVENTDESC + index);
			dHandler.sendEmptyMessage(0);
		}
	}

	class DescHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			loadDescComplete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		index = (String) map.get(arg2).get(ENUM);
		loadDesc();
		mdesc.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.eventcase1:
			input(event.get(ECASE1).toString());
			break;
		case R.id.eventcase2:
			input(event.get(ECASE2).toString());
			break;
		}
	}

	void input(String selected) {
		if (!dialog.isShowing())
			dialog.show();
		new InputSync().execute(selected);
	}

	class InputSync extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				iParser.load(EVENTINPUT + "e_num=" + event.get(ENUM) + "&u_value="
						+ URLEncoder.encode(arg0[0], CHARACTER_SET) + "&u_id=" + mPref.getString("id", "")
						+ "&u_phone=" + mPref.getString(ATEL, ChunhoUtil.getPhoneNum(GetEventActivity.this)));
				String result = iParser.getResult();
				if (result.contains("true")) {
					return "Y";
				} else if (result.contains("false")){
					return "N";
				} else throw new NullPointerException("result null");
			} catch (Exception e) {
				return "E";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (dialog.isShowing())
				dialog.dismiss();
			String Result;
			if (result.equalsIgnoreCase("Y")) {
				// 성공
				Result = "이벤트에 참여하셨습니다.";
			} else if (result.equalsIgnoreCase("N")) {
				// 실패
				Result = "이미 참여하신 이벤트입니다.";
			} else {
				Result = "로그인 하신 후 참여하실 수 있습니다.";				
			}
			
//			AlertDialog.Builder builder = new AlertDialog.Builder(getParent()
//					.getParent());
//			builder.setMessage(Result);
//			builder.setTitle("이벤트");
//			builder.setPositiveButton(R.string.done,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//					}).show();
			Toast.makeText(GetEventActivity.this, Result, Toast.LENGTH_SHORT).show();
			GetEventActivity.this.onBackPressed();
			
			super.onPostExecute(result);
		}

	}

}
