package kr.co.chunhoshop;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.chunhoshop.util.CartDBController;
import kr.co.chunhoshop.util.ChunhoUtil;
import kr.co.chunhoshop.util.ItemParser;
import kr.co.chunhoshop.util.ParserTag;
import kr.co.chunhoshop.util.ParserUtil;
import kr.co.chunhoshop.util.facebook.DialogError;
import kr.co.chunhoshop.util.facebook.Facebook;
import kr.co.chunhoshop.util.facebook.Facebook.DialogListener;
import kr.co.chunhoshop.util.facebook.FacebookError;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends Activity implements ParserTag,
		OnClickListener {

	Facebook mFacebook = new Facebook(FACEBOOK_APP_ID);

	boolean main = false;

	ItemParser parser;
	ImageView icon;
	Intent getIntent;
	Button giftBtn;
	ProgressBar loading;

	String optval;
	String opt;

	AlertDialog.Builder feedtext;
	EditText feededit;

	LinearLayout body;

	TextView cardFlag;

	Twitter mTwitter;
	RequestToken mRqToken;
	AccessToken mAccessToken;

	String mFacebookAccessToken;
	ProductThread pThread = new ProductThread();
	ProductHandler pHandler = new ProductHandler();

	CartDBController cart;

	TwitThread tThread;
	FeedThread mThread;
	FeedHandler mHandler = new FeedHandler();

	ProgressDialog floading;

	Map<String, CharSequence> map = new HashMap<String, CharSequence>();
	ArrayList<String> options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		setContentView(R.layout.product_desc);
		findViewById(R.id.productbody).setVisibility(View.INVISIBLE);
		giftBtn = (Button) findViewById(R.id.productnumgift);
		cardFlag = (TextView) findViewById(R.id.productcardflag);
		parser = new ItemParser();
		getIntent = getIntent();
		if (getIntent.getBooleanExtra("main", false))
			main = true;
		cart = CartDBController.getInstance(getApplicationContext());
		floading = new ProgressDialog(this);
		floading.setMessage("보내는 중입니다...");
		floading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		mFacebookAccessToken = ChunhoUtil.getAppPreferences(this,
				"ACCESS_TOKEN");
		mFacebook.setAccessToken(mFacebookAccessToken);

		SharedPreferences pref = getSharedPreferences("TwitterCon", 0);
		if (pref.getBoolean("twauthrized", false)) {
			mRqToken = new RequestToken(TWITTER_CONSUMER_KEY,
					TWITTER_CONSUMER_SECRET);
			mAccessToken = new AccessToken(pref.getString("twaccess", ""),
					pref.getString("twaccesssecret", ""));
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthAccessToken(mAccessToken.getToken());
			cb.setOAuthAccessTokenSecret(mAccessToken.getTokenSecret());
			cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration config = cb.build();
			TwitterFactory tFactory = new TwitterFactory(config);
			mTwitter = tFactory.getInstance();
		}

		body = (LinearLayout) findViewById(R.id.productbody);
		icon = (ImageView) findViewById(R.id.productnumicon);
		loading = (ProgressBar) findViewById(R.id.productnumdialog);
		loading();
		if (getParent() != null) {
			RelativeLayout producttitle = (RelativeLayout) findViewById(R.id.producttitle);
			producttitle.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		ChunhoUtil.recursiveRecycle(getWindow().getDecorView());
		System.gc();
		super.onDestroy();
	}

	class FeedThread extends Thread {
		@Override
		public void run() {
			try {
				Log.v("Chunho", "access token : " + mFacebook.getAccessToken());
				Log.v("Chunho",
						"access Expires : " + mFacebook.getAccessExpires());
				Bundle params = new Bundle();
				params.putString("message", feededit.getText().toString());
				params.putString("name", "천호쇼핑"); // 링크 이름
				params.putString("link", "http://www.chunhoshop.com/"); // 링크 주소
				params.putString("description", "건강식품 명품몰 천호쇼핑"); // 링크 설명
				params.putString("picture", ""); // 사진 경로. 인터넷 경로인듯.
				String result = mFacebook.request("me/feed", params, "POST");
				if (result.contains("Exception")) {
					mHandler.sendEmptyMessage(1);
				} else {
					mHandler.sendEmptyMessage(0);
					mFacebookAccessToken = null;
				}
			} catch (Exception e) {
				mHandler.sendEmptyMessage(1);
				e.printStackTrace();
			}
		}
	}

	class TwitThread extends Thread {
		@Override
		public void run() {
			// String path =
			// Environment.getExternalStorageDirectory().getAbsolutePath();
			// String fileName = "example.jpg";
			// InputStream is = null;
			//
			try {
				// if (new File(path + File.separator + fileName).exists())
				// is = new FileInputStream(path + File.separator + fileName);
				// else
				// is = null;
				//
				// OAuthAuthorization auth = new OAuthAuthorization(config);
				//
//				 ImageUploadFactory iFactory = new
//				 ImageUploadFactory(getConfiguration(TWITPIC_API_KEY));
//				 ImageUpload upload =
//				 iFactory.getInstance(MediaProvider.TWITPIC, auth);

				// if (is != null)
				// {
				// String strResult = upload.upload("example.jpg", is,
				// feededit.getText().toString());
				// twitter.updateStatus(feededit.getText().toString() + " " +
				// strResult);
				// }
				// else
				mTwitter.updateStatus(feededit.getText().toString());
				mHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				mHandler.sendEmptyMessage(1);
				e.printStackTrace();
			}
			// finally {
			// try {
			// is.close();
			// } catch (Exception e) {}
			// }
		}
	}

	class FeedHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (floading != null)
					floading.dismiss();
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ProductActivity.this);
				dialog.setMessage("보내기가 완료되었습니다.")
						.setTitle("공유")
						.setPositiveButton(R.string.done,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
				break;
			case 1:
				if (floading != null)
					floading.dismiss();
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ProductActivity.this);
				alert.setMessage(
						"보내기가 실패했습니다. 다시 시도해 주세요. "
								+ "세션이 종료되었거나 계정 정보가 변경되었습니다.")
						.setTitle("공유")
						.setPositiveButton(R.string.done,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
				SharedPreferences pref = getSharedPreferences("FacebookCon", 0);
				Editor edit = pref.edit();
				edit.putBoolean("fbauthrized", false);
				edit.commit();
				break;
			}
		}
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
		if (getParent() instanceof ChunhoZoneActivity) {
			LinearLayout body = (LinearLayout) findViewById(R.id.productbody);
			body.setVisibility(View.INVISIBLE);
			loading();
			// 천호존에서 접근했을때만 실행된다.
		}
	}

	public void loading() {
		loading.setVisibility(View.VISIBLE);
		pThread = new ProductThread();
		pThread.start();
	}

	public void loadComplete() {
		loading.setVisibility(View.GONE);
		map = parser.get();
		options = parser.getOptions();
		ArrayList<String> txts = parser.getTxts();
		ArrayList<String> imgs = parser.getImgs();
		// 뷰 항목 셋팅
		if (map.size() > 0) {
			map.put(PGIFT, "");
			map.put(POPTION, "N");
			map.put(POPTIONNAME, "");
			map.put(POPTIONVALUE, "");
			icon.setImageResource(R.drawable.icon_140);
			TextView name = (TextView) findViewById(R.id.productnumname);
			name.setText(map.get(PNAME));
			TextView num = (TextView) findViewById(R.id.productnumnum);
			num.setText(map.get(PNUM));

			Button video = (Button) findViewById(R.id.productvideobtn);

			DecimalFormat df = new DecimalFormat("#,##0");
			TextView price = (TextView) findViewById(R.id.productnumprice);
			price.setText(df.format(Integer.valueOf((String) map.get(PPRICE)))
					+ "원");
			TextView point = (TextView) findViewById(R.id.productnumpoint);
			point.setText(df.format(Integer.valueOf((String) map.get(PPOINT)))
					+ "원");
			body.setVisibility(View.VISIBLE);

			if (map.get(PVIDEO) == null || map.get(PVIDEO).equals("")) {
				video.setVisibility(View.GONE);
			} else {
				if (map.get(PVIDEOLENGTH) == null
						|| map.get(PVIDEOLENGTH).length() < 1) {
					video.setVisibility(View.VISIBLE);
					video.setText("동영상");
				} else {
					video.setVisibility(View.VISIBLE);
					int length = Integer.parseInt(map.get(PVIDEOLENGTH)
							.toString());
					if (length >= 60) {
						length /= 60;
						if (length >= 60) {
							length /= 60;
							video.setText("동영상 (" + length + "시간)");
						} else {
							video.setText("동영상 (" + length + "분)");
						}
					} else {
						video.setText("동영상 (" + length + "초)");
					}
				}
			}

			ImageView eventflag = (ImageView) findViewById(R.id.desceventflag);
			ImageView newflag = (ImageView) findViewById(R.id.descnewflag);
			ImageView monthflag = (ImageView) findViewById(R.id.descmonthflag);
			ImageView healthflag = (ImageView) findViewById(R.id.deschealthflag);

			if (map.get(PEVENT).toString().equalsIgnoreCase("N")) {
				eventflag.setVisibility(View.GONE);
			} else {
				eventflag.setVisibility(View.VISIBLE);
			}

			if (map.get(PNEW).toString().equalsIgnoreCase("N")) {
				newflag.setVisibility(View.GONE);
			} else {
				newflag.setVisibility(View.VISIBLE);
			}

			if (map.get(PMONTH).toString().equalsIgnoreCase("N")) {
				monthflag.setVisibility(View.GONE);
			} else {
				monthflag.setVisibility(View.VISIBLE);
				new BannerSync().execute(map.get(PMONTHURL).toString());
			}

			if (map.get(PHEALTH).toString().equalsIgnoreCase("N")) {
				healthflag.setVisibility(View.GONE);
			} else {
				healthflag.setVisibility(View.VISIBLE);
			}

			if (map.get(PCARDFLAG).toString().equalsIgnoreCase("N")) {
				cardFlag.setVisibility(View.GONE);
			} else {
				cardFlag.setText(map.get(PCARDNAME) + "\n"
						+ map.get(PCARDCONTENT).toString().split(" ")[0]
						+ "\n할인");
			}

			String[] make = ((String) map.get(PMAKER)).split("/");
			TextView maker = (TextView) findViewById(R.id.ptextView2);
			maker.setText(make[0]);

			LinearLayout contents = (LinearLayout) findViewById(R.id.productnumcontents);
			for (int i = 0; i < imgs.size(); i++) {
				if (imgs.get(i).length() > 1) {
					ImageView iv = new ImageView(this);
					iv.setPadding(20, 0, 20, 0);
					iv.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.icon_140));
					new DescSync(iv).execute(imgs.get(i));
					contents.addView(iv);
				}
				if (txts.get(i).length() > 0) {
					WebView wv = new WebView(this);
					wv.setPadding(30, 0, 30, 0);
					// wv.getSettings().setDefaultZoom(ZoomDensity.FAR);
					// wv.getSettings().setLoadWithOverviewMode(true);
					// wv.getSettings().setTextSize(TextSize.NORMAL);
					wv.loadDataWithBaseURL("a", txts.get(i), "text/html",
							CHARACTER_SET, "");
					wv.setWebViewClient(new WebSet());
					contents.addView(wv);
				}
			}
		} else {
			Toast.makeText(this, "서버에서 상품정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	class WebSet extends WebViewClient {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
		 * java.lang.String)
		 */
		@Override
		public void onLoadResource(WebView wv, String url) {
			// TODO Auto-generated method stub
			// wv.getSettings().setDefaultZoom(ZoomDensity.FAR);
			// wv.getSettings().setLoadWithOverviewMode(true);
			// wv.getSettings().setTextSize(TextSize.NORMAL);
			super.onLoadResource(wv, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView,
		 * java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView wv, String url) {
			// TODO Auto-generated method stub
			// wv.getSettings().setDefaultZoom(ZoomDensity.FAR);
			// wv.getSettings().setLoadWithOverviewMode(true);
			// wv.getSettings().setTextSize(TextSize.NORMAL);
			super.onPageFinished(wv, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView,
		 * java.lang.String, android.graphics.Bitmap)
		 */
		@Override
		public void onPageStarted(WebView wv, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			wv.getSettings().setLoadWithOverviewMode(true);
			wv.getSettings().setTextSize(TextSize.NORMAL);

			super.onPageStarted(wv, url, favicon);
		}
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

	class ProductThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String pnum;
			if (getIntent.getAction() == null) {
				pnum = ChunhoZoneActivity.pnum;
			} else {
				pnum = getIntent.getAction();
			}
			parser.load(PNUMSEARCH + pnum);
			pHandler.sendEmptyMessage(0);
		}
	}

	class ProductHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			loadComplete();
			new ImgSync().execute((String) map.get(PIMG));
		}
	}

	class BannerSync extends ImgSync {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * kr.co.chunhoshop.ProductActivity.ImgSync#doInBackground(java.lang
		 * .String[])
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap b = ParserUtil.getImageURLNonSampling(params[0]);
			if (b == null)
				return null;
			return b;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * kr.co.chunhoshop.ProductActivity.ImgSync#onPostExecute(android.graphics
		 * .Bitmap)
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if (result == null)
				return;
			ImageView iv = (ImageView) findViewById(R.id.productbanner);
			iv.setImageBitmap(result);
			iv.setVisibility(View.VISIBLE);
			findViewById(R.id.bannerline).setVisibility(View.VISIBLE);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			// 페북
			if (requestCode == FACEBOOK_AUTH_CODE) {
				mFacebook.authorizeCallback(requestCode, resultCode, data);
			}
			// 뜨윝허
			if (requestCode == TWITTER_LOGIN_CODE) {
				try {
					mAccessToken = mTwitter.getOAuthAccessToken(mRqToken,
							data.getStringExtra("oauth_verifier"));

					SharedPreferences pref = getSharedPreferences("TwitterCon",
							0);
					SharedPreferences.Editor edit = pref.edit();
					edit.putString("twaccess", mAccessToken.getToken());
					edit.putString("twaccesssecret",
							mAccessToken.getTokenSecret());
					edit.putBoolean("twauthrized", true);
					edit.commit();
					Log.v("Chunho",
							"Twitter Access Token : " + mAccessToken.getToken());
					Log.v("Chunho", "Twitter Access Token Secret : "
							+ mAccessToken.getTokenSecret());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (requestCode == 0) {
				try {
					ChunhoTabActivity.showSetting();
				} catch (Exception e) {
					Intent setting = new Intent(this, ChunhoTabActivity.class);
					setting.setAction("setting");
					startActivity(setting);
				}
				finish();
			}
		}

		// else {
		// if (requestCode == FACEBOOK_AUTH_CODE) {
		// mFacebook.authorizeCallback(requestCode, resultCode, data);
		// }
		// }
	}

	void login() {
		if (!"".equals(mFacebookAccessToken) && mFacebookAccessToken != null)
			mFacebook.setAccessToken(mFacebookAccessToken);
		else
			mFacebook.authorize2(this,
					new String[] { "publish_stream, user_photos, email" },
					new AuthorizeListener());
	}

	void twlogin() {
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true);
			cb.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			cb.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			TwitterFactory factory = new TwitterFactory(cb.build());
			mTwitter = factory.getInstance();
			mRqToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);

			// Intent intent = new Intent(MOVE_TWITTER_LOGIN);
			Intent intent = new Intent(this, TwitterLogin.class);
			intent.putExtra("auth_url", mRqToken.getAuthorizationURL());
			startActivityForResult(intent, TWITTER_LOGIN_CODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void feed() {
		feededit = null;
		feededit = new EditText(this);
		feededit.setHint("내용");
		feededit.setText(map.get(PNAME));
		feededit.setGravity(Gravity.NO_GRAVITY);
		feededit.setLines(4);
		feededit.setMaxLines(4);
		feededit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		feedtext = new AlertDialog.Builder(this);
		feedtext.setView(null);
		feedtext.setTitle("보내기");
		feedtext.setView(feededit);
		feedtext.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						floading = new ProgressDialog(ProductActivity.this);
						floading.setMessage("포스팅 중입니다...");
						floading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						floading.show();
						mThread = new FeedThread();
						mThread.start();
					}
				});
		feedtext.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		feedtext.show();
	}

	void twit() {
		feededit = null;
		feededit = new EditText(this);
		feededit.setHint("내용");
		feededit.setText(map.get(PNAME) + " " + PAGE);
		feededit.setGravity(Gravity.NO_GRAVITY);
		feededit.setLines(4);
		feededit.setMaxLines(4);
		feededit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		feedtext = new AlertDialog.Builder(this);
		feedtext.setView(null);
		feedtext.setTitle("보내기");
		feedtext.setView(feededit);
		feedtext.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						floading = new ProgressDialog(ProductActivity.this);
						floading.setMessage("올리는 중입니다...");
						floading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						floading.show();
						tThread = new TwitThread();
						tThread.start();
					}
				});
		feedtext.setNegativeButton("취소", null);
		feedtext.show();
	}

	// Facebook 인증후 처리를 위한 callback class
	public class AuthorizeListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			Toast.makeText(ProductActivity.this, "인증에 성공했습니다!",
					Toast.LENGTH_SHORT).show();
			mFacebookAccessToken = mFacebook.getAccessToken();
			ChunhoUtil.setAppPreferences(ProductActivity.this, "ACCESS_TOKEN",
					mFacebookAccessToken, true);
		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			Toast.makeText(ProductActivity.this,
					"인증에 실패했습니다. 사용자 정보 또는 네트워크 연결 상태를 확인해주세요.",
					Toast.LENGTH_SHORT).show();
			ChunhoUtil.setAppPreferences(ProductActivity.this, "ACCESS_TOKEN",
					"", false);
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			Toast.makeText(ProductActivity.this,
					"인증에 실패했습니다. 사용자 정보 또는 네트워크 연결 상태를 확인해주세요.",
					Toast.LENGTH_SHORT).show();
			ChunhoUtil.setAppPreferences(ProductActivity.this, "ACCESS_TOKEN",
					"", false);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.productcartbtn:
			// 디비나 쓰자 망할

			if (((optval == null || optval.length() < 2) && Integer
					.parseInt(map.get(PPRICE).toString()) >= 100000)
					&& map.get(PHEALTH).toString().equalsIgnoreCase("N")) {
				Toast.makeText(this, "먼저 사은품을 선택해 주세요.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			boolean result = cart.insert(map);

			if (result) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("장바구니")
						.setMessage("장바구니에 추가하였습니다.")
						.setPositiveButton("장바구니 보기",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										if (!main) {
											try {
												ChunhoTabActivity.showCart();
											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											finish();
										} else {
											finish();
											Intent i = new Intent(
													ProductActivity.this,
													ChunhoTabActivity.class);
											i.setAction("cart");
											startActivity(i);
										}
									}
								})
						.setNegativeButton(R.string.done,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("장바구니")
						.setMessage("이미 장바구니에 담으신 제품입니다.\n장바구니를 확인하시겠습니까?")
						.setPositiveButton("장바구니 보기",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										if (!main) {
											try {
												ChunhoTabActivity.showCart();
											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											finish();
										} else {
											finish();
											Intent i = new Intent(
													ProductActivity.this,
													ChunhoTabActivity.class);
											i.setAction("cart");
											startActivity(i);
										}
									}
								})
						.setNegativeButton("취소",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).show();
			}
			break;
		case R.id.productnumgift:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			// @SuppressWarnings("unchecked")
			// ArrayList<String> options = (ArrayList<String>) this.options
			// .clone();

			// for (int i = 0; i < options.size(); i++) {
			// if (options.get(i).contains("금액대별")) {
			// // 9/14 일단 금액대별 사은품은 밑에 버튼으로 뺄것.
			// builder.setPositiveButton("금액대별사은품",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// // TODO Auto-generated method stub
			// startActivity(new Intent(
			// ProductActivity.this,
			// BonusActivity.class));
			// }
			// });
			// }
			// }

			final String[] optval = parser.getOptval().toArray(
					new String[parser.getOptval().size()]);
			final String[] opt = options.toArray(new String[options.size()]);

			if (map.get(PHEALTH).toString().equalsIgnoreCase("Y")) {
				Toast.makeText(this, "건강기능식품은 관련 법규상 사은품 지급이 되지 않습니다.",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (Integer.valueOf((String) map.get(PPRICE)) < 100000) {
				AlertDialog.Builder palert = new AlertDialog.Builder(this);
				palert.setTitle(R.string.alert);
				palert.setMessage("구매금액대별 사은품은 10만원 이상부터 증정됩니다.");
				palert.setNegativeButton(R.string.done,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				palert.setPositiveButton("금액대별사은품",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								startActivity(new Intent(ProductActivity.this,
										BonusActivity.class));
							}
						}).show();
				return;
			}

			// builder.setItems(opt, new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// Toast.makeText(ProductActivity.this,
			// "사은품이 " + opt[which] + " (으)로 설정되었습니다.",
			// Toast.LENGTH_SHORT).show();
			// map.put(PGIFT, opt[which]);
			// dialog.dismiss();
			// }
			// });
			// 요거 대신 텍스트뷰가 쫙 하고 나와야 함.

			// LinearLayout ll = new LinearLayout(this);
			// ll.setOrientation(LinearLayout.VERTICAL);
			//
			// for (int i = 0; i < opt.length; i++) {
			// if (opt.length > 0) {
			// TextView tv = new TextView(this);
			// tv.setText(opt[i]);
			// Log.i("Chunho", opt[i]);
			// tv.setTextSize(20.0f);
			// tv.setPadding(20, 10, 20, 10);
			// tv.setLayoutParams(new LayoutParams(
			// LayoutParams.MATCH_PARENT,
			// LayoutParams.WRAP_CONTENT));
			// ll.addView(tv);
			// }
			// }

			// builder.setView(ll);
			builder.setSingleChoiceItems(opt,
					android.R.layout.simple_list_item_single_choice,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							ProductActivity.this.optval = optval[which];
							ProductActivity.this.opt = opt[which];
						}
					});

			builder.setNegativeButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							try {
								Toast.makeText(
										ProductActivity.this,
										"해당 제품에 대한 사은품이 "
												+ ProductActivity.this.opt
												+ " (으)로 설정되었습니다.",
										Toast.LENGTH_SHORT).show();
								if (ProductActivity.this.opt.contains("금액대별")) {
									map.put(POPTION, "N");
								} else {
									map.put(POPTION, "Y");
								}
								map.put(POPTIONNAME, ProductActivity.this.opt);
								map.put(POPTIONVALUE,
										ProductActivity.this.optval);
							} catch (Exception e) {

							}
							dialog.dismiss();
						}
					});

			LinearLayout titlell = (LinearLayout) View.inflate(this,
					R.layout.giftbox_title, null);
			builder.setCustomTitle(titlell);
			// builder.setTitle("사은품 증정 목록(택1)");
			if (opt.length > 0) {
				builder.show();
			} else {
				Toast.makeText(this, "해당 상품은 사은품이 제공되지 않습니다.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// Intent ii = new Intent(this, FontTest.class);
			// startActivity(ii);
			break;
		case R.id.productsns:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("공유")
					.setItems(new String[] { "페이스북", "트위터" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									switch (which) {
									case 0:
										SharedPreferences pref = getSharedPreferences(
												"FacebookCon", 0);
										if (!pref.getBoolean("fbauthrized",
												false)) {
											Toast.makeText(
													ProductActivity.this,
													"최초 사용시 사용자 인증이 필요합니다.",
													Toast.LENGTH_SHORT).show();
											login();
										} else {
											feed();
										}
										dialog.dismiss();
										break;
									case 1:
										SharedPreferences tpref = getSharedPreferences(
												"TwitterCon", 0);
										if (!tpref.getBoolean("twauthrized",
												false)) {
											Toast.makeText(
													ProductActivity.this,
													"최초 사용시 사용자 인증이 필요합니다.",
													Toast.LENGTH_SHORT).show();
											twlogin();
											// 트위터 인증
										} else {
											twit();
											// 글쓰기
										}
										break;
									}
								}
							}).show();
			break;
		case R.id.productsamplebtn:
			Intent i = new Intent(this, SampleActivity.class);
			i.putExtra(PNAME, map.get(PNAME));
			i.putExtra(PNUM, map.get(PNUM));
			startActivity(i);
			break;
		case R.id.productcomment:
			Intent intent = new Intent(this, CommentListActivity.class);
			intent.putExtra(PNUM, map.get(PNUM));
			intent.putExtra(PNAME, map.get(PNAME));
			intent.putExtra("product", (Serializable) map);
			startActivityForResult(intent, 0);
			break;
		case R.id.productbackbtn:
			finish();
			break;
		case R.id.productsettingbtn:
			if (!main) {
				try {
					ChunhoTabActivity.showSetting();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			} else {
				finish();
				Intent setting = new Intent(this, ChunhoTabActivity.class);
				setting.setAction("setting");
				startActivity(setting);
			}
			break;
		case R.id.productcallbtn:
			// 전화걸기 전에 사용자에게 확인과정을 거쳐야 한다.
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setMessage("누를수록 건강해지는 번호\n[080-607-1005]\n전화 연결 하시겠습니까?");
			alert.setTitle("전화연결");
			alert.setNegativeButton("취소", null);
			alert.setPositiveButton("확인",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent call = new Intent(Intent.ACTION_CALL);
							call.setData(Uri.parse("tel:0806071005"));
							startActivity(call);
						}
					});
			alert.show();
			break;
		case R.id.productbigsend:
			startActivity(new Intent(this, WorldActivity.class));
			break;
		case R.id.productvideobtn:
			String MOVIE_URL = map.get(PVIDEO).toString();
			Uri uri = Uri.parse(MOVIE_URL);
			Intent inte = new Intent(android.content.Intent.ACTION_VIEW, uri);
			inte.setDataAndType(uri, "video/*");
			startActivity(inte);
			break;
		}
	}

}
