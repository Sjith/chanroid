package kr.co.vipsapp;

import java.util.ArrayList;
import java.util.Calendar;

import kr.co.vipsapp.util.XmlParserNews;
import kr.co.vipsapp.util.XmlParserNewsPlay;
import kr.co.vipsapp.util.vo.AODVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.spoledge.aacplayer.ArrayAACPlayer;
import com.spoledge.aacplayer.ArrayDecoder;
import com.spoledge.aacplayer.Decoder;
import com.spoledge.aacplayer.PlayerCallback;


public class NewsTab extends Activity implements PlayerCallback{

	final static int NEWS_DATE = 0;
	final String LAST_NEWS = "http://www.befm.or.kr/app/LinkageAction.do?cmd=aodList&prgmId=news&playDt=";
	
	final static String BUSAN_EFM = "http://www.befm.or.kr"; 
	
	String LOG_TAG="eFM BUSAN";

	String newDate = "";
	String script;
	String fileURL;
	
	long currentTime=0L;
	
	ImageView ivBtnPlayControl; 
	
	AudioManager audioManager;
	SeekBar seekVolumn;
	
	RadioPlayer player = RadioPlayer.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.news_acitivity);		
		
		ImageView btnControl = (ImageView) findViewById(R.id.btn_lastnews);		
		btnControl.setOnClickListener(onClickLastNews);		

		loadLastNews();
		
	}
	
	private void setVolumn(){
		
		seekVolumn = (SeekBar) findViewById(R.id.news_volumn_sk);

		// AudioManager
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		seekVolumn.setMax(nMax);
		seekVolumn.setProgress(nCurrentVolumn);
		seekVolumn.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

//			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,	progress, 0);
				Log.i(LOG_TAG,"set volumn: "+progress);
			}

//			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

//			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
			
		});

	}
	
	/**
	 * 날짜 선택 화면
	 */
	private OnClickListener onClickLastNews = new OnClickListener() {
		
		public void onClick(View v) {
			// depth = 1
			//player.setViewDepth(1);
			player.setTabViewDepth(1);
			
			setContentView(R.layout.news_date);	
//			Log.i("NEWS AOD - ","날짜 선택 화면");
			// 날짜 선택 버튼
			ImageView btnSelect = (ImageView) findViewById(R.id.btn_news_select);			
			btnSelect.setOnClickListener(selectOnClickListener);	
			
			// 날짜 선택 취소 버튼
			ImageView btnBack = (ImageView) findViewById(R.id.btn_news_back);			
			btnBack.setOnClickListener(backOnClickListener);						
		}
	};
	
	/**
	 * 날짜 선택화면에서 취소 버튼 선택시
	 */
	private OnClickListener backOnClickListener = new OnClickListener(){

//		@Override
		public void onClick(View arg0) {
			// depth = 0
			//player.setViewDepth(0);
			player.setTabViewDepth(0);
//			Log.i("NEWS AOD - ","날짜 선택에서 취소 버튼");
			setContentView(R.layout.news_acitivity);	
			ImageView btnControl = (ImageView) findViewById(R.id.btn_lastnews);
			
			btnControl.setOnClickListener(onClickLastNews);		
			
			loadLastNews();	
		}
		
	};
	
	/**
	 * 날짜 선택화면에서 Select 버튼 선택시
	 */
	private OnClickListener selectOnClickListener = new OnClickListener(){
		
//		@Override
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		public void onClick(View v) {
			//Intent intent = new Intent();
//			Log.i("NEWS AOD - ","날짜 선택후 선택 버튼 클릭");
			final DatePicker datePicker = (DatePicker) findViewById(R.id.new_datepicker);
			
			int mYear = datePicker.getYear();
			int mMonth = datePicker.getMonth()+1; 
			int mDay = datePicker.getDayOfMonth();

			Calendar cal = Calendar.getInstance();
			int cYear = cal.get(Calendar.YEAR);
			int cMonth = cal.get(Calendar.MONTH)+1;
			int cDay = cal.get(Calendar.DAY_OF_MONTH);
			
			Log.i(LOG_TAG,""+mYear+","+mMonth+","+mDay);
			Log.i(LOG_TAG,""+cYear+","+cMonth+","+cDay);
			
			String sMonth = "";
			// 달 정보를 2자리 문자열로 바꾸기
			if(mMonth<10) sMonth = "0"+mMonth;
			else sMonth = ""+mMonth;
			
			String sDay = "";
			// 달 정보를 2자리 문자열로 바꾸기
			if(mDay<10) sDay = "0"+mDay;
			else sDay = ""+mDay;			
			newDate = ""+mYear+sMonth+sDay;
			
			
			setContentView(R.layout.news_acitivity);	
			ImageView btnControl = (ImageView) findViewById(R.id.btn_lastnews);
			
			btnControl.setOnClickListener(onClickLastNews);		
			
			// depth = 0
			//player.setViewDepth(0);
			player.setTabViewDepth(0);
			
			if(mYear >= cYear && mMonth >= cMonth && mDay >= cDay){
				loadLastNews();
			}else{
				loadLastNews(newDate);
			}
		}
		
	};	
	/**
	 * 
	 */
	private void loadLastNews(){
		
		//Log.i(LOG_TAG, "현재 Play 상태: "+player.isPlayerStarted()+", "+player.getPlayFeathers());
//		Log.i("NEWS AOD - ","최근 뉴스 목록 가져오기");
		// 월 단위로 호출
		XmlParserNews news = new XmlParserNews();
		news.parse();
		final ArrayList<AODVO> aodList = news.getAod();
		
		NewsAdapter adapter = new NewsAdapter(this, R.layout.news_row, aodList);
		//adapter.setNotifyOnChange(true);
		ListView lv = (ListView) findViewById(R.id.news_listview);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			// Play 화면
//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				// depth = 1
				//player.setViewDepth(1);
				player.setTabViewDepth(1);
				
				AODVO vo = aodList.get(position);
//				Log.i(LOG_TAG, "날짜:"+vo.getpDate());
//				Log.i(LOG_TAG, "prgmId:"+vo.getPrgmId());
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll_news);
				//ll.setVisibility(View.GONE);
				ll.removeAllViews();
								
				View subview =  View.inflate(NewsTab.this, R.layout.news_play, null);
				ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
				
				ImageView btnBack = (ImageView) findViewById(R.id.news_play_backControl);
				btnBack.setOnClickListener(playBackOnClickListener);
				//
