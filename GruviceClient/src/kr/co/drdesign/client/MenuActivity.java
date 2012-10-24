package kr.co.drdesign.client;

import java.io.File;

import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.client.service.ArgosService;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class MenuActivity extends Activity implements Loggable{

//	private static final String CLASS_NAME = "MenuActivity";

	private GruviceUtillity gutil;
	private String skinType;
	public static String[] SKIN_TYPES ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gutil = GruviceUtillity.getInstance(getApplicationContext());

		setBackGround(); // ������ ���� ȭ�� ǥ��
		showRegistrationDialog(); // ��� ����� �ȵȰ�� ��� Ȯ��â ǥ��
		
		// 2011-05-20 ���̵� ���µ� �޽����� ������ �ȵȴ�. �����Ϸ�.
		// 2011-05-23 Ǫ�ü����� �Ҿ����ҽ� ���� �׾������ ����� ������� �ణ ����.
		ArgosStart argos = new ArgosStart();
		if (gutil.isRegistration())	argos.start();


		// ����޸𸮰� ���ų� PC�� ����Ʈ�� ��� �޽��� ǥ��
		if( Environment.getExternalStorageState().equals
				(Environment.MEDIA_MOUNTED) == false ) {
			showExternalStorageDialog();
		} else { 
			File folder = new File(GruviceUtillity.TEMP_STORAGE);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}
	}

	// ���񽺸� ���۽�Ű�� ����Ŭ����
	class ArgosStart extends Thread {
		public void run() {
			// 2011-05-24 �޽��� ���񽺴� �ڵ����� ����ǹǷ� �ٽ� �����ų �ʿ� ����.
//			DRMService.actionStart(getApplicationContext());
			// MQTTService ����. 2011-05-12
			ArgosService.actionStart(getApplicationContext());
		}
	}
	
	// ����޸� ����Ʈ �ȵɽ� �˸�â ǥ��
	private void showExternalStorageDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle( R.string.txtAlret );
		alert.setMessage( R.string.mnTxtStorageMessage );

		alert.setPositiveButton( R.string.txtYES, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int which) {
				dialog.dismiss();   //�ݱ�
			}
		});
		alert.show();
	}
	
	// ��� ��� Ȯ��â
	private void showRegistrationDialog() {
		// ��ϵ� ���� ������ ���⿡�� ���ϵȴ�.
		if( gutil.isRegistration() == true ) return;

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.mn_noti_dialog);
		dialog.setTitle(R.string.mnTxtNotice);
		dialog.setCancelable(false);

		Button btnOK = (Button)dialog.findViewById(R.id.mnBtnOK);
		Button btnRegistration = (Button)dialog.findViewById(R.id.mnBtnRegistration);
		final CheckBox cbNowShowing = (CheckBox)dialog.findViewById(R.id.mnCbNotShowing);

		// Ȯ�ι�ư
		btnOK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if ( cbNowShowing.isChecked() == true )
					gutil.setRegistration(true);
				// ����� ������ ������ �ٲ� â�� ���� �ʵ��� ����
				dialog.dismiss(); 
				finish();
			}
		});
		
		// ��Ϲ�ư
		btnRegistration.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ( cbNowShowing.isChecked() == true )
					gutil.setRegistration(true);
				Intent i = new Intent( getApplicationContext(), RegistrationActivity.class);
				startActivityForResult(i, 1);
				dialog.dismiss();
			}
		});

		dialog.show();		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			Log.i("Gruvice", "result Canceled.");
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setBackGround();
		boolean isAliveConnectionNoti = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("ConnectionNoti", false);
		if ( isAliveConnectionNoti == false )
		{
			// 2011-05-12 ������� ��Ƽ ����. ���̻� ������� ����.
//			NotificationManager mNotifMan =
//				(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//			mNotifMan.cancel(DisableArgosService.NOTIF_CONNECTED);
		}
	}

	// ����� �������� ���� ���� �׸� �� �ִϸ��̼� ����
	private void setBackGround()
	{
		L( "setBackGround()");
		SKIN_TYPES = GruviceUtillity.getInstance(getApplicationContext()).getSKIN_TYPES();
		skinType = GruviceUtillity.getInstance(getApplicationContext()).getSkinType();
		if( skinType.equalsIgnoreCase(SKIN_TYPES[0]) == true)
		{
			setContentView(R.layout.v1_menu);
			startAnimationVer1();
		} else
		{
			setContentView(R.layout.v2_menu);
			startAnimationVer2();
		}
	}

	// SET Animation for SKINTYPE 1 
	private void startAnimationVer1()
	{
		RelativeLayout menuRl03 = (RelativeLayout) findViewById(R.id.menuRl_03);
		Animation aniMenuBG = AnimationUtils.loadAnimation(this, R.anim.v1_mn_bg);
		menuRl03.setAnimation(aniMenuBG);
	}

	// SET Animation for SKINTYPE 2
	private void startAnimationVer2()
	{
		RelativeLayout rlGrpList = (RelativeLayout)findViewById(R.id.mnRlGrpList);
		Animation aniGrpList= AnimationUtils.loadAnimation(this, R.anim.v2_mn_grplist);
		rlGrpList.setAnimation(aniGrpList);

		RelativeLayout rlAttachList = (RelativeLayout)findViewById(R.id.mnRlAttachList);
		Animation aniAttachList= AnimationUtils.loadAnimation(this, R.anim.v2_mn_attachlist);
		rlAttachList.setAnimation(aniAttachList);

		RelativeLayout rlGrpSetting= (RelativeLayout)findViewById(R.id.mnRlGrpSetting);
		Animation aniGrpSetting= AnimationUtils.loadAnimation(this, R.anim.v2_mn_grpsetting);
		rlGrpSetting.setAnimation(aniGrpSetting);

		RelativeLayout rlMsgList = (RelativeLayout)findViewById(R.id.mnRlMsgList);
		Animation aniMsgList = AnimationUtils.loadAnimation(this, R.anim.v2_mn_msglist);
		rlMsgList.setAnimation(aniMsgList);

		RelativeLayout rlSendMsg = (RelativeLayout)findViewById(R.id.mnRlSendMessage);
		Animation aniSendMsg = AnimationUtils.loadAnimation(this, R.anim.v2_mn_sendmsg);
		rlSendMsg.setAnimation(aniSendMsg);
	}

	public void onClickButton(View view){

		Intent i = new Intent();
		switch( view.getId() )
		{
		case R.id.mnBtnAttachList:
			i.setClass(this, AttachmentListActivity.class );
			i.putExtra(ReceiveMsgController._id, ReceiveMsgController.UID);
			startActivity(i);
			break;
		case R.id.mnBtnGrpList:
			i.setClass(this, GroupListActivity.class );
			startActivity(i);
			break;
		case R.id.mnBtnMsgList:
			i.setClass(this, MsgTabActivity.class );
			i.putExtra(ReceiveMsgController._id, ReceiveMsgController.UID);
			startActivity(i);
			break;
		case R.id.mnBtnSendMsg:
			i.setClass(this, SendMessageActivity.class );
			startActivity(i);
			break;
		case R.id.mnBtnGrpSetting:
			i.setClass(this, GroupSettingActivity.class);
			startActivity(i);
			break;
		default : 
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mn_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch( item.getItemId() )
		{
		case R.id.mnPreferences:
			i = new Intent( MenuActivity.this, SvcSettingActivity.class);
			startActivity(i);
			break;
		case R.id.mnRegistration:
			i = new Intent( MenuActivity.this, RegistrationActivity.class);
			startActivity(i);
			break;
		default:
			Log.e(DEBUG_TAG, "Optional Menu selected Default");
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.txtAlret)
		.setMessage("�����Ͻðڽ��ϱ�?")
		.setPositiveButton(R.string.txtYES, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		})
		.setNegativeButton(R.string.txtNO, null);
		dialog.show();		
		
	}

	// �α׸� ��� ���� �޼���
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