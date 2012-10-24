package kr.co.drdesign.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.MsgController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

// 상태에 따라 뷰를 편집하지만 실제로 메시지는 다른 클래스에서 뿌려준다.
public abstract class MsgListActivity extends Activity 
implements Loggable{

	private static final String CLASS_NAME = "MsgListActivity ";
	
	protected static Set<Integer> checkedSet = new TreeSet<Integer>(); 

	protected MsgController msgCtrl;

	protected LinearLayout mlLlBottom;
	protected ListView mlLvMsgList;
	protected ImageView mlIvBottom;

	private Drawable sBackGDrawable; 

	protected Button btnDeleteItem;
	protected Button btnSelectAll;

	protected List<Map<String,String>> msgList ;
	protected String[] groupIds;

	protected static String skinType;
	protected GestureDetector mGestureDetector;

	protected boolean willSelectAll = true;
	// mlMsgList Scrolling Position, after select msg.
	protected static int selectItemPosition;
	protected int mlLlBottomHeight;
	private boolean previousShownBtnMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L( CLASS_NAME + " onCreate()");
		
		selectItemPosition = -1;

		setContentView(R.layout.msg_list);

		skinType = GruviceUtillity.getInstance(this).getSkinType();
		String[] SKIN_TYPES = GruviceUtillity.getInstance(this).getSKIN_TYPES();

		if( skinType.equalsIgnoreCase(SKIN_TYPES[0]) == true)
			sBackGDrawable = getResources().getDrawable(R.drawable.v1_ml_bg);
		else
			sBackGDrawable = getResources().getDrawable(R.drawable.v2_ml_bg);

		mlLvMsgList = (ListView)findViewById(R.id.mlLvMsgList);
		mlLvMsgList.setBackgroundDrawable(sBackGDrawable);
		mlLvMsgList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		mlLlBottom = (LinearLayout) findViewById(R.id.mlLlBottom);
		mlIvBottom = (ImageView) findViewById(R.id.mlIvBottom);

		btnSelectAll = (Button) findViewById(R.id.mlBtnSelectAll);

		btnDeleteItem = (Button) findViewById(R.id.mlBtnDeleteItem);
		btnDeleteItem.setOnClickListener( new DeleteMsgListener() );

		Intent intent = getIntent();
		if( intent != null )
			groupIds = intent.getStringArrayExtra(GroupController.GROUP_ID);
	}

	//Please, Check usage of Resource of this code.
	//this code for Activity refesh, When Tabhost Change.   
	@Override
	protected void onResume() {
		super.onResume();
		fillMsgList();
		showButtonMenu(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 체크된 리스트를 삭제한다.
		checkedSet.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sBackGDrawable != null) sBackGDrawable.setCallback(null);	
	}

	protected abstract void fillMsgList();
	protected abstract void fillMsgList(List<Map<String,String>> list);

	protected void changeAllCheckState( boolean allChecked )
	{
		L("changeAllCheckState()");
		if( msgList == null)
			msgList = msgCtrl.getMsgList();
		if( allChecked )
		{
			Iterator<Map<String,String>> it = msgList.iterator();
			String s_id;
			Integer i_id;
			while(it.hasNext())
			{
				s_id = it.next().get(ReceiveMsgController._id);
				i_id = Integer.parseInt(s_id);
				checkedSet.add(i_id);
			}
			btnSelectAll.setText(R.string.txtDeselectAll);
		}
		else{
			checkedSet.clear();
			btnSelectAll.setText(R.string.txtSelectAll);
		}
		fillMsgList( msgList );
		willSelectAll = !(willSelectAll);
		showButtonMenuByCheckedItem();
	}

	protected void showNoMsgText(){
		
		if( mlLvMsgList != null && mlLvMsgList.getCount() == 0 ){ 
			L("SHOW NO MSG ");
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle( R.string.txtAlret );
			alert.setMessage(  R.string.mlTxtNoMessage );

			alert.setCancelable(false);
			alert.setPositiveButton( R.string.txtDone, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int which) {
					dialog.dismiss();   //닫기
					if (groupIds != null) finish();
				}
			});
			alert.show();
		}
	}

	private Runnable updateResults = new Runnable() {
		public void run () {
			fillMsgList();
			showButtonMenuByCheckedItem();
		}
	};
	protected class DeleteMsgListener implements View.OnClickListener{

		protected ProgressDialog pgdlg ;

		private final Handler mHandler = new Handler() 
		{
			public void handleMessage(Message msg) {
				pgdlg.incrementProgressBy(1);
			}
		};

		public void onClick(View view) {

			if( checkedSet.size() == 0) return;
			final AlertDialog.Builder alert = new AlertDialog.Builder(MsgListActivity.this);
			alert.setTitle( R.string.txtAlret );
			alert.setMessage( R.string.txtWarnningDeleteMsg );

			alert.setPositiveButton( R.string.txtYES, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int which) {
					dialog.dismiss();   //닫기

					pgdlg = new ProgressDialog(MsgListActivity.this);
					pgdlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					pgdlg.setMax(checkedSet.size());
					pgdlg.setMessage( getString(R.string.txtDeletingMsg) );
					pgdlg.show();

					new Thread( new DeleteProgressDlg()).start();
				}
			});
			alert.setNegativeButton( R.string.txtNO, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int which) {
					dialog.dismiss();   //닫기
				}
			});
			alert.show();
		}

		private class DeleteProgressDlg implements Runnable{
			public void run() {
				L("DELETE Message.");
				Message msg;
				for( Integer _id : checkedSet)
				{
					msg = mHandler.obtainMessage();
					msgCtrl.delete( String.valueOf(_id));
					mHandler.sendMessage(msg);					
				}
				pgdlg.dismiss();
				mHandler.post(updateResults);
				checkedSet.clear();
			}
		}
	}

	public void onClick(View view){
		switch( view.getId() )
		{
		case R.id.mlBtnSelectAll:
			changeAllCheckState(willSelectAll);
			break;
		default :
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.ml_menu, menu);
		return 	super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch( item.getItemId() )
//		{
//		case R.id.mlBack2Menu:
//			L(" onOption Item Select Menu.");
//			Intent intentMenu = new Intent(MsgListActivity.this, MenuActivity.class);
//			intentMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intentMenu);
//			finish();
//			break;
//		case R.id.mlDelete:
//			L(" onOption Item Select DELETE.");
//			new DeleteMsgListener().onClick(null);
//			break;
//		default:
//			Log.e("DR", "Option Item selected Default.");
//		}
		return super.onOptionsItemSelected(item);
	}

	protected void showButtonMenuByCheckedItem(){
		if( checkedSet.size() == 0 )
		{
			showButtonMenu( false );
		}else{
			showButtonMenu( true );
		}
	}
	private void showButtonMenu( boolean isShow )
	{
		if( isShow ){
			mlLlBottomHeight = Math.max(mlLlBottomHeight, mlLlBottom.getHeight());

			mlLlBottom.setVisibility(View.VISIBLE);
			mlIvBottom.setVisibility(View.VISIBLE);

			mlLlBottom.setPadding(
					mlLlBottom.getPaddingLeft(), 
					mlLlBottom.getPaddingTop(), 
					mlLlBottom.getPaddingRight(), 
					0);
			mlLvMsgList.setPadding(
					mlLvMsgList.getPaddingLeft(),
					mlLvMsgList.getPaddingTop(),
					mlLvMsgList.getPaddingRight(),
					mlLlBottomHeight);

			if ( previousShownBtnMode == false )
			{
				Animation aniMlBottomMenu = AnimationUtils.loadAnimation(this, R.anim.ml_bottom_menu);
				mlLlBottom.setAnimation(aniMlBottomMenu);
			}
			//애니메이션
		}else{
			mlLvMsgList.setPadding(
					mlLvMsgList.getPaddingLeft(),
					mlLvMsgList.getPaddingTop(),
					mlLvMsgList.getPaddingRight(),
					0);
			mlLlBottom.setPadding(
					mlLlBottom.getPaddingLeft(), 
					mlLlBottom.getPaddingTop(), 
					mlLlBottom.getPaddingRight(), 
					-1 * mlLlBottom.getHeight());

			mlLlBottom.setVisibility(View.INVISIBLE);
			mlIvBottom.setVisibility(View.INVISIBLE);
		}

		previousShownBtnMode = isShow;
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
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}