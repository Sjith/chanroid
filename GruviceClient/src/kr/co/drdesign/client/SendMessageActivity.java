package kr.co.drdesign.client;

// 메시지 보내는 부분은 Thread로 만들고, Handler를 이용해서 뭔가 해줘야 함.
// 저기.... 돼 있는거 아님?

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
	// 2011-05-17 메시지 보낼때는 첨부파일목록에 안넣는다.
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
	

	//Group으로 보낼때 필요한 변수
	private String stid = "1";
	// 기본값. MtoM으로 보낼때의 값.
	private String grpID = "100"; 
	// 기본값. 그룹명에 ToMembers로 표시되도록 서버단에서 설정되어 있음.
	
	private String grpName;
	private String rcvNumber;
	//private Handler handler = new Handler();
	//private ProgressDialog pd;

	//100817 받는 사람 번호는 Property로 저장하기.또는 더 좋은 방법
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
	// 메시지 전송시 서버에서 처리하도록 넘겨주는 주소
	
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
		// 2011-06-07 다른 앱에서 보내기 등으로 데이터를 보내면 받아서 처리해 줄 수 있도록 해야 한다.
		// 액티비티 호출까지는 성공. 가져온 데이터들을 변수에 할당해주는 작업이 필요하다.
		

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
		// 2011-04-18 왜 굳이 메서드로 빼놓은걸까.. 여기서만 쓰는데
		setTextView();
	}

	private void setTextView() {
		// 2011-05-17 답장, 전달 기능
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
				// 2011-06-07 액티비티 종료시 임시폴더 파일 모두 삭제.
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
					// 설정창 호출
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

	//다이얼로그
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
//				dialog.dismiss();   //닫기
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
		// 딴데 구현되어 있다.
			break;

		case R.id.snmBtnImgAtta:
			L("onClick snmBtnImgAtta" );
			i = new Intent(Intent.ACTION_PICK)
			.setType("image/*")
			.setAction(Intent.ACTION_GET_CONTENT);
			
			AlertDialog.Builder imgAttachDlg = new AlertDialog.Builder(this);
			imgAttachDlg.setTitle(R.string.snmBtnImgAtta)
			.setItems(new String[]{ "기존사진에서 첨부", "새로운 사진" }, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0 :
								startActivityForResult(i, REQ_CODE_PICK_PICTURE);
								break;
							case 1 :
								// 카메라로 촬영해서 바로 때려넣기.
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
								break;
							}
						}
					})
			.show();
			
			// 갤러리 사진 픽업 예제
			// 2011-05-02 나중에 사진을 바로 찍어 업로드할 수 있도록 수정해야 한다.
			break;
			
		// 2011-04-20 첨부파일 기능 테스트 - 실패. 나중에 하자..
		case R.id.snmBtnAtta :
			i = new Intent(Intent.ACTION_PICK)
			.setType("*/*")
			.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(i, REQ_CODE_PICK_FILE);
			break;

		// 2011-05-12 첨부파일 목록 보기
		case R.id.snmBtnAttachList :
			i = new Intent(getApplicationContext(), SendAttachListActivity.class);
			
			for (int j = 0; j < attachImgUri.length; j++) {
				if (attachImgUri[j] != AttachController.NO_CONTENTS) i.putExtra("image" + (j+1), attachImgUri[j]);
				if (attachFileUri != AttachController.NO_CONTENTS) i.putExtra("file", attachFileUri);
			}
			
			startActivity(i);
			break;
			
		case R.id.snmBtnPreview:
			// 2011-04-26 미리보기 기능 제거.
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
		// 전화번호나 사진첩에서 데이터를 가져왔을때
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_PICK_PICTURE) { // 사진
			// 2011-06-14 그냥 버튼 하나로 계속 첨부할 수 있도록 변경
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
			// 2011-06-14 찍은 사진 바로 때려넣기
			if (resultCode == RESULT_OK) {
				Log.i("Gruvice", "received Camera Result");
				Bundle extras = data.getExtras();
				if (extras != null) {
					Log.i("Gruvice", "getExtras()");
					Bitmap bitmap = extras.getParcelable("data"); // 비트맵이 리턴된다.
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
			// 2011-04-20 첨부파일을 위한 리시버. 일단은 놔둠
			if (resultCode == RESULT_OK) {
				// 2011-05-12 좀 무식한 방법이지만... 2.1에서도 돌아가야 된다구요 ㅠㅠ
				String temp = Uri.decode(data.getDataString()); // 읽어왔을때 깨지는 문자 복구
				String fileName = temp.replace("file://", "");
				L("fileName : " + fileName);
				if (temp.contains("file://")) {
					// 2011-06-02 일반 파일의 경우는 기존 방식대로 받아오면 되므로 조건문으로 분리.
					// 2011-06-02 파일에 대해 한글명도 표시가능하도록 해결
					L("fileName : " + fileName);
					attachFileUri = fileName;
				} else {
					// 2011-06-02 파일 타입에 따라 파일명을 적절하게 얻어올 수 있도록 설정.
					// 이미지, 동영상, 음악은 파일명을 받아오는 방식이 다른 점에 대한 대응.			
					// Android 3.0 (Api level 11)부터는 일반 파일에 대해서도 이러한 방식이 적용됨.
					if (fileName.contains("audio")) {
						String[] indexOfAudio = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Audio.AudioColumns.DATA,
								MediaStore.Audio.AudioColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // 컨텐츠
								cols , // 커서로 리턴받을 컬럼
								MediaStore.Audio.AudioColumns._ID + "=?",  // id 가 ? 인걸 다 가져옴
								new String[]{indexOfAudio[indexOfAudio.length-1]}, // 이게 파일명 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA 컬럼이 경로인듯하다.
						}
					} else if (fileName.contains("images")) {
						String[] indexOfImg = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Images.ImageColumns.DATA,
								MediaStore.Images.ImageColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 컨텐츠
								cols , // 커서로 리턴받을 컬럼
								MediaStore.Images.ImageColumns._ID + "=?",  // id 가 ? 인걸 다 가져옴
								new String[]{indexOfImg[indexOfImg.length-1]}, // 이게 파일명 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA 컬럼이 경로인듯하다.
						}
					} else if (fileName.contains("video")) {
						String[] indexOfVideo = data.getData().toString().split("/");
						String[] cols = new String[] {
								MediaStore.Video.VideoColumns.DATA,
								MediaStore.Video.VideoColumns._ID
						};
						Cursor cur = getContentResolver().query(
								MediaStore.Video.Media.EXTERNAL_CONTENT_URI, // 컨텐츠
								cols , // 커서로 리턴받을 컬럼
								MediaStore.Video.VideoColumns._ID + "=?",  // id 가 ? 인걸 다 가져옴
								new String[]{indexOfVideo[indexOfVideo.length-1]}, // 이게 파일명 
								null);
						if( cur.moveToFirst() )
						{
							attachFileUri = cur.getString(0); // DATA 컬럼이 경로인듯하다.
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
			// 첨부파일목록에서 편집한 목록을 적용시킴.
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
		// 2011-05-24 이미지 용량제한 다시 설정. 첨부파일 용량제한 설정.
		// 이미지파일의 경우 기기마다 특성이 있기 때문에 저사양에 맞춰야 함.
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
		// 2011-04-27 쓰잘데기없는 코드 박멸

		public void onClick(View arg0) {
			if( setMap() )
			{
				// 로딩 다이얼로그
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
//				sender = tvSender.getText().toString(); // 수신
				sender = gUtil.getPhoneNumber(); // 폰번호
				groupid = grpID; // 그룹
				senddate = System.currentTimeMillis();
				L("senddate : " + senddate);
				if( stid.equals("2")) { // 그룹 메시지로 구분
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
				//채워지지 않은 빈칸이 있으므로 경고창을 띄우고, 메소드 종료
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

			// 암호화해서 전송해야 하나 서버쪽의 Reboot 문제가 발생하여( JEUS 문제로 추정 ) 구냥 전송 ^_^
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
			
			
			// 2011-05-12 파일전송까지 깔끔하게 구현!
			// 2011-06-03 은 아니고 한글파일 전송 안되는 문제 발생.
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
					// 2011-04-18 sendMultipartFile 에서 post 객체에 값을 집어넣고 attCtrl 에서 사용.
				} catch ( IOException ioe )	{
					// 밑에서 던지는 예외는 다 여기서 받는다. 이러면 디버깅 하기 힘들텐데..
					Log.e(DEBUG_TAG, ioe.getMessage() );
					Log.e(DEBUG_TAG, "Message Send Fail.." );
				}
				pgdlg.dismiss();
				mHandler.post(updateResults);
			}

			// 2011-04-18 여기서 예외를 던지면 어디서 받지?
			// 2011-04-26 확실히 여기서 서버로 던져주는게 맞는거 같긴 한데... 어떻게 받는지를 도통 모르겠군
			// 2011-04-28 감이 잡혀간다.. 이제 서버단에서 자바 파일만 봐주면 될거같다.
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

					// 이게 실제로 서버로 보내주는 데이터인것 같다.
					// 여기에 조건체크 등으로 데이터를 넣고 서버로 보낸다.
					MultipartEntity reqEntity = new MultipartEntity
									(HttpMultipartMode.BROWSER_COMPATIBLE); 
					
					

					Set<String> keyset = post.keySet();
					if (keyset == null || keyset.size() == 0 )
					{
						return false;
					}

					if( post.containsKey("sender") )
					{
						// 서버에서 안받기때문에 필요없다.
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
									
						// 여기 계속 고쳐야함.
						reqEntity.addPart("bcontent", contents);
						Log.i("Encode", "contents Encoding : " + contents.getCharset());
						map.put(SendMsgController.CONTENTS, scontents);
					}
					if( post.containsKey("number") )
					{
						String sreceiver = post.get("receiver");
						StringBody members = new StringBody(sreceiver);
						reqEntity.addPart("members", members);
						reqEntity.addPart("groups", members); // 설정하지 않을시 기본값으로 100이 들어간다. 
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
					// 2011-06-07 이미지에 대해서는 파일명을 바꿔서 보내도 별 문제가 없다.

					// 2011-04-28 근데... 서버에서 받는 file 라는 파라미터는 어디갔지?
					// 2011-05-11 그냥 되는대로 넣어봤다.
					if(post.containsKey("file")) {
						// 2011-05-12 오... 잘된다..
						String filepath = post.get("file");
						if (filepath.contains("http")) {
							// 2011-05-16 다운받지 않은 파일을 전달할때 문제가 발생한다.
							StringBody stringBody = new StringBody(filepath);
							reqEntity.addPart("fileurl", stringBody);
							L("reqEnt.addpart : " + stringBody.toString());
						} else { // 2011-05-18 전달기능을 위해 파라미터를 따로 생성.
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
					// 위에서 설정한 entity 를 가지고 서버에 post 하게 설정한다.
					client.execute(httpPost);
					// UTF-8로 갈껄?
					isSucessfulTrensfer = true;
				} catch (Exception e) {
					Log.e(DEBUG_TAG, e.getMessage());
					isSucessfulTrensfer = false;
				}
				return isSucessfulTrensfer;
				

				}				
			}
		}
	
	// 2011-06-07 이미지 전송시 파일명 변환 메서드.
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
	
	
	// 네트워크 연결상태 체크
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
		// 메뉴. 임시저장, 불러오기 기능
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
		
		// 2011-06-01 보낼 그룹 선택시 db에서 그룹정보를 받아와서 locale 에 따라 표시해줌.
		// 첫번째 줄에는 항상 ToMembers가 출력되기 때문에 읽지 않는다.
		for( int i = 1 ; i < size; i++)
		{
			grpIDs[i-1] = grpList.get(i).get( GroupController.GROUP_ID );
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("한") ||
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
				// 2011-06-01 -1을 입력하면 아무것도 선택되지 않은 상태가 됨.
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 각 리스트를 선택했을때 
				// 2011-03-25 그룹 정보를 받아와서 뿌려주는 별도의 로직 필요
				// 2011-04-18 받은 메시지에 한해 그룹 목록을 뿌려줌. 서버에서 전체 그룹 정보를 받아오는 로직 필요.
				// 2011-05-31 그룹정보 구현 완료
				L( "Selected Group ID " + whichButton);
				grpID = grpIDs[whichButton];
				grpName = grpNames[whichButton];
			}
		}).setPositiveButton(R.string.txtOK,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
				stid = "2";
				actvReceiver.setText(grpName);
			}
		}).setNegativeButton(R.string.txtCancel,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Cancel 버튼 클릭시
				
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
	// 전화부에서 번호 선택
	{
		Intent i = new Intent(Intent.ACTION_PICK,  Phone.CONTENT_URI); // ACTION_PICK 액션을 사용하는 예
		//i.setData(Uri.parse("content://com.android.contacts/data/phones"));
		// 2.1에서 사용되던 contacts. 이제 필요없어
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