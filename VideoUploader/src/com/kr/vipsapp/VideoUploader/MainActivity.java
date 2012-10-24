package com.kr.vipsapp.VideoUploader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kr.rulubla.VideoUploader.Uploader.ThumbCallBack;
import com.kr.rulubla.VideoUploader.Uploader.ThumbChecker;
import com.kr.rulubla.VideoUploader.Uploader.ThumbResult;
import com.kr.rulubla.VideoUploader.Uploader.ThumbSelectCallBack;
import com.kr.rulubla.VideoUploader.Uploader.ThumbSelector;
import com.kr.rulubla.VideoUploader.Uploader.ThumbUploader;
import com.kr.rulubla.VideoUploader.Uploader.ThumbUploaderCallBack;
import com.kr.rulubla.VideoUploader.Uploader.TransCallBack;
import com.kr.rulubla.VideoUploader.Uploader.TransChecker;
import com.kr.rulubla.VideoUploader.Uploader.TransResult;
import com.kr.rulubla.VideoUploader.Uploader.UploadResult;
import com.kr.rulubla.VideoUploader.Uploader.Uploader;
import com.kr.rulubla.VideoUploader.Uploader.UploaderCallBack;

public class MainActivity extends Activity implements OnClickListener,
UploaderCallBack, ThumbCallBack, TransCallBack, ThumbSelectCallBack, OnItemClickListener,
ThumbUploaderCallBack {

	Button mSelectVideoBtn;
	Button mUploadVideoBtn;
	TextView mSelectVideoName;
	TextView mSelectVideoPath;
	
	Button mSelectMyThumb;
	Button mNoSelectThumb;

	ProgressDialog mSendingDialog;
	ProgressDialog mTransDialog;
	ProgressDialog mThumbSelectDialog;
	ProgressDialog mThumbExtractDialog;
	ProgressDialog mThumbUploadDialog;
	
	LinearLayout mainLayout;
	LinearLayout thumbSelectLayout;
	LinearLayout thumbsLayout;

	String videoPath;
	String videoName;
	String cID;
	
	Uploader mUploader;
	ThumbChecker mThumbChecker;
	TransChecker mTransChecker;
	ThumbSelector mThumbSelector;
	ThumbUploader mThumbUploader;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mainLayout = (LinearLayout) findViewById(R.id.MainLayout);
		mainLayout.setVisibility(View.VISIBLE);
		thumbSelectLayout = (LinearLayout) findViewById(R.id.selectThumbLayout);
		thumbsLayout = (LinearLayout) findViewById(R.id.selectThumbs);
		
		mSelectMyThumb = (Button) findViewById(R.id.btnSelectMyThumb);
		mSelectMyThumb.setOnClickListener(this);
		mNoSelectThumb = (Button) findViewById(R.id.btnNoSelectThumb);
		mNoSelectThumb.setOnClickListener(this);

		mSelectVideoBtn = (Button) findViewById(R.id.btnSelectVOD);
		mSelectVideoBtn.setOnClickListener(this);
		mUploadVideoBtn = (Button) findViewById(R.id.btnUploadVOD);
		mUploadVideoBtn.setOnClickListener(this);

		mSelectVideoName = (TextView) findViewById(R.id.textView1);
		mSelectVideoPath = (TextView) findViewById(R.id.textView2);
	}
	
	private void init() {
		mSelectVideoBtn.setVisibility(View.VISIBLE);
		mUploadVideoBtn.setVisibility(View.GONE);
		mSelectVideoName.setVisibility(View.GONE);
		mSelectVideoPath.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btnSelectVOD:
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.setType("video/*");
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			try {
				startActivityForResult(i, Uploader.SELECT_MOVIE_CODE);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case R.id.btnUploadVOD:
			Upload();
			break;
		case R.id.btnNoSelectThumb:
			TransCheck(cID);
			mTransDialog.show();
			break;
		case R.id.btnSelectMyThumb:
			Intent ii = new Intent(Intent.ACTION_GET_CONTENT);
			ii.setType("image/*");
			ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			try {
				startActivityForResult(ii, Uploader.SELECT_THUMB_CODE);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case Uploader.SELECT_MOVIE_CODE:
				Uri uri = data.getData();
				videoPath = getPath(uri);
				videoName = getName(uri);
				mSelectVideoName.setVisibility(View.VISIBLE);
				mSelectVideoName.setText("파일명 : " + videoName);
				mSelectVideoPath.setVisibility(View.VISIBLE);
				mSelectVideoPath.setText("파일 경로 : " + videoPath);
				mUploadVideoBtn.setVisibility(View.VISIBLE);
				break;
			case Uploader.SELECT_THUMB_CODE:
				Uri thumburi = data.getData();
				uploadThumb(thumburi);
			default:
				break;
			}
			break;
		case RESULT_CANCELED:
			break;
		default:
			break;
		}
	}
	
	private void Upload() {
		mUploader = new Uploader();
		mUploader.setUploaderCallback(this);
		mUploader.setUploadVideo(videoPath);
		mUploader.Upload();
	}
	
	private void uploadThumb(Uri uri) {
		mThumbUploader = new ThumbUploader();
		mThumbUploader.setFilePath(getPath(uri));
		mThumbUploader.setImgPath(mThumbChecker.getResult().getImgPath());
		mThumbUploader.setcID(cID);
		mThumbUploader.setUploadCallBack(this);
		mThumbUploader.execute();
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private String getName(Uri uri) {
		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	
	void TransCheck(String cid) {
		mTransChecker = new TransChecker(cid);
		mTransChecker.setTransferCallBack(this);
		mTransChecker.execute(cid);
	}

	@Override
	public void onUploadStart() {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onUploadStart()");
		mSendingDialog = new ProgressDialog(this);
		mSendingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mSendingDialog.setMessage("동영상을 업로드 하는 중입니다...");
		mSendingDialog.setButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mUploader.cancel(true);
			}
		});
		mSendingDialog.show();
	}

	@Override
	public void onUploadProgress(int progress) {
		// TODO Auto-generated method stub
		Log.i(Uploader.TAG, "onUploadprogress(" + progress + ")");
		mSendingDialog.setProgress(progress);
	}

	@Override
	public void onUploadError(String message) {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onUploadError(" + message + ")");
		if (mSendingDialog.isShowing()) mSendingDialog.dismiss();
		mUploader.cancel(true);
		AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this);
		cancelDialog.setMessage("동영상 전송 중 오류가 발생했습니다. 오류메시지 : " + message);
		cancelDialog.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}
		).show();
	}
	@Override
	public void onUploadCanceled() {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onUploadCanceled()");
	}

	@Override
	public void onUploadCompleted(UploadResult result) {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onUploadCompleted(" + result.getcID() + ")");
		if (mSendingDialog.isShowing()) mSendingDialog.dismiss();
		init();
		cID = result.getcID();
		thumbCheck(cID);
	}
	
	private void thumbCheck(String cid) {
		mThumbChecker = new ThumbChecker(cid);
		mThumbChecker.setThumbCallBack(this);
		mThumbChecker.execute(cid);
	}
	
	private void showThumb(String cid) {
		mainLayout.setVisibility(View.GONE);
		thumbSelectLayout.setVisibility(View.VISIBLE);
		for (int i = 0; i < mThumbChecker.getResult().getBitmapArray().size(); i++) {
			ImageView v = new ImageView(this);
			final int num = i;
			v.setAdjustViewBounds(true);
//			v.setBackgroundDrawable(new BitmapDrawable(mThumbChecker.getResult().getBitmapArray().get(i)));
			v.setImageBitmap(mThumbChecker.getResult().getBitmapArray().get(i));
			v.setPadding(20, 0, 20, 0);
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selectThumb(cID, num);
				}
			});
			thumbsLayout.addView(v);
		}
	}
	
	private void selectThumb(String cid, int seq) {
		mThumbSelector = new ThumbSelector();
		mThumbSelector.setcID(cid);
		mThumbSelector.setNum(seq);
		mThumbSelector.setThumbCallBack(this);
		mThumbSelector.execute();
	}

	@Override
	public void onThumbStart() {
		// TODO Auto-generated method stub
		mThumbExtractDialog = new ProgressDialog(this);
		mThumbExtractDialog.setMessage("썸네일 추출 중입니다...");
		mThumbExtractDialog.setProgress(ProgressDialog.STYLE_SPINNER);
		mThumbExtractDialog.show();
	}

	@Override
	public void onThumbError(String result) {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("썸네일 추출 중 오류가 발생했습니다. 오류메시지 : " + result);
		dialog.setPositiveButton("확인", null);
		dialog.show();
	}

	@Override
	public void onThumbCompleted(ThumbResult result) {
		// TODO Auto-generated method stub		
		if (mThumbExtractDialog.isShowing()) mThumbExtractDialog.dismiss();
		showThumb(cID);
	}

	@Override
	public void onTransProgress(int progress) {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onTransProgress(" + progress + ")");		
		mTransDialog.setProgress(progress);
	}

	@Override
	public void onTransCompleted(TransResult result) {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onTransComplete()");
		if (mTransDialog.isShowing()) mTransDialog.dismiss();
		AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this);
		cancelDialog.setMessage(
				"동영상 변환이 완료되었습니다. 동영상 url : " + result.getVideoURL() + 
				", 썸네일 url : " + result.getThumbURL() + ", cid : " + result.getcID());
		cancelDialog.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mainLayout.setVisibility(View.VISIBLE);
				thumbSelectLayout.setVisibility(View.GONE);
				init();
				finish();
				System.exit(0);
			}
		}
		).show();
	}

	@Override
	public void onTransError(String message) {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onTransError(" + message + ")");	
		mTransDialog.dismiss();
		AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this);
		cancelDialog.setMessage("동영상 변환 중 오류가 발생했습니다. 오류메시지 : " + message);
		cancelDialog.setPositiveButton("확인",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}
		).show();		
	}

	@Override
	public void onTransStart() {
		// TODO Auto-generated method stub
//		Log.i(Uploader.TAG, "onTransStart()");
		mTransDialog = new ProgressDialog(this);
		mTransDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mTransDialog.setMessage("동영상 변환 중입니다...");
		Toast.makeText(this, "동영상 변환을 시작합니다.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.selectThumbs:
			selectThumb(cID, arg2);
			break;
		}
	}

	@Override
	public void onSelectStart() {
		// TODO Auto-generated method stub
		mThumbSelectDialog = new ProgressDialog(this);
		mThumbSelectDialog.setMessage("선택한 썸네일을 적용 중입니다..");
		mThumbSelectDialog.setProgress(ProgressDialog.STYLE_SPINNER);
		mThumbSelectDialog.show();
	}

	@Override
	public void onSelectComplete() {
		// TODO Auto-generated method stub
		if (mThumbSelectDialog.isShowing()) mThumbSelectDialog.dismiss();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("썸네일 적용이 완료되었습니다.");
		dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mTransDialog.show();
			}
		});
		// 이건 그냥 던져놓기만 하고
		TransCheck(cID);
		mTransDialog.show();
		mainLayout.setVisibility(View.VISIBLE);
		thumbSelectLayout.setVisibility(View.GONE);
	}

	@Override
	public void onSelectError(String message) {
		// TODO Auto-generated method stub
		mThumbSelectDialog.dismiss();
	}

	@Override
	public void onThumbUploadStart() {
		// TODO Auto-generated method stub
		mThumbUploadDialog = new ProgressDialog(this);
		mThumbUploadDialog.setMessage("썸네일 업로드 중입니다..");
		mThumbUploadDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
		mThumbUploadDialog.show();
	}

	@Override
	public void onThumbUploadError(String message) {
		// TODO Auto-generated method stub)
		mThumbUploadDialog.dismiss();
	}

	@Override
	public void onThumbUploadProgress(int progress) {
		// TODO Auto-generated method stub
		mThumbUploadDialog.setProgress(progress);		
	}

	@Override
	public void onThumbUploadComplete() {
		// TODO Auto-generated method stub
		if (mThumbUploadDialog.isShowing()) mThumbUploadDialog.dismiss();
		TransCheck(cID);
		mTransDialog.show();
	}

}