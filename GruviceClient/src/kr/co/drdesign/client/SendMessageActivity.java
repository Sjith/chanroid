package kr.co.drdesign.client;

// �޽��� ������ �κ��� Thread�� �����, Handler�� �̿��ؼ� ���� ����� ��.
// ����.... �� �ִ°� �ƴ�?

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.client.controller.SendMsgController;
import kr.co.drdesign.client.service.Communicator;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageActivity extends Activity implements Loggable{

//	private static final String CLASS_NAME = "SendMessageActivity";

//	private ARIAHelper ah = new ARIAHelper(WebConnector.keycode);

	private LinearLayout snmLl01;
	private TextView tvSender;
	private AutoCompleteTextView actvReceiver;
	private EditText edTitle;
	private EditText edContents;
	private Button btnSendMsg;
	private SendMsgController msgCtrl;
	// 2011-05-17 �޽��� �������� ÷�����ϸ�Ͽ� �ȳִ´�.
//	private AttachController attCtrl;
	private GroupController grpCtrl;

	public static final String SERVER_IMG_URL = "http://121.254.228.178:8080/xmlmsgsys/file/";
	public static final String IMG_URL = "IMG_URL";
//	private static final String TEMP_IMG_URL = "";
	private String attachImgUri[] = new String[] {
			AttachController.NO_CONTENTS, AttachController.NO_CONTENTS,
			AttachController.NO_CONTENTS, AttachController.NO_CONTENTS,
			AttachController.NO_CONTENTS, AttachController.NO_CONTENTS,
			AttachController.NO_CONTENTS, AttachController.NO_CONTENTS,
			AttachController.NO_CONTENTS, AttachController.NO_CONTENTS
	};
	private String attachFileUri = AttachController.NO_CONTENTS;
	

	//Group���� ������ �ʿ��� ����
	private String stid = "1";
	// �⺻��. MtoM���� �������� ��.
	private String grpID = "100"; 
	// �⺻��. �׷�� ToMembers�� ǥ�õǵ��� �����ܿ��� �����Ǿ� ����.
	
	private String grpName;
	private String rcvNumber;
	//private Handler handler = new Handler();
	//private ProgressDialog pd;

	//100817 �޴� ��� ��ȣ�� Property�� �����ϱ�.�Ǵ� �� ���� ���
//	private final String[] NUMBERS = 
//		new String[]{
//			"", ""
//	};

	private final int REQ_CODE_PICK_PICTURE = 1883;
	private final int REQ_CODE_PICK_CAMERA = 1884;
	private final int REQ_CODE_PICK_FILE = 1886;
	private final int REQ_CODE_PICK_CONTACTS = 1887;
	private final int REQ_CODE_MODIFY_ATTACH_LIST = 1888;
	
	private final String ADDR = Communicator.SERVERURL + "sendMsg.jsp";
	// �޽��� ���۽� �������� ó���ϵ��� �Ѱ��ִ� �ּ�
	
	private boolean isSucessfulTrensfer = true;

	private static final String PREF_RECEIVER 	= "RECEIVER";
	private static final String PREF_TITLE 		= "TITLE";
	private static final String PREF_CONTENTS 	= "CONTENTS";

	private SharedPreferences mPref ;
	private String skinType;

	private Drawable sBackGround;
	private static SendMsgController mSendMsgCtrl;
	private static GruviceUtillity gUtil;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 2011-06-07 �ٸ� �ۿ��� ������ ������ �����͸� ������ �޾Ƽ� ó���� �� �� �ֵ��� �ؾ� �Ѵ�.
		// ��Ƽ��Ƽ ȣ������� ����. ������ �����͵��� ������ �Ҵ����ִ� �۾��� �ʿ��ϴ�.
		

		if ( gUtil == null ) gUtil = GruviceUtillity.getInstance(getApplicationContext());
		if ( mSendMsgCtrl == null ) mSendMsgCtrl = SendMsgController.getInstance(getApplicationContext());

		skinType = gUtil.getSkinType();
		String[] SKIN_TYPES = gUtil.getSKIN_TYPES();

		setContentView(R.layout.snm_view);
		if( skinType.equals( SKIN_TYPES[0]) )
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		else
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);

		snmLl01 = (LinearLayout)findViewById(R.id.snmLl01);
		snmLl01.setBackgroundDrawable(sBackGround);

