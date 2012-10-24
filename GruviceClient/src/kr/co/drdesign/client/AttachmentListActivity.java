package kr.co.drdesign.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import kr.co.drdesign.client.connector.HttpConnector;
import kr.co.drdesign.client.connector.HttpsConnector;
import kr.co.drdesign.client.connector.WebConnector;
import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import kr.co.drdesign.util.StorageStatus;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AttachmentListActivity 
extends Activity
implements Loggable
{
	public String id;
	public String password;

	// 10-08-30 notificationId가 쭉 증가하다가 간혹 메모리등의 이유로 AttachmentListActivity 가  GC 되는 경우
	// 0이 될 수 있다. 만약 이런 경우가 발생한다면.. 아래 구문을 수정하거나 DownloadThread에서 해당 Activity 를 참조 하도록 해서
	// GC 되지 않도록 해야한다. 동작하는 DownloadThread가 없다면 0이되어도 무관하다.
	public static int notificationId = 0;

	//view
	private RelativeLayout snmRl01;
	private ListView lvFileList;
	private Drawable sBackGround;

	// List for Attach file
	public ArrayList <Map<String, String>> attachFileList = new ArrayList<Map<String, String>>();
	// Checked List Set
	public Set<Integer> checkedSet = new TreeSet<Integer>();
	public Set<Integer> downSet = new TreeSet<Integer>();

	public static final String INDEX 			= "INDEX";
	public static final String NOTIFICATION_ID 	= "NOTIFICATIONID";
	public static final String ACTION_CANCEL 	= "CANCEL";
	public static final String ACTION_DOWNLOAD 	= "DOWNLOAD";
	private static final String fileStorePath 	= GruviceUtillity.DATA_STORAGE; // 첨부파일 저장경로

	private static AttachController attCtrl;
	private static ReceiveMsgController msgCtrl;
	private String uID;
	private long availableSize;
	
	private static Handler msgHandler;

	public static Map<Integer, FileDownloadNotification> fileDownloderMap = new HashMap<Integer, FileDownloadNotification>();

	public String SKINTYPE;

	private static final SimpleDateFormat TIMEFORMAT = new SimpleDateFormat("[MM-dd HH:mm:ss]");
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		
		if( msgCtrl == null ) msgCtrl = ReceiveMsgController.getInstance(getApplicationContext());
		if( attCtrl == null ) attCtrl = AttachController.getInstance(getApplicationContext());

		// 
		if ( ACTION_CANCEL.equals(intent.getAction()) )
		{
			Integer notiId = intent.getIntExtra(NOTIFICATION_ID, -1); // 노티피케이션 제거
			FileDownloadNotification filedownloader = fileDownloderMap.get( notiId );
			filedownloader.cancel(); // 파일전송 취소
			finish();
		}

		// 2011-05-19 외장메모리가 없는 경우에 대한 핸들링. 갤럭시s2 등 삼성단말기는 외장메모리를 안준다고 한다. 망할
		
		int usage;
		if (StorageStatus.externalStorageAvailable()) {
			availableSize = StorageStatus.getAvailableExternalStorageSize();
			usage =(int)(100 * availableSize/StorageStatus.getTotalExternalStorageSize());
		} else {
			Toast.makeText(this, 
					R.string.mnTxtStorageMessage, Toast.LENGTH_LONG)
			.show();
			availableSize = StorageStatus.getAvailableInternalStorageSize();
			usage =(int)(100 * availableSize/StorageStatus.getTotalInternalStorageSize());
		}
		
		msgHandler = new Handler() {
			// 2011-04-28 파일 다운로드 오류 발생시 오류메시지 출력을 위한 핸들러
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0 : Toast.makeText(getBaseContext(), 
						R.string.altxtFileNotFound, Toast.LENGTH_SHORT).show();
					break;
				case 1 : Toast.makeText(getBaseContext(), 
						R.string.dnsTxtDownloadComplete, Toast.LENGTH_SHORT).show();
					break;
				case 2 : Toast.makeText(getBaseContext(), 
						R.string.altxtUnfinishDownload, Toast.LENGTH_SHORT).show();
					break;
				// 2011-05-25 저장공간 부족시 메시지 출력
				case 3 : Toast.makeText(getBaseContext(),
						R.string.altxtNotEnoughStorage, Toast.LENGTH_SHORT).show();
					break;
				default :
					break;
				}
			}
		};
		
		
		//Log.i("intent check", intent.getAction());
		if ( ACTION_DOWNLOAD.equals(intent.getAction()) )
		{
			String UID = intent.getStringExtra(AttachController.UID);
			Map<String, String> map = attCtrl.getAppByUID(UID);

			NotificationManager notify = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			
			FileDownloadNotification fileDownloaderThread = new FileDownloadNotification(notify, notificationId, map );

			if( fileDownloderMap.containsValue( fileDownloaderThread ) == false )
			{
				L("download Start");
				fileDownloderMap.put( notificationId , fileDownloaderThread ); 
				notificationId++;
				new Thread( fileDownloaderThread ).start();
			}
			finish();
		}

		setContentView(R.layout.attachment_list);
		String[] SKIN_TYPES = GruviceUtillity.getInstance(getApplicationContext()).getSKIN_TYPES();
		String skinType = GruviceUtillity.getInstance(getApplicationContext()).getSkinType();
		if( skinType.equalsIgnoreCase(SKIN_TYPES[0]) == true)
		{
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		}else  
		{
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);
		}

		snmRl01 = (RelativeLayout) findViewById(R.id.snmRl01);
		snmRl01.setBackgroundDrawable(sBackGround);

		lvFileList = (ListView)findViewById(R.id.alLvFileList);
		lvFileList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		uID = intent.getStringExtra(ReceiveMsgController._id);
		TextView tvStorageStatus = (TextView)findViewById(R.id.alTvStorageSatus);
		
		

		String temp = StorageStatus.formatSize(availableSize);
		temp = getString(R.string.altxtDataStorage) + " : " + temp + " " + usage + "%"; 
		tvStorageStatus.setText( temp );
		
		// 2011-05-25 여기서 서비스를 시작할 필요가 없다.
