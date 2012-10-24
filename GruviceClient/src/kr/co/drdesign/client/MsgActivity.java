package kr.co.drdesign.client;

import java.text.SimpleDateFormat;
import java.util.Map;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.MsgController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class MsgActivity 
extends Activity 
implements View.OnLongClickListener, Loggable
{
	//상세보기에서 시간변환시 사용.
	protected static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("[MM-dd HH:mm:ss]");
	protected MsgController msgCtrl;
	protected GroupController grpCtrl;

	protected String _id;

	protected String UID;
	protected String TITLE;
	protected String CONTENTS;
	protected String APPPATH;
	protected String ATTACHMENTS;	
	protected String READED;
	protected String GROUP_ID;
	protected String SENDER; 
	protected String CREATEDATE;
	protected String RECEIVEDATE;
	protected String SENDDATE;
	protected String RECEIVER;
	protected String ETC;

	protected String[] image = { AttachController.NO_CONTENTS, AttachController.NO_CONTENTS,
							AttachController.NO_CONTENTS};
	// 망할 배열버그 ㅠ
	protected String file = AttachController.NO_CONTENTS;
	
	//Views
	protected RelativeLayout msgRl;
	protected LinearLayout ll01;
	protected LinearLayout msgView;
	protected TextView tvTitle;
	protected TextView tvContents;

	protected Drawable sBackGround;
	protected GestureDetector mGestureDetector;
	
	protected String skinType;
	protected String CLASS_NAME; 

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CLASS_NAME = this.getClass().getName();
		L( CLASS_NAME + " onCreate()");
		
		setContentView(R.layout.msg_view);
		_id = getIntent().getStringExtra(MsgController._id);
		L( "_id = " + _id );
		msgRl = (RelativeLayout)findViewById(R.id.msgRl);

		skinType = GruviceUtillity.getInstance(this).getSkinType();
		String[] SKIN_TYPES = GruviceUtillity.getInstance(this).getSKIN_TYPES();

		if( skinType.equals( SKIN_TYPES[0]) )
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		else
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);
		
		msgRl.setBackgroundDrawable(sBackGround);

		tvTitle = (TextView) findViewById(R.id.msgTvTitle);
		ll01 = (LinearLayout) findViewById(R.id.msgLl01);
		// 2011-05-25 패딩이 너무 과도하게 설정되어 이미지 첨부시 내용이 잘리는 현상 발생.
		// 레이아웃이 아닌 스크롤뷰에 패딩을 설정함.
		msgView = (LinearLayout) findViewById(R.id.msgLinearLayout);

		setMsgCtrl();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unBindDrawable();
	}
	
	//Drawable Resource 해제.
	protected void unBindDrawable()
	{
		if ( sBackGround != null ) sBackGround.setCallback(null);
	}
	
	protected abstract void setMsgCtrl();
	protected abstract void parsingSavedMsg(String msg);
	protected abstract void showDetailInfo();
	protected void showImg( String fileName ){
		/*Image Scale adjust.*/

		// 이미지를 읽어 들일 때 작은 사이즈로 읽어들이는 방법 필요.
		Bitmap bm = BitmapFactory.decodeFile(fileName.trim());

		int width = bm.getWidth();
		int height = bm.getHeight();

		double scale;
		double size = 430.0;

		if ( width > height )
		{
			scale = size/width;
		} else if ( width < height ) {
			scale = size/height;
		} else {
			// 2011-05-25 비율이 같은 사진의 경우 뷰가 잘려서 나오는 문제가 발생함.
			// 이에 대한 조치.
			scale = size/((width+height)/2);
		}
		if( scale < 1  )
		{
			L("scale : " + scale + ", width : " + width + ", height : " + height);
			width = (int)(width * scale);
			height = (int)(height * scale);
		}

		Bitmap resized = Bitmap.createScaledBitmap(bm, width, height, true);

		
		ImageView newImage = new ImageView(this);
		newImage.setImageBitmap(resized);
		msgView.addView(newImage);
	}

	protected void showTxt( String txt ){
		L("showTxt");
		tvContents = new TextView(this);
		tvContents.setText(txt);
		tvContents.setClickable(true);
		tvContents.setAutoLinkMask(Linkify.ALL);
		tvContents.setMovementMethod(LinkMovementMethod.getInstance());
		tvContents.setTextColor(Color.WHITE);
		msgView.addView( tvContents );
	}

	public boolean onLongClick(View view) {
		deleteMsg( view );
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.msg_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.msgReply:
			replyMessage();
			break;
		case R.id.msgReSend :
			resendMessage();
			break;
		default :
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void replyMessage() {
		Intent i = new Intent(this, SendMessageActivity.class);
		// 2011-05-17 2011-05-17 답장시 제목 및 내용 자동생성.
		String body = "";
		body = new String((getString(R.string.msgResendPiece1) 
				+ tvContents.getText().toString() + getString(R.string.msgResendPiece2)));
//		body = new String("원본 메시지 : \n" 
//		+ tvContents.getText().toString() + "\n본문 : ");
		i.setAction("REPLY");
		i.putExtra(ReceiveMsgController.SENDER, SENDER);
		i.putExtra(ReceiveMsgController.TITLE, "RE: " + tvTitle.getText().toString());
		i.putExtra(ReceiveMsgController.CONTENTS, body);
		startActivity( i );
	}
	
	private void resendMessage() {
		Intent i = new Intent(this, SendMessageActivity.class);
		// 2011-05-17 인텐트에 putExtra로 메시지의 값을 다 넘겨준다. 전달기능
		// 2011-05-24 첨부파일을 제외한 모든 내용 전달가능.
		// 첨부파일은 실제로 다운로드 하지 않았을 수 있으므로 다른 방법으로 처리가 필요함.
		String body = "";
		body = new String((getString(R.string.msgResendPiece1) 
				+ tvContents.getText().toString() + getString(R.string.msgResendPiece2)));
//		body = new String("원본 메시지 : \n" 
//				+ tvContents.getText().toString() + "\n본문 : ");
		
		i.setAction("RESEND");
		i.putExtra(ReceiveMsgController.TITLE, "FW : " + tvTitle.getText().toString());
		i.putExtra(ReceiveMsgController.CONTENTS, body);
		i.putExtra("file", file);
		for (int j = 0; j < image.length; j++) {
			i.putExtra("image" + Integer.toString(j+1), image[j]);
		}
		startActivity( i );
	}

	protected void deleteMsg(View view)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(MsgActivity.this);
		alert.setTitle( R.string.txtAlret );
		alert.setMessage( R.string.txtWarnningDeleteMsg );
		alert.setPositiveButton( R.string.txtYES, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int which) {
				dialog.dismiss();   //닫기
				ReceiveMsgController msgCtrl = ReceiveMsgController.getInstance(getApplicationContext());
				msgCtrl.delete(_id);
				finish();
			}
		});
		alert.setNegativeButton( R.string.txtNO, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int which) {
				dialog.dismiss();   //닫기
			}
		});
		alert.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	// 왼쪽으로 넘기든 오른쪽으로 넘기든 애니메이션은 같은방향으로 하나만 실행되는 현상 발생.
	// 2011-05-25 페이지 이동시 애니메이션 변경
	protected class LinearGestureListener extends GestureDetector.SimpleOnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 100;
