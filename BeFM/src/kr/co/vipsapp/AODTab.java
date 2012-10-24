package kr.co.vipsapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.vipsapp.util.XmlAODList;
import kr.co.vipsapp.util.XmlAODProgram;
import kr.co.vipsapp.util.XmlParserBusanEFM;
import kr.co.vipsapp.util.vo.AODListVO;
import kr.co.vipsapp.util.vo.AODProgramVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
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

public class AODTab extends Activity implements PlayerCallback{

	final String STREAMING_WMA_URL = "http://www.befm.or.kr/";
	final String BUSAN_EFM = "http://www.befm.or.kr"; 
	
	private String prgmId = "";
	String LOG_TAG = "eFM BUSAN";
	
	long currentTime=0L;
	
	String fileURL;
	
	static ImageView ivAODBtnPlayControl;
	ImageView ivAODICanBtnPlayControl;
	ImageView ivAODWhatBtnPlayControl;
	
	AudioManager audioManager;
	SeekBar seekVolumn;

	RadioPlayer player = RadioPlayer.getInstance();
	int currentAOD = player.PLAY_AOD_EFM;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aod_activity);	
		// 첫번째. Busan e-FM 화면 
		ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_btn_control);	
		ivAODICanBtnPlayControl = (ImageView) findViewById(R.id.aod_ican_btn_control);
		ivAODWhatBtnPlayControl = (ImageView) findViewById(R.id.aod_what_btn_control);
		generateTabScreen(1);		
	}
	
	private void setVolumn(int id){
		
		seekVolumn = (SeekBar) findViewById(id);

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
			public void onStartTrackingTouch(SeekBar seekBar) {	}

//			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}			
		});
	}
	
	/**
	 * 
	 */
	private OnClickListener onClickLastAOD = new OnClickListener() {
		
		public void onClick(View v) {
			// depth = 31
			//player.setViewDepth(21);
			player.setTabViewDepth(21);
			
			setContentView(R.layout.aod_date);	
			
			final ImageView btnSelect = (ImageView) findViewById(R.id.btn_aod_select);			
			btnSelect.setOnClickListener(selectOnClickListener);	
			
			final ImageView btnBack = (ImageView) findViewById(R.id.btn_aod_back);			
			btnBack.setOnClickListener(cancelOnClickListener);			
			

		}
	};
	
	

	/**
	 * 날짜선택에서 취소 버튼을 누른 경우
	 */
	private OnClickListener cancelOnClickListener = new OnClickListener(){

//		@Override
		public void onClick(View v) {
			
			setContentView(R.layout.aod_play);
			loadAod(prgmId);

			ImageView btnControl = (ImageView) findViewById(R.id.btn_aod_select_date);		
			btnControl.setOnClickListener(onClickLastAOD);	

			ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_btn_control);
			ivAODBtnPlayControl.setOnClickListener(onAODPlayBtnControlListener);

			ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_play_backControl);
			aodplay_btnBack.setOnClickListener(aodBackOnClickListener);
			
			setVolumn(R.id.aod_volumn_sk);
			
		}
		
	};	
	
	/**
	 * 날짜 선택에서 select를 선택한 경우
	 */
	private OnClickListener selectOnClickListener = new OnClickListener(){
		private String playDt;
//		@Override
		public void onClick(View v) {
			 
			player.setTabViewDepth(11);
			
			final DatePicker datePicker = (DatePicker) findViewById(R.id.aod_datepicker);
			
			int mYear = datePicker.getYear();
			int mMonth = datePicker.getMonth()+1; 
			int mDay = datePicker.getDayOfMonth();
			
			//
			Log.i(LOG_TAG,""+mYear+","+mMonth+","+mDay);
			//Log.i(LOG_TAG,""+cYear+","+cMonth+","+cDay);
			
			String sMonth = "";
			// 달 정보를 2자리 문자열로 바꾸기
			if(mMonth<10) sMonth = "0"+mMonth;
			else sMonth = ""+mMonth;
			
			String sDay = "";
			// 달 정보를 2자리 문자열로 바꾸기
			if(mDay<10) sDay = "0"+mDay;
			else sDay = ""+mDay;			
			playDt = ""+mYear+sMonth+sDay;


			setContentView(R.layout.aod_play);

			ImageView btnControl = (ImageView) findViewById(R.id.btn_aod_select_date);		
			btnControl.setOnClickListener(onClickLastAOD);	

			ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_btn_control);
			ivAODBtnPlayControl.setOnClickListener(onAODPlayBtnControlListener);

			ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_play_backControl);
			aodplay_btnBack.setOnClickListener(aodBackOnClickListener);
			
			setVolumn(R.id.aod_volumn_sk);
			
			loadAod(prgmId, playDt);
		}
	};
	
	/**
	 * 날짜 선택에서 뒤로 버튼을 선택한 경우
	 */
	OnClickListener backPlayOnClickListener = new OnClickListener(){

		public void onClick(View v) {
			
			if(player.isPlaying() && player.getPlayFeathers() != player.PLAY_RADIO){
				stop();		
				ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_play_backControl);	
				ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);									
			}
			
			setContentView(R.layout.aod_activity);
			generateTabScreen(1);

