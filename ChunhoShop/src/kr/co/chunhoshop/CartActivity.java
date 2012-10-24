package kr.co.chunhoshop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.chunhoshop.util.CartDBController;
import kr.co.chunhoshop.util.ListParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends Activity implements ParserTag,
		OnClickListener {

	ArrayList<Map<String, String>> cartArray = new ArrayList<Map<String, String>>();
	ArrayList<Map<String, CharSequence>> bonusArray = new ArrayList<Map<String, CharSequence>>();

	CartDBController control;

	ListParser parser = new ListParser();

	ProgressBar loading;
	ProgressDialog load;
	ListView cartList;

	TextView totalPrice;
	TextView healthPrice;
	TextView tv1;
	TextView tv2;
	ImageView iv1;
	int price;
	int hprice = 0;
	int oprice;
	int category;
	String gname = "";
	String gnum = "";

	RelativeLayout bottom;

	BonusThread bThread = new BonusThread();
	BonusHandler bHandler = new BonusHandler();

	CartThread cThread = new CartThread();
	CartHandler cHandler = new CartHandler();
	CartAdapter cAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cart);
		control = CartDBController.getInstance(getApplicationContext());
		bottom = (RelativeLayout) findViewById(R.id.cartbottom);
		loading = (ProgressBar) findViewById(R.id.cartprogress);
		load = new ProgressDialog(getParent().getParent());
		load.setMessage("서버의 응답을 기다리는 중입니다..");
		load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		cartList = (ListView) findViewById(R.id.cartlist);
		cartList.setDividerHeight(5);
		totalPrice = (TextView) findViewById(R.id.cpricetext);
		healthPrice = (TextView) findViewById(R.id.chealthpricetext);

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		iv1 = (ImageView) findViewById(R.id.imageView1);
	}

	@Override
	public void onResume() {
		super.onResume();
		loading.setVisibility(View.VISIBLE);
		bottom.setVisibility(View.INVISIBLE);
		cartList.setVisibility(View.INVISIBLE);
		if (cThread.isAlive())
			cThread.interrupt();
		cThread = new CartThread();
		cThread.start();
	}

	void loading() {
		loading.setVisibility(View.VISIBLE);
		bottom.setVisibility(View.INVISIBLE);
		cartList.setVisibility(View.INVISIBLE);
		if (cThread.isAlive())
			cThread.interrupt();
		cThread = new CartThread();
		cThread.start();
	}

	void loadComplete() {
		loading.setVisibility(View.GONE);
		bottom.setVisibility(View.VISIBLE);
		cartList.setVisibility(View.VISIBLE);
		cAdapter = new CartAdapter(this, cartArray, R.layout.cart_item, null,
				null);
		cartList.setAdapter(cAdapter);
		calcPrice();
	}

	class CartThread extends Thread {
		@Override
		public void run() {
			cartArray = control.get();
			cHandler.sendEmptyMessage(0);
		}
	}

	class CartHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadComplete();
				break;
			}
		}
	}

	void loadBonus() {

		if (price - hprice - oprice < 100000) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getParent()
					.getParent());
			dialog.setTitle(R.string.alert);
			dialog.setMessage("금액대별 사은품은 건강기능 식품과 옵션 선택 제품을 제외한 총 주문 금액 10만원 이상부터 증정됩니다.");
			dialog.setPositiveButton(R.string.done,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();
			return;
		}

		if (!load.isShowing())
			load.show();
		bThread = new BonusThread();
		bThread.start();
	}

	int which = -1;

	void loadBonusComplete() {
		load.dismiss();
		bonusArray = parser.get();
		ArrayList<String> gnum = new ArrayList<String>();
		ArrayList<String> gname = new ArrayList<String>();

		for (int i = 0; i < bonusArray.size(); i++) {
			String name = (String) bonusArray.get(i).get(GNAME);
			String num = (String) bonusArray.get(i).get(GNUM);
			gname.add(name);
			gnum.add(num);
		}

		View tView = View.inflate(this, R.layout.gift_title, null);
		TextView title = (TextView) tView.findViewById(R.id.gifttitle);

		calcPrice();

		if (price - hprice - oprice >= 400000) {
			title.setText("40만원대 사은품");
		} else if (price - hprice - oprice >= 300000) {
			title.setText("30만원대 사은품");
		} else if (price - hprice - oprice >= 200000) {
			title.setText("20만원대 사은품");
		} else if (price - hprice - oprice >= 100000) {
			title.setText("10만원대 사은품");
		}

		String[] showlist = gname.toArray(new String[bonusArray.size()]);
		for (int i = 0; i < showlist.length; i++) {
			showlist[i] = showlist[i].split("-")[1];
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(getParent()
				.getParent());
		dialog.setCustomTitle(tView);
		dialog.setSingleChoiceItems(showlist, which,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == bonusArray.size())
							return;
						CartActivity.this.gnum = (String) bonusArray.get(which)
								.get(GNUM);
						CartActivity.this.gname = "";
						CartActivity.this.gname = (String) bonusArray
								.get(which).get(GNAME);
						CartActivity.this.which = which;
					}
				});
		dialog.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (CartActivity.this.gname != null
								|| !CartActivity.this.gname
										.equalsIgnoreCase("")
								|| !(CartActivity.this.gname.length() < 1)) {
							Toast.makeText(
									CartActivity.this,
									"사은품이 " + CartActivity.this.gname
											+ " (으)로 설정되었습니다.",
									Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	class BonusThread extends Thread {
		@Override
		public void run() {
			if (price - hprice - oprice >= 400000) {
				category = 4;
			} else if (price - hprice - oprice >= 300000) {
				category = 3;
			} else if (price - hprice - oprice >= 200000) {
				category = 2;
			} else if (price - hprice - oprice >= 100000) {
				category = 1;
			} else {
				bHandler.sendEmptyMessage(2);
				return;
			}
			parser.load(BONUSLIST + category);
			bHandler.sendEmptyMessage(0);
		}
	}

	class BonusHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				loadBonusComplete();
				break;
			case 1:
				load.dismiss();
				break;
			case 2:
				load.dismiss();
				Toast.makeText(CartActivity.this,
						"금액대별 사은품은 건강기능 식품을 제외한 총 주문 금액 10만원 이상부터 증정됩니다.",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	void calcPrice() {
		int price = 0;
		int hprice = 0;
		int oprice = 0;
		for (int i = 0; i < cartArray.size(); i++) {
			price += Integer.parseInt(cartArray.get(i).get(PPRICE))
					* Integer.parseInt(cartArray.get(i).get(PCOUNT));
			if (cartArray.get(i).get(PHEALTH).equals("Y")) {
				hprice += Integer.parseInt(cartArray.get(i).get(PPRICE))
						* Integer.parseInt(cartArray.get(i).get(PCOUNT));
			} else if (cartArray.get(i).get(POPTION).equals("Y")) {
				oprice += Integer.parseInt(cartArray.get(i).get(PPRICE))
						* Integer.parseInt(cartArray.get(i).get(PCOUNT));
			}
		}
		if (price < 30000 && price > 2500)
			price = price + 2500;

		this.price = price;
		this.hprice = hprice;
		this.oprice = oprice;

		if (hprice == 0) {
			tv1.setVisibility(View.GONE);
			tv2.setVisibility(View.GONE);
			iv1.setVisibility(View.GONE);
			healthPrice.setVisibility(View.GONE);
		} else if (hprice > 0) {
			tv1.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
			iv1.setVisibility(View.VISIBLE);
			healthPrice.setVisibility(View.VISIBLE);
		}

		DecimalFormat df = new DecimalFormat("#,##0");
		totalPrice.setText(df.format(price) + "원");
		healthPrice.setText(df.format(hprice) + "원");
	}

	class CartAdapter extends SimpleAdapter {

		public Map<Integer, Bitmap> icons = new HashMap<Integer, Bitmap>();
		public Map<Integer, ImgSync> syncs = new HashMap<Integer, ImgSync>();

		public CartAdapter(Context context,
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
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.cart_item, null);
			}

			TextView name = (TextView) view.findViewById(R.id.cartname);
			name.setText(cartArray.get(position).get(PNAME));

			ImageView healthflag = (ImageView) view
					.findViewById(R.id.carthealthflag);
			if (cartArray.get(position).get(PHEALTH).equalsIgnoreCase("N")) {
				healthflag.setVisibility(View.GONE);
			} else {
				healthflag.setVisibility(View.VISIBLE);
			}

			ImageView optionflag = (ImageView) view
					.findViewById(R.id.cartoptionflag);
			if (cartArray.get(position).get(POPTION).equalsIgnoreCase("N")) {
				optionflag.setVisibility(View.GONE);
				view.findViewById(R.id.cartoptiontext).setVisibility(View.GONE);
			} else {
				optionflag.setVisibility(View.VISIBLE);
				TextView tv = (TextView) view.findViewById(R.id.cartoptiontext);
				tv.setText(cartArray.get(position).get(POPTIONNAME));
				tv.setVisibility(View.VISIBLE);
			}

			DecimalFormat df = new DecimalFormat("#,##0");
			final TextView price = (TextView) view.findViewById(R.id.cartprice);
			int pricetext = Integer.parseInt(cartArray.get(position)
					.get(PPRICE));
			price.setText(df.format(pricetext) + "원");

			TextView point = (TextView) view.findViewById(R.id.cartpoint);
			int pointtext = Integer.parseInt(cartArray.get(position)
					.get(PPOINT));
			point.setText(df.format(pointtext) + "원");

			final TextView count = (TextView) view.findViewById(R.id.cartcount);
			count.setText(cartArray.get(position).get(PCOUNT));
			Button plus = (Button) view.findViewById(R.id.cartplusbtn);
			plus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int countnum = Integer.parseInt(count.getText().toString());
					if (countnum < 20) {
						countnum = Integer.parseInt(cartArray.get(position)
								.get(PCOUNT));
						countnum++;
						cartArray.get(position).put(PCOUNT,
								String.valueOf(countnum));
						control.count(String.valueOf(countnum),
								cartArray.get(position).get("_id"));
						count.setText(String.valueOf(countnum));
						calcPrice();
					}
				}
			});
			Button minus = (Button) view.findViewById(R.id.cartminusbtn);
			minus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int countnum = Integer.parseInt(count.getText().toString());
					if (countnum > 1) {
						Log.i("Chunho", "minus");
						countnum = Integer.parseInt(cartArray.get(position)
								.get(PCOUNT));
						countnum--;
						cartArray.get(position).put(PCOUNT,
								String.valueOf(countnum));
						control.count(String.valueOf(countnum),
								cartArray.get(position).get("_id"));
						count.setText(String.valueOf(countnum));
						calcPrice();
					} else {
						AlertDialog.Builder alert = new AlertDialog.Builder(
								getParent().getParent());
						alert.setTitle("삭제")
								.setMessage("해당 제품이 장바구니에서 제거됩니다. 계속하시겠습니까?")
								.setPositiveButton(R.string.done,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												control.delete(Integer
														.parseInt(cartArray
																.get(position)
																.get("_id")));
												cartArray.remove(position);
												notifyDataSetChanged();
												CartActivity.this.onResume();
												calcPrice();
											}
										}).setNegativeButton("취소", null).show();
					}
				}
			});

			ImageView icon = (ImageView) view.findViewById(R.id.carticon);

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
				String url = null;
				try {
					url = (String) cartArray.get(position).get(PIMG);
				} catch (Exception e) {
				}

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
				// notifyDataSetChanged();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i("Chunho", "RESULT : " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cartorderbtn:
			if (gname.length() < 1 && price - hprice - oprice >= 100000) {
				Toast.makeText(this, "금액대별 사은품을 선택해주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (cartArray.size() < 1) {
				Toast.makeText(this, "장바구니에 제품이 없습니다.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Intent i = new Intent(this, OrderActivity.class);
			i.putExtra("data", cartArray);
			i.putExtra("gname", gname);
			i.putExtra("gnum", gnum);
			startActivity(i);
			break;
		case R.id.cartgiftbtn:
			loadBonus();
			break;
		}
	}
	
}