//		String receiverId = i.getStringExtra(ReceiveMsgController.SENDER);
//
//		if ( receiverId == null || receiverId.length() == 0 ) 
//			receiverId = gUtil.getPhoneNumber();

		String senderId = gUtil.getPhoneNumber();  

		tvSender = (TextView) findViewById(R.id.snmTvSender);
		tvSender.setText( senderId );

		actvReceiver = (AutoCompleteTextView) findViewById(R.id.snmActvReceiver);
//		actvReceiver.append(receiverId);
//		ArrayAdapter<String> autoCompleteData 
//		= new ArrayAdapter<String>(this, R.layout.dropdown_item_1line, R.id.TextView01, NUMBERS);
//		actvReceiver.setAdapter(autoCompleteData);

		edTitle = (EditText) findViewById(R.id.snmEtTitle);
		edContents = (EditText) findViewById(R.id.snmEtContents);
		btnSendMsg = (Button) findViewById(R.id.snmBtnSendMsg);

		btnSendMsg.setOnClickListener(new MessageSenderListener());


		mPref = PreferenceManager.getDefaultSharedPreferences(this);

		msgCtrl = SendMsgController.getInstance(getApplicationContext());
//		attCtrl = AttachController.getInstance(getApplicationContext());
		grpCtrl = GroupController.getInstance(getApplicationContext());
		
		showRegistrationDialog();
		// 2011-04-18 �� ���� �޼���� �������ɱ�.. ���⼭�� ���µ�
		setTextView();
	}

	private void setTextView() {
		// 2011-05-17 ����, ���� ���
		Intent i = getIntent();
		if(i.getAction() != null) {
			if (i.getAction().equals("REPLY")) {
				actvReceiver.setText(i.getStringExtra(ReceiveMsgController.SENDER));
				edTitle.setText(i.getStringExtra(ReceiveMsgController.TITLE));
				edContents.setText(i.getStringExtra(ReceiveMsgController.CONTENTS));
			} else if (i.getAction().equals("RESEND")) {
				edTitle.setText(i.getStringExtra(ReceiveMsgController.TITLE));
				edContents.setText(i.getStringExtra(ReceiveMsgController.CONTENTS));
				for (int j = 0; j < attachImgUri.length; j++) {
					if (i.getStringExtra("image" + (j+1)) != AttachController.NO_CONTENTS ) {
						attachImgUri[j] = i.getStringExtra("image" + (j+1));
						if (attachImgUri[j] == null) {
							attachImgUri[j] = AttachController.NO_CONTENTS;
						}
						L("attachImgUri Received : " + attachImgUri[j]);
					}
//					if (i.getStringExtra("file") != AttachController.NO_CONTENTS ) {
//						attachFileUri = i.getStringExtra("file");
//						L("attachFileUri Received : " + attachFileUri);
//					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		if( sBackGround != null ) sBackGround.setCallback(null);
		File delFile = new File(GruviceUtillity.TEMP_STORAGE);
		File delList[] = delFile.listFiles();
		if (delFile.exists()) {
			for (int i = 0; i < delList.length; i++) {
				delList[i].delete();
				// 2011-06-07 ��Ƽ��Ƽ ����� �ӽ����� ���� ��� ����.
			}
		}
	}

	private void showRegistrationDialog() {
		if (!gUtil.isRegistration()) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this)
			.setTitle(R.string.txtAlret)
			.setMessage(R.string.mnTxtNoticeMessage)
			.setPositiveButton(R.string.mnBtnRegistration, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent i = new Intent( getApplicationContext(), RegistrationActivity.class);
					// ����â ȣ��
					startActivityForResult(i, Activity.RESULT_OK);
					finish();
				}
			})
			.setNegativeButton(R.string.txtCancel, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			ab.show();
		}
	}
	
	private void saveTempMessage()
	{
		Editor editor = mPref.edit();
		editor.putString(PREF_RECEIVER, actvReceiver.getText().toString());
		editor.putString(PREF_TITLE, edTitle.getText().toString());
		editor.putString(PREF_CONTENTS, edContents.getText().toString());
		editor.commit();
	}

	private void resumeTempMessage()
	{
		actvReceiver.setText( mPref.getString(PREF_RECEIVER, ""));
		edTitle.setText(mPref.getString(PREF_TITLE, ""));
		edContents.setText(mPref.getString(PREF_CONTENTS, ""));
	}

	//���̾�α�
