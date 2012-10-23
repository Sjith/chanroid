
package kr.co.chan.util;

import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

public class MediaBOX extends RelativeLayout implements OnErrorListener, OnCompletionListener, OnPreparedListener {

	private RelativeLayout		mRoot;
	// 미리 짜놓은 레이아웃
	private Activity            mContext;
	// 뷰는 항상 Context를 생성자로 받아야 함
	private VideoView           mAnchor;
	// 여기서 컨트롤할 VideoView
	private LinearLayout		mPanel;
	// 재생 정지 등등 이것저것 들어가는 뷰 컨테이너
	private LinearLayout		mLoading;
	// 로딩창이 들어있는 레이아웃
	private SeekBar         mProgress;
	// 동영상 진행상황을 표시하거나 조절하는 바
	private TextView            mEndTime, mCurrentTime;
	// 현재시간, 총 시간을 표시
	private boolean             mShowing;
	// 컨트롤러가 표시되고 있는지의 여부
	private boolean             mDragging;
	// 진행바를 드래그 중인지의 여부
	private static final int    sDefaultTimeout = 3000;
	// 나타났다가 사라지기까지의 기본 대기시간
	private static final int    FADE_OUT = 1;
	private static final int    SHOW_PROGRESS = 2;
	private boolean             mUseFastForward;
	private boolean             mFromXml;
	private boolean             mListenersSet;
	private View.OnClickListener mNextListener, mPrevListener;
	StringBuilder               mFormatBuilder;
	Formatter                   mFormatter;
	// 재생시간 표시에 필요한 객체
	private Button         mPauseButton;
	// 재생 정지 버튼
	private Button         mFfwdButton;
	// 앞으로
	private Button         mRewButton;
	// 뒤로
	private Button         mNextButton;
	// 재생목록의 다음항목(기본값 숨김)
	private Button         mPrevButton;
	// 재생목록의 이전항목(기본값 숨김)
	private RelativeLayout mVolumeControl;
	private LinearLayout   mVolumeBox;

	private void inflateRootView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = (RelativeLayout) inflater.inflate(R.layout.mediacontrolbox, null);
		addView(mRoot);
		if (mRoot != null)
			initRootView(mRoot);

	}

	public MediaBOX(Activity context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mUseFastForward = true;
		mFromXml = true; 
		initFloatingWindow();
	}

	public MediaBOX(Activity context, boolean useFastForward) {
		super(context);
		mContext = context;
		mUseFastForward = useFastForward;
		initFloatingWindow();
	}

	public MediaBOX(Activity context) {
		super(context);
		mContext = context;
		mUseFastForward = true;
		initFloatingWindow();
	}

	private void initFloatingWindow() {
		mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		requestFocus();
		inflateRootView();
	}

	//    private OnTouchListener mTouchListener = new OnTouchListener() {
	//        public boolean onTouch(View v, MotionEvent event) {
	//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
	//                if (mShowing) {
	//                    hide();
	//                } else {
	//                	show();
	//                }
	//            }
	//            return false;
	//        }
	//    };


	private void initRootView(View v) {
		mPanel = (LinearLayout) v.findViewById(R.id.mediapanel);
		initControllerView(mPanel);

		mLoading = (LinearLayout) v.findViewById(R.id.loadinglayout);
		initLoadingView(mLoading);

		mAnchor = (VideoView) v.findViewById(R.id.videoView1);
		initAnchorView(mAnchor);  

		mVolumeControl = (RelativeLayout) v.findViewById(R.id.volumelayout);
		initVolumeView(mVolumeControl);
	}

	private void initVolumeView(View v) {
		// 볼륨컨트롤 박스
		mVolumeBox = (LinearLayout) v.findViewById(R.id.volumecontrollayout);
		mVolumeBox.setVisibility(View.GONE);
	}

	private void initLoadingView(View v) {
		// 로딩창에 필요한 초기화 코드를 넣음
	}

	private void initAnchorView(View v) {
		//        mAnchor.setOnTouchListener(mTouchListener);
		mAnchor.setOnPreparedListener(this);
		mAnchor.setOnCompletionListener(this);
		// VideoView 에 필요한 초기화 코드를 넣음
	}

	private void initControllerView(View v) {
		mPauseButton = (Button) v.findViewById(R.id.pause);
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}
		// 정지 버튼

		mFfwdButton = (Button) v.findViewById(R.id.ffwd);
		if (mFfwdButton != null) {
			mFfwdButton.setOnClickListener(mFfwdListener);
			if (!mFromXml) {
				mFfwdButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
			}
		}
		// 앞으로 버튼

		mRewButton = (Button) v.findViewById(R.id.rew);
		if (mRewButton != null) {
			mRewButton.setOnClickListener(mRewListener);
			if (!mFromXml) {
				mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
			}
		}
		// 되감기 버튼

		mNextButton = (Button) v.findViewById(R.id.next);
		if (mNextButton != null && !mFromXml && !mListenersSet) {
			mNextButton.setVisibility(View.GONE);
		}
		mPrevButton = (Button) v.findViewById(R.id.prev);
		if (mPrevButton != null && !mFromXml && !mListenersSet) {
			mPrevButton.setVisibility(View.GONE);
		}
		// 다음, 이전 버튼. 기본값은 숨김으로 되어 있고 setPrevNextListeners() 메서드를 호출하면 활성화된다.

		mProgress = (SeekBar) v.findViewById(R.id.progress);
		if (mProgress != null) {
			mProgress.setOnSeekBarChangeListener(mSeekListener);
			mProgress.setMax(1000);
		}
		// 재생상태 막대기

		mEndTime = (TextView) v.findViewById(R.id.time);
		mCurrentTime = (TextView) v.findViewById(R.id.current);
		// 현재 시간, 총 시간 TextView
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		installPrevNextListeners();
	}

	public void show() {
		show(sDefaultTimeout);
	}

	public void show(int timeout) {

		if (!mShowing && mAnchor != null) {
			setProgress();

			mPanel.setVisibility(View.VISIBLE);
			mShowing = true;
		}
		updatePausePlay();

		mHandler.sendEmptyMessage(SHOW_PROGRESS);

		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public boolean isShowing() {
		return mShowing;
	}

	public void hide() {
		if (mAnchor == null)
			return;

		if (mShowing) {
			try {
				mHandler.removeMessages(SHOW_PROGRESS);
				mPanel.setVisibility(View.INVISIBLE);
			} catch (IllegalArgumentException ex) {
				Log.w("MediaController", "already removed");
			}
			mShowing = false;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && mShowing && mAnchor.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	};

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours   = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private int setProgress() {
		if (mAnchor == null || mDragging) {
			return 0;
		}
		int position = mAnchor.getCurrentPosition();
		int duration = mAnchor.getDuration();
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress( (int) pos);
			}
			int percent = mAnchor.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	private float downPoint = 0.0f;
	private float currentPoint = 0.0f;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			currentPoint = ev.getY();
			mVolumeBox.setVisibility(View.VISIBLE);
			
			l.i("touch x : " + (int)ev.getX() + ", y : " + (int)ev.getY());
			
			mVolumeControl.removeView(mVolumeBox);
			RelativeLayout.LayoutParams paramtext = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			paramtext.setMargins((int)ev.getX() - (mVolumeBox.getWidth()/2), (int)ev.getY() - (mVolumeBox.getHeight()/2), 0, 0); 
			
			mVolumeControl.addView(mVolumeBox, paramtext);
			
			downPoint = ev.getY();
			if (mShowing) {
				hide();
			} else {
				show();
			}
			break;
		case MotionEvent.ACTION_UP:
			mVolumeBox.setVisibility(View.GONE);
			downPoint = 0.0f;
			currentPoint = 0.0f;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(currentPoint - ev.getY()) > 7) {
				currentPoint = ev.getY();
				if (currentPoint > downPoint) {
					((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).
					setStreamVolume(AudioManager.STREAM_MUSIC, 
							((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC) - 1 
							, AudioManager.FLAG_SHOW_UI);
				} else {
					((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).
					setStreamVolume(AudioManager.STREAM_MUSIC, 
							((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC) + 1 
							, AudioManager.FLAG_SHOW_UI);    			
				}			
			}
			
			break;
		}
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN && (
				keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK ||
				keyCode ==  KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
				keyCode ==  KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(sDefaultTimeout);
			return true;
		} else if (keyCode ==  KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mAnchor.isPlaying()) {
				mAnchor.pause();
				updatePausePlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
				keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			// don't show the controls for volume adjustment
			return super.dispatchKeyEvent(event);
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			hide();	
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			mContext.onBackPressed();
		} else {
			show(sDefaultTimeout);
		}
		return super.dispatchKeyEvent(event);
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private void updatePausePlay() {
		if (mPanel == null)
			return;

		Button button = (Button) mPanel.findViewById(R.id.pause);
		if (button == null)
			return;

		if (mAnchor.isPlaying()) {
			button.setText("pause");
		} else {
			button.setText("play");
			// 상태에 따라 버튼 이미지를 변경
		}
	}

	private void doPauseResume() {
		if (mAnchor.isPlaying()) {
			mAnchor.pause();
		} else {
			mAnchor.start();
		}
		updatePausePlay();
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);

			mDragging = true;

			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser) {
				return;
			}

			long duration = mAnchor.getDuration();
			long newposition = (duration * progress) / 1000L;
			mAnchor.seekTo( (int) newposition);
			if (mCurrentTime != null)
				mCurrentTime.setText(stringForTime( (int) newposition));
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mDragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);

			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	@Override
	public void setEnabled(boolean enabled) {
		if (mPauseButton != null) {
			mPauseButton.setEnabled(enabled);
		}
		if (mFfwdButton != null) {
			mFfwdButton.setEnabled(enabled);
		}
		if (mRewButton != null) {
			mRewButton.setEnabled(enabled);
		}
		if (mNextButton != null) {
			mNextButton.setEnabled(enabled && mNextListener != null);
		}
		if (mPrevButton != null) {
			mPrevButton.setEnabled(enabled && mPrevListener != null);
		}
		if (mProgress != null) {
			mProgress.setEnabled(enabled);
		}

		super.setEnabled(enabled);
	}

	private View.OnClickListener mRewListener = new View.OnClickListener() {
		public void onClick(View v) {
			int pos = mAnchor.getCurrentPosition();
			pos -= 5000; // milliseconds
			mAnchor.seekTo(pos);
			setProgress();

			show(sDefaultTimeout);
		}
	};

	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
		public void onClick(View v) {
			int pos = mAnchor.getCurrentPosition();
			pos += 15000; // milliseconds
			mAnchor.seekTo(pos);
			setProgress();

			show(sDefaultTimeout);
		}
	};

	private void installPrevNextListeners() {
		if (mNextButton != null) {
			mNextButton.setOnClickListener(mNextListener);
			mNextButton.setEnabled(mNextListener != null);
		}

		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(mPrevListener);
			mPrevButton.setEnabled(mPrevListener != null);
		}
	}

	public void setPrevNextListeners(View.OnClickListener next, View.OnClickListener prev) {
		mNextListener = next;
		mPrevListener = prev;
		mListenersSet = true;

		if (mPanel != null) {
			installPrevNextListeners();

			if (mNextButton != null && !mFromXml) {
				mNextButton.setVisibility(View.VISIBLE);
			}
			if (mPrevButton != null && !mFromXml) {
				mPrevButton.setVisibility(View.VISIBLE);
			}
		}
	}


	/*
	 * mediaplayer methods
	 */

	public void start() {
		mAnchor.start();
	}

	public void setVideoUri(Uri uri) {
		mAnchor.setVideoURI(uri);
	}

	public void setVideoPath(String path) {
		mAnchor.setVideoPath(path);
	}

	public int getDuration() {
		return mAnchor.getDuration();
	}

	public interface MediaPlayerControl {
		void    start();
		void    pause();
		int     getDuration();
		int     getCurrentPosition();
		void    seekTo(int pos);
		boolean isPlaying();
		int     getBufferPercentage();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (mLoading.getVisibility() == View.VISIBLE)
			mLoading.setVisibility(View.INVISIBLE);
		updatePausePlay();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (mPanel.getVisibility() == View.INVISIBLE)
			show(3600000);

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		mContext.finish();
		return false;
	}
}
