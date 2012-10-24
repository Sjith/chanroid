package kr.co.chunhoshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.FlowGallery;
import kr.co.chunhoshop.util.FlowImageAdapter;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.MainParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import kr.co.chunhoshop.util.RotateView;
import kr.co.chunhoshop.util.RotateView.OnDegreesChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemSelectedListener, OnItemClickListener, ParserTag,
		OnDegreesChangeListener {

	SharedPreferences mPref;
	Editor mEditor;

	FlowGallery pListView;
	RotateView rotate;
	ProgressBar loading;
	ListParser parser;
	ItemParser iParser = new ItemParser();
	MainParser mParser = new MainParser();
	Button goBtn;
	ArrayList<Map<String, CharSequence>> pList = new ArrayList<Map<String, CharSequence>>();
	List<Bitmap> imageList = new ArrayList<Bitmap>();

	ProductThread pThread = new ProductThread();
	ProductHandler pHandler = new ProductHandler();
	ProductAdapter pAdapter;

	TextView pTitle;
	RelativeLayout bottom;
	PopSync popSync;

	boolean rotated = true;
	int point;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		parser = new ListParser();
		popSync = new PopSync();
		goBtn = (Button) findViewById(R.id.mainbtn);
		rotate = (RotateView) findViewById(R.id.rotateView1);
		rotate.setClickable(true);
		// rotate.setOnClickListener(this);
		rotate.setItemCount(10);
		rotate.setOnDegreesChangedListener(this);
		rotate.setScaleType(ScaleType.MATRIX);
		loading = (ProgressBar) findViewById(R.id.mainprogress);
		pTitle = (TextView) findViewById(R.id.mainproductname);
		pListView = (FlowGallery) findViewById(R.id.mainGallery);
		pListView.setOnItemSelectedListener(this);
		pListView.setOnItemClickListener(this);

		Handler h = new Handler();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				RotateAnimation rot = new RotateAnimation(360, 0,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f);
				rot.setInterpolator(MainActivity.this,
						android.R.anim.decelerate_interpolator);
				rot.setDuration(1500);
				// rotate.startAnimation(rot);
			}
		};
		h.postDelayed(r, 500);

		popSync.execute(POPPAGE);
		loading();

	}

// 메인버튼 기능변경을 위한 메서드. 디자인 완료시 주석 해제.
//	@Override
//	public void onResume() {
//
//		if (mPref.getString(ANAME, "").equalsIgnoreCase("")) {
//			goBtn.setText("로그인");
//		} else {
//			goBtn.setText(mPref.getString(ANAME, "") + "님\n환영합니다!");
//		}
//	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (popSync.loading.isShowing())
			popSync.loading.dismiss();
		popSync.cancel(true);
	}

	class PopSync extends AsyncTask<String, Void, Bitmap> {

		ProgressDialog loading;

		PopSync() {
			loading = new ProgressDialog(MainActivity.this);
			loading.setMessage("정보를 가져오는 중입니다.");
			loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (!loading.isShowing())
				loading.show();
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			iParser.load(arg0[0]);
			Map<String, CharSequence> map = iParser.get();
			if (map.size() < 1)
				return null;
			if (map.get(POPFLAG).toString().equalsIgnoreCase("Y")) {
				String imgPath = map.get(POPURL).toString();
				Bitmap b = ParserUtil.getImageURLAbsolutePath(imgPath);
				if (b == null)
					return null;
				return b;
			} else
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
			if (loading.isShowing())
				loading.dismiss();
			if (result == null)
				return;

			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.main_popup, null);
			ImageView iv = (ImageView) view.findViewById(R.id.mainpopimage);
			iv.setImageBitmap(result);
			iv.setClickable(true);
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent event = new Intent(MainActivity.this,
							ChunhoTabActivity.class);
					event.setAction("event");
					startActivity(event);
					try {
						ChunhoTabActivity.showEvent();
					} catch (Exception e) {
					}
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setView(view);
			builder.setTitle("천호쇼핑 Event!");
			builder.setPositiveButton("닫기",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();

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
		if (pList.size() < 1)
			return;
		for (int i = 0; i < pList.size(); i++) {
			Bitmap d = BitmapFactory.decodeResource(getResources(),
					R.drawable.icon_140);
			imageList.add(d);
		} // 실제 상품 이미지가 로드되기 전에 보여질 이미지
		pAdapter = new ProductAdapter(this, imageList);
		pListView.setAdapter(pAdapter);
		pListView.setSpacing(-100);
		pListView.setSelection(pList.size() / 2);
	}

	class ProductThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mParser.load(NEWPRODUCT);
			if (mParser.get().size() < 1) {
				pHandler.sendEmptyMessage(0);
				return;
			}
			parser.load(mParser.get().get(CATEGORYURL).toString());
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

	class ProductAdapter extends FlowImageAdapter {

		Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public ProductAdapter(Context c, List<Bitmap> list) {
			super(c, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto generated method stub
			if (view == null) {
				view = new ImageView(MainActivity.this);
				((ImageView) view).setImageResource(R.drawable.icon_140);
			}

			if (icons.containsKey(position)) {
				((ImageView) view).setImageBitmap(icons.get(position));
			} else {
				if (syncs.containsKey(position)) {
					if (syncs.get(position).getStatus() == AsyncTask.Status.PENDING) {
						try {
							syncs.get(position).execute(position);
						} catch (Exception e) {
						}
					} else if (syncs.get(position).getStatus() == AsyncTask.Status.RUNNING) {
						((ImageView) view).setImageBitmap(icons.get(position));
						imageList.get(position).recycle();
					}
				} else {
					ImgSync sync = new ImgSync(position, (ImageView) view);
					syncs.put(position, sync);
					try {
						syncs.get(position).execute(position);
					} catch (Exception e) {
					}
				}
			}
			view.setLayoutParams(new Gallery.LayoutParams(250, 250));
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
				if (pList.size() < 1)
					return null;
				String url = (String) pList.get(position).get(PIMG);

				if (b == null) {
					int overlayres = R.drawable.main_box;
					try {
						if (pList.get(position).get(PNEW).toString()
								.equalsIgnoreCase("Y")) {
							overlayres = R.drawable.main_newbox;
						} else {
							overlayres = R.drawable.main_box;
						}
					} catch (Exception e) {
					}

					Bitmap overRay = BitmapFactory.decodeResource(
							getResources(), overlayres);
					b = ParserUtil.overlayMark(
							ParserUtil.getImageURLNonSampling(url), overRay);
					overRay.recycle();
					icons.put(position, b);
					if (b == null)
						return null;
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
				if (result == null)
					return;
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
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		pTitle.setText(pList.get(arg2).get(PNAME));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, ProductActivity.class);
		i.setAction((String) pList.get(arg2).get(PNUM));
		i.putExtra("main", true);
		startActivity(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.alert)
				.setMessage(R.string.reallyexit)
				.setNegativeButton(R.string.no, null)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						}).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, ChunhoTabActivity.class);
		switch (v.getId()) {
		case R.id.mainbtn:
			i.putExtra("TAG", point);
			startActivity(i);
			break;
		case R.id.maininfobtn:
			startActivity(new Intent(this, CompanyActivity.class));
			break;
		// case R.id.rotateView1:
		// i.putExtra("TAG", point);
		// startActivity(i);
		// break;
		// case R.id.button1:
		// i.putExtra("TAG", 0);
		// startActivity(i);
		// break;
		// case R.id.button2:
		// i.putExtra("TAG", 1);
		// startActivity(i);
		// break;
		// case R.id.button3:
		// i.putExtra("TAG", 2);
		// startActivity(i);
		// break;
		// case R.id.button4:
		// i.putExtra("TAG", 3);
		// startActivity(i);
		// break;
		// case R.id.button5:
		// i.putExtra("TAG", 4);
		// startActivity(i);
		// break;
		}
	}

	@Override
	public void onDegreesChanged(float degrees) {
		// TODO Auto-generated method stub

		int count = rotate.getCurrentCount();
		if (count % rotate.getItemCount() < 0) {
			count = count % rotate.getItemCount() + rotate.getItemCount();
		}
		switch (count) {
		case 0:
			// 검색
			point = 0;
			goBtn.setText(getString(R.string.searchproduct));
			break;
		case 1:
			// 이벤트
			point = 1;
			goBtn.setText(getString(R.string.event));
			break;
		case 2:
			// 천호존
			point = 2;
			goBtn.setText(getString(R.string.chunhozone));
			break;
		case 3:
			// 마이천호
			point = 4;
			goBtn.setText(getString(R.string.mychunho));
			break;
		case 4:
			// 장바구니
			point = 3;
			goBtn.setText(getString(R.string.mycart));
			break;
		case 5:
			// 검색
			point = 0;
			goBtn.setText(getString(R.string.searchproduct));
			break;
		case 6:
			// 이벤트
			point = 1;
			goBtn.setText(getString(R.string.event));
			break;
		case 7:
			// 천호존
			point = 2;
			goBtn.setText(getString(R.string.chunhozone));
			break;
		case 8:
			// 마이천호
			point = 4;
			goBtn.setText(getString(R.string.mychunho));
			break;
		case 9:
			// 장바구니
			point = 3;
			goBtn.setText(getString(R.string.mycart));
			break;
		}
	}

}