//	private void displayResult(boolean isSucess){
//		btnSendMsg.setClickable(true);
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//		if( isSucess )	alert.setMessage( R.string.snmTxtSendSuccess );
//		else	alert.setMessage( R.string.snmTxtSendUnsuccess );
//
//		alert.setPositiveButton( R.string.txtDone, new DialogInterface.OnClickListener() {
//			public void onClick( DialogInterface dialog, int which) {
//				finish();
//				dialog.dismiss();   //�ݱ�
//			}
//		});
//		alert.show();
//	}

//	private String encodeString( String source )
//	{
//		L( "encodeString(" + source + ")");
//		byte[] encryptSource = ah.encrypt(source);
//		return Base64Coder.encodeLines(encryptSource); 
//	}

	public void onClick(View view)
	{
		final Intent i;
		switch( view.getId() )
		{
		case R.id.snmBtnSendMsg:
		// ���� �����Ǿ� �ִ�.
			break;

		case R.id.snmBtnImgAtta:
			L("onClick snmBtnImgAtta" );
			i = new Intent(Intent.ACTION_PICK)
			.setType("image/*")
			.setAction(Intent.ACTION_GET_CONTENT);
			
			AlertDialog.Builder imgAttachDlg = new AlertDialog.Builder(this);
			imgAttachDlg.setTitle(R.string.snmBtnImgAtta)
			.setItems(new String[]{ "������������ ÷��", "���ο� ����" }, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0 :
								startActivityForResult(i, REQ_CODE_PICK_PICTURE);
								break;
							case 1 :
								// ī�޶�� �Կ��ؼ� �ٷ� �����ֱ�.
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
								break;
							}
						}
					})
			.show();
			
			// ������ ���� �Ⱦ� ����
			// 2011-05-02 ���߿� ������ �ٷ� ��� ���ε��� �� �ֵ��� �����ؾ� �Ѵ�.
			break;
			
		// 2011-04-20 ÷������ ��� �׽�Ʈ - ����. ���߿� ����..
		case R.id.snmBtnAtta :
			i = new Intent(Intent.ACTION_PICK)
			.setType("*/*")
			.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(i, REQ_CODE_PICK_FILE);
			break;

		// 2011-05-12 ÷������ ��� ����
		case R.id.snmBtnAttachList :
			i = new Intent(getApplicationContext(), SendAttachListActivity.class);
			
			for (int j = 0; j < attachImgUri.length; j++) {
				if (attachImgUri[j] != AttachController.NO_CONTENTS) i.putExtra("image" + (j+1), attachImgUri[j]);
				if (attachFileUri != AttachController.NO_CONTENTS) i.putExtra("file", attachFileUri);
			}
			
			startActivity(i);
			break;
			
		case R.id.snmBtnPreview:
			// 2011-04-26 �̸����� ��� ����.