//			ImageView btnControl = (ImageView) findViewById(R.id.btn_aod_select_date);		
//			btnControl.setOnClickListener(onClickLastAOD);	

			ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_btn_control);
			ivAODBtnPlayControl.setOnClickListener(onAODPlayBtnControlListener);

			ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_play_backControl);
			aodplay_btnBack.setOnClickListener(aodBackOnClickListener);
			
			setVolumn(R.id.aod_volumn_sk);
		}
		
	};
	
	

	private void generateTabScreen(int num){
		LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
		//ll.removeAllViewsInLayout();
		
		Button iv1, iv2, iv3;
		
		View view = null;		
		
		switch(num){		
		case 1:
			// View 정보를 갱신한다.
			// depth = 11
			//player.setViewDepth(0);
			player.setTabViewDepth(0);
			
			iv1  = (Button) findViewById(R.id.iv_befm);
			iv1.setBackgroundResource(R.drawable.btn_befm_sel);
			
			iv2  = (Button) findViewById(R.id.iv_ican);
			iv2.setBackgroundResource(R.drawable.btn_ican_non);
			
			iv3  = (Button) findViewById(R.id.iv_what);
			iv3.setBackgroundResource(R.drawable.btn_what_non);
			
			view = View.inflate(AODTab.this, R.layout.aod_list, null);
			ll.addView(view, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));		
			currentAOD = player.PLAY_AOD_EFM;
			load_eFM();
			
			break;
		case 2:
			// View 정보를 갱신한다.
			// depth = 0
			//player.setViewDepth(0);
			player.setTabViewDepth(0);
			
			prgmId = "ican";
			iv1  = (Button) findViewById(R.id.iv_befm);
			iv1.setBackgroundResource(R.drawable.btn_befm_non);
			
			iv2  = (Button) findViewById(R.id.iv_ican);
			iv2.setBackgroundResource(R.drawable.btn_ican_sel);
			
			iv3  = (Button) findViewById(R.id.iv_what);
			iv3.setBackgroundResource(R.drawable.btn_what_non);
			
			view = View.inflate(AODTab.this, R.layout.aod_list, null);
			ll.addView(view, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
			currentAOD = player.PLAY_AOD_ICAN;
			load_iCan();
			
			break;
		case 3:
			// View 정보를 갱신한다.
			// depth = 0
			//player.setViewDepth(0);
			player.setTabViewDepth(0);
			
			prgmId = "what";
			iv1  = (Button) findViewById(R.id.iv_befm);
			iv1.setBackgroundResource(R.drawable.btn_befm_non);
			
			iv2  = (Button) findViewById(R.id.iv_ican);
			iv2.setBackgroundResource(R.drawable.btn_ican_non);
			
			iv3  = (Button) findViewById(R.id.iv_what);
			iv3.setBackgroundResource(R.drawable.btn_what_sel);
			
			view = View.inflate(AODTab.this, R.layout.aod_list, null);
			ll.addView(view, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
			currentAOD = player.PLAY_AOD_WHAT;
			load_What();
			break;
		}
			
		setListeners();	
	}
	
	/**
	 * 
	 */
	private void load_eFM(){
		// depth = 21
		//player.setViewDepth(21);
		
		XmlAODProgram prgm = new XmlAODProgram();
		prgm.parsingAODProgram();
		
		final ArrayList<AODProgramVO> aodPrgmList = prgm.getPrgmList();
		
		AODProgramAdapter adapter = new AODProgramAdapter(this, R.id.aod_efm, aodPrgmList);
		ListView lv = (ListView) findViewById(R.id.aod_listview);
		lv.setAdapter(adapter);		
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				AODProgramVO vo = (AODProgramVO) aodPrgmList.get(position);
				
				//Log.i("INFO",vo.getId());
				
				LinearLayout rl = (LinearLayout) findViewById(R.id.aod_rl);
				rl.setVisibility(View.GONE);
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
				ll.removeAllViews();				

				View subview =  View.inflate(AODTab.this, R.layout.aod_play, null);
				ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));				
		
				ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_play_backControl);
				aodplay_btnBack.setOnClickListener(aodBackOnClickListener);
				
				ImageView btnControl = (ImageView) findViewById(R.id.btn_aod_select_date);		
				btnControl.setOnClickListener(onClickLastAOD);	
				
//				TextView et = (TextView) findViewById(R.id.aod_play_title);
//				et.setText(vo.getName());
				
				//if(StreamControl.playerStarted) stop();
				ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_btn_control);				
			
				
				//Log.i(LOG_TAG,aodEBusan);
				prgmId = vo.getId();
				//TextView et = (TextView) findViewById(R.id.aodprgm_tv);
				loadAod(prgmId);// 프로그램 이름을 넘김		
				setVolumn(R.id.aod_volumn_sk);
				
			}

			
		});
	}

	private void loadAod(String prgmId, String playDt) {
		player.setTabViewDepth(11);
		
		XmlParserBusanEFM efm = new XmlParserBusanEFM();		
		efm.parsing(prgmId, playDt);
		
		ImageView ivTitle = (ImageView) findViewById(R.id.aod_play_titleImg);
		
			URL imageURL;
			HttpURLConnection conn;
			BufferedInputStream bis;
			try {
				imageURL = new URL(BUSAN_EFM+efm.getPrgmImg());
				Log.i(LOG_TAG, BUSAN_EFM+efm.getPrgmImg());
				conn = (HttpURLConnection) imageURL.openConnection();
				bis = new BufferedInputStream(conn.getInputStream(), 10240);
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				
				ivTitle.setImageBitmap(bm);		
			} catch (MalformedURLException e) {
				Log.i(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.i(LOG_TAG, e.getMessage());
			}
	
		TextView ivScript = (TextView) findViewById(R.id.aod_play_script);
		ivScript.setTextSize(TypedValue.COMPLEX_UNIT_PX, player.getScriptFontSize());
		ivScript.setText(efm.getScript());
		
		TextView tvDate = (TextView) findViewById(R.id.textView1);
		tvDate.setText(efm.getDate());
		
		this.fileURL = efm.getFileURL();
		
		if(efm.getFileURL().equals("NO DATA")){
			new AlertDialog.Builder( AODTab.this )
            .setTitle( "Alert!" )
            .setMessage("AOD info is not found.")
            .setNeutralButton( R.string.button_close,
                new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
             )
            .show();
		}else{

			if((player.getPlayFeathers() == player.PLAY_AOD_EFM) && player.isPlaying()) {					
				ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);		
			}else{
				ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);		
			}
			ivAODBtnPlayControl.setOnClickListener(onAODPlayBtnControlListener);
		}

	}
	
	/**
	 * eFM AOD Play/Stop 버튼을 선택할 경우
	 */
	private OnClickListener onAODPlayBtnControlListener = new OnClickListener() {

//		@Override
		public void onClick(View v) {
			synchronized(player) {
				if(player.isPlaying() && player.getPlayFeathers() != player.PLAY_RADIO){
					stop();
					ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);
				}else{
					start( Decoder.DECODER_FFMPEG_WMA );
//					start( Decoder.DECODER_FFMPEG );
					ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);
				}	
			}
		}
		
	};
	
	private void loadAod(String prgmId) {
		player.setTabViewDepth(11);
		
		XmlParserBusanEFM efm = new XmlParserBusanEFM();		
		efm.parsing(prgmId);
		
		ImageView ivTitle = (ImageView) findViewById(R.id.aod_play_titleImg);
		
			URL imageURL;
			HttpURLConnection conn;
			BufferedInputStream bis;
			try {
				imageURL = new URL(BUSAN_EFM+efm.getPrgmImg());
				Log.i(LOG_TAG, BUSAN_EFM+efm.getPrgmImg());
				conn = (HttpURLConnection) imageURL.openConnection();
				bis = new BufferedInputStream(conn.getInputStream(), 10240);
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				
				ivTitle.setImageBitmap(bm);		
			} catch (MalformedURLException e) {
				Log.i(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.i(LOG_TAG, e.getMessage());
			}
	
		TextView ivScript = (TextView) findViewById(R.id.aod_play_script);
		ivScript.setTextSize(TypedValue.COMPLEX_UNIT_PX, player.getScriptFontSize());
		ivScript.setText(efm.getScript());
		
		TextView tvDate = (TextView) findViewById(R.id.textView1);
		tvDate.setText(efm.getDate());
		
		this.fileURL = efm.getFileURL();
		
		if(efm.getFileURL().equals("NO DATA")){
			new AlertDialog.Builder( AODTab.this )
            .setTitle( "Alert!" )
            .setMessage("AOD info is not found.")
            .setNeutralButton( R.string.button_close,
                new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
             )
            .show();
		}else{

			if((player.getPlayFeathers() == player.PLAY_AOD_EFM) && player.isPlaying()){					
				ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);		
			}else{
				ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);		
			}
			ivAODBtnPlayControl.setOnClickListener(onAODPlayBtnControlListener);
		}

	}
	
	/**
	 * BeFM AOD Play 화면에서 Back 버튼 클릭시 호출 
	 */
	OnClickListener aodBackOnClickListener = new OnClickListener(){
		public void onClick(View view) {

			if(player.isPlaying() && (player.getPlayFeathers() != player.PLAY_RADIO)){
				stop();
			}
			
			setContentView(R.layout.aod_activity);
			
			generateTabScreen(1);
			
			player.setTabViewDepth(0);
		}
		
	};
	
	/**
	 * 
	 * @author hopy
	 *
	 */
	private class AODProgramAdapter extends ArrayAdapter<AODProgramVO>{
		ArrayList<AODProgramVO> aodPrgmList;
		
		public AODProgramAdapter(Context context, int textViewResourceId,
				ArrayList<AODProgramVO> aodPrgmList) {
			super(context, textViewResourceId, aodPrgmList);

			this.aodPrgmList = aodPrgmList;			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// 
			View v = convertView;
			
			if(v == null){
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.aod_efm_row, null);
			}
			
			AODProgramVO vo = aodPrgmList.get(position);
			
			if(vo != null){
				TextView tvEFM = (TextView) v.findViewById(R.id.aod_efm);
				if(tvEFM !=null){
					tvEFM.setText(vo.getName());
					Log.i("INFO","id: "+vo.getId()+", name: "+vo.getName());	
				}
				
			}
			
			return v;
		}
		
		
		
	}
	
	
	/**
	 * I Can you can
	 */
	private void load_iCan(){

		
		XmlAODList ican = new XmlAODList();
		ican.parsingAOD(prgmId);
		
		final ArrayList<AODListVO> aodList = ican.getAodList();

		ListView lv = (ListView) findViewById(R.id.aod_listview);
		AODAdapter adapter = new AODAdapter(this, R.id.aod_efm, aodList);
		
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				AODListVO vo = (AODListVO) aodList.get(position);
				
				LinearLayout rl = (LinearLayout) findViewById(R.id.aod_rl);
				rl.setVisibility(View.GONE);
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
				ll.removeAllViews();				
				
				View subview =  View.inflate(AODTab.this, R.layout.aod_ican_play, null);
				ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));				
		
				ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_ican_play_backControl);
				aodplay_btnBack.setOnClickListener(aodIcanBackOnClickListener);
				
				
				//if(StreamControl.playerStarted) stop();

				ivAODICanBtnPlayControl = (ImageView) findViewById(R.id.aod_ican_btn_control);								
				
				// play 버튼 이미지
				if((player.getPlayFeathers() == player.PLAY_AOD_ICAN) && player.isPlaying()){					
					ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_pause);		
				}else{
					ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);		
				}
				
/*				if((player.getPlayFeathers() & player.PLAY_AOD_ICAN) == player.PLAY_AOD_ICAN){
					ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_pause);
				}else{
					ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);	
				}*/
				
				
				playAODIcan(vo.getpDate());
				setVolumn(R.id.aod_ican_volumn_sk);
				
				Log.i("INFO-aod",prgmId);
				

			}
			
		});
				
	}
	
	
	/**
	 * What did ... 
	 */
	private void load_What(){
		XmlAODList what = new XmlAODList();
		
		what.parsingAOD(prgmId);
		
		final ArrayList<AODListVO> aodList = what.getAodList();
		
		AODAdapter adapter = new AODAdapter(this, R.id.aod_efm, aodList);
		ListView lv = (ListView) findViewById(R.id.aod_listview);
		
		lv.setAdapter(adapter);		
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			
				AODListVO vo = (AODListVO) aodList.get(position);
				
				LinearLayout rl = (LinearLayout) findViewById(R.id.aod_rl);
				rl.setVisibility(View.GONE);
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
				ll.removeAllViews();				
				
				View subview =  View.inflate(AODTab.this, R.layout.aod_what_play, null);
				ll.addView(subview, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));				
		
				ImageView aodplay_btnBack = (ImageView) findViewById(R.id.aod_what_play_backControl);
				aodplay_btnBack.setOnClickListener(aodWhatBackOnClickListener);
				
				//if(StreamControl.playerStarted) stop();
				ivAODWhatBtnPlayControl = (ImageView) findViewById(R.id.aod_what_btn_control);				
				
				//if((StreamControl.playFeathers & StreamControl.PLAY_AOD_WHAT) == StreamControl.PLAY_AOD_WHAT){
				if(player.getPlayFeathers() == player.PLAY_AOD_WHAT){
					ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_pause);		
				}else{
					ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);		
				}
				
				setVolumn(R.id.aod_what_volumn_sk);
				playAODWhat(vo.getpDate());
				Log.i("INFO-aod",prgmId);
				
			}
			
		});
	}
	
	
	private void playAODIcan(String playDt){
		// depth = 22
		//player.setViewDepth(12);	
		player.setTabViewDepth(12);
		
		XmlAODList aod = new XmlAODList();
		Log.i(LOG_TAG,"playAOD_ICAN: "+prgmId+","+playDt);
		aod.parsingAOD(prgmId, playDt);
		TextView tv = (TextView) findViewById(R.id.aod_ican_play_script);
		tv.setText(aod.getScript());
		if(!aod.getFileURL().equalsIgnoreCase("NO DATA")){
			this.fileURL = aod.getFileURL();	
			Log.i(LOG_TAG, "ICAN : " + fileURL);
			// play 버튼 이미지
			ivAODICanBtnPlayControl = (ImageView) findViewById(R.id.aod_ican_btn_control);
			ivAODICanBtnPlayControl.setOnClickListener(onAODIcanPlayBtnControlListener);		
		}
	}
	
	private void playAODWhat(String playDt){
		// depth = 23
		//player.setViewDepth(13);
		player.setTabViewDepth(13);
		XmlAODList aod = new XmlAODList();
		
		aod.parsingAOD(prgmId, playDt);

		TextView tv = (TextView) findViewById(R.id.aod_what_play_script);
		tv.setText(aod.getScript());

		if(!aod.getFileURL().equals("NO DATA")){
			this.fileURL = aod.getFileURL();
			// 	play 버튼 이미지		
			ivAODWhatBtnPlayControl = (ImageView) findViewById(R.id.aod_what_btn_control);
//			ivAODWhatBtnPlayControl.setOnClickListener(onAODWhatPlayBtnControlListener);
			ivAODWhatBtnPlayControl.setOnClickListener(onAODIcanPlayBtnControlListener);
		}
	}	
	/**
	 * Play/Stop 버튼을 선택할 경우
	 */
	private OnClickListener onAODIcanPlayBtnControlListener = new OnClickListener() {

//		@Override
		public void onClick(View v) {
			synchronized(player){
				if(player.isPlaying() && player.getPlayFeathers() != player.PLAY_RADIO){
					stop();
					if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);
					if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);
					if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);
				}else{
					start( Decoder.DECODER_FFMPEG_WMA );
//					start( Decoder.DECODER_FFMPEG );
					if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);
					if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_pause);
					if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_pause);
				}					
			}		
		}

	};		
	
	private class AODAdapter extends ArrayAdapter<AODListVO>{

		ArrayList<AODListVO> aodList;
		
		public AODAdapter(Context context, int textViewResourceId,
				ArrayList<AODListVO> aodList) {
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
		
			AODListVO vo = (AODListVO) aodList.get(position);
			
			if(vo != null){
				TextView tvDate = (TextView) v.findViewById(R.id.news_date);
				if(tvDate != null){					
					tvDate.setText(vo.getDate());
				}
				TextView tvTitle = (TextView) v.findViewById(R.id.news_title);
				if(tvTitle != null){
					if(prgmId.equals("ican")){
						tvTitle.setText("I Can You Can");
					}else{
						tvTitle.setText("What do you say");
					}
				}
				
			}		
			
			return v;
		}
		
		
		
	}
	
	
	/**
	 * 3개의 탭 메뉴에 대한 클릭 리스너를 정의한다.
	 * Busan e-FM, I can you can, What did you say
	 * 
	 */
	private void setListeners(){
		// Busan e-FM
		Button iv1  = (Button) findViewById(R.id.iv_befm);
		iv1.setOnClickListener(onClickListener);
		// I can you can
		Button iv2 = (Button) findViewById(R.id.iv_ican);
		iv2.setOnClickListener(onClickListener);
		// What did you say
		Button iv3 = (Button) findViewById(R.id.iv_what);
		iv3.setOnClickListener(onClickListener);
			
	}
	
	private OnClickListener onClickListener = new OnClickListener(){
		public void onClick(final View v){
			LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
			//ImageView iv1,iv2, iv3;
			
			switch(v.getId()){
			case R.id.iv_befm:

				
				ll.removeAllViews();
				generateTabScreen(1);
				
				break;
			case R.id.iv_ican:
				//System.out.println("tab2");

				
				ll.removeAllViews();
				generateTabScreen(2);			
				break;
			case R.id.iv_what:
				//System.out.println("tab2");

				
				ll.removeAllViews();
				generateTabScreen(3);							
				break;
			
			}
		}
	};	
	

	
	private OnClickListener aodIcanBackOnClickListener = new OnClickListener(){
		
//		@Override
		public void onClick(View view) {
			// depth = 12
			//player.setViewDepth(12);
			player.setTabViewDepth(12);
			if(player.isPlaying()  && (player.getPlayFeathers() != player.PLAY_RADIO)){ 
				stop();
				//ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_play_backControl);	
				ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);					
			}			
			
			LinearLayout rl = (LinearLayout) findViewById(R.id.aod_rl);
			rl.setVisibility(View.VISIBLE);
			
			LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
			ll.removeAllViews();
			
			View v = View.inflate(AODTab.this, R.layout.aod_list, null);
			ll.addView(v, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
			load_iCan();						
		}
		
	};
	
	
	private OnClickListener aodWhatBackOnClickListener = new OnClickListener(){
		
//		@Override
		public void onClick(View view) {
			// depth = 13
			//player.setViewDepth(13);
			player.setTabViewDepth(13);			
			if(player.isPlaying()  && (player.getPlayFeathers() != player.PLAY_RADIO)){ 
				stop();
				//ivAODBtnPlayControl = (ImageView) findViewById(R.id.aod_play_backControl);	
				ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);					
			}			
			
			LinearLayout rl = (LinearLayout) findViewById(R.id.aod_rl);
			rl.setVisibility(View.VISIBLE);
			
			LinearLayout ll = (LinearLayout) findViewById(R.id.aod_sub_box);
			ll.removeAllViews();
			
			View v = View.inflate(AODTab.this, R.layout.aod_list, null);
			ll.addView(v, new LinearLayout.LayoutParams(ll.getLayoutParams().width, ll.getLayoutParams().height));
			
			load_What();			
			
		}
		
	};


//	@Override
	public void playerStarted() {

		player.getUiHandler().post( new Runnable() {
	          public void run() {
	        	player.setPlayerStarted(true);
	        	if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);
	        	if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_pause);
	        	if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_pause);
	          }
		});
	}

//	@Override
	public void playerPCMFeedBuffer(final boolean isPlaying, final int audioBufferSizeMs,
		final int audioBufferCapacityMs) {
//			player.getUiHandler().post( new Runnable() {
//				public void run() {
                //progress.setProgress( audioBufferSizeMs * progress.getMax() / audioBufferCapacityMs );
//                if (!isPlaying){
                	//txtPlayStatus.setText( R.string.text_playing );
//                	Toast.makeText(AODTab.this, "Buffering..."+(audioBufferSizeMs / audioBufferCapacityMs), Toast.LENGTH_SHORT);
//                }
//            }
//        });
		
	}

//	@Override
	public void playerStopped(int perf) {
		player.getUiHandler().post( new Runnable() {
            public void run() {
            	player.setPlayerStarted(false);
        	  	if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);
      		  if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);
      		  if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);
            }
        });
		
	}

//	@Override
	public void playerException(final Throwable t) {
			player.getUiHandler().post( new Runnable() {
	            public void run() {
	                new AlertDialog.Builder( AODTab.this )
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
				player.getTabFeathers() != player.TAB_AOD) {
			if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);	
			if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);
			if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);
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
				if(player.getTabViewDepth() ==0 || player.getTabViewDepth()==21){
					Toast.makeText(AODTab.this, "In the current screen will not be able to adjust the volume.",Toast.LENGTH_SHORT).show();			
				}else{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
					nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					seekVolumn.setProgress(nCurrentVolumn);
					
				}
				return true;
				
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				if(player.getTabViewDepth() ==0 || player.getTabViewDepth() == 21){
					Toast.makeText(AODTab.this, "In the current screen will not be able to adjust the volume.",Toast.LENGTH_SHORT).show();					
				}else{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
					nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					seekVolumn.setProgress(nCurrentVolumn);
					
				}
				return true;
				
			case KeyEvent.KEYCODE_BACK:
				
				switch(player.getTabViewDepth()){
				case 0:
				
//					new AlertDialog.Builder( AODTab.this )
//					.setTitle("Alert!")						
//					.setMessage("Are you sure exit?")
//					.setPositiveButton( "Yes",
//						new DialogInterface.OnClickListener() {
//						public void onClick( DialogInterface dialog, int id) {
//							
//							moveTaskToBack(true);
//							finish();
//                    		}
//                		}
//						)
//						.setNegativeButton("No", null)
//						.show();
					((eBFMView)getParent()).setTabIndex(0);
					break;
				case 11:
				case 21:		
					if(player.isPlaying() && (player.getPlayFeathers() != player.PLAY_RADIO)){
						stop();
						player.setPlayFeathers(player.PLAY_OFF);
						player.setPlaying(false);
					}
					
					setContentView(R.layout.aod_activity);
					
					generateTabScreen(1);
					
					player.setTabViewDepth(0);
					break;
				case 12:
					if(player.isPlaying() && (player.getPlayFeathers() != player.PLAY_RADIO)){
						stop();
						player.setPlayFeathers(player.PLAY_OFF);
						player.setPlaying(false);
					}
					
					setContentView(R.layout.aod_activity);
					
					generateTabScreen(2);					
					//player.setViewDepth(0);
					player.setTabViewDepth(0);
					break;
				case 13:		
					if(player.isPlaying() && (player.getPlayFeathers() != player.PLAY_RADIO)){
						stop();
						player.setPlayFeathers(player.PLAY_OFF);
						player.setPlaying(false);
					}
					
					setContentView(R.layout.aod_activity);
					
					generateTabScreen(3);					
					//player.setViewDepth(0);		
					player.setTabViewDepth(0);
					break;
				
				}				
				
				return true;

		}
		//return super.onKeyDown(keyCode, event);
		return false;
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
		player.setPlayFeathers(player.PLAY_AOD_EFM);
    }

	@Override
	public void onResume() {
		super.onResume();
		player.setTabFeathers(player.TAB_AOD);
		if (player.getPlayFeathers() != player.PLAY_AOD_EFM) {
			if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_play);	
			if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_play);
			if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_play);
		} else {
			if (ivAODICanBtnPlayControl != null) ivAODICanBtnPlayControl.setImageResource(R.drawable.btn_pause);	
			if (ivAODWhatBtnPlayControl != null) ivAODWhatBtnPlayControl.setImageResource(R.drawable.btn_pause);
			if (ivAODBtnPlayControl != null) ivAODBtnPlayControl.setImageResource(R.drawable.btn_pause);
		}
	}
	
    void stop() {
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
