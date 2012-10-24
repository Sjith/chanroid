package kr.co.chunhoshop;

import java.lang.Thread.State;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ItemParser;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends Activity implements OnClickListener,
		OnItemClickListener, ParserTag {

	TextView eventTop;
	ListView eventList;
	ImageView icon;
	ProgressBar loading;
	ProgressBar sloading;
	FrameLayout productdesc;
	LinearLayout productbody;
	FrameLayout eventbody;
	LinearLayout eventtop;
	FrameLayout searchbody;
	Button giftBtn;
	ListParser parser;
	ItemParser iparser;
	ProductThread sThread = new ProductThread();
	ProductHandler sHandler = new ProductHandler();
	Map<String, CharSequence> desc = new HashMap<String, CharSequence>();
	ArrayList<Map<String, CharSequence>> eventArray = new ArrayList<Map<String, CharSequence>>();

	EventThread eThread = new EventThread();
	EventHandler eHandler = new EventHandler();
	EventAdapter eAdapter;

	String currentPNUM;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		productdesc = (FrameLayout) inflater.inflate(R.layout.product_desc,
				null);
		productdesc.setVisibility(View.GONE);
		eventbody = (FrameLayout) findViewById(R.id.eventbody);
		eventbody.addView(productdesc, 1);
		RelativeLayout producttitle = (RelativeLayout) findViewById(R.id.producttitle);
		producttitle.setVisibility(View.GONE);

		productbody = (LinearLayout) productdesc.findViewById(R.id.productbody);
		icon = (ImageView) productdesc.findViewById(R.id.productnumicon);
		giftBtn = (Button) productdesc.findViewById(R.id.productnumgift);
		eventTop = (TextView) findViewById(R.id.eventtitle);
		eventList = (ListView) findViewById(R.id.eventlist);
		eventList.setOnItemClickListener(this);
		eventList.setCacheColorHint(0);
		eventList.setDividerHeight(5);
		loading = (ProgressBar) findViewById(R.id.eventprogress);
		sloading = (ProgressBar) findViewById(R.id.productnumdialog);
		parser = new ListParser();
		iparser = new ItemParser();

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("M");
		String today = format.format(date);

		eventTop.setText(today + "월 이달의 행사");
		TextView tv = (TextView) findViewById(R.id.eventtoptxt);
		tv.setText("제품을 클릭하여 특별한 혜택을 확인하세요!");
		// 매달 바뀌기 때문에 서버에서 받아와야 한다. 는 개뿔 그냥 텍스트 ㄱㄱ
		loading();
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		eThread = new EventThread();
		eThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		eventArray = parser.get();
		eAdapter = new EventAdapter(this, eventArray, R.layout.product_item,
				null, null);
		eventList.setAdapter(eAdapter);
	}

	class EventThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(EVENTLIST);
			eHandler.sendEmptyMessage(0);
		}
	}

	class EventHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}

	}

	class EventAdapter extends SimpleAdapter {

		Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public EventAdapter(Context context,
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
			ImageView healthflag = (ImageView) view
					.findViewById(R.id.healthflag);

			// if
			// (eventArray.get(position).get(PEVENT).toString().equalsIgnoreCase("N"))
			// {
			eventflag.setVisibility(View.GONE);
			// }
			//
			// if
			// (eventArray.get(position).get(PNEW).toString().equalsIgnoreCase("N"))
			// {
			newflag.setVisibility(View.GONE);
			// }
			//
			// if
			// (eventArray.get(position).get(PMONTH).toString().equalsIgnoreCase("N"))
			// {
			monthflag.setVisibility(View.GONE);
			// }
			//
			// if
			// (eventArray.get(position).get(PHEALTH).toString().equalsIgnoreCase("N"))
			// {
			healthflag.setVisibility(View.GONE);
			// }

			TextView name = (TextView) view.findViewById(R.id.productname);
			name.setText(eventArray.get(position).get(PNAME));

			DecimalFormat df = new DecimalFormat("#,##0");

			TextView point = (TextView) view.findViewById(R.id.productpoint);
			point.setText(df.format(Integer.valueOf((String) eventArray.get(
							position).get(PPOINT))) + "원");

			TextView price = (TextView) view.findViewById(R.id.productprice);
			price.setText(df.format(Integer.valueOf((String) eventArray.get(
							position).get(PPRICE))));

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
				String url = (String) eventArray.get(position).get(PIMG);

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

	public void loadProduct() {
		Log.i("Chunho", "loadProduct()");
		productdesc.setVisibility(View.VISIBLE);
		sloading.setVisibility(View.VISIBLE);
		LinearLayout body = (LinearLayout) findViewById(R.id.productbody);
		body.setVisibility(View.INVISIBLE);
		if (sThread.getState() == State.RUNNABLE) {
			sThread.interrupt();
		}
		sThread = new ProductThread();
		sThread.start();
	}

	public void loadProductComplete() {
		Log.i("Chunho", "loadProductComplete()");
		sloading.setVisibility(View.GONE);
		desc = iparser.get();

		// 뷰 항목 셋팅
		if (desc.size() > 0) {
			TextView name = (TextView) productdesc
					.findViewById(R.id.productnumname);
			name.setText(desc.get(PNAME));
			TextView num = (TextView) productdesc
					.findViewById(R.id.productnumnum);
			num.setText("상품번호 : " + desc.get(PNUM));
			TextView price = (TextView) productdesc
					.findViewById(R.id.productnumprice);
			price.setText("가격 : " + desc.get(PPRICE));
			TextView point = (TextView) productdesc
					.findViewById(R.id.productnumpoint);
			point.setText("적립금 : " + desc.get(PPOINT));
			icon.setImageResource(R.drawable.icon);
			LinearLayout body = (LinearLayout) findViewById(R.id.productbody);
			body.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(this, "서버에서 상품정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	class ProductThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			iparser.load(PNUMSEARCH + currentPNUM);
			sHandler.sendEmptyMessage(0);
		}
	}

	class ProductHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadProductComplete();
			new ImgSync().execute((String) desc.get(PIMG));
		}

	}

	class ImgSync extends AsyncTask<String, Void, Bitmap> {

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
			icon.setImageBitmap(result);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.eventbackbtn:
			if (productdesc.getVisibility() == View.VISIBLE) {
				productdesc.setVisibility(View.GONE);
			} else {
				finish();
			}
			break;
		case R.id.eventsettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {}
			break;
		case R.id.productcomment:
			Intent intent = new Intent(this, CommentListActivity.class);
			intent.putExtra(PNUM, desc.get(PNUM));
			startActivity(intent);
			break;
		case R.id.productnumgift:
			if (desc.get(PPRICE) == null)
				return;
			int c = Integer.parseInt((String) desc.get(PPRICE));
			String category;
			if (c > 400000) {
				category = "4";
			} else if (c > 300000) {
				category = "3";
			} else if (c > 200000) {
				category = "2";
			} else if (c > 100000) {
				category = "1";
			} else {
				category = "0";
				Toast.makeText(this, "10만원 미만의 상품은 사은품이 지급되지 않습니다.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent i = new Intent(this, BonusActivity.class);
			i.setAction(category);
			startActivityForResult(i, 1988);
			break;
		case R.id.productsamplebtn:
			startActivity(new Intent(this, SampleActivity.class));
			break;
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
		if (productdesc.getVisibility() == View.VISIBLE) {
			productdesc.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// if (eventArray.get(arg2).get(PNUM) == null) {
		// Toast.makeText(this, "서버에 파라미터가 존재하지 않습니다.", Toast.LENGTH_SHORT)
		// .show();
		// return;
		// }
		// currentPNUM = (String) eventArray.get(arg2).get(PNUM);
		// loadProduct();
		Intent i = new Intent(this, ProductActivity.class);
		i.setAction((String) eventArray.get(arg2).get(PNUM));
		startActivity(i);
	}

}