//		startService(new Intent(getApplicationContext(), DRMService.class));
		fillFileList();
		showNoAttachFile();
		showStorageAlert(usage);
	}

	private void showNoAttachFile()
	{
		if( attachFileList != null && attachFileList.size() == 0 ){ 
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle( R.string.txtAlret );
			alert.setMessage(  R.string.altxtNoAttachFile );

			alert.setCancelable(false);
			alert.setPositiveButton( R.string.txtDone, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int which) {
					dialog.dismiss();   //닫기
					finish();
				}
			});
			alert.show();
		}
	}
	
	private void showStorageAlert(int i) {
		if ( i < 1 ) { 
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.txtAlret)
			.setMessage(R.string.altxtStorage10)
			.setPositiveButton(R.string.txtOK, null)
			.show();			
		} else if ( i < 10 ) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.txtAlret)
			.setMessage(R.string.altxtStorage1)
			.setPositiveButton(R.string.txtOK, null)
			.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( sBackGround != null ) sBackGround.setCallback(null);
	}

	public void onClickDownloadFile(View view)
	{
		if ( checkedSet.size() != 0 )
		{
			Map<String, String> map;
			Iterator<Integer> it = checkedSet.iterator();
//			Iterator<Integer> it = downSet.iterator();
			// 2011-04-28 DB에서는 _id로 처리하게 해놓고는 여기서 리스트 인덱스를 쓰면 어쩌잔거지?
			// 일단 임의로 리스트 인덱스랑 id 를 따로 처리하게끔 수정.

			NotificationManager notify = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			FileDownloadNotification fileDownloaderThread ;
			
			L("onclickDownloadFile()");
			while(it.hasNext())
			{
				map = attachFileList.get(it.next());

				fileDownloaderThread = new FileDownloadNotification(notify, notificationId, map );
				if( fileDownloderMap.containsValue( fileDownloaderThread ) == false)
				{
					fileDownloderMap.put( notificationId , fileDownloaderThread ); 
					notificationId++;
					L("fileDownloader.start()");
					new Thread( fileDownloaderThread ).start();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillFileList();
	}

	//Fill Attachfile List.
	private int fillFileList(){
		L( "fillFileList()");
		attachFileList = attCtrl.getAttachByUIDWithoutAPP(uID);
		

		SimpleAdapter adapter = new SimpleFileListAdapter(this, attachFileList, R.layout.attachment_item, 
				new String[]{AttachController.NAME, INDEX},
				new int[]{R.id.alAttachName, R.id.alCtvItem}
		);

		lvFileList.setAdapter(adapter);
		return attachFileList.size();
	}

	class SimpleFileListAdapter extends SimpleAdapter{
		private List<Map<String, String>> list;

		private TextView tvFileName;
		private TextView tvAttachMsgInfo;
		private CheckedTextView ctvItem;
		private Button btnRunApp;
		private String savepath;

		public SimpleFileListAdapter(Context context, 
				List<Map<String, String>> list, int resource, String[] from, int[] to)
		{
			super(context, list, resource, from, to);
			this.list = list;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
		// 2011-04-20 여기서 파일첨부 리스트를 생성하여 뿌려줌
			
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.attachment_item, null);
			}

			tvFileName = (TextView)view.findViewById(R.id.alAttachName);
			tvAttachMsgInfo = (TextView)view.findViewById(R.id.alAttachMsgInfo);
			btnRunApp = (Button)view.findViewById(R.id.alBtnRunAPP);

			final String _id = list.get(position).get(AttachController._id);
			final String uid = list.get(position).get(AttachController.UID);

			String uRL = list.get(position).get(AttachController.URL);
			String fileName = list.get(position).get(AttachController.NAME);
			final String mimeType = list.get(position).get(AttachController.TYPE);
			L("casting Long error : " + list.get(position).get(AttachController.LENGTH));
			Long length = Long.parseLong( list.get(position).get(AttachController.LENGTH) );

			L( "_id = " + _id );
			L( "uid = " + uid );
			L( "URL = " + uRL );
			L( "fileName = " + fileName );
			L( "mimeType = " + mimeType );
			L( "length = " + length );

			ctvItem = (CheckedTextView)view.findViewById(R.id.alCtvItem);
			ctvItem.setChecked(false);

			// If download file is existed, This code process. 
			savepath =  list.get(position).get(AttachController.SAVEPATH);
			final Integer index = position; 
			//Integer.parseInt( list.get(position).get(INDEX) );
			//final Integer index = Integer.parseInt( list.get(position).get(_id) );

			Map<String, String> msg = msgCtrl.getMsgbyUID(uid);
			// uid 로 db에서 첨부파일 목록을 가져옴
			if( msg != null && msg.size() > 0){
				String sender = msg.get(ReceiveMsgController.SENDER);
				String createDate = msg.get(ReceiveMsgController.CREATEDATE);
				tvAttachMsgInfo.setText( sender + " / " + TIMEFORMAT.format(new Date(new Long(createDate))) );
			}
			if( savepath != null && savepath.contains(".apk")){
				L("SAVEPATH : " + savepath);
				btnRunApp.setVisibility(View.VISIBLE);
				final File file = new File(savepath).getAbsoluteFile();
				btnRunApp.setOnClickListener(new View.OnClickListener() {

					public void onClick(View view) {
						// 2011-04-20 첨부파일목록에서 버튼 클릭시 실행되는 부분
						// savepath에 잘못된 값이 들어 있는 경우에 처리해주는 코드를 만들어야 한다.
						Uri uri = Uri.fromFile(file);
						L(file.getName());
						Intent intent = new Intent();
						intent.setData(uri);
						startActivity(intent);
					}
				});
				ctvItem.setVisibility(View.GONE);
				checkedSet.remove(index);
			}else
			{
				btnRunApp.setVisibility(View.GONE);
				ctvItem.setVisibility(View.VISIBLE);
			}

			String filename = list.get(position).get(AttachController.NAME);
			tvFileName.setText(filename);
			if( savepath != null && savepath.length() > 1 )
			{
				// 2011-04-20 첨부파일명 롱클릭시 실행되는 부분
				tvFileName.setOnLongClickListener( new View.OnLongClickListener() {
					public boolean onLongClick(View v) {
						AlertDialog.Builder alert = new AlertDialog.Builder(AttachmentListActivity.this);

						alert.setTitle( R.string.txtAlret );
						alert.setMessage(  R.string.altxtDelete );
						alert.setPositiveButton( R.string.txtYES, new DialogInterface.OnClickListener() {
							public void onClick( DialogInterface dialog, int which) {
								try{
									new File( savepath ).delete();
								}catch( Exception e )
								{
									Log.e("DR", e.getMessage());
								}
								attCtrl.setSavePath(_id, null);
								fillFileList();
							}
						});
						// 2011-04-20 빈 클릭리스너는 왜 넣어놓는건지....
						alert.setNegativeButton( R.string.txtNO, null);
						alert.show();
						return false;
					}
				});
			}

//			if( checkedSet.contains(index) )
//				ctvItem.setChecked(true);
//			else
//				ctvItem.setChecked(false);

			//ctvItem.setText(_id);
			savepath = fileStorePath+fileName;

			
			ctvItem.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					((CheckedTextView)view).toggle();
					if(((CheckedTextView)view).isChecked()){
//							checkedSet.add(index);
						L("Checked " + Integer.toString(position));
						checkedSet.add(position);
						downSet.add(position);
					}else{
//							checkedSet.remove(index);
						L("removed " + Integer.toString(position));
						checkedSet.remove(position);
						downSet.remove(position);
					}
				}
			});

			//savePathList에 값이 있으면 버튼을 인비즈블한다.
			return view ;
		}
		
	}
	
	
	// Actually This Class will Download File And Notify Notification. 
	// 2011-04-20 파일 다운로드시 노티 출력
	public class FileDownloadNotification implements Runnable
	{
		private NotificationManager notify;
		private Notification notification;
		private RemoteViews contentView;
		private int notificationID;

		private BufferedInputStream bi;
		private BufferedOutputStream bo;

		private String _id;
		private String uid;
		private String url;
		private String fileName;

		private int fileLength;
		private int readSize;
		private int readedSize = 0;
		private boolean isCancel = false;
		private long priviousTime=0;
		private String saveFilePath;
		
		public FileDownloadNotification(NotificationManager notify, int notificationID, 
				Map<String, String> map) {
			
			this.notify = notify;
			this.notificationID = notificationID;

			_id = map.get( AttachController._id );
			uid = map.get( AttachController.UID );
			url = map.get( AttachController.URL );
			fileName = map.get( AttachController.NAME );
			fileLength = Integer.parseInt( map.get( AttachController.LENGTH ));
			
			L("Filename = " + fileName);
			
			// 생성자. 파일 다운로드에 필요한 각종 정보를 가지고 있음.
		}

		public void setCancel(){
			isCancel = true;
		}

		public void run()
		{
			// 2011-05-25 다운로드시 디스크 용량 부족할시 다운로드 취소 및 메시지 출력
			L("fileLength : " + fileLength + ", availableSize :" + availableSize);
			if (fileLength > availableSize ) {
				L("Not Enough Size!!");
				mHandler.sendEmptyMessage(3);
				return;
			}
			
			L( "DOWNLOAD FILE : " + uid + " : " + url );
			setWebConnection( url );

			saveFilePath = fileStorePath + uid + "/" + fileName;
			setFileConnection( saveFilePath );
			L( "savePath : " + saveFilePath );

			notification = new Notification(R.drawable.icon, fileName + " " + R.string.dnsTxtDownloadStart , System.currentTimeMillis());

			//halftale :추후 Cancel Dialog 부분이 들어가야  할  부분.
			//현재(10-09-06)는 Notification을 클릭하면 Cancel이 된다. 
			Intent notificationIntent = new Intent(getApplicationContext(), AttachmentListActivity.class);
			notificationIntent.setAction(ACTION_CANCEL);
			notificationIntent.putExtra(NOTIFICATION_ID, notificationID );

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), notificationID, notificationIntent, 0);
			notification.contentIntent = contentIntent;			

			contentView = new RemoteViews(getPackageName(), R.layout.download_status_progress);
			contentView.setImageViewResource(R.id.ntcIvIcon, R.drawable.icon);
			contentView.setTextViewText(R.id.ntcTvTitle, "FileDownload : " + fileName);
			contentView.setProgressBar(R.id.ntcPbDownload, 0, 0, true);
			notification.contentView = contentView;

			// bi가 null 이거나 하면 생기면 안됨.
			// 2011-04-28 서버에 파일이 존재하지 않을시 알림 및 예외처리
			if (bi != null) {
				notify.notify(notificationID, notification);				

				byte[] buffer = new byte[1024*16];

				try {
					while( (readSize = bi.read(buffer)) != -1 
							&& isCancel == false)
					{
						bo.write(buffer, 0, readSize );
						readedSize += readSize;
						notifyProgressBy( readedSize );
					}
					if ( isCancel == false){
						notifyfinish();
						attCtrl.setSavePath( _id, saveFilePath);
						mHandler.post(updateResults);
					}
					bo.close();
					bi.close();
				} catch (IOException e) {
					//e.printStackTrace();
					//halftale 08-30
					//IF broken Stream, Set Error Message.
					L("file download error");
					notification.contentView.setTextViewText(R.id.ntcTvTitle, "Error");
					notify.notify(notificationID, notification);
					msgHandler.sendEmptyMessage(2);
					// 2011-04-29 오류 발생시 생성된 파일 제거
					if (new File(saveFilePath).exists()) {
						new File(saveFilePath).delete();
						L("file is exists. delete it.");
					}
				}	
			} else {
				msgHandler.sendEmptyMessage(0);
				L("input Buffer is null");
				// 2011-04-28 오류메시지 토스트 출력
				// 한번 눌렀을땐 뜨는데 다시누르면 안뜨는거 해결해야할것 같다.
			}
			
		}

		private void notifyProgressBy(int readedSize)
		{
			long presentTime = System.currentTimeMillis();
			if ( presentTime - priviousTime < 1000 ) return;
			priviousTime = presentTime;
			contentView.setProgressBar(R.id.ntcPbDownload, fileLength, readedSize, false);
			notification.contentView = contentView;
			notify.notify(notificationID, notification);
		}

		private void notifyfinish( )
		{
			// Intent 변경해야함.( 파일 실행할 수 있도록 )
			// 파일 실행이 바로 되도록 하는 코드는 아래를 수정해서 할 것.
			//mimeType = list.get(position).get(AttachController.TYPE);
			//Uri uri = Uri.fromFile(file);
//			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
			// 2011-04-29 이제 uri 만 어디서 가져오면 파일 실행이 바로 될것이다.
			//Log.i("DR", "mimeType : " + mimeType);
			//intent.setDataAndType(uri, mimeType);
//			startActivity(intent);
			

			// 2010-08-27 : 다운로드 완료후에 Progress 바를 삭제하고, 파일명 /  다운로등가 완료 되었습니다.류의 메시지를 보여주자.
			// 다운로드가 완료되면 Attach LIst 의 목록을 갱신해야 하는데... 이건 어쩌나..?
			// 아니면 Attach List를 finish() 해버리던지..
			Intent notificationIntent;
			if( fileName.contains(".apk") )
				// 노티피케이션 클릭시 앱이면 설치하고 아니면 첨부파일 목록으로 이동한다.
			{					
				notificationIntent = new Intent(Intent.ACTION_VIEW);
				File apkFile = new File(saveFilePath);
				notificationIntent.setDataAndType( Uri.fromFile(apkFile), "application/vnd.android.package-archive");
				//APP 저장
				int cnt = ReceiveMsgController.getInstance(getApplicationContext()).updateAPPPath( uid, saveFilePath);
				L( "UPDATE APP PATH UID = " + uid + " APP_PATH = " + saveFilePath + " CNT = " + cnt);
			}else {
				notificationIntent = new Intent(getApplicationContext(), AttachmentListActivity.class);	
			}

			PendingIntent contentIntent = 
				PendingIntent.getActivity(getApplicationContext(), notificationID, notificationIntent, 0);
			notification.contentIntent = contentIntent;

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			contentView.setProgressBar(R.id.ntcPbDownload, 100, 100, false);
			notification.contentView = contentView;
			notify.notify(notificationID, notification);

			fileDownloderMap.remove(notificationID);


			// 2010-08-31 아래 구문은 다운로드된 파일이 다시 다운로드되는 것을 막아준다. 
//			for(int i = 0 ; i < attachFileList.size() ; i++ )
//			{
//				Map<String, String> map = attachFileList.get(i);
//				String _id = map.get(AttachController._id);
//				if( this._id.equals(_id) == true ) 
//				{
//					downSet.remove(i);
//					break;
//				}
//			}
			L( "DOWNLOAD FILE FINISH. " + fileName );
			
			msgHandler.sendEmptyMessage(1);
			// 2011-04-28 다운로드 완료시 알림
		}

		private void setWebConnection( String url )
		{
			WebConnector conn;
			// https 보안 적용 여부에 따라 연결방식 설정
			if( url.toLowerCase().startsWith("https") ){
				conn = new HttpsConnector();
				L( "create HttpsConnector()");
			}else
			{
				conn = new HttpConnector();
				L( "create HttpConnector()");
			}
			try {
				bi = conn.getBufferedInputStreamFromURLwithoutSession( url, id, password, null );
				//여기
			}catch( Exception e){
				Log.e("DR", e.getMessage() );
			}
		}

		private void setFileConnection( String saveFilePath )
		{
			File f = new File(fileStorePath + uid + "/");
			if ( f.exists() == false )
			{
				f.mkdir();
			}

			File saveFile = new File ( saveFilePath  );
			try {
				FileOutputStream fos = new FileOutputStream( saveFile );
				bo = new BufferedOutputStream( fos );
			} catch (FileNotFoundException e) {
				Log.e(DEBUG_TAG, e.getMessage());
			}
		}

		public void cancel()
		{
			isCancel = true;
			notify.cancel(notificationID);
			fileDownloderMap.remove(notificationID);
			L( "FileDownload Cancel : " + fileName );
		}

		@Override
		public boolean equals(Object o) {
			//Java 문법 상으로 매우 이상한 부분이네요.. 
			// 그러게요.. Object 클래스 자체에 equals 메서드가 분명히 있을텐데...
			String objectId = ((FileDownloadNotification)o)._id;
			if( _id != null && _id.equals(objectId) ) return true;
			else return false;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.al_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		switch( item.getItemId() )
		{
		case R.id.alDownload:
			onClickDownloadFile(null);
			break;
		case R.id.alDelete:
			// 일단 첨부파일 목록을 DB에서 삭제하는 부분.
//			String path;
			for( int i : checkedSet )
			{
				// 2011-04-28 실제파일을 지우는 부분같으나 일단 제외. 다른 형태로 사용해야 할것 같은데..
//				String path = attachFileList.get(i).get(AttachController.SAVEPATH);
//				try{
//					new File( path ).delete(); - 널입니다요.
//				}catch( Exception e )
//				{
//					L(e.getMessage());
//					Log.e("DR", e.getMessage());
//				}
				L("checkedSet position : " + i);
				// 2011-05-26 첨부파일 목록 삭제 및 예외처리
				if (i < checkedSet.size()) attCtrl.delete(attachFileList.get(i).get(AttachController.UID));
				L("DELETE ITEM : " + Integer.toString(i));
				// 2011-04-28 비교값을 Integer 에서 int 로 변경.
			}
			checkedSet.clear();
			fillFileList();
			// 2011-04-28 나름 리스트뷰 갱신...
			// 2011-04-28 정상동작 확인. 서버에서도 삭제해야 되는데 이건 어쩌나. 구대리님 헬프..
			showNoAttachFile();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// Update Result when Download File.
	private Handler mHandler = new Handler();
	private Runnable updateResults = new Runnable() {
		public void run () {
			fillFileList();
		}
	};

	//JUST Method for LOG.
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
	//JUST Method for LOG.
	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}
