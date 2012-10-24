package kr.co.drdesign.parmtree.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.SendedMsgController;
import kr.co.drdesign.parmtree.util.ParmUtil;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMsgActivity extends Activity implements OnClickListener, c {

	Button friendBtn;
	Button estBtn;
	Button sendBtn;
	Button etcBtn;
	Button clearBtn;
	
	EditText userText;
	EditText estText;
	EditText contentText;
	EditText titleText;
	
	Intent msgIntent;
	
	String imageUri;
	
	Thread msgThread;
	MsgHandler msgHandler;
	
	String msgUrl;
	Map<String,String> post = new HashMap<String,String>();

	SendedMsgController msgCtrl;
	ParmUtil util;
	

	private final int REQ_CODE_PICK_PICTURE = 1883;
	private final int REQ_CODE_PICK_CAMERA = 1884;
//	private final int REQ_CODE_PICK_FILE = 1886;
//	private final int REQ_CODE_PICK_CONTACTS = 1887;
//	private final int REQ_CODE_MODIFY_ATTACH_LIST = 1888;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_msg);
		
		
		friendBtn = (Button) findViewById(R.id.msgfriendbutton1);
		estBtn = (Button) findViewById(R.id.msgestbutton);
		sendBtn = (Button) findViewById(R.id.sendmsgbtn);
		etcBtn = (Button) findViewById(R.id.attachbtn);
		clearBtn = (Button) findViewById(R.id.clearbtn);
		friendBtn.setOnClickListener(this);
		estBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		etcBtn.setOnClickListener(this);
		clearBtn.setOnClickListener(this);
		
		msgHandler = new MsgHandler();
		msgThread = new Thread(new MsgSender(this, msgUrl));
		
		userText = (EditText) findViewById(R.id.msguserText1);
		estText = (EditText) findViewById(R.id.msgestText2);
		contentText = (EditText) findViewById(R.id.msgeditText3);
		titleText = (EditText) findViewById(R.id.msgtitletext);
		
		if (getIntent() != null) {
			msgIntent = getIntent();
			userText.setText(msgIntent.getStringExtra(USERID));
			titleText.setText(msgIntent.getStringExtra(TITLE));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msgfriendbutton1 :
			picFriend();
			break;
		case R.id.msgestbutton :
			picEst();
			break;
		case R.id.sendmsgbtn :
			send();
			break;
		case R.id.attachbtn :
			showEtcDialog();
			break;
		case R.id.clearbtn :
			Toast.makeText(this, R.string.msgTxtInit, Toast.LENGTH_SHORT).show();
			init();
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		hideKeyboard();
		// 화면 넘어가면 키보드 내리기.
		super.onPause();
	}
	
	/*
	 * custom methods.
	 */

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(userText.getWindowToken(),0);
		inputManager.hideSoftInputFromWindow(estText.getWindowToken(),0);
		inputManager.hideSoftInputFromWindow(contentText.getWindowToken(),0);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(SendMsgActivity.this);
		alert.setTitle(R.string.alert);
		alert.setMessage(getString(R.string.msgTxtBack));
		alert.setNegativeButton(R.string.no, null);
		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		alert.show();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		l.s("result3");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQ_CODE_PICK_PICTURE :
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
					imageUri = cur.getString(0);
					l.s("attImg : " + imageUri);
				}
				break;
			case REQ_CODE_PICK_CAMERA :
				Bundle extras = data.getExtras();
				if (extras != null) {
					Log.i("Gruvice", "getExtras()");
					Bitmap bitmap = extras.getParcelable("data"); // 비트맵이 리턴된다.
					String tempPath = ParmUtil.TEMP + System.currentTimeMillis();
					String ext = ".jpg";
					
					try {
						Log.i("Gruvice", "Stream start!");
						FileOutputStream out = new FileOutputStream(tempPath + ext);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
						imageUri = tempPath + ext;
						post.put(IMAGE, imageUri);
						l.s("attImg : " + imageUri);
						out.close();
						Log.i("Gruvice", "Stream close.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e("Gruvice", e.getMessage());
					}
				}
				break;
			default :
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		File delFile = new File(ParmUtil.TEMP);
		File delList[] = delFile.listFiles();
		if (delFile.exists()) {
			for (int i = 0; i < delList.length; i++) {
				delList[i].delete();
				// 2011-06-07 액티비티 종료시 임시폴더 파일 모두 삭제.
			}
		}
	}

	void init() {
		userText.setText("");
		estText.setText("");
		contentText.setText("");
		imageUri = "";
		post = new HashMap<String,String>();
	}
	
	void send() {		
		if (userText.length() < 2) {
			Toast.makeText(this, 
					R.string.msgTxtUsername, Toast.LENGTH_SHORT).show(); 
			return; 
		} else if (estText.length() < 2) { 
			Toast.makeText(this, 
					R.string.msgTxtEstname, Toast.LENGTH_SHORT).show(); 
			return;
		} else if (titleText.length() < 1) {
			Toast.makeText(this, R.string.msgTxttitle, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (!msgThread.isAlive()) {
			showLoadingDialog();
			msgThread = new Thread(new MsgSender(this, msgUrl));
			msgThread.start();
		}
	}
	
	void cancel() {
		if (msgThread.isAlive()) msgThread.interrupt();
	}
	
	void showEtcDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.attachlink);
		dialog.setItems(new String[]{ "사진첨부", "쿠폰발송", "이벤트알림" }, 
				new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0 :
							picPicture();
							break;
						case 1 : 
							picCoupon();
							break;
						case 2 :
							picEvent();
							break;
						default :
							break;
						}
					}
				})
		.show();
	}

	void picFriend() {
		
	}
	
	void picEst() {
		
	}
	
	void picCoupon() {
		
	}
	
	void picEvent() {
		
	}
	
	void picPicture() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.attachpic)
		.setItems(new String[]{ "기존사진에서 첨부", "새로운 사진"} , 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0 :
							// 기존사진
							Intent i = new Intent(Intent.ACTION_PICK)
							.setType("image/*")
							.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(i, REQ_CODE_PICK_PICTURE);
							break;
						case 1 :
							// 새로운 사진
							startActivityForResult(new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE),
									REQ_CODE_PICK_CAMERA);
							break;
						}
					}
				})
		.show();
	}
	
	void showLoadingDialog() {
		Toast.makeText(getApplicationContext(), R.string.msgTxtSending, Toast.LENGTH_LONG);
	}

	
	void dismiss(boolean result) {
		if (result) {
			Toast.makeText(getApplicationContext(), R.string.msgTxtSuccess, Toast.LENGTH_LONG);
		} else {
			Toast.makeText(getApplicationContext(), R.string.msgTxtFail, Toast.LENGTH_LONG);
		}
	}
	

	public class MsgSender implements Runnable, c {
		
		String url;
		Context c;
		
		public MsgSender(Context c, String url){
			this.c = c;
			this.url = url;
			util = ParmUtil.getInstance(c.getApplicationContext());
			msgCtrl = SendedMsgController.getInstance(c.getApplicationContext());
		}

		public void run() {
			try {
				makePost();
				boolean result = sendMultipartFile(url, post);
				if(result) {
					msgCtrl.insert(post);
					msgHandler.sendEmptyMessage(0);
				} else {
					msgHandler.sendEmptyMessage(1);
				}
				// 2011-04-18 sendMultipartFile 에서 post 객체에 값을 집어넣고 attCtrl 에서 사용.
			} catch ( IOException ioe )	{
				// 밑에서 던지는 예외는 다 여기서 받는다. 이러면 디버깅 하기 힘들텐데..
				l.s(ioe.getMessage());
			}
		}

		public void makePost() {
			post.put(RECEIVER, userText.getText().toString());
			post.put(ESTNAME, estText.getText().toString());
			post.put(CONTENTS, contentText.getText().toString());
			post.put(TITLE, titleText.getText().toString());
			// 쿠폰, 이벤트, 첨부사진 정보는 선택하면 바로 집어넣도록 설계.
		}
		
		public boolean sendMultipartFile( String url, Map<String, String> post) throws IOException
		{

				HttpClient client = new DefaultHttpClient();  
				HttpPost httpPost = new HttpPost(url);
				File file;
				FileBody image1;
//				FileBody fileBody;

				// 이게 실제로 서버로 보내주는 데이터인것 같다.
				// 여기에 조건체크 등으로 데이터를 넣고 서버로 보낸다.
				MultipartEntity reqEntity = new MultipartEntity
								(HttpMultipartMode.BROWSER_COMPATIBLE); 
				
				Set<String> keyset = post.keySet();
				if (keyset == null || keyset.size() == 0 )
				{
					return false;
				}

				if( post.containsKey(SENDER) )
				{
					// 서버에서 안받기때문에 필요없다.
					String ssender = util.getAccountID();
					StringBody sender = new StringBody(ssender);
					reqEntity.addPart(SENDER, sender);
				}
				if( post.containsKey(TITLE) )
				{
					String stitle = post.get(TITLE);
					StringBody title = new StringBody(stitle);
					reqEntity.addPart(TITLE, title);
				}
				if( post.containsKey(CONTENTS) )
				{
					String scontents = new String(post.get(CONTENTS));
					StringBody contents = new StringBody(scontents);
								
					// 여기 계속 고쳐야함.
					reqEntity.addPart("bcontent", contents);
				}
				if( post.containsKey(RECEIVER) )
				{
					String sreceiver = post.get(RECEIVER);
					StringBody members = new StringBody(sreceiver);
					reqEntity.addPart("members", members);
					reqEntity.addPart("groups", members); // 설정하지 않을시 기본값으로 100이 들어간다. 
				}
				
				if( post.containsKey(ESTID) )
				{
					
				}
				
				if (post.containsKey(COUPONID)) {
					
				}
				
				if (post.containsKey(EVENTID)) {
					
				}
				
				if ( post.containsKey(USERID)) {
					
				}
				
				if( post.containsKey(IMAGE) )
				{
					String filepath = post.get(IMAGE);
					if (filepath != null) {
						file = createTempFile(filepath);
						image1 = new FileBody(file);
						reqEntity.addPart(IMAGE, image1);
					}
				}
				// 2011-06-07 이미지에 대해서는 파일명을 바꿔서 보내도 별 문제가 없다.


				StringBody sendermid = new StringBody(util.getAccountID());
				reqEntity.addPart("bsendermid", sendermid);		


				httpPost.setEntity(reqEntity);
				client.execute(httpPost);
				// UTF-8로 갈껄?
				return true;
		}
		
		public File createTempFile(String file) throws FileNotFoundException, IOException  {
			File orgFile = new File(file);
			String tempPath = ParmUtil.TEMP + System.currentTimeMillis();
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
			
			FileInputStream in = new FileInputStream(orgFile);
			File tempFile = new File(ParmUtil.TEMP);
			if (!tempFile.exists()) tempFile.mkdir();
			FileOutputStream out = new FileOutputStream(tempPath + ext, false);
			
			while((i = in.read()) != -1) {
				out.write(i);
			}		
			in.close();
			out.close();

			File newFile = new File(tempPath + ext);
			return newFile;
		
		}
	}
	
	class MsgHandler extends Handler {

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0 :
				dismiss(true);
				break;
			case 1 :
				dismiss(false);
			default :
				break;
			}
		}
		
	}
	
}
