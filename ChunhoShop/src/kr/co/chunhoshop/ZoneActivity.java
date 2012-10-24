package kr.co.chunhoshop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ZoneActivity extends Activity implements OnItemClickListener,
		ParserTag {

	String searchUrl;
	String searchText;
	ListView zoneList;
	ProgressBar loading;

	ListParser parser = new ListParser();
	ZoneThread zThread = new ZoneThread();
	ZoneHandler zHandler = new ZoneHandler();
	ZoneAdapter zAdapter;

	ArrayList<Map<String, CharSequence>> zoneArray = new ArrayList<Map<String, CharSequence>>();

	Intent i;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chunho_zone_content);

		zoneList = (ListView) findViewById(R.id.zonelist);
		zoneList.setOnItemClickListener(this);
		zoneList.setCacheColorHint(0);
		zoneList.setDividerHeight(5);
		loading = (ProgressBar) findViewById(R.id.zoneprogress);

		i = getIntent();
		searchUrl = i.getAction();
		loading();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.gc();
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
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		zThread = new ZoneThread();
		zThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		zoneArray = parser.get();
		zAdapter = new ZoneAdapter(this, zoneArray, R.layout.product_item,
				null, null);
		zoneList.setAdapter(zAdapter);
	}

	class ZoneThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(searchUrl);
			zHandler.sendEmptyMessage(0);
		}
	}

	class ZoneHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}

	}

	class ZoneAdapter extends SimpleAdapter {

		public Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		public Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public ZoneAdapter(Context context,
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
				view = inflater.inflate(R.layout.product_item, null);
			}

			ImageView eventflag = (ImageView) view.findViewById(R.id.eventflag);
			ImageView newflag = (ImageView) view.findViewById(R.id.newflag);
			ImageView monthflag = (ImageView) view.findViewById(R.id.monthflag);
			ImageView healthflag = (ImageView) view.findViewById(R.id.healthflag);
			
			if (zoneArray.get(position).get(PEVENT).toString().equalsIgnoreCase("N")) {
				eventflag.setVisibility(View.GONE);
			} else {
				eventflag.setVisibility(View.VISIBLE);
			}
			
			if (zoneArray.get(position).get(PNEW).toString().equalsIgnoreCase("N")) {
				newflag.setVisibility(View.GONE);
			} else {
				newflag.setVisibility(View.VISIBLE);
			}
			
			if (zoneArray.get(position).get(PMONTH).toString().equalsIgnoreCase("N")) {
				monthflag.setVisibility(View.GONE);
			} else {
				monthflag.setVisibility(View.VISIBLE);
			}
			
			if (zoneArray.get(position).get(PHEALTH).toString().equalsIgnoreCase("N")) {
				healthflag.setVisibility(View.GONE);
			} else {
				healthflag.setVisibility(View.VISIBLE);
			}
			
			TextView name = (TextView) view.findViewById(R.id.productname);
			name.setText((String) zoneArray.get(position).get(PNAME));

			DecimalFormat df = new DecimalFormat("#,##0");
			
			TextView point = (TextView) view.findViewById(R.id.productpoint);
			point.setText(df.format(Integer.valueOf((String) zoneArray.get(position).get(PPOINT))) + "원");

			TextView price = (TextView) view.findViewById(R.id.productprice);
			price.setText(df.format(Integer.valueOf((String) zoneArray.get(position).get(PPRICE))));

			ImageView icon = (ImageView) view.findViewById(R.id.producticon);

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
				String url = (String) zoneArray.get(position).get(PIMG);

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
				iv.setImageBitmap(result);
				notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 상품번호 엘리먼트가 없음.
		if (zoneArray.get(arg2).get(PNUM) != null) {
			// ChunhoZoneActivity.pnum =
			// zoneArray.get(arg2).get(PNUM).toString();
			// ((ChunhoZoneActivity)getParent()).showProductDesc();
			// ((ChunhoZoneActivity)getParent()).hideTab();
			Intent i = new Intent(this, ProductActivity.class);
			i.setAction(zoneArray.get(arg2).get(PNUM).toString());
			startActivity(i);
		} else {
			Toast.makeText(getParent().getParent(), "서버에 파라미터가 존재하지 않습니다.",
					Toast.LENGTH_SHORT).show();
		}
	}

}