//		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.e("onFling", "onFling()");
			
			
			try {
//			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
//				return false;
//			}

			Intent intent;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Map<String, String> map = msgCtrl.getSmallMsg(_id);
				if( map.size() == 0 ) 
					Toast.makeText(getApplicationContext(), R.string.msgTxtLastMsg, Toast.LENGTH_SHORT).show();
				else
				{
					try {
						intent = new Intent( getApplicationContext(), Class.forName(CLASS_NAME));
						intent.putExtra(ReceiveMsgController._id, map.get(MsgController._id));
						startActivity( intent );
						overridePendingTransition
						(R.anim.left_in, R.anim.left_out);
						finish();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			// left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				Map<String, String> map = msgCtrl.getBiggerMsg(_id);
				if( map.size() == 0 ) 
					Toast.makeText(getApplicationContext(), R.string.msgTxtLastMsg, Toast.LENGTH_SHORT).show();
				else
				{
					try {
						intent = new Intent( getApplicationContext(), Class.forName(CLASS_NAME));
						intent.putExtra(ReceiveMsgController._id, map.get(ReceiveMsgController._id));
						startActivity( intent );
						overridePendingTransition
						(R.anim.right_in, R.anim.right_out);
						finish();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}   
			} catch (NullPointerException npe) {
				npe.getMessage();
			}
			return true;
		}
	}
	public void L(char i, String log) {
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		Log.i(DEBUG_TAG, log);
	}
}