package kr.co.vipsapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import kr.co.vipsapp.util.XmlLogin;
import kr.co.vipsapp.util.XmlParserComment;
import kr.co.vipsapp.util.XmlParserDelete;
import kr.co.vipsapp.util.XmlParserIntro;
import kr.co.vipsapp.util.XmlParserMain;
import kr.co.vipsapp.util.XmlParserPlaylist;
import kr.co.vipsapp.util.XmlParserSchedule;
import kr.co.vipsapp.util.XmlParserWrite;
import kr.co.vipsapp.util.vo.CommentVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.spoledge.aacplayer.ArrayAACPlayer;
import com.spoledge.aacplayer.ArrayDecoder;
import com.spoledge.aacplayer.Decoder;
import com.spoledge.aacplayer.PlayerCallback;

public class RadioTab extends Activity implements PlayerCallback {


	
	boolean isPlaying = false;
	
	final String PROGRAM_SCHEDULE_WEEK = "wee";
	final String PROGRAM_SCHEDULE_SAT = "sat";
	final String PROGRAM_SCHEDULE_SUN = "sun";

	String LOG_TAG = "eFM BUSAN";
	final String BUSAN_EFM = "http://www.befm.or.kr";
	
	final String STREAMING_MMS_URL = "mms://115.68.15.116/efm/";
//	final String STREAMING_MMS_URL = "rtsp://115.68.15.116/efm/";	
	// 1
	
	TelephonyManager mPhone;

	final static String DB_NAME = "befm.db";
	final static String DB_TABLE = "user";
	final static int DB_VERSION = 1;

	// private ProgressBar progress;

	long currentTime = 0L;

	String isLocal = "";

	// DB
	SQLiteDatabase db;
	UserDBHelper dbHelper;

	String userId;
	String password;
	String rememberMe;
	String introScript;

	// Audio
	AudioManager audioManager;
	SeekBar seekVolumn;
	String prgmId;

	ImageView ivBtnRadioPlayControl;

	ImageView imgMainBG;
	ImageView ivBanner;
	ImageView ivTitle;

	// 요일별 버튼 이벤트 처리
	ImageView[] mButton = new ImageView[7];

	ProgressDialog loadingDialog; // Loading Dialog
	// ProgressDialog commentDialog; // comment Dialog

	// ProgressDialog
	Bitmap bm;
	Bitmap bmMain, bmBanner, bmTitle;
	TextView tvScript;

	RadioPlayer player = RadioPlayer.getInstance();

	// 코멘트
	ArrayList<CommentVO> comment;
	ListView lvComment;
	CommentAdapter adapterComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		setContentView(R.layout.radio_activity);
		
		mPhone = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mPhone.listen(state, PhoneStateListener.LISTEN_CALL_STATE);
		

		// play 버튼 이미지
		ivBtnRadioPlayControl = (ImageView) findViewById(R.id.btn_control);
		ivBtnRadioPlayControl.setOnClickListener(onPlayBtnControlListener);

		// 메인 이미지
		imgMainBG = (ImageView) findViewById(R.id.img_radio_maintitle);
		imgMainBG.setOnClickListener(onImgMainBgControlListener);

		// playlist, schedule, comment 버튼이벤트 리스너 등록
		setListeners();

		// StreamControl.uiHandler = new Handler();

		ivBtnRadioPlayControl = (ImageView) findViewById(R.id.btn_control);
		// ivBtnPlayControl.setImageResource(R.drawable.btn_pause);
		// progress = (ProgressBar) findViewById( R.id.buffer_progressBar );

		// setPlayBtnImg();
		loadMain();
		setVolumn();

		dbHelper = new UserDBHelper(this);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		player.setTabFeathers(player.TAB_RADIO);
		if (player.getPlayFeathers() != player.PLAY_RADIO) {
			stop();
		}
		
