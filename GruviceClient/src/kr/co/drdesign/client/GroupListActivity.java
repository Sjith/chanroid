package kr.co.drdesign.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.client.service.GrpReceiver;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GroupListActivity 
extends Activity implements Loggable{

	private static final String CLASS_NAME = "GroupListActivity";
	
	private final HashSet<String> checkedSet = new HashSet<String>();
	private ArrayList<Map<String,String>> grpList;
	private Map<String, Integer> grpSizeMap = new HashMap<String, Integer>();

	private ListAdapter adapter;
	private ListView grpListView;

	private GroupController grpCtrl;
	private GrpReceiver grpReceive;
	private ReceiveMsgController msgCtrl;

	private GroupHandler grpHandler;
	private GroupThread grpThread;
	
	private GruviceUtillity gUtil;

	private ProgressDialog pgdlg;
	private ProgressDialog grpdlg;
	private Button btnSelectAll;
	
	private boolean willSelectAll = true;
	
	private static Drawable sBackGround;
	
	private String skinType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L( CLASS_NAME + "onCreate()");
		
		setContentView(R.layout.group_list);

		grpListView = (ListView) findViewById(R.id.glLvGroups);
		btnSelectAll = (Button) findViewById(R.id.glBtnSelectAll);
		
		String[] SKIN_TYPES = GruviceUtillity.getInstance(getApplicationContext()).getSKIN_TYPES();
		skinType = GruviceUtillity.getInstance(getApplicationContext()).getSkinType();
		
		if( skinType.equalsIgnoreCase(SKIN_TYPES[0]) == true)
		{
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		}else  if( skinType.equalsIgnoreCase(SKIN_TYPES[1]) == true) 
		{
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);
		}
		grpListView.setBackgroundDrawable(sBackGround);
		
		gUtil = GruviceUtillity.getInstance(getApplicationContext());
		
		grpHandler = new GroupHandler();
		grpThread = new GroupThread();
		
		grpCtrl = GroupController.getInstance(getApplicationContext());
		grpReceive = new GrpReceiver(getApplicationContext(), gUtil.getClientId(), gUtil.getPassword());
		msgCtrl = ReceiveMsgController.getInstance(getApplicationContext());

//		fillList();
		refreshList();
//		if (grpList == null || grpList.size() == 0)	refreshList();
	}
	
	public void getGrouplist() {
		// 2011-05-31 서버에서 그룹정보 가져오기.
		grpCtrl.deleteAll();
		grpReceive.getGrplist();
	}
	
	// 2011-06-01 그룹정보 로딩 다이얼로그
	public void showLoadingDialog() {
		grpdlg = null;
		grpdlg = new ProgressDialog(this);
		grpdlg.setCancelable(true);
		grpdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		grpdlg.setMessage("서버에서 그룹정보를\n가져오는 중입니다.");
		// 이부분은 int형의 인자값을 받는 메서드가 없음. 일단 int값으로 문자 객체를 만들어야함.
		grpdlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				grpHandler.sendEmptyMessage(1);
				dialog.dismiss();
			}
		});
		grpdlg.show();
	}
	
	// 2011-06-01 스레드와 다이얼로그가 통신할 핸들러
	class GroupHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0 : // 스레드에서 사용
				grpdlg.dismiss();
				grpThread.interrupt();
				fillList();
				break;
			case 1 : // 다이얼로그에서 사용
				grpThread.interrupt();
				fillList();
				break;
			default :
				break;
			}
		}
	}

	// 2011-06-01 서버에서 그룹정보를 받아오는 스레드
	class GroupThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getGrouplist();
			grpHandler.sendEmptyMessage(0);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( sBackGround != null )sBackGround.setCallback(null);
	}
	private void changeAllCheckState( boolean allChecked )
	{
		if( allChecked )
		{
			Iterator<Map<String,String>> it = grpList.iterator();
			String id;
			while(it.hasNext())
			{
				id = it.next().get(GroupController.GROUP_ID);
				checkedSet.add(id);
			}
			btnSelectAll.setText(R.string.txtDeselectAll);
			L( "Select All");
		}
		else{
			checkedSet.clear();
			btnSelectAll.setText(R.string.txtSelectAll);
			L( "DeSelect All");
		}
		fillList();
		willSelectAll = !(willSelectAll);
	}
	public void showNoGrpMsg()
	{
		L( "showNoGroupMsg ");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle( R.string.txtAlret );
		alert.setMessage( R.string.glTxtNoGroupList );

		alert.setCancelable(false);
		alert.setPositiveButton( R.string.txtDone, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int which) {
				dialog.dismiss();   //닫기
				finish();
			}
		});

		alert.show();
	}
	private void fillList()
	{
		L( "Fill List. ");
		grpList = grpCtrl.getGroupList();

		String groupId;
		grpSizeMap.clear();
		for( Map<String, String> map : grpList )
		{
			groupId = map.get(GroupController.GROUP_ID);
			grpSizeMap.put( groupId, msgCtrl.cntRecordByGroup(groupId) );	
		}

		adapter = new GroupListAdapter(
				this,
				grpList,
				R.layout.group_item,
				new String[]{
						GroupController.GROUP_ID,
						GroupController.GROUP_NAME,
						GroupController.GROUP_KNAME
				},
				new int[]{ R.id.giCtvID, R.id.giTvName }
		);

		grpListView.setAdapter(adapter);
	}

	public void onClickBtnReadMsg(View view){
		Intent intent = new Intent( GroupListActivity.this, ReceivedMsgListActivity.class );

		String[] groupIds = new String[checkedSet.size()];
		if ( groupIds.length == 0 ) return;

		checkedSet.toArray(groupIds);
		// extra값으로 그룹명을 넘겨 해당 그룹에 대한 메시지만 표시되도록 한다.
		intent.putExtra(ReceiveMsgController.GROUP_ID, groupIds);

		startActivity(intent);
	}

	public void onClick(View view)
	{
		// 모두선택
		switch(view.getId())
		{
		case R.id.glBtnSelectAll:
			changeAllCheckState(willSelectAll);
			break;
		default :
			break;
		}
	}

	public void onClickBtnDeleteGrp(View view)
	{
		L( "Delete Group. onClickBtnDeleteGrp()");
		if( checkedSet.size() == 0) return ;
		String[] groupIds = new String[checkedSet.size()];
		checkedSet.toArray(groupIds);
		new DeleteGroupListener().deleteGroup(groupIds);
	}

	public class GroupListAdapter extends SimpleAdapter{

		public GroupListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
						String[] from, int[] to) 
		{
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.group_item, null);
			}

			Map<String, String> map = grpList.get(position);
			//final Integer index = position;
			final String groupId = map.get( GroupController.GROUP_ID );
			final String groupName;
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("한") ||
					getResources().getConfiguration().locale.getDisplayLanguage().contains("Korea")) {
				L("KOREA");
				groupName = map.get( GroupController.GROUP_KNAME );
			} else {
				L("DDANNARA");
				groupName = map.get( GroupController.GROUP_NAME );
			}

			L( "groupID = " + groupId );
			L( "groupName = " + groupName );
			
			CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.giCtvID);
			ctv.setBackgroundResource(R.drawable.v2_ic_checkbox);
			if( checkedSet.contains(groupId) ){
				ctv.setChecked(true);
			}else{
				ctv.setChecked(false);
			}
			ctv.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					((CheckedTextView)view).toggle();
					if ( ((CheckedTextView)view).isChecked() )
					{
						checkedSet.add(groupId);
					}
					else
					{
						checkedSet.remove(groupId);
					}
				}
			});

			TextView tv = (TextView)view.findViewById(R.id.giTvName);
			tv.setText( groupName );
			tv.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent( GroupListActivity.this, ReceivedMsgListActivity.class );
					intent.putExtra(ReceiveMsgController.GROUP_ID, new String[]{groupId});
					startActivity(intent);
				}
			});
			tv.setOnLongClickListener(new View.OnLongClickListener() {

				public boolean onLongClick(View view) {
					final CharSequence[] items = 
						new String[]{ 
							getString(R.string.glMenuDeleteGroup),
							getString(R.string.glMenuEditGroup)};

					AlertDialog.Builder builder = new AlertDialog.Builder(GroupListActivity.this);
					builder.setTitle(R.string.glMenuGroup);
					builder.setItems(items, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int item)
						{
							switch(item)
							{
							case 0:
								new DeleteGroupListener().deleteGroup( new String[]{ groupId } );
								break;
							case 1:
								editGroupNameDlg(groupId);
								break;
							default :
								Log.e("DR", "LongClicked but wrong item.. ");
								break;
							}
						}

						private void editGroupNameDlg(final String groupId) {
							L("EDIT GROUP NAME");
							final Dialog dialog = new Dialog(GroupListActivity.this);
							dialog.setContentView(R.layout.gl_edit_dialog);
							dialog.setTitle(R.string.glMenuEditGroup);

							final EditText etGroupName = (EditText)dialog.findViewById(R.id.glEtGroupName);
							etGroupName.setHint(groupName);

							Button btnEditOK = (Button)dialog.findViewById(R.id.glBtnEditOK);
							btnEditOK.setOnClickListener(new View.OnClickListener() {
								public void onClick(View view) {
									if( etGroupName.getText().length() > 0 )
									{
										grpCtrl.updateGroupInfo( groupId, etGroupName.getText().toString() );
										fillList();
										dialog.dismiss();
										L("GROUP NAME CHANGE TO " + etGroupName.getText().toString());
									}
								}
							});

							Button btnEditCancel  = (Button)dialog.findViewById(R.id.glBtnEditCancel);
							btnEditCancel.setOnClickListener(new View.OnClickListener() {
								public void onClick(View view) {
									dialog.dismiss();
									L("EDIT GROUP NAME CANCEL.");
								}
							});
							dialog.show();							
						}
					});
					builder.setNegativeButton( R.string.txtDone, null);
					final AlertDialog alert = builder.create();
					alert.show();
					return true;
				}
			});

			TextView tvcnt = (TextView)view.findViewById(R.id.giTvCnt);
			tvcnt.setText(" (" + grpSizeMap.get(groupId) + ")");
			
			// 2011-06-01 그룹 기타정보도 목록에 표시하도록 구현
			TextView tvDesc = (TextView)view.findViewById(R.id.giTvDesc);
			tvDesc.setText(map.get(GroupController.GROUP_DESC));

			return view;
		}
	}

	
	// 그룹 삭제시 확인창을 출력하고 선택에 따른 명령을 실행하는 클래스
	private class DeleteGroupListener implements View.OnClickListener{

		private List<Map<String,String>> msgList;

		private final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				pgdlg.incrementProgressBy(1);
			}
		};
		public void deleteGroup( final String[] groupIds ){
			L("PRESS BTN DELETE GROUP ");
			for( String groupID : groupIds )
			{
				L("DELETE GROUP ID : " + groupID);
			}
			final AlertDialog.Builder alert = new AlertDialog.Builder(GroupListActivity.this);
			alert.setTitle( R.string.txtAlret );
			alert.setMessage( R.string.glTxtDeleteGroup );
			alert.setPositiveButton( 
					R.string.txtYES, 
					new DialogInterface.OnClickListener() 
					{
						public void onClick( DialogInterface dialog, int which) {
							dialog.dismiss();   //닫기

							pgdlg = new ProgressDialog(GroupListActivity.this);
							pgdlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

							msgList = msgCtrl.getMsgListbyGroup(groupIds);
							pgdlg.setMax( msgList.size() );
							pgdlg.setMessage( getString(R.string.txtDeletingMsg) );
							pgdlg.show();

//							grpCtrl.delete(groupIds);
							new Thread( new DeleteProgressDlg()).start();
							L("DELETE GROUPS RUNNING THREAD");
						}
					});
			alert.setNegativeButton( 
					R.string.txtNO, 
					new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int which) {
							dialog.dismiss();   //닫기
						}
					});
			alert.show();
		}
		public void onClick(View view) {
			//if( checkedSet.size() == 0) return ;
			//String[] groupIds = new String[checkedSet.size()];
			//deleteGroup( groupIds );
		}


		// Delete Msg one by one and sendMessage to Progreesbar
		private class DeleteProgressDlg implements Runnable{
			public void run() {
				
				Message handlerMmsg;
				for( Map<String,String> msg : msgList)
				{
					handlerMmsg = mHandler.obtainMessage();
					msgCtrl.delete( msg.get(ReceiveMsgController._id) );
					mHandler.sendMessage(handlerMmsg);
				}

				pgdlg.dismiss();
				mHandler.post(	
						new Runnable() {
							public void run () {
								fillList();
							}
						});
				checkedSet.clear();
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gl_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() )
		{
		case R.id.glRemove:
			onClickBtnDeleteGrp(null);
			break;
		case R.id.glRead:
			onClickBtnReadMsg(null);
			break;
		// 2011-06-01 그룹정보 새로 받아오는 메뉴버튼 구현
		case R.id.glRefreshList:
			refreshList();
			break;
		default:
			Log.e("DR", "Optional Menu selected Default");
		}
		return super.onOptionsItemSelected(item);
	}

	// 2011-06-01 그룹정보 새로 받아오기
	public void refreshList() {
		showLoadingDialog();
		grpCtrl.deleteAll();
		grpThread.interrupt();
		try {
			grpThread.start();
		} catch (IllegalThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			grpThread = new GroupThread();
			grpThread.start();
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
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}
