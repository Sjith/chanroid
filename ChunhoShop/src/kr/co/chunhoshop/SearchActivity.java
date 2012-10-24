package kr.co.chunhoshop;

import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.CategoryParser;
import kr.co.chunhoshop.util.ChunhoUtil;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements ParserTag,
		OnItemClickListener, OnClickListener, OnKeyListener {

//	FrameLayout productdesc;
	LinearLayout productbody;
	FrameLayout searchbody;
	ListView searchList;
	EditText searchEdit;
	ImageView icon;
	Button giftBtn;
	TextView title;
	ProgressBar sloading;
	ProgressBar loading;
	ProgressDialog cloading;
	
	LinearLayout searchlistbody;

	ArrayList<Map<String, CharSequence>> searchArray = new ArrayList<Map<String, CharSequence>>();

	SearchThread pThread = new SearchThread();
	SearchHandler pHandler = new SearchHandler();
	SearchAdapter pAdapter;
	ListParser parser = new ListParser();
	ItemParser iparser = new ItemParser();
	CategoryParser cParser = new CategoryParser();
	CategoryThread cThread = new CategoryThread();
	CategoryHandler cHandler = new CategoryHandler();

	ArrayList<ArrayList<Map<String, String>>> categoryList = new ArrayList<ArrayList<Map<String, String>>>();
	ArrayList<String> categoryname = new ArrayList<String>();

	String searchText = "";
	String currentPNUM;

	Button[] c1btn;
	Button[] c2btn;
	Button[] c3btn;
	Button[] c4btn;

	Button c1title;
	Button c2title;
	Button c3title;
	Button c4title;
	Button c5title;
	Button c6title;

	TableLayout c1;
	TableLayout c2;
	TableLayout c3;
	TableLayout c4;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cloading = new ProgressDialog(this);
		cloading.setMessage("카테고리 정보를 가져오는 중입니다.");
		cloading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadCategory();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	 protected void onPause() {
	// TODO Auto-generated method stub
	 hideKeyboard();
	 super.onPause();
	// if (categoryList.size() > 0) categoryList.clear();
	// if (categoryname.size() > 0) categoryname.clear();
	// if (searchArray.size() > 0) {
	// searchArray.clear();
//	 try {
//		 if (searchArray.size() > 0) {
//			if (pAdapter.icons.size() > 0) {
//				 for (int i = 0; i < pAdapter.icons.size(); i++) {
//					pAdapter.icons.get(i).recycle();
//				 }
//				 pAdapter.icons.clear();
//			}
//			if (pAdapter.syncs.size() > 0) {
//				for (int i = 0; i < pAdapter.syncs.size(); i++) {
//					pAdapter.syncs.get(i).cancel(true);
//				}
//				pAdapter.syncs.clear();
//			}
//		 }
//	 } catch (NullPointerException e) {}
	 
	// ChunhoUtil.recursiveRecycle(getWindow().getDecorView());
	// setContentView(new View(this));
	// System.gc();
	 }

	void initLayout() {
		//
		setContentView(R.layout.search);
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		productdesc = (FrameLayout) inflater.inflate(R.layout.product_desc,
//				null);
//		productdesc.setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.searchname);
		title.setVisibility(View.GONE);
		searchbody = (FrameLayout) findViewById(R.id.searchbody);
		searchlistbody = (LinearLayout) findViewById(R.id.searchlistbody);
//		searchbody.addView(productdesc);
//		RelativeLayout producttitle = (RelativeLayout) findViewById(R.id.producttitle);
//		producttitle.setVisibility(View.GONE);

//		productbody = (LinearLayout) productdesc.findViewById(R.id.productbody);
//		icon = (ImageView) productdesc.findViewById(R.id.productnumicon);
//		giftBtn = (Button) productdesc.findViewById(R.id.productnumgift);

		c2 = (TableLayout) findViewById(R.id.tableLayout2);
		c1 = (TableLayout) findViewById(R.id.tableLayout1);
		c3 = (TableLayout) findViewById(R.id.tableLayout3);
		c4 = (TableLayout) findViewById(R.id.tableLayout4);

		c1btn = new Button[] { (Button) findViewById(R.id.c1btn1),
				(Button) findViewById(R.id.c1btn2),
				(Button) findViewById(R.id.c1btn3),
				(Button) findViewById(R.id.c1btn4) };

		c2btn = new Button[] { (Button) findViewById(R.id.c2btn1),
				(Button) findViewById(R.id.c2btn2),
				(Button) findViewById(R.id.c2btn3),
				(Button) findViewById(R.id.c2btn4),
				(Button) findViewById(R.id.c2btn5),
				(Button) findViewById(R.id.c2btn6),
				(Button) findViewById(R.id.c2btn7),
				(Button) findViewById(R.id.c2btn8),
				(Button) findViewById(R.id.c2btn9),
				(Button) findViewById(R.id.c2btn10) };

		c3btn = new Button[] { (Button) findViewById(R.id.c3btn1),
				(Button) findViewById(R.id.c3btn2),
				(Button) findViewById(R.id.c3btn3),
				(Button) findViewById(R.id.c3btn4),
				(Button) findViewById(R.id.c3btn5),
				(Button) findViewById(R.id.c3btn6) };

		c4btn = new Button[] { (Button) findViewById(R.id.c4btn1),
				(Button) findViewById(R.id.c4btn2),
				(Button) findViewById(R.id.c4btn3),
				(Button) findViewById(R.id.c4btn4) };

		searchList = (ListView) findViewById(R.id.searchlist);
		searchList.setOnItemClickListener(this);
		searchList.setCacheColorHint(0);
		searchList.setDividerHeight(5);
		searchEdit = (EditText) findViewById(R.id.searchtext);
		searchEdit.setHintTextColor(Color.LTGRAY);
		searchEdit.setOnKeyListener(this);
		searchEdit.setOnClickListener(this);
		loading = (ProgressBar) findViewById(R.id.searchprogress);
		sloading = (ProgressBar) findViewById(R.id.productnumdialog);

		c1title = (Button) findViewById(R.id.searchcategory1);
		c1title.setText(categoryname.get(0));

		c2title = (Button) findViewById(R.id.searchcategory2);
		c2title.setText(categoryname.get(1));

		c3title = (Button) findViewById(R.id.searchcategory3);
		c3title.setText(categoryname.get(2));

		c4title = (Button) findViewById(R.id.searchcategory4);
		c4title.setText(categoryname.get(3));

		c5title = (Button) findViewById(R.id.searchcategory5);
		c5title.setText(categoryname.get(4));

		c6title = (Button) findViewById(R.id.searchcategory6);
		c6title.setText(categoryname.get(5));

		for (int i = 0; i < c1btn.length; i++) {
			c1btn[i].setText(categoryList.get(0).get(i).get(CATEGORYNAME));
		}
		for (int i = 0; i < c2btn.length; i++) {
			c2btn[i].setText(categoryList.get(1).get(i).get(CATEGORYNAME));
		}
		for (int i = 0; i < c3btn.length; i++) {
			c3btn[i].setText(categoryList.get(2).get(i).get(CATEGORYNAME));
		}
		for (int i = 0; i < c4btn.length; i++) {
			c4btn[i].setText(categoryList.get(3).get(i).get(CATEGORYNAME));
		}

		ChunhoUtil.setOnClickListener((ViewGroup) getWindow().getDecorView(),
				this);
		//
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		if (pThread.getState() != State.RUNNABLE) {
			pThread = new SearchThread();
			pThread.start();
		} else {
			pThread.interrupt();
		}
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		searchArray = parser.get();
		pAdapter = new SearchAdapter(this, searchArray, R.layout.product_item,
				null, null);
		searchList.setAdapter(pAdapter);
		searchlistbody.setVisibility(View.VISIBLE);
		title.setVisibility(View.VISIBLE);
	}

	void loadCategory() {
		if (!cloading.isShowing())
			cloading.show();
		cThread = new CategoryThread();
		cThread.start();
	}

	void loadCategoryComplete() {
		if (cloading.isShowing())
			cloading.dismiss();
		categoryList = cParser.get();
		categoryname = cParser.getCategory();
		if (categoryList.size() > 0)
			initLayout();
		else
			Toast.makeText(this, "서버의 응답이 없습니다.", Toast.LENGTH_SHORT).show();
	}

	class CategoryThread extends Thread {
		@Override
		public void run() {
			cParser.load();
			cHandler.sendEmptyMessage(0);
		}
	}

	class CategoryHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadCategoryComplete();
				break;
			}
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

	class SearchThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			parser.load(searchText);
			pHandler.sendEmptyMessage(0);
		}
	}

	class SearchHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
		}
	}

	class SearchAdapter extends SimpleAdapter {

		public Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		public Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public SearchAdapter(Context context,
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

			TextView name = (TextView) view.findViewById(R.id.productname);
			name.setText((String) searchArray.get(position).get(PNAME));

			ImageView eventflag = (ImageView) view.findViewById(R.id.eventflag);
			ImageView newflag = (ImageView) view.findViewById(R.id.newflag);
			ImageView monthflag = (ImageView) view.findViewById(R.id.monthflag);
			ImageView healthflag = (ImageView) view
					.findViewById(R.id.healthflag);

			if (searchArray.get(position).get(PEVENT).equals("N")) {
				eventflag.setVisibility(View.GONE);
			} else {
				eventflag.setVisibility(View.VISIBLE);
			}

			if (searchArray.get(position).get(PNEW).equals("N")) {
				newflag.setVisibility(View.GONE);
			} else {
				newflag.setVisibility(View.VISIBLE);
			}

			if (searchArray.get(position).get(PMONTH).equals("N")) {
				monthflag.setVisibility(View.GONE);
			} else {
				monthflag.setVisibility(View.VISIBLE);
			}

			if (searchArray.get(position).get(PHEALTH).equals("N")) {
				healthflag.setVisibility(View.GONE);
			} else {
				healthflag.setVisibility(View.VISIBLE);
			}

			DecimalFormat df = new DecimalFormat("#,##0");

			TextView point = (TextView) view.findViewById(R.id.productpoint);
			point.setText(df.format(Integer.valueOf(searchArray.get(position)
					.get(PPOINT).toString()))
					+ "원");

			TextView price = (TextView) view.findViewById(R.id.productprice);
			price.setText(df.format(Integer.valueOf(searchArray.get(position)
					.get(PPRICE).toString())));

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
				String url = (String) searchArray.get(position).get(PIMG);

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
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (!(v instanceof EditText)) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
		}

		switch (v.getId()) {
		case R.id.searchbackbtn:
			try {
				if (searchlistbody.getVisibility() == View.VISIBLE) {
					searchlistbody.setVisibility(View.GONE);
					title.setVisibility(View.GONE);
				} else if (searchlistbody.getVisibility() != View.VISIBLE) {
					super.onBackPressed();
				}
			} catch (Exception e) {
				super.onBackPressed();
			}
			break;
		case R.id.searchsettingbtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e1) {
			}
			break;
		case R.id.searchbtn:
			try {
				searchText = PNAMESEARCH
						+ URLEncoder.encode(searchEdit.getText().toString(),
								CHARACTER_SET);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (searchEdit.getText().toString() == null
					|| searchEdit.getText().toString().length() < 2
					|| searchEdit.getText().toString().contains(" ")) {
				Toast.makeText(this,
						"검색어를 입력해주세요.\n검색어에는 공백이나 특수문자를 사용할 수 없습니다.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			hideKeyboard();
			title.setVisibility(View.VISIBLE);
			title.setText(searchEdit.getText().toString());
			loading();
			break;

		case R.id.productcomment:
			Intent intent = new Intent(this, CommentListActivity.class);
			intent.putExtra(PNUM, currentPNUM);
			startActivity(intent);
			break;
		case R.id.productsamplebtn:
			try {
				ChunhoTabActivity.showSetting();
			} catch (Exception e) {
			}
			break;
		case R.id.searchtext:
			break;

		// CATEGORY BUTTONS

		case R.id.searchcategory1:
			showCategory(1);
			break;
		case R.id.searchcategory2:
			showCategory(2);
			break;
		case R.id.searchcategory3:
			showCategory(3);
			break;
		case R.id.searchcategory4:
			showCategory(4);
			break;

		case R.id.c1btn1:
			searchText = categoryList.get(0).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(0).get(0).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c1btn2:
			searchText = categoryList.get(0).get(1).get(CATEGORYURL);
			title.setText(categoryList.get(0).get(1).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c1btn3:
			searchText = categoryList.get(0).get(2).get(CATEGORYURL);
			title.setText(categoryList.get(0).get(2).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c1btn4:
			searchText = categoryList.get(0).get(3).get(CATEGORYURL);
			title.setText(categoryList.get(0).get(3).get(CATEGORYNAME));
			loading();
			break;

		case R.id.c2btn1:
			searchText = categoryList.get(1).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(0).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn2:
			searchText = categoryList.get(1).get(1).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(1).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn3:
			searchText = categoryList.get(1).get(2).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(2).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn4:
			searchText = categoryList.get(1).get(3).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(3).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn5:
			searchText = categoryList.get(1).get(4).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(4).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn6:
			searchText = categoryList.get(1).get(5).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(5).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn7:
			searchText = categoryList.get(1).get(6).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(6).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn8:
			searchText = categoryList.get(1).get(7).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(7).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn9:
			searchText = categoryList.get(1).get(8).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(8).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c2btn10:
			searchText = categoryList.get(1).get(9).get(CATEGORYURL);
			title.setText(categoryList.get(1).get(9).get(CATEGORYNAME));
			loading();
			break;

		case R.id.c3btn1:
			searchText = categoryList.get(2).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(0).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c3btn2:
			searchText = categoryList.get(2).get(1).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(1).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c3btn3:
			searchText = categoryList.get(2).get(2).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(2).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c3btn4:
			searchText = categoryList.get(2).get(3).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(3).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c3btn5:
			searchText = categoryList.get(2).get(4).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(4).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c3btn6:
			searchText = categoryList.get(2).get(5).get(CATEGORYURL);
			title.setText(categoryList.get(2).get(5).get(CATEGORYNAME));
			loading();
			break;

		case R.id.c4btn1:
			searchText = categoryList.get(3).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(3).get(0).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c4btn2:
			searchText = categoryList.get(3).get(1).get(CATEGORYURL);
			title.setText(categoryList.get(3).get(1).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c4btn3:
			searchText = categoryList.get(3).get(2).get(CATEGORYURL);
			title.setText(categoryList.get(3).get(2).get(CATEGORYNAME));
			loading();
			break;
		case R.id.c4btn4:
			searchText = categoryList.get(3).get(3).get(CATEGORYURL);
			title.setText(categoryList.get(3).get(3).get(CATEGORYNAME));
			loading();
			break;

		case R.id.searchcategory5:
			searchText = categoryList.get(4).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(4).get(0).get(CATEGORYNAME));
			loading();
			break;
		case R.id.searchcategory6:
			searchText = categoryList.get(5).get(0).get(CATEGORYURL);
			title.setText(categoryList.get(5).get(0).get(CATEGORYNAME));
			loading();
			break;

		}
	}

	void showCategory(int index) {
		TableLayout[] category = new TableLayout[] { c1, c2, c3, c4 };
		if (category[index - 1].getVisibility() == View.VISIBLE) {
			category[index - 1].setVisibility(View.GONE);
		} else {
			for (int i = 0; i < category.length; i++) {
				category[i].setVisibility(View.GONE);
			}
			if (category[index - 1].getVisibility() != View.VISIBLE) {
				category[index - 1].setVisibility(View.VISIBLE);
			} else {
				category[index - 1].setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 상품검색에서 제품번호 엘리먼트가 없는 관계로 아직 작동하지 않음.
		// hideKeyboard();
		// currentPNUM = (String) desc.get(PNUM);
		// loadProduct();
		Intent i = new Intent(this, ProductActivity.class);
		i.setAction((String) searchArray.get(arg2).get(PNUM));
		startActivity(i);
	}

	void hideKeyboard() {
		try {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
		} catch (Exception e) {
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
		try {
			if (searchlistbody.getVisibility() == View.VISIBLE) {
				searchlistbody.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
			} else if (searchlistbody.getVisibility() != View.VISIBLE) {
				super.onBackPressed();
			}
		} catch (Exception e) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			try {
				searchText = PNAMESEARCH
						+ URLEncoder.encode(searchEdit.getText().toString(),
								CHARACTER_SET);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (searchText == null || searchText.length() < 2
					|| searchText.contains(" ")) {
				Toast.makeText(this,
						"검색어를 입력해주세요.\n검색어에는 공백이나 특수문자를 사용할 수 없습니다.",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			title.setText(searchEdit.getText().toString());
			hideKeyboard();
			loading();
		}
		return false;
	}

}