		if (player.isPlaying()) {
			ivBtnRadioPlayControl.setImageResource(R.drawable.btn_pause);
		} else {
			ivBtnRadioPlayControl.setImageResource(R.drawable.btn_play);
		}
	}

	private void setVolumn() {

		seekVolumn = (SeekBar) findViewById(R.id.radio_volumn_sk);

		// AudioManager
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int nCurrentVolumn = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		seekVolumn.setMax(nMax);
		seekVolumn.setProgress(nCurrentVolumn);
		seekVolumn.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			// @Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
				Log.i(LOG_TAG, "set volumn: " + progress);
			}

			// @Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			// @Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

		});

	}

	/**
	 * 
	 */
	private OnClickListener onBackBtnControlListener = new OnClickListener() {

		// @Override
		public void onClick(View v) {
			// depth = 0
			// player.setViewDepth(0);
			player.setTabViewDepth(0);

			setContentView(R.layout.radio_activity);

			ivBtnRadioPlayControl = (ImageView) findViewById(R.id.btn_control);
			ivBtnRadioPlayControl.setOnClickListener(onPlayBtnControlListener);

			imgMainBG = (ImageView) findViewById(R.id.img_radio_maintitle);
			imgMainBG.setOnClickListener(onImgMainBgControlListener);

			setListeners();

			// setPlayBtnImg();
			loadMain();
			setVolumn();

			// Log.i(LOG_TAG, "Status(BackButton): "+player.getPlayFeathers());
			if ((player.getPlayFeathers() & player.PLAY_RADIO) == player.PLAY_RADIO) {
				ivBtnRadioPlayControl.setImageResource(R.drawable.btn_pause);
			} else {
				ivBtnRadioPlayControl.setImageResource(R.drawable.btn_play);
			}
		}

	};

	/**
	 * Play/Stop 버튼을 선택할 경우
	 */
	private OnClickListener onPlayBtnControlListener = new OnClickListener() {

		// @Override
		public void onClick(View v) {
			if (System.currentTimeMillis() - currentTime < 2000)
				return;

			synchronized (player) {
				if (player.isPlaying()) {
					isPlaying = false;
					stop();
					player.setPlayFeathers(player.PLAY_OFF);
					player.setPlaying(false);
					currentTime = 0L;
				} else {
					isPlaying = true;
					start(Decoder.DECODER_FFMPEG_WMA);
					player.setPlayFeathers(player.PLAY_RADIO);
					player.setPlaying(true);
					currentTime = System.currentTimeMillis();
				}
			}
		}

	};

	private OnClickListener onImgMainBgControlListener = new OnClickListener() {

		// @Override
		public void onClick(View v) {

			generateTabScreen(1);
		}

	};

	private void loadMain() {

		/* ProgressDialog */
		loadingDialog = ProgressDialog.show(this, "Loading",
				"Loading. Please Wait...", true, false);

		Thread thread = new Thread(new Runnable() {
			public void run() {

				XmlParserMain main = new XmlParserMain();
				main.parser();

				prgmId = main.getPrgmId();

				String titleImg = main.getTitleImg();
				String bannerImg = main.getBannerImg();

				URL imageURL;
				HttpURLConnection conn;
				// 메인 이미지

				try {
					imageURL = new URL(BUSAN_EFM + titleImg);
					conn = (HttpURLConnection) imageURL.openConnection();
					BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream(), 10240);
					bmMain = BitmapFactory.decodeStream(bis);
					bis.close();
				} catch (MalformedURLException e) {

					e.printStackTrace();
				} catch (IOException e) {

					Log.e(LOG_TAG, e.getMessage());
				}

				// 배너 이미지
				try {
					imageURL = new URL(BUSAN_EFM + bannerImg);
					conn = (HttpURLConnection) imageURL.openConnection();
					BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream(), 10240);
					bmBanner = BitmapFactory.decodeStream(bis);
					bis.close();

				} catch (MalformedURLException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage());
				}

				// 프로그램의 속성
				XmlParserIntro intro = new XmlParserIntro();
				intro.parsingIntro();
				isLocal = intro.getIsLocal();
				Log.i(LOG_TAG, "Broadcast isLocal: " + isLocal);
				//
				handlerMain.sendEmptyMessage(0);
			} // run
		});
		thread.start();
	}

	private Handler handlerMain = new Handler() {
		public void handleMessage(Message msg) {
			loadingDialog.dismiss(); // 다이얼로그 삭제
			// View갱신
			// 메인 이미지
			imgMainBG = (ImageView) findViewById(R.id.img_radio_maintitle);
			imgMainBG.setImageBitmap(bmMain);
			// 배너
			ivBanner = (ImageView) findViewById(R.id.img_radio_ad);
			ivBanner.setImageBitmap(bmBanner);

		}
	};

	/**
	 * Intro 화면에 필요한 정보를 얻는다. - 타이틀 이미지,설명 내용
	 */
	private void loadIntro() {
		/* ProgressDialog */
		loadingDialog = ProgressDialog.show(this, "Loading..",
				"Loading. Please Wait...", true, false);

		Thread thread = new Thread(new Runnable() {
			public void run() {

				// XML 파싱
				XmlParserIntro intro = new XmlParserIntro();
				intro.parsingIntro();
				introScript = intro.getIntroText();

				try {

					URL imageURL = new URL(intro.getImgURL());
					HttpURLConnection conn = (HttpURLConnection) imageURL
							.openConnection();
					BufferedInputStream bis = new BufferedInputStream(conn
							.getInputStream(), 10240);
					bmTitle = BitmapFactory.decodeStream(bis);
					bis.close();


				} catch (Exception e) {
					;
				}

				handlerIntro.sendEmptyMessage(0);
			} // run
		});
		thread.start();

	}

	private Handler handlerIntro = new Handler() {
		public void handleMessage(Message msg) {
			loadingDialog.dismiss(); // 다이얼로그 삭제

			ivTitle = (ImageView) findViewById(R.id.intro_image);
			ivTitle.setImageBitmap(bmTitle);

			tvScript = (TextView) findViewById(R.id.radio_intro_tx);
			tvScript.setText(introScript);
		}
	};

	/**
	 *
	 * 
	 * 
	 */
	private String getWeekToday() {
		String week = null;

		Calendar cal = Calendar.getInstance();

		int d = cal.get(Calendar.DAY_OF_WEEK);

		switch (d) {
		case 1:
			mButton[6].setImageResource(R.drawable.sun_on);
			week = PROGRAM_SCHEDULE_SUN;
			break;
		case 2:
			mButton[0].setImageResource(R.drawable.mon_on);
			week = PROGRAM_SCHEDULE_WEEK;
			break;
		case 3:
			mButton[1].setImageResource(R.drawable.tue_on);
			week = PROGRAM_SCHEDULE_WEEK;
			break;
		case 4:
			mButton[2].setImageResource(R.drawable.wed_on);
			week = PROGRAM_SCHEDULE_WEEK;
			break;
		case 5:
			mButton[3].setImageResource(R.drawable.thu_on);
			week = PROGRAM_SCHEDULE_WEEK;
			break;
		case 6:
			mButton[4].setImageResource(R.drawable.fri_on);
			week = PROGRAM_SCHEDULE_WEEK;
			break;
		case 7:
			mButton[5].setImageResource(R.drawable.sat_on);
			week = PROGRAM_SCHEDULE_SAT;
			break;
		}

		return week;
	}

	/**
	 * Tab용 Screen을 만든다.
	 * 
	 * @param num
	 *            tab 순서
	 */
	private void generateTabScreen(int num) {

		// View view = null;

		switch (num) {
		case 1:
			player.setTabViewDepth(1);
			setContentView(R.layout.radio_intro);

			ImageView introBtnControl = (ImageView) findViewById(R.id.radio_intro_btnback);
			introBtnControl.setOnClickListener(onBackBtnControlListener);

			// 해당 intro 내용을 가져온다.
			loadIntro();

			break;
		case 2:
			// player.setViewDepth(1);
			player.setTabViewDepth(1);
			setContentView(R.layout.radio_program);

			// 요일별 버튼 이벤트 처리
			// ImageView[] mButton = new ImageView[7];

			mButton[0] = (ImageView) findViewById(R.id.day_mon); // 월
			mButton[1] = (ImageView) findViewById(R.id.day_tue); // 화
			mButton[2] = (ImageView) findViewById(R.id.day_wed); // 수
			mButton[3] = (ImageView) findViewById(R.id.day_thu); // 목
			mButton[4] = (ImageView) findViewById(R.id.day_fri); // 금
			mButton[5] = (ImageView) findViewById(R.id.day_sat); // 토
			mButton[6] = (ImageView) findViewById(R.id.day_sun); // 일

			mButton[0].setOnClickListener(onScheduleMonClickListener);
			mButton[1].setOnClickListener(onScheduleTueClickListener);
			mButton[2].setOnClickListener(onScheduleWedClickListener);
			mButton[3].setOnClickListener(onScheduleThuClickListener);
			mButton[4].setOnClickListener(onScheduleFriClickListener);
			mButton[5].setOnClickListener(onScheduleSatClickListener);
			mButton[6].setOnClickListener(onScheduleSunClickListener);

			ImageView programBtnControl = (ImageView) findViewById(R.id.radio_program_btnback);
			programBtnControl.setOnClickListener(onBackBtnControlListener);

			// 해당 Program의 내용을 가져온다.
			// loadProgram(PROGRAM_SCHEDULE_WEEK);
			loadProgram(getWeekToday());
			break;
		case 3:
			// player.setViewDepth(1);
			player.setTabViewDepth(1);
			// View 정보를 갱신한다.
			setContentView(R.layout.radio_playlist);

			ImageView playlistBtnControl = (ImageView) findViewById(R.id.radio_playlist_btnback);
			playlistBtnControl.setOnClickListener(onBackBtnControlListener);

			loadPlaylist();

			break;
		case 4:
			if (isLocal.equals("false")) {
				new AlertDialog.Builder(RadioTab.this)
						.setTitle("Alert!")
						.setMessage("This program does not support comments.")
						.setNeutralButton(R.string.button_close,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).show();
			} else {
				// player.setViewDepth(1);
				player.setTabViewDepth(1);
				// login 화면 띄우기
				setContentView(R.layout.loginform);
				// deleteDB();

				final EditText etId = (EditText) findViewById(R.id.login_id);
				final EditText etPwd = (EditText) findViewById(R.id.login_pw);
				final CheckBox checkbox = (CheckBox) findViewById(R.id.login_auto);

				if (readDB()) {
					etId.setText(userId);
					etPwd.setText(password);
					if (rememberMe.equals("t")) {
						checkbox.setChecked(true);
					} else {
						checkbox.setChecked(false);
					}
					if (isAutoLogin()) {
						XmlLogin login = new XmlLogin(userId, password);
						login.parsingLogin();

						if ("true".equals(login.getResult())) {
							// finish();
							setContentView(R.layout.radio_comment);

							ImageView commentBtnControl = (ImageView) findViewById(R.id.radio_comment_btnback);
							commentBtnControl
									.setOnClickListener(onBackBtnControlListener);

							ImageView submitBtnControl = (ImageView) findViewById(R.id.iv_submit);
							submitBtnControl
									.setOnClickListener(onSubmitControlListener);

							if (checkbox.isChecked()) {
								writeDB(userId, password, rememberMe);
							} else {
								deleteDB();
							}

							loadComment();
						} else {
							new AlertDialog.Builder(RadioTab.this)
									.setTitle("Alert!")
									.setMessage("ID or Password is incorrect.")
									.setNeutralButton(
											R.string.button_close,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											}).show();
						}
						return;
					}
				}
				ImageView btnLoginCancel = (ImageView) findViewById(R.id.login_cancel);
				btnLoginCancel.setOnClickListener(new View.OnClickListener() {

					// @Override
					public void onClick(View v) {

						setContentView(R.layout.radio_activity);

						ivBtnRadioPlayControl = (ImageView) findViewById(R.id.btn_control);
						ivBtnRadioPlayControl
								.setOnClickListener(onPlayBtnControlListener);

						imgMainBG = (ImageView) findViewById(R.id.img_radio_maintitle);
						imgMainBG
								.setOnClickListener(onImgMainBgControlListener);

						setListeners();

						// setPlayBtnImg();
						loadMain();
						setVolumn();

						// Log.i(LOG_TAG,
						// "현재상태(BackButton): "+player.getPlayFeathers());
						if (player.getPlayFeathers() == player.PLAY_RADIO) {
							ivBtnRadioPlayControl
									.setImageResource(R.drawable.btn_pause);
						} else {
							ivBtnRadioPlayControl
									.setImageResource(R.drawable.btn_play);
						}
					}

				});

				ImageView btnLogin = (ImageView) findViewById(R.id.login_confirm);
				btnLogin.setOnClickListener(new View.OnClickListener() {

					// @Override
					public void onClick(View v) {

						// if(isLocal.equals("true")){
						userId = etId.getText().toString();
						password = etPwd.getText().toString();
						if (checkbox.isChecked()) {
							rememberMe = "t";
						} else {
							rememberMe = "f";
						}

						XmlLogin login = new XmlLogin(userId, password);
						login.parsingLogin();

						if ("true".equals(login.getResult())) {
							// finish();
							setContentView(R.layout.radio_comment);

							ImageView commentBtnControl = (ImageView) findViewById(R.id.radio_comment_btnback);
							commentBtnControl
									.setOnClickListener(onBackBtnControlListener);

							ImageView submitBtnControl = (ImageView) findViewById(R.id.iv_submit);
							submitBtnControl
									.setOnClickListener(onSubmitControlListener);

							if (checkbox.isChecked()) {
								writeDB(userId, password, rememberMe);
							} else {
								deleteDB();
							}

							loadComment();
						} else {
							new AlertDialog.Builder(RadioTab.this)
									.setTitle("Alert!")
									.setMessage("ID or Password is incorrect.")
									.setNeutralButton(
											R.string.button_close,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											}).show();
						}
						//
						// }else{
						// new AlertDialog.Builder( RadioTab.this )
						// .setTitle("알림")
						// .setMessage("부산영어방송에만 Comment를 작성할 수 있습니다.")
						// .setNeutralButton( R.string.button_close,
						// new DialogInterface.OnClickListener() {
						// public void onClick( DialogInterface dialog, int id)
						// {
						// dialog.cancel();
						// }
						// }
						// )
						// .show();
						// }
					}
					// click
				});

			}
			break;
		}

		// setListeners();
	}

	private OnClickListener onScheduleMonClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[0].setImageResource(R.drawable.mon_on);

			loadProgram(PROGRAM_SCHEDULE_WEEK);
			Log.i(LOG_TAG, "Program...Schedule Mon");

		}

	};

	private OnClickListener onScheduleTueClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[1].setImageResource(R.drawable.tue_on);

			loadProgram(PROGRAM_SCHEDULE_WEEK);
			Log.i(LOG_TAG, "Program...Schedule Tue");

		}

	};

	private OnClickListener onScheduleWedClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[2].setImageResource(R.drawable.wed_on);

			loadProgram(PROGRAM_SCHEDULE_WEEK);
			Log.i(LOG_TAG, "Program...Schedule Wed");

		}

	};

	private OnClickListener onScheduleThuClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[3].setImageResource(R.drawable.thu_on);

			loadProgram(PROGRAM_SCHEDULE_WEEK);
			Log.i(LOG_TAG, "Program...Schedule Thu");

		}

	};

	private OnClickListener onScheduleFriClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[4].setImageResource(R.drawable.fri_on);

			loadProgram(PROGRAM_SCHEDULE_WEEK);
			Log.i(LOG_TAG, "Program...Schedule Fri");

		}

	};

	private OnClickListener onScheduleSatClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[5].setImageResource(R.drawable.sat_on);

			loadProgram(PROGRAM_SCHEDULE_SAT);
			Log.i(LOG_TAG, "Program...Schedule Sat");

		}

	};

	private OnClickListener onScheduleSunClickListener = new OnClickListener() {
		// @Override
		public void onClick(View v) {
			scheduleButtonInit();
			mButton[6].setImageResource(R.drawable.sun_on);

			loadProgram(PROGRAM_SCHEDULE_SUN);
			Log.i(LOG_TAG, "Program...Schedule Sun");

		}

	};
	private OnClickListener onSubmitControlListener = new OnClickListener() {

		// @Override
		public void onClick(View v) {

			// String id="";
			TextView content = (TextView) findViewById(R.id.edit_comment);
			String comment = content.getText().toString();

			HttpPostData(userId, comment);
		}

	};

	private void scheduleButtonInit() {
		// ImageView[] mButton = new ImageView[7];
		// mButton[0] = (ImageView) findViewById(R.id.day_mon); // 월
		mButton[0].setImageResource(R.drawable.mon_off);
		// mButton[1] = (ImageView) findViewById(R.id.day_tue); // 화
		mButton[1].setImageResource(R.drawable.tue_off);
		// mButton[2] = (ImageView) findViewById(R.id.day_wed); // 수
		mButton[2].setImageResource(R.drawable.wed_off);
		// mButton[3] = (ImageView) findViewById(R.id.day_thu); // 목
		mButton[3].setImageResource(R.drawable.thu_off);
		// mButton[4] = (ImageView) findViewById(R.id.day_fri); // 금
		mButton[4].setImageResource(R.drawable.fri_off);
		// mButton[5] = (ImageView) findViewById(R.id.day_sat); // 토
		mButton[5].setImageResource(R.drawable.sat_off);
		// mButton[6] = (ImageView) findViewById(R.id.day_sun); // 일
		mButton[6].setImageResource(R.drawable.sun_off);

	}

	/**
	 * 
	 */
	private void loadComment() {
		// ArrayList<CommentVO> comment = new ArrayList<CommentVO>();
		/* ProgressDialog */
		loadingDialog = ProgressDialog.show(RadioTab.this, "Loading...",
				"Loading. Please Wait...", true, false);

		Thread thread = new Thread(new Runnable() {
			public void run() {
				XmlParserComment parser = new XmlParserComment();
				parser.parsing(getPrgmId());

				comment = parser.getComments();

				handlerComment.sendEmptyMessage(0);
			}

			// } // run
		});
		thread.start();
	}

	private Handler handlerComment = new Handler() {
		public void handleMessage(Message msg) {
			loadingDialog.dismiss(); // 다이얼로그 삭제
			// comment = parser.getComments();
			//
			adapterComment = new CommentAdapter(RadioTab.this,
					R.layout.comment_row, comment);
			lvComment = (ListView) findViewById(R.id.comment_listview);
			lvComment.setAdapter(adapterComment);
			lvComment.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			lvComment.setOnItemClickListener(commentClickListener);
		}
	};

	AdapterView.OnItemClickListener commentClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {

			// String mes = "Select item = "+ comment.get(position);
			// Toast.makeText(RadioTab.this, mes, Toast.LENGTH_SHORT).show();

			new AlertDialog.Builder(RadioTab.this)
					.setTitle("Alert!")
					.setMessage(
							"Are you sure you want to delete selected messages?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// dialog.cancel();
									CommentVO vo = comment.get(position);
									XmlParserDelete parser = new XmlParserDelete();
									parser.parsingResult(prgmId, userId,
											vo.getSeq());
									String result = parser.getResult();

									// Toast.makeText(RadioTab.this,
									// "삭제결과: "+result,
									// Toast.LENGTH_SHORT).show();
									if (result.equals("true")) {
										comment.remove(position);
										lvComment.clearChoices();
										// adapterComment.notifyDataSetChanged();
										Toast.makeText(RadioTab.this,
												"The message was deleted.",
												Toast.LENGTH_SHORT).show();
										loadComment();
									} else {
										Toast.makeText(
												RadioTab.this,
												"Failed to delete the message.",
												Toast.LENGTH_SHORT).show();
									}

								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}

	};

	/**
	 * Play 목록을 가져온다.
	 */
	private void loadPlaylist() {

		XmlParserPlaylist playlist = new XmlParserPlaylist(this.getPrgmId());
		playlist.parsing();

		String plist = playlist.getPlayList();
		TextView tv = (TextView) findViewById(R.id.radio_playlist_tv);
		tv.setText(plist);

	}

	/**
	 * 프로그램 목록을 가져온다.
	 */
	private void loadProgram(final String day) {
		if (player.isLoading()) {
			Toast.makeText(RadioTab.this, "Program is already start loading.",
					Toast.LENGTH_SHORT);
		} else {
			// loading...
			player.setLoading(true);
			/* ProgressDialog */
			loadingDialog = ProgressDialog.show(this, "Loading",
					"Loading. Please Wait...", true, false);

			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 페이지 정보 XML
					XmlParserSchedule prgm = new XmlParserSchedule();
					prgm.parsingProgram(day);
					// 버튼 초기화

					String radioScheduleImage = prgm.getRadioScheduleImage();

					/*
					 * ImageView scheduleImage = (ImageView)
					 * findViewById(R.id.radio_schedule_img);
					 */

					URL imageURL;
					HttpURLConnection conn;
					BufferedInputStream bis;

					try {
						imageURL = new URL(BUSAN_EFM + radioScheduleImage);
						Log.i(LOG_TAG, "URL: " + BUSAN_EFM + radioScheduleImage);
						conn = (HttpURLConnection) imageURL.openConnection();

						bis = new BufferedInputStream(conn.getInputStream(),
								10240);
						bm = BitmapFactory.decodeStream(bis);
						bis.close();

						// scheduleImage.setImageBitmap(bm);

					} catch (MalformedURLException e) {
						Log.e(LOG_TAG, e.getMessage());

					} catch (IOException e) {
						Log.e(LOG_TAG, e.getMessage());
					}

					handlerProgram.sendEmptyMessage(0);
				}
			});
			thread.start();

		}
	}

	private Handler handlerProgram = new Handler() {
		public void handleMessage(Message msg) {
			loadingDialog.dismiss(); // 다이얼로그 삭제
			// View갱신
			// String radioScheduleImage = prgm.getRadioScheduleImage();

			ImageView scheduleImage = (ImageView) findViewById(R.id.radio_schedule_img);
			scheduleImage.setImageBitmap(bm);
			// loading...
			player.setLoading(false);
		}
	};

	/**
	 * 3개의 메뉴에 대한 클릭 리스너를 정의한다. Schedule, Play list, Comment
	 * 
	 */
	private void setListeners() {

		ImageView ivPlaylist = (ImageView) findViewById(R.id.radio_btn_playlist);
		ivPlaylist.setOnClickListener(onClickListener);

		ImageView ivSchedule = (ImageView) findViewById(R.id.radio_btn_schedule);
		ivSchedule.setOnClickListener(onClickListener);

		ImageView ivComment = (ImageView) findViewById(R.id.radio_btn_comment);
		ivComment.setOnClickListener(onClickListener);

	}

	/**
	 * 4개의 탭 메뉴에 대한 클릭 이벤트 리스너 구현
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.radio_btn_schedule:
				generateTabScreen(2);
				break;
			case R.id.radio_btn_playlist:
				generateTabScreen(3);
				break;
			case R.id.radio_btn_comment:
				generateTabScreen(4);

				break;
			}
		}
	};

	/**
	 * Comment의 항목을 ListView로 처리하기 위한 Adapter 재 정의
	 * 
	 * @author hopy
	 * 
	 */
	private class CommentAdapter extends ArrayAdapter<CommentVO> {

		private ArrayList<CommentVO> commentlist;

		public CommentAdapter(Context context, int textViewResourceId,
				ArrayList<CommentVO> commentlist) {
			super(context, textViewResourceId, commentlist);
			this.commentlist = commentlist;
			Log.i("INFO", "CommentList: " + commentlist.size());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.comment_row, null);
			}

			CommentVO vo = commentlist.get(position);

			if (vo != null) {
				// TextView tvDate = (TextView)
				// v.findViewById(R.id.comment_date);
				TextView tvComment = (TextView) v
						.findViewById(R.id.comment_text);
				TextView tvId = (TextView) v.findViewById(R.id.comment_id);

				if (tvComment != null) {
					tvComment.setText(vo.getContent());
				}
				Log.i("INFO", "Comment: " + vo.getContent());
				if (tvId != null) {
					tvId.setText(vo.getId());
				}
				Log.i("INFO", "ID: " + vo.getId());
			}

			return v;
		}

	}

	private void writeDB(String id, String pw, String flag) {

		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("pw", pw);
		values.put("flag", flag);
		int colNum = db.update(DB_TABLE, values, null, null);
		if (colNum == 0)
			db.insert(DB_TABLE, "", values);
		db.close();
	}

	private void deleteDB() {

		db = dbHelper.getWritableDatabase();
		db.delete(DB_TABLE, null, null);
		db.close();
	}

	private boolean readDB() {

		boolean flag;
		db = dbHelper.getReadableDatabase();
		Cursor c = db.query(DB_TABLE, new String[] { "id", "pw", "flag" },
				null, null, null, null, null);
		// Cursor c = db.rawQuery("SELECT id, pw, flag FROM "+DB_TABLE, null);

		if (c.getCount() == 0) {
			// Log.i(LOG_TAG,"저장된 유저 정보가 없다.");
			// AlertDialog alert = new AlertDialog.Builder( RadioTab.this )
			// .setTitle("경고")
			// .setMessage("저장된 유저 정보가 없습니다.")
			// .setNeutralButton( R.string.button_close,
			// new DialogInterface.OnClickListener() {
			// public void onClick( DialogInterface dialog, int id) {
			// dialog.cancel();
			// }
			// }
			// )
			// .show();
			c.close();
			flag = false;
		} else {
			c.moveToFirst();
			this.userId = c.getString(0);
			this.password = c.getString(1);
			this.rememberMe = c.getString(2);
			Log.i(LOG_TAG, "" + userId + "," + password + "," + rememberMe);
			c.close();
			flag = true;
		}
		return flag;
	}

	class UserDBHelper extends SQLiteOpenHelper {

		public UserDBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table if not exists " + DB_TABLE
					+ "(id text primvary key, pw text, flag text);");
			Log.i(LOG_TAG, "created table");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + DB_TABLE);
			onCreate(db);
		}

	}

	/*
	 * private String parsingData(InputStream input){ String result = null; try{
	 * XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	 * XmlPullParser parser = factory.newPullParser(); parser.setInput(new
	 * InputStreamReader(input)); while(parser.next() !=
	 * XmlPullParser.END_DOCUMENT){ String name = parser.getName(); if( name !=
	 * null && name.equals("result")){ result = parser.nextText(); } }
	 * }catch(Exception e){ Log.e(LOG_TAG, e.getMessage()); } Log.i(LOG_TAG,
	 * "RE: "+result); return result; }
	 */

	private void HttpPostData(final String id, final String content) {

		XmlParserWrite parser = new XmlParserWrite();
		parser.parsingResult(prgmId, id, content);

		Log.i(LOG_TAG, "submit result:" + parser.getResult());

		if (parser.getResult().equals("true")) { // 성공하면...
			loadComment();
			TextView ivContent = (TextView) findViewById(R.id.edit_comment);
			ivContent.setText("");

		} else { // 실패하면..
			TextView ivContent = (TextView) findViewById(R.id.edit_comment);
			ivContent.setText("");

			new AlertDialog.Builder(RadioTab.this)
					.setTitle("Alert!")
					.setMessage("write comment is failed.")
					.setNeutralButton(R.string.button_close,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}

	}

	// @Override
	public void playerStarted() {

		player.getUiHandler().post(new Runnable() {
			public void run() {
				ivBtnRadioPlayControl.setImageResource(R.drawable.btn_pause);
				player.setPlayerStarted(true);
			}
		});
	}

	// @Override
	public void playerPCMFeedBuffer(final boolean isPlaying,
			final int audioBufferSizeMs, final int audioBufferCapacityMs) {
		player.getUiHandler().post(new Runnable() {
			public void run() {
				// progress.setProgress( audioBufferSizeMs * progress.getMax() /
				// audioBufferCapacityMs );
				// if (!isPlaying){
				// txtPlayStatus.setText( R.string.text_playing );
				// Toast.makeText(RadioTab.this, "버퍼링 중..."+(audioBufferSizeMs /
				// audioBufferCapacityMs), Toast.LENGTH_SHORT);
				// }
			}
		});

	}

	// @Override
	public void playerStopped(int perf) {
		player.getUiHandler().post(new Runnable() {
			public void run() {
				ivBtnRadioPlayControl.setImageResource(R.drawable.btn_play);
				player.setPlayerStarted(false);
				stop();
			}
		});

	}

	// @Override
	public void playerException(final Throwable t) {
//		player.getUiHandler().post(new Runnable() {
//			public void run() {
//				new AlertDialog.Builder(RadioTab.this)
//						.setTitle(R.string.text_exception)
//						.setMessage(t.toString())
//						.setNeutralButton(R.string.button_close,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int id) {
//										dialog.cancel();
//									}
//								}).show();
//
//				// txtStatus.setText( R.string.text_stopped );
//
				if (player.isPlayerStarted())
					playerStopped(0);
//			}
//		});
	}

	// @Override
	// public void onClick(View v) {
	//
	//
	// }
	private boolean isAutoLogin() {
		boolean flag;

		SharedPreferences pref = getSharedPreferences("Prefs", 0);
		String autoLogin = pref.getString("autoLogin", "f");

		if (autoLogin.equals("t")) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	// //////////////////////////////////////////////////////////////////////////
	// acitivity
	// //////////////////////////////////////////////////////////////////////////

	@Override
	protected void onPause() {
		super.onPause();
		// history.write();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPhone.listen(state, PhoneStateListener.LISTEN_NONE);
		stop();
	}

	// //////////////////////////////////////////////////////////////////////////
	// 메인 키 버튼 처리
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(LOG_TAG, "[onKeyDown] keyCode:" + Integer.toString(keyCode));
		int nCurrentVolumn = 0;

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			// if(player.getViewDepth() !=0){
			if (player.getTabViewDepth() != 0) {

				Toast.makeText(
						RadioTab.this,
						"In the current screen will not be able to adjust the volume.",
						Toast.LENGTH_SHORT).show();
			} else {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				nCurrentVolumn = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				seekVolumn.setProgress(nCurrentVolumn);
			}
			return true;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (player.getTabViewDepth() != 0) {
				Toast.makeText(
						RadioTab.this,
						"In the current screen will not be able to adjust the volume.",
						Toast.LENGTH_SHORT).show();

			} else {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				nCurrentVolumn = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				seekVolumn.setProgress(nCurrentVolumn);
			}
			return true;

		case KeyEvent.KEYCODE_BACK:
			if (player.getTabViewDepth() == 0) {
				new AlertDialog.Builder(RadioTab.this)
						.setTitle("Alert!")
						.setMessage("Are you sure exit?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										moveTaskToBack(true);
										finish();
									}
								}).setNegativeButton("No", null).show();

			} else {
				player.setTabViewDepth(0);

				setContentView(R.layout.radio_activity);

				ivBtnRadioPlayControl = (ImageView) findViewById(R.id.btn_control);
				ivBtnRadioPlayControl
						.setOnClickListener(onPlayBtnControlListener);

				imgMainBG = (ImageView) findViewById(R.id.img_radio_maintitle);
				imgMainBG.setOnClickListener(onImgMainBgControlListener);

				setListeners();

				// setPlayBtnImg();
				loadMain();
				setVolumn();

				// Log.i(LOG_TAG,
				// "현재상태(BackButton): "+player.getPlayFeathers());
				if (player.getPlayFeathers() == player.PLAY_RADIO) {
					ivBtnRadioPlayControl
							.setImageResource(R.drawable.btn_pause);
				} else {
					ivBtnRadioPlayControl.setImageResource(R.drawable.btn_play);
				}

			}
			return true;
		}
		// return super.onKeyDown(keyCode, event);
		return false;
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private
	// //////////////////////////////////////////////////////////////////////////

	private void start(int decoder) {
		ArrayAACPlayer aacplay = new ArrayAACPlayer(
				ArrayDecoder.create(decoder), this,
				player.STREAMING_BUFFER_AUDIO, player.STREAMING_BUFFER_DECODE);
		aacplay.setPlayerCallback(this);
		player.setAacPlayer(aacplay);
		isPlaying = true;
		player.getAacPlayer().playAsync(STREAMING_MMS_URL);
		player.setPlaying(true);
		player.setPlayFeathers(player.PLAY_RADIO);
	}

	private void stop() {
		if (player.getAacPlayer() != null) {		
			player.getAacPlayer().stop();
			player.setPlaying(false);
			player.setPlayFeathers(player.PLAY_OFF);
		}
	}

	public String getPrgmId() {
		return prgmId;
	}

	public void playerLoaded() {
		// TODO Auto-generated method stub
		
	}
	
	PhoneStateListener state = new PhoneStateListener() {
		/* (non-Javadoc)
		 * @see android.telephony.PhoneStateListener#onCallStateChanged(int, java.lang.String)
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				Log.i(LOG_TAG, "call state idle");
				synchronized(player) {
					if (isPlaying) {
						start(Decoder.DECODER_FFMPEG_WMA);
						player.setPlayFeathers(player.PLAY_RADIO);
						player.setPlaying(true);
					}
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(LOG_TAG, "call state ringing");
				if (player.isPlaying()) {
					isPlaying = true;
					stop();
					player.setPlayFeathers(player.PLAY_OFF);
					player.setPlaying(false);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			}
		}
	};

}
//