//			previewMsg();
			break;
		case R.id.snmBtnGrp:
			L("onClick snmBtnBrowser");
			pickGroupID();
			break;
		case R.id.snmBtnPerson:
			L("onClick snmBtnPerson");
			pickNumber();
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ��ȭ��ȣ�� ����ø���� �����͸� ����������
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_PICK_PICTURE) { // ����
			// 2011-06-14 �׳� ��ư �ϳ��� ��� ÷���� �� �ֵ��� ����
			if (resultCode == RESULT_OK) { 
				String[] indexOfImg = data.getData().toString().split("/");
				String[] cols = new String[] {
						MediaStore.Images.ImageColumns.DATA,
						MediaStore.Images.ImageColumns._ID
				};
				Cursor cur = getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						cols , 
						MediaStore.Images.ImageColumns._ID + "=?", 
						new String[]{indexOfImg[indexOfImg.length-1]}, 
						null);
				if( cur.moveToFirst() )
				{
					for (int i = 0; i < attachImgUri.length; i++) {
						if (attachImgUri[i].equals(AttachController.NO_CONTENTS)) {
							attachImgUri[i] = cur.getString(0);
							L("attachImgUri1 : " + attachImgUri[i]);
							break;
						}
					}
				}
			}
		} else if (requestCode == REQ_CODE_PICK_CAMERA) {
			// 2011-06-14 ���� ���� �ٷ� �����ֱ�
			if (resultCode == RESULT_OK) {
				Log.i("Gruvice", "received Camera Result");
				Bundle extras = data.getExtras();
				if (extras != null) {
					Log.i("Gruvice", "getExtras()");
					Bitmap bitmap = extras.getParcelable("data"); // ��Ʈ���� ���ϵȴ�.
					String tempPath = GruviceUtillity.TEMP_STORAGE + System.currentTimeMillis();
					String ext = ".jpg";
					
					try {
						Log.i("Gruvice", "Stream start!");
						FileOutputStream out = new FileOutputStream(tempPath + ext);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
						for (int i = 0; i < attachImgUri.length; i++) {
							if (attachImgUri[i].equals(AttachController.NO_CONTENTS)) {
								attachImgUri[i] = tempPath + ext;
								L("attachImgUri : " + attachImgUri[i]);
								break;
							}
						}
						out.close();
						Log.i("Gruvice", "Stream close.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("Gruvice", e.getMessage());
					}
				}
			}
		} else if (requestCode == REQ_CODE_PICK_FILE) {
			// 2011-04-20 ÷�������� ���� ���ù�. �ϴ��� ����
			if (resultCode == RESULT_OK) {
				// 2011-05-12 �� ������ ���������... 2.1������ ���ư��� �ȴٱ��� �Ф�
				String temp = Uri.decode(data.getDataString()); // �о������ ������ ���� ����
				String fileName = temp.replace("file://", "");
				L("fileName : " + fileName);
				if (temp.contains("file://")) {
					// 2011-06-02 �Ϲ� ������ ���� ���� ��Ĵ�� �޾ƿ��� �ǹǷ� ���ǹ����� �и�.
					// 2011-06-02 ���Ͽ� ���� �ѱ۸� ǥ�ð����ϵ��� �ذ�
					L("fileName : " + fileName);
					attachFileUri = fileName;
				} else {
					// 2011-06-02 ���� Ÿ�Կ� ���� ���ϸ��� �����ϰ� ���� �� �ֵ��� ����.
					// �̹���, ������, ������ ���ϸ��� �޾ƿ��� ����� �ٸ� ���� ���� ����.			
					// Android 3.0 (Api level 11)���ʹ� �Ϲ� ���Ͽ� ���ؼ��� �̷��� ����� �����.
					if (fileName.contains("audio")) {
						String[] indexOfAudio = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Audio.AudioColumns.DATA,
								MediaStore.Audio.AudioColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // ������
								cols , // Ŀ���� ���Ϲ��� �÷�
								MediaStore.Audio.AudioColumns._ID + "=?",  // id �� ? �ΰ� �� ������
								new String[]{indexOfAudio[indexOfAudio.length-1]}, // �̰� ���ϸ� 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA �÷��� ����ε��ϴ�.
						}
					} else if (fileName.contains("images")) {
						String[] indexOfImg = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Images.ImageColumns.DATA,
								MediaStore.Images.ImageColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // ������
								cols , // Ŀ���� ���Ϲ��� �÷�
								MediaStore.Images.ImageColumns._ID + "=?",  // id �� ? �ΰ� �� ������
								new String[]{indexOfImg[indexOfImg.length-1]}, // �̰� ���ϸ� 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA �÷��� ����ε��ϴ�.
						}
					} else if (fileName.contains("video")) {
						String[] indexOfVideo = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Video.VideoColumns.DATA,
								MediaStore.Video.VideoColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Video.Media.EXTERNAL_CONTENT_URI, // ������
								cols , // Ŀ���� ���Ϲ��� �÷�
								MediaStore.Video.VideoColumns._ID + "=?",  // id �� ? �ΰ� �� ������
								new String[]{indexOfVideo[indexOfVideo.length-1]}, // �̰� ���ϸ� 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA �÷��� ����ε��ϴ�.
						}
					}
					L("attachFileUri : " + attachFileUri);
				}
				
			}
		}
		else if (requestCode == REQ_CODE_PICK_CONTACTS){
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String name = c.getString(c.getColumnIndex("display_name"));
					String number = c.getString(c.getColumnIndex("data1"));
					L("read contacts : " + name + "," + number);
					if( number != null ) {
						number = number.replaceAll("-", "");
					}
					stid = "1";
					actvReceiver.setText(name);
					rcvNumber = number;
				}
			}
		} 
		else if (requestCode == REQ_CODE_MODIFY_ATTACH_LIST) {
			// ÷�����ϸ�Ͽ��� ������ ����� �����Ŵ.
			if (resultCode == RESULT_OK) {
				Log.i("Gruvice", "modify attach list.");
				for (int i = 0; i < attachImgUri.length; i++) {
					try {
						attachImgUri[i] = data.getStringExtra( "image" + (i+1) );
					} catch (NullPointerException e) {
						attachImgUri[i] = AttachController.NO_CONTENTS;
					}
				}
			}
		}
		fileLengthCheck();
	}
	
	private boolean fileLengthCheck() {
		// 2011-05-24 �̹��� �뷮���� �ٽ� ����. ÷������ �뷮���� ����.
		// �̹��������� ��� ��⸶�� Ư���� �ֱ� ������ ����翡 ����� ��.
		try {
			for (int i = 0; i < attachImgUri.length; i++) {
				if (new File(attachImgUri[i]) != null) {
					if( new File(attachImgUri[i]).length() > 2 * 1024 * 1024 )
					{
						Toast.makeText(SendMessageActivity.this, 
								R.string.snmOverlengthImage, Toast.LENGTH_SHORT).show();
						attachImgUri[i] = AttachController.NO_CONTENTS;
						return false;
					}
				}
			}
			if (new File(attachFileUri) != null) {
				if( new File(attachFileUri).length() > 30 * 1024 * 1024 )
				{
					Toast.makeText(SendMessageActivity.this, 
							R.string.snmOverlengthFile , Toast.LENGTH_SHORT).show();
					attachFileUri = AttachController.NO_CONTENTS;
					return false;
				}
			}
		} catch (Exception e) {
			L("message send error : " + e.getMessage());
		}
		return true;
	}

	private Runnable updateResults = new Runnable() {
		public void run () {
			AlertDialog.Builder alert = new AlertDialog.Builder(SendMessageActivity.this);
			if( isSucessfulTrensfer == true) alert.setMessage( R.string.snmTxtSendSuccess );
			else {
				alert.setMessage( R.string.snmTxtSendFaile );
				btnSendMsg.setEnabled(false);
			}
			
			alert.setPositiveButton( R.string.txtDone, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			alert.show();
		}
	};

	private ProgressDialog pgdlg;
	
	
	private class MessageSenderListener implements View.OnClickListener {

		private Map<String, String> post 	= new HashMap<String, String>();
		private Map<String, String> map 	= new HashMap<String, String>();
		
		private final Handler mHandler = new Handler();
		// 2011-04-27 ���ߵ������ �ڵ� �ڸ�

		public void onClick(View arg0) {
			if( setMap() )
			{
				// �ε� ���̾�α�
				pgdlg = new ProgressDialog(SendMessageActivity.this);
				pgdlg.setMessage(getString(R.string.snmTxtSending));
				pgdlg.show();
				new Thread( new MsgSenderPgdlg() ).start();
				return;
			}
		}

		private boolean setMap()
		{
			String sender = null;
			String receiver = null;
			String title = null;
			String contents = null;
			String groupid = null;
			long senddate = 0;
			try {
//				sender = tvSender.getText().toString(); // ����
				sender = gUtil.getPhoneNumber(); // ����ȣ
				groupid = grpID; // �׷�
				senddate = System.currentTimeMillis();
				L("senddate : " + senddate);
				if( stid.equals("2")) { // �׷� �޽����� ����
					receiver = grpID;
				}
				else {
					receiver = actvReceiver.getText().toString();
					try {
						if( receiver != null ) {
							Integer.parseInt(receiver);
						}
						
					}catch( NumberFormatException nfe) {
							receiver = rcvNumber;
					}
				}
				title = edTitle.getText().toString();
				contents = edContents.getText().toString();
			}catch( NullPointerException npe )
			{
				//ä������ ���� ��ĭ�� �����Ƿ� ���â�� ����, �޼ҵ� ����
				Log.e(DEBUG_TAG, npe.getMessage());
				return false;
			}

			// sender check
			if ( sender == null || sender.length() == 0)
			{	
				Toast.makeText(SendMessageActivity.this, R.string.snmErrSender, Toast.LENGTH_SHORT).show();
				return false;
			}
			if ( receiver == null || receiver.length() == 0)
			{
				Toast.makeText(SendMessageActivity.this, R.string.snmErrReceiver, Toast.LENGTH_SHORT).show();
				return false;
			}
			if ( contents == null || contents.length() == 0)
			{	
				Toast.makeText(SendMessageActivity.this, R.string.snmErrContent, Toast.LENGTH_SHORT).show();
				return false;
			}
			if ( title == null || title.length() == 0 )
			{
				if( contents != null && contents.length() > 0)
				{
					title = contents.substring(0, 
							(20 > contents.length())?contents.length():20 );
				}
			}
			
			
			
			if ( isNetworkAvailable() == false )
			{
				Toast.makeText(SendMessageActivity.this, R.string.snmErrNetwork, Toast.LENGTH_SHORT).show();
				return false;
			}

			// ��ȣȭ�ؼ� �����ؾ� �ϳ� �������� Reboot ������ �߻��Ͽ�( JEUS ������ ���� ) ���� ���� ^_^
			//map.put("sender", encodeString(sender));
			//map.put("receiver", encodeString(receiver));
			//map.put("title", encodeString(title));
			//map.put("contents", encodeString(contents));
			//map.put("number", getPhoneNumber());
			map.put(SendMsgController.SENDDATE, Long.toString(senddate));

			post.put("sender", sender);
			post.put("receiver", receiver);
			post.put("title", title);
			post.put("contents", contents);
			post.put("number", gUtil.getClientId());
			post.put("groupid", groupid);
			post.put("senddate", Long.toString(senddate));
			
			
			// 2011-05-12 �������۱��� ����ϰ� ����!
			// 2011-06-03 �� �ƴϰ� �ѱ����� ���� �ȵǴ� ���� �߻�.
			for (int i = 0; i < attachImgUri.length; i++) {
				if ( !attachImgUri[i].equals(AttachController.NO_CONTENTS) ) {
					post.put("imagefile" + (i+1), attachImgUri[i]);
				}
			}
			if ( !attachFileUri.equals(AttachController.NO_CONTENTS) ) {
				post.put("file", attachFileUri); 
				L("atatatatat : " + attachFileUri);
			}

			return true;
		}
		
		private class MsgSenderPgdlg implements Runnable{
			
			public MsgSenderPgdlg( ){
				
			}

			public void run() {
				try {
					boolean result = sendMultipartFile(ADDR, post);
					if(result) msgCtrl.insert(map);
					// 2011-04-18 sendMultipartFile ���� post ��ü�� ���� ����ְ� attCtrl ���� ���.
				} catch ( IOException ioe )	{
					// �ؿ��� ������ ���ܴ� �� ���⼭ �޴´�. �̷��� ����� �ϱ� �����ٵ�..
					Log.e(DEBUG_TAG, ioe.getMessage() );
					Log.e(DEBUG_TAG, "Message Send Fail.." );
				}
				pgdlg.dismiss();
				mHandler.post(updateResults);
			}

			// 2011-04-18 ���⼭ ���ܸ� ������ ��� ����?
			// 2011-04-26 Ȯ���� ���⼭ ������ �����ִ°� �´°� ���� �ѵ�... ��� �޴����� ���� �𸣰ڱ�
			// 2011-04-28 ���� ��������.. ���� �����ܿ��� �ڹ� ���ϸ� ���ָ� �ɰŰ���.
			public boolean sendMultipartFile( String url, Map<String, String> post) throws IOException
			{
				L( "Send Multipart FILE");

				try {
					HttpClient client = new DefaultHttpClient();  
					HttpPost httpPost = new HttpPost(url);
					File file;
					File file1 = null;
					FileBody image1;
					FileBody fileBody;

					// �̰� ������ ������ �����ִ� �������ΰ� ����.
					// ���⿡ ����üũ ������ �����͸� �ְ� ������ ������.
					MultipartEntity reqEntity = new MultipartEntity
									(HttpMultipartMode.BROWSER_COMPATIBLE); 
					
					

					Set<String> keyset = post.keySet();
					if (keyset == null || keyset.size() == 0 )
					{
						return false;
					}

					if( post.containsKey("sender") )
					{
						// �������� �ȹޱ⶧���� �ʿ����.
						String ssender = post.get("sender");
						StringBody sender = new StringBody(ssender);
						reqEntity.addPart("sender", sender);
						map.put(SendMsgController.SENDER, ssender);
					}
					if( post.containsKey("title") )
					{
						String stitle = post.get("title");
						StringBody title = new StringBody(stitle);
						reqEntity.addPart("btitle", title);
						Log.i("Encode", "title Encoding : " + title.getCharset());
						map.put(SendMsgController.TITLE, stitle);
					}
					if( post.containsKey("contents") )
					{
						String scontents = new String(post.get("contents"));
						StringBody contents = new StringBody(scontents);
									
						// ���� ��� ���ľ���.
						reqEntity.addPart("bcontent", contents);
						Log.i("Encode", "contents Encoding : " + contents.getCharset());
						map.put(SendMsgController.CONTENTS, scontents);
					}
					if( post.containsKey("number") )
					{
						String sreceiver = post.get("receiver");
						StringBody members = new StringBody(sreceiver);
						reqEntity.addPart("members", members);
						reqEntity.addPart("groups", members); // �������� ������ �⺻������ 100�� ����. 
						map.put(SendMsgController.RECEIVER, sreceiver);
					}
					if( post.containsKey("groupid") )
					{
						map.put(SendMsgController.GROUP_ID, post.get("groupid"));
					}
					
					for (int i = 0; i < attachImgUri.length; i++)
					if( post.containsKey("imagefile" + (i+1)) )
					{
						String filepath = post.get("imagefile" + (i+1));
						if (filepath != AttachController.NO_CONTENTS) {
							file1 = createTempFile(filepath);
							image1 = new FileBody(file1);
							reqEntity.addPart("image" + (i+1), image1);
							L("reqEntity.add : " + filepath);
						}
					}
					// 2011-06-07 �̹����� ���ؼ��� ���ϸ��� �ٲ㼭 ������ �� ������ ����.

					// 2011-04-28 �ٵ�... �������� �޴� file ��� �Ķ���ʹ� �����?
					// 2011-05-11 �׳� �Ǵ´�� �־�ô�.
					if(post.containsKey("file")) {
						// 2011-05-12 ��... �ߵȴ�..
						String filepath = post.get("file");
						if (filepath.contains("http")) {
							// 2011-05-16 �ٿ���� ���� ������ �����Ҷ� ������ �߻��Ѵ�.
							StringBody stringBody = new StringBody(filepath);
							reqEntity.addPart("fileurl", stringBody);
							L("reqEnt.addpart : " + stringBody.toString());
						} else { // 2011-05-18 ���ޱ���� ���� �Ķ���͸� ���� ����.
							file = new File(filepath);
							L("filePath : " + filepath);
							fileBody = new FileBody(file);
							reqEntity.addPart("file", fileBody);
						}
					}

					map.put(SendMsgController.SENDDATE, Long.toString(System.currentTimeMillis()));
					
					StringBody sendermid = new StringBody(gUtil.getClientId());
					reqEntity.addPart("bsendermid", sendermid);		

					StringBody stid2 = new StringBody(stid);
					reqEntity.addPart("stid", stid2);

					httpPost.setEntity(reqEntity);
					// ������ ������ entity �� ������ ������ post �ϰ� �����Ѵ�.
					client.execute(httpPost);
					// UTF-8�� ����?
					isSucessfulTrensfer = true;
				} catch (Exception e) {
					Log.e(DEBUG_TAG, e.getMessage());
					isSucessfulTrensfer = false;
				}
				return isSucessfulTrensfer;
				

				}				
			}
		}
	
	// 2011-06-07 �̹��� ���۽� ���ϸ� ��ȯ �޼���.
	private File createTempFile(String file) throws FileNotFoundException, IOException {
		L("createTempFile : " + file);
		File orgFile = new File(file);
		String tempPath = GruviceUtillity.TEMP_STORAGE + System.currentTimeMillis();
		String ext = "";		
		int i = 0;
		
		if (file.contains(".png")) {
			ext = ".png";
		} else if (file.contains(".jpg")) {
			ext = ".jpg";
		} else if (file.contains(".gif")) {
			ext = ".gif";
		} else if (file.contains(".bmp")) {
			ext = ".bmp";
		}
		L("tempPath : " + tempPath + ext);
		FileInputStream in = new FileInputStream(orgFile);
		File tempFile = new File(GruviceUtillity.TEMP_STORAGE);
		if (!tempFile.exists()) tempFile.mkdir();
		FileOutputStream out = new FileOutputStream(tempPath + ext, false);
		
		while((i = in.read()) != -1) {
			out.write(i);
		}		
		in.close();
		out.close();

		L("tempPath : " + tempPath);
		File newFile = new File(tempPath + ext);
		return newFile;
	}
	
	
	// ��Ʈ��ũ ������� üũ
	private boolean isNetworkAvailable() {
		ConnectivityManager mConnMan = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.snm_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() )
		{
		// �޴�. �ӽ�����, �ҷ����� ���
		case R.id.snmSaveTempMsg:
			saveTempMessage();
			Toast.makeText(this, R.string.snmSaveTempMsg, Toast.LENGTH_SHORT).show();
			break;
		case R.id.snmResumeTempMsg:
			resumeTempMessage();
			Toast.makeText(this, R.string.snmResumeTempMsg, Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void pickGroupID() {
		ArrayList<Map<String,String>> grpList = grpCtrl.getGroupList();
		int size = grpList.size();
		if (size < 1) return;
		
		final String grpIDs[] = new String[size-1];
		final String grpNames[] = new String[size-1];
		
		// 2011-06-01 ���� �׷� ���ý� db���� �׷������� �޾ƿͼ� locale �� ���� ǥ������.
		// ù��° �ٿ��� �׻� ToMembers�� ��µǱ� ������ ���� �ʴ´�.
		for( int i = 1 ; i < size; i++)
		{
			grpIDs[i-1] = grpList.get(i).get( GroupController.GROUP_ID );
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("��") ||
					getResources().getConfiguration().locale.getDisplayLanguage().contains("Korea")) {
				L("KOREA");
				grpNames[i-1] = grpList.get(i).get( GroupController.GROUP_KNAME );
			} else {
				L("DDANNARA");
				grpNames[i-1] = grpList.get(i).get( GroupController.GROUP_NAME );
			}
		}

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.snmSelectgrp);
		ab.setSingleChoiceItems(grpNames, -1, 
				// 2011-06-01 -1�� �Է��ϸ� �ƹ��͵� ���õ��� ���� ���°� ��.
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// �� ����Ʈ�� ���������� 
				// 2011-03-25 �׷� ������ �޾ƿͼ� �ѷ��ִ� ������ ���� �ʿ�
				// 2011-04-18 ���� �޽����� ���� �׷� ����� �ѷ���. �������� ��ü �׷� ������ �޾ƿ��� ���� �ʿ�.
				// 2011-05-31 �׷����� ���� �Ϸ�
				L( "Selected Group ID " + whichButton);
				grpID = grpIDs[whichButton];
				grpName = grpNames[whichButton];
			}
		}).setPositiveButton(R.string.txtOK,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// OK ��ư Ŭ���� , ���⼭ ������ ���� ���� Activity �� �ѱ�� �ȴ�.
				stid = "2";
				actvReceiver.setText(grpName);
			}
		}).setNegativeButton(R.string.txtCancel,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Cancel ��ư Ŭ����
				
			}
		});
		if( size != 0)
		{
			grpID = grpIDs[0];
			grpName = grpNames[0];
			ab.show();
		}
	}

	

	private void pickNumber()
	// ��ȭ�ο��� ��ȣ ����
	{
		Intent i = new Intent(Intent.ACTION_PICK,  Phone.CONTENT_URI); // ACTION_PICK �׼��� ����ϴ� ��
		//i.setData(Uri.parse("content://com.android.contacts/data/phones"));
		// 2.1���� ���Ǵ� contacts. ���� �ʿ����
		startActivityForResult(i, REQ_CODE_PICK_CONTACTS);
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