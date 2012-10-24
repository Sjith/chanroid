package kr.co.chunhoshop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CommentListActivity extends Activity implements ParserTag,
		OnItemClickListener, OnClickListener {

	ListView pListView;
	ProgressBar loading;
	ListParser parser;
	ItemParser iparser;
	Intent getIntent;
	ArrayList<Map<String, CharSequence>> pList = new ArrayList<Map<String, CharSequence>>();
	Map<String, CharSequence> desc = new HashMap<String, CharSequence>();
	Map<String,CharSequence> product;

	ProductThread pThread = new ProductThread();
	ProductHandler pHandler = new ProductHandler();
	ProductAdapter pAdapter;
	
	CommentThread cThread = new CommentThread();
	CommentHandler cHandler = new CommentHandler();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.comment_list);
		getIntent = getIntent();
		product = (Map<String,CharSequence>) getIntent.getSerializableExtra("product");
		((TextView)findViewById(R.id.commentproductname)).setText(getIntent.getStringExtra(PNAME));
		pListView = (ListView) findViewById(R.id.commentlist);
		pListView.setOnItemClickListener(this);
		loading = (ProgressBar) findViewById(R.id.commentdialog);
		parser = new ListParser();
		iparser = new ItemParser();
		setProduct();
		loading();
	}
	
	void setProduct() {
		ImageView icon = (ImageView) findViewById(R.id.producticon);
		ImageView eventflag = (ImageView) findViewById(R.id.eventflag);
		ImageView newflag = (ImageView) findViewById(R.id.newflag);
		ImageView monthflag = (ImageView) findViewById(R.id.monthflag);
		ImageView healthflag = (ImageView) findViewById(R.id.healthflag);
		
		new DescSync(icon).execute(product.get(PIMG).toString());
		
		if (product.get(PEVENT).toString().equalsIgnoreCase("N")) {
			eventflag.setVisibility(View.GONE);
		}
		if (product.get(PNEW).toString().equalsIgnoreCase("N")) {
			newflag.setVisibility(View.GONE);
		}
		if (product.get(PMONTH).toString().equalsIgnoreCase("N")) {
			monthflag.setVisibility(View.GONE);
		}
		if (product.get(PHEALTH).toString().equalsIgnoreCase("N")) {
			healthflag.setVisibility(View.GONE);
		}
		
		TextView title = (TextView) findViewById(R.id.productname);
		TextView price = (TextView) findViewById(R.id.productprice);
		TextView point = (TextView) findViewById(R.id.productpoint);

		
		DecimalFormat df = new DecimalFormat("#,##0");
		price.setText(df.format(Integer.valueOf((String) product.get(PPRICE))));
		point.setText(df.format(Integer.valueOf((String) product.get(PPOINT)))
				+ "원");
		title.setText(product.get(PNAME));
		
		
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
			iv.setImageBitmap(result);
		}

	}
	
	public void loading() {
		loading.setVisibility(View.VISIBLE);
		pThread = new ProductThread();
		pThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		pList = parser.get();
		if (pList.size() < 1) {
			Toast.makeText(this, "선택된 상품에 대한 상품평이 없습니다.", Toast.LENGTH_SHORT).show();
			finish();
		}
		pAdapter = new ProductAdapter(this, pList, R.layout.comment_item, null,
				null);
		pListView.setAdapter(pAdapter);
	}

	class ProductThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(COMMENTLIST + getIntent().getStringExtra(PNUM));
			pHandler.sendEmptyMessage(0);
		}
	}

	class ProductHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}
	}

	class ProductAdapter extends SimpleAdapter {

//		Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
//		Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public ProductAdapter(Context context,
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
				view = inflater.inflate(R.layout.comment_item, null);
			}
			
			if (position % 2 == 0) view.setBackgroundColor(Color.WHITE);

			TextView name = (TextView) view.findViewById(R.id.commentitemname);
			name.setVisibility(View.GONE);
			name.setText(pList.get(position).get(CNAME));

			TextView point = (TextView) view
					.findViewById(R.id.commentitemtitle);
			point.setText(pList.get(position).get(CSUBJECT));

			TextView event = (TextView) view.findViewById(R.id.commentitemdate);
			event.setText(pList.get(position).get(CDATE));

//			view.findViewById(R.id.commentitemcontent).setVisibility(View.GONE);

//			ImageView icon = (ImageView) view
//					.findViewById(R.id.commentitemicon);

//			if (icons.containsKey(position)) {
//				icon.setImageBitmap(icons.get(position));
//			} else {
//				if (syncs.containsKey(position)) {
//					if (syncs.get(position).getStatus() == AsyncTask.Status.PENDING) {
//						try {
//							syncs.get(position).execute(position);
//						} catch (Exception e) {
//						}
//					} else if (syncs.get(position).getStatus() == AsyncTask.Status.RUNNING) {
//						icon.setImageResource(R.drawable.icon);
//					}
//				} else {
//					ImgSync sync = new ImgSync(position, icon);
//					syncs.put(position, sync);
//					try {
//						syncs.get(position).execute(position);
//					} catch (Exception e) {
//					}
//				}
//			}

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
//				Bitmap b = icons.get(position);
//				String url = (String) pList.get(position).get(CIMG);
//
//				if (b == null) {
//					b = ParserUtil.getImageURL(url);
//					icons.put(position, b);
//				}

//				return b;
				return null;
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (pThread.isAlive()) {
			pThread.interrupt();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 상세내용 다이얼로그를 띄워줘야 한다.
		descNum = Integer.valueOf((String) pList.get(arg2).get(CNUM));
		loadDesc();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.commentlistbackbtn :
			finish();
			break;
		case R.id.commentsettingbtn:
			try {
				setResult(RESULT_OK);
				finish();
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {
				Intent setting = new Intent(this, ChunhoTabActivity.class);
				setting.setAction("setting");
				startActivity(setting);
			}
			break;
		}
	}
	
	void loadDesc() {
		loading.setVisibility(View.VISIBLE);
		cThread = new CommentThread();
		cThread.start();		
	}
	
	int descNum;
	
	void loadDescComplete() {
		loading.setVisibility(View.GONE);
		desc = iparser.get();
		
		if (desc.size() < 1) {
			Toast.makeText(this, "상품평 상세정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View content = inflater.inflate(R.layout.comment_item2, null);
		ScrollView scroll = new ScrollView(this);
		scroll.addView(content);
		
//		ImageView commentitemicon = (ImageView) content.findViewById(R.id.commentitemicon);
//		commentitemicon.setVisibility(View.GONE);
//		new ImgSync(commentitemicon).execute(descNum);		
		TextView commentname = (TextView) content.findViewById(R.id.commentitemname);
		commentname.setText(desc.get(CNAME));		
		TextView commenttitle = (TextView) content.findViewById(R.id.commentitemtitle);
		commenttitle.setText(desc.get(CSUBJECT));		
		TextView commentdate  = (TextView) content.findViewById(R.id.commentitemdate);
		commentdate.setText(desc.get(CDATE));		
		TextView commentcontent = (TextView) content.findViewById(R.id.commentitemcontent);
		commentcontent.setText(desc.get(CCONTENT));		
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("상품평")
		.setView(scroll)
		.setPositiveButton(R.string.done, null)
		.show();
	}
	

	class CommentThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			iparser.load(COMMENTDESC + descNum);
			cHandler.sendEmptyMessage(0);
		}
	}

	class CommentHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadDescComplete();
		}
	}

	class ImgSync extends AsyncTask<Integer, Void, Bitmap> {

		ImageView iv;

		public ImgSync(ImageView v) {
			// TODO Auto-generated constructor stub
			iv = v;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String url = (String) desc.get(CIMG);
			Bitmap b = ParserUtil.getImageURL(url);

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
		}
	}
	
}
