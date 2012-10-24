package kr.co.drdesign.parmtree.message;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.DBController;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class MsgActivity extends Activity 
	implements OnClickListener, OnLongClickListener, c {
	// 이걸 상속받는 액티비티들은 탭과 별개로 작동하므로 메뉴도 구현해준다.
	
	protected DBController msgCtrl;
	protected Intent msgIntent;
	
	TextView tvTitle;
	TextView tvSender;
	TextView tvSenddate;
	TextView tvContent;
	
	Button replyBtn;
	Button etcBtn;
	Button delBtn;
	
	LinearLayout llContent;
	LinearLayout llButtons;
	
	int index;
	
	Class<?> cls;
	
	
	
	
	protected GestureDetector mGestureDetector;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_view);
		if (getIntent() == null) msgIntent = new Intent();
		else {
			msgIntent = getIntent();
			index = msgIntent.getIntExtra(ID, 0);
			l.s("msgIndex : " + index);
		}
		
		
		tvTitle = (TextView) findViewById(R.id.titletext);
		tvSender = (TextView) findViewById(R.id.sendertext);
		tvSenddate = (TextView) findViewById(R.id.senddatetext);
		tvContent = new TextView(this);
		tvContent.setOnLongClickListener(this);
		tvContent.setTextSize(24.0f);
		
		replyBtn = (Button) findViewById(R.id.msgviewreplybtn);
		etcBtn = (Button) findViewById(R.id.msgviewetcbtn);
		delBtn = (Button) findViewById(R.id.msgviewdelbtn);
		
		replyBtn.setOnClickListener(this);
		etcBtn.setOnClickListener(this);
		delBtn.setOnClickListener(this);
		
		llContent = (LinearLayout) findViewById(R.id.msgviewcontent);
		llButtons = (LinearLayout) findViewById(R.id.msgviewbottom);
		
		llContent.setLongClickable(true);
		llContent.setClickable(true);
		llContent.setOnLongClickListener(this);
		
		mGestureDetector = new GestureDetector(this, new FlingListener());
		
	}

	void invisibleController() {
		// TODO Auto-generated method stub
		llButtons.setVisibility(View.GONE);
	}
	
	void visibleController() {
		llButtons.setVisibility(View.VISIBLE);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		l.s("onLongClick");
		showLongClickDialog();
		return false;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.msg_menu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.msgDelete :
			showDeleteDialog();
			break;
		case R.id.msgReply :
			replyMsg();
			break;
		default :
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void showDeleteDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.danger)
		.setMessage(R.string.deletemsg)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				deleteMsg(index); // 임시값. 차후 수정
				dialog.dismiss();
			}
		})
		.setNegativeButton(R.string.no, null)
		.show();
	}
	
	protected void showLongClickDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setItems(
				new String[]{ getString(R.string.copy), getString(R.string.reply), getString(R.string.delete) }, 
		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0 : copyMsg();	break;
				case 1 : replyMsg(); break;
				case 3 : showDeleteDialog(); break;
				default : break;
				}
				dialog.dismiss();
			}
		});
		alert.show();
	}
	
	void showPrev() {
		if (index > 1) {
			Intent i = new Intent(this, cls);
			i.putExtra(ID, index-1);
			finish();
			startActivity(i);
		}
	}
	
	void showNext() {
		Intent i = new Intent(this, cls);
		i.putExtra(ID, index+1);
		finish();
		startActivity(i);		
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msgviewreplybtn :
			replyMsg();
			break;
		case R.id.msgviewetcbtn :
			break;
		case R.id.msgviewdelbtn :
			showDeleteDialog();
			break;
		}
	}

	protected void copyMsg() {
		ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(tvContent.getText());
		Toast.makeText(this, R.string.copymsg, Toast.LENGTH_SHORT).show();
	}
	
	protected abstract void setContent();
	protected void deleteMsg(int mid) {
		msgCtrl.delete(mid);
	}

	protected void replyMsg() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, SendMsgActivity.class);
		i.putExtra(USERID, tvSender.getText());
		i.putExtra(TITLE, tvTitle.getText());
		startActivity(i);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		l.s("onTouchEvent()");
		if (mGestureDetector.onTouchEvent(event)) return false;
		else return true;
	}
	

	protected class FlingListener extends GestureDetector.SimpleOnGestureListener {
		private final int SWIPE_MIN_DISTANCE = 100;
		private final int SWIPE_THRESHOLD_VELOCITY = 200;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (msgCtrl.get(index+1).size() != 0) showNext();
				else Toast.makeText(MsgActivity.this, R.string.nonextmsg, Toast.LENGTH_SHORT).show();
				}
			// left to right swipe
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if (msgCtrl.get(index-1).size() != 0) showPrev();
				else Toast.makeText(MsgActivity.this, R.string.noprevmsg, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}
}