//				Log.i(LOG_TAG, "현재상태(BackButton): "+player.getPlayFeathers());
				
				ivBtnPlayControl = (ImageView) findViewById(R.id.news_btn_control);				
				
				//if((StreamControl.playFeathers & StreamControl.PLAY_NEWS) == StreamControl.PLAY_NEWS){
				if((player.getPlayFeathers() == player.PLAY_NEWS) && player.isPlaying()){					
					ivBtnPlayControl.setImageResource(R.drawable.btn_pause);		
				}else{
					ivBtnPlayControl.setImageResource(R.drawable.btn_play);
				}
				
				//
				loadNewsAOD(vo.getPrgmId(), vo.getpDate());
				setVolumn();
			}
			
		});		
	}

	private void loadLastNews(String date){
		//Log.i("INFO", date);			
		//Log.i(LOG_TAG, "현재 Play 상태: "+player.isPlayerStarted()+", "+player.getPlayFeathers());
//		Log.i("NEWS AOD - ","선택한 날짜의 뉴스 목록 가져오기");
		
		XmlParserNews news = new XmlParserNews();
		news.parse(date);
		final ArrayList<AODVO> aodList = news.getAod();
		
		NewsAdapter adapter = new NewsAdapter(this, R.layout.news_row, aodList);
		//adapter.setNotifyOnChange(true);
		ListView lv = (ListView) findViewById(R.id.news_listview);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			// Play 화면
//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// depth = 1
				//player.setViewDepth(1);
				player.setTabViewDepth(1);
				
				AODVO vo = aodList.get(position);
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.ll_news);
				//ll.setVisibility(View.GONE);
				ll.removeAllViews();
								
				View subview =  View.inflate(NewsTab.this, R.layout.news_play, null);
				ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
				
				ImageView btnBack = (ImageView) findViewById(R.id.news_play_backControl);
				//btnBack.setOnClickListener(playBackDateOnClickListener);
				btnBack.setOnClickListener(playBackOnClickListener);
				
				//
//				Log.i(LOG_TAG, "현재상태(BackButton): "+player.getPlayFeathers());
				ivBtnPlayControl = (ImageView) findViewById(R.id.news_btn_control);
				
				if((player.getPlayFeathers() == player.PLAY_NEWS) && player.isPlaying()){					
					ivBtnPlayControl.setImageResource(R.drawable.btn_pause);		
				}else{
					ivBtnPlayControl.setImageResource(R.drawable.btn_play);		
				}				

				
//				Log.i(LOG_TAG, "날짜:"+vo.getpDate());
//				Log.i(LOG_TAG, "prgmId:"+vo.getPrgmId());
				
				loadNewsAOD(vo.getPrgmId(), vo.getpDate());
				setVolumn();
			}
			
		});
	}
	
	
	private void loadNewsAOD(String prgmId, String playDt){
//		Log.i(LOG_TAG, "News AOD 정보 가져오기:"+prgmId+","+playDt);
		XmlParserNewsPlay nPlay = new XmlParserNewsPlay();		
		nPlay.parser(prgmId, playDt);
		script = nPlay.getScript();
		fileURL = nPlay.getFileURL();

		TextView ivScript = (TextView) findViewById(R.id.news_script);
		ivScript.setTextSize(TypedValue.COMPLEX_UNIT_PX, player.getScriptFontSize());
		ivScript.setText(script);
		
		// play 버튼 이미지
		if(!fileURL.equals("NO DATA")){
			ivBtnPlayControl = (ImageView) findViewById(R.id.news_btn_control);
			ivBtnPlayControl.setOnClickListener(onPlayBtnControlListener);
		}

	}
	
	/**
	 * Play/Stop 버튼을 선택할 경우
	 */
	private OnClickListener onPlayBtnControlListener = new OnClickListener() {

//		@Override
		public void onClick(View v) {
			synchronized(player) {
				if(player.isPlaying() && player.getPlayFeathers() != player.PLAY_RADIO){
					stop();
					ivBtnPlayControl.setImageResource(R.drawable.btn_play);
				}else{
					start( Decoder.DECODER_FFMPEG_WMA );
//					start( Decoder.DECODER_FFMPEG );
					currentTime = System.currentTimeMillis();
					ivBtnPlayControl.setImageResource(R.drawable.btn_pause);
				}	
			}
		}
	};
	
	private OnClickListener playBackOnClickListener = new OnClickListener(){

//		@Override
		public void onClick(View v) {
			player.setTabViewDepth(0);			
			
			if(player.getPlayFeathers() != player.PLAY_RADIO){
				stop();			
				player.setPlayFeathers(player.PLAY_OFF);
				player.setPlaying(false);
				ivBtnPlayControl = (ImageView) findViewById(R.id.news_btn_control);	
				ivBtnPlayControl.setImageResource(R.drawable.btn_play);									
			}
			
			LinearLayout ll = (LinearLayout) findViewById(R.id.ll_news);
			ll.removeAllViews();

			
			View subview =  View.inflate(NewsTab.this, R.layout.news_acitivity, null);
			ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
			
			loadLastNews();
			
			ImageView btnControl = (ImageView) findViewById(R.id.btn_lastnews);		
			btnControl.setOnClickListener(onClickLastNews);		
		}
	};
	
	private class NewsAdapter extends ArrayAdapter<AODVO>{
		
		ArrayList<AODVO> aodList;
		
		public NewsAdapter(Context context, int textViewResourceId,
				ArrayList<AODVO> aodList) {
			super(context, textViewResourceId, aodList);
			this.aodList = aodList;
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v == null){
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.news_row, null);
			}
		
			AODVO vo = (AODVO) aodList.get(position);
			
			if(vo != null){
				TextView tvDate = (TextView) v.findViewById(R.id.news_date);
				TextView tvTitle = (TextView) v.findViewById(R.id.news_title);
				if(tvTitle != null){
					tvDate.setText(vo.getDate());
					tvTitle.setText(vo.getTitle());
				}
			}
			
			return v;
		}
		
	}


//	@Override
	public void playerStarted() {

		player.getUiHandler().post( new Runnable() {
	          public void run() {
	        	  player.setPlayerStarted(true);
	        	  ivBtnPlayControl.setImageResource(R.drawable.btn_pause);	
	          }
		});
	}

//	@Override
	public void playerPCMFeedBuffer(final boolean isPlaying, final int audioBufferSizeMs,
			final int audioBufferCapacityMs) {
//			player.getUiHandler().post( new Runnable() {
//            public void run() {
                //progress.setProgress( audioBufferSizeMs * progress.getMax() / audioBufferCapacityMs );
//                if (!isPlaying){
                	// txtPlayStatus.setText( R.string.text_playing );
//                	Toast.makeText(NewsTab.this, "버퍼링 중..."+(audioBufferSizeMs / audioBufferCapacityMs), Toast.LENGTH_SHORT);
//                }
//            }
//        });
		
	}

//	@Override
	public void playerStopped(int perf) {
		player.getUiHandler().post( new Runnable() {
            public void run() {
            	player.setPlayerStarted(false);
            	//StreamControl.playerStarted = false;
                ivBtnPlayControl.setImageResource(R.drawable.btn_play);	
            }
        });
		
	}

//	@Override
	public void playerException(final Throwable t) {
		player.getUiHandler().post( new Runnable() {
	            public void run() {
	                new AlertDialog.Builder( NewsTab.this )
	                    .setTitle( R.string.text_exception )
	                    .setMessage( t.toString())
	                    .setNeutralButton( R.string.button_close,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick( DialogInterface dialog, int id) {
	                                dialog.cancel();
	                            }
	                        }
	                     )
	                    .show();

	                //txtStatus.setText( R.string.text_stopped );

	                if (player.isPlayerStarted()) playerStopped( 0 );
	            }
	        });
	}

//	@Override
//	public void onClick(View v) {
//		
//		
//	}
	
    @Override
    protected void onPause() {
        super.onPause();
		if (player.getPlayFeathers() != player.PLAY_RADIO &&
				player.getTabFeathers() != player.TAB_NEWS) {
	        if (ivBtnPlayControl != null) ivBtnPlayControl.setImageResource(R.drawable.btn_play);
	        synchronized(player) {
	        	stop();
	        }
		}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // 메인 키 버튼 처리
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(LOG_TAG, "[onKeyDown] keyCode:"+Integer.toString(keyCode));
		int nCurrentVolumn =0;
		
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_UP :	
				if(player.getTabViewDepth() ==0){
					Toast.makeText(NewsTab.this, "In the current screen will not be able to adjust the volume.",Toast.LENGTH_SHORT).show();
				
				}else{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
					nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					seekVolumn.setProgress(nCurrentVolumn);
				}
				return true;
				
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if(player.getTabViewDepth() ==0){
					Toast.makeText(NewsTab.this, "In the current screen will not be able to adjust the volume.",Toast.LENGTH_SHORT).show();
					
				}else{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
					nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					seekVolumn.setProgress(nCurrentVolumn);
				}
				return true;
				
			case KeyEvent.KEYCODE_BACK:
				
				if(player.getTabViewDepth() == 0){
//					new AlertDialog.Builder( NewsTab.this )
//						.setTitle("Alert!")						
//						.setMessage("Are you sure exit?")
//						.setPositiveButton( "Yes",
//							new DialogInterface.OnClickListener() {
//							public void onClick( DialogInterface dialog, int id) {
//								
//								moveTaskToBack(true);
//								finish();
//							}
//						}
//						)
//						.setNegativeButton("No", null)
//						.show();
					((eBFMView)getParent()).setTabIndex(0);
				}else{	// 1
					if(player.isPlaying() && (player.getPlayFeathers() != player.PLAY_RADIO)){
						stop();
						player.setPlayFeathers(player.PLAY_OFF);
						player.setPlaying(false);
					}
					
					//player.setViewDepth(0);
					player.setTabViewDepth(0);
					
					setContentView(R.layout.news_acitivity);	
					ImageView btnControl = (ImageView) findViewById(R.id.btn_lastnews);
					
					btnControl.setOnClickListener(onClickLastNews);		
					
					loadLastNews();
				}
				
	            return true;

		}
		//return super.onKeyDown(keyCode, event);
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		player.setTabFeathers(player.TAB_NEWS);
		if (player.getPlayFeathers() != player.PLAY_NEWS) {
			if (ivBtnPlayControl != null) ivBtnPlayControl.setImageResource(R.drawable.btn_play);
		} else {
			if (ivBtnPlayControl != null) ivBtnPlayControl.setImageResource(R.drawable.btn_pause);
		}
//		if (player.getPlayFeathers() != player.PLAY_RADIO) {
//			stop();
//		}
	}
	
    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private void start( int decoder ) {
		ArrayAACPlayer aacplay = new ArrayAACPlayer(
				ArrayDecoder.create(decoder), this,
				player.STREAMING_BUFFER_AUDIO, player.STREAMING_BUFFER_DECODE);
		aacplay.setPlayerCallback(this);
		player.setAacPlayer(aacplay);
		player.getAacPlayer().playAsync(fileURL);
		player.setPlaying(true);
		player.setPlayFeathers(player.PLAY_NEWS);
    }
    
    private void stop() {
        if (player.getAacPlayer() != null) { 
        	player.getAacPlayer().stop(); 
        }
    	player.setPlaying(false);
    	player.setPlayFeathers(player.PLAY_OFF);
    }


	public void playerLoaded() {
		// TODO Auto-generated method stub
		
	}

	
}
