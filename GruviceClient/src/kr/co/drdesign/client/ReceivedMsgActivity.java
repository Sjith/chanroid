package kr.co.drdesign.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.util.APKAnalyzer;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ReceivedMsgActivity 
extends MsgActivity 
implements OnClickListener
{
	private Button btnAppInstall;
	private Button btnDownLoadFile;
	private Button btnMovieClip;
	private Button btnImageList;

	private String movieClipUrl;
	

	private ArrayList<Map<String, String>> attachFileList;
	private Map<String, String> APKfileItem;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Map<String, String> msgInfo = msgCtrl.getMsg(_id);

		_id 		= msgInfo.get(ReceiveMsgController._id);
		UID 		= msgInfo.get(ReceiveMsgController.UID);
		TITLE 		= msgInfo.get(ReceiveMsgController.TITLE);
		CONTENTS 	= msgInfo.get(ReceiveMsgController.CONTENTS);
		APPPATH 	= msgInfo.get(ReceiveMsgController.APPPATH);
		ATTACHMENTS	= msgInfo.get(ReceiveMsgController.ATTACHMENTS);
		READED 		= msgInfo.get(ReceiveMsgController.READED);
		GROUP_ID 	= msgInfo.get(ReceiveMsgController.GROUP_ID);
		ETC 		= msgInfo.get(ReceiveMsgController.ETC);
		L("sender : " + SENDER);
		SENDER 		= msgInfo.get(ReceiveMsgController.SENDER);
		CREATEDATE 	= msgInfo.get(ReceiveMsgController.CREATEDATE);
		RECEIVEDATE	= msgInfo.get(ReceiveMsgController.RECEIVEDATE);

		tvTitle = (TextView) findViewById(R.id.msgTvTitle);
		if( TITLE != null)
		tvTitle.setText(TITLE.trim());
		tvTitle.setSelected(true);

		L("_id = " + _id);
		L("UID = " + UID);
		L("TITLE = " + TITLE);

		//halftale 100817 - NewMsg has DarkGray Backgournd.
		//		if( "0".equals(READED)) 
		//		{
		//			tv.setBackgroundColor(Color.DKGRAY);
		//		}

		showDetailInfo();
		if ( CONTENTS == null || CONTENTS.length() == 0)
			CONTENTS = "text_\nNo Contents";
		parsingSavedMsg(CONTENTS);
		
		attachFileList = 
			AttachController.getInstance(getApplicationContext()).getAttachesByUID(UID);

		try {
			for( Map<String, String> item : attachFileList)
			{
				file = attachFileList.get(0).get(AttachController.URL);
				if( item.get( AttachController.NAME ).contains(".apk") || item.get( AttachController.NAME ).contains(".APK") )
				{
					APKfileItem = item;
					attachFileList.remove(item);
				}
			}
		} catch ( ConcurrentModificationException e) {
			e.printStackTrace();
			Log.i("mqtt", "C.M.E!!!");
		}


		btnAppInstall = new Button(this);
		btnAppInstall.setBackgroundResource(R.drawable.v2_btn_bg);
		btnAppInstall.setTextColor(Color.WHITE);

		//설치할 APP가 없으면 버튼을 Invisible 시킴.
		if( APPPATH != null && APPPATH.trim().length() != 0 ) 
		{
			String packageNameFromAPK = null;
			try {
				packageNameFromAPK = APKAnalyzer.findPackageNameFromAPK(APPPATH);
				L( "packageNameFromAPK is " + packageNameFromAPK);
				btnAppInstall.setText(R.string.msgBtnAppInstall);
				ll01.addView(btnAppInstall);
				btnAppInstall.setOnClickListener(this);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (APKfileItem != null )
		{
			btnAppInstall.setText(R.string.msgBtnAppInstall);
			ll01.addView(btnAppInstall);
			btnAppInstall.setOnClickListener(this);
		}

		btnDownLoadFile = new Button(this);
		btnDownLoadFile.setBackgroundResource(R.drawable.v2_btn_bg);
		btnDownLoadFile.setTextColor(Color.WHITE);

		if( attachFileList.size() == 0 ) 
		{
			btnDownLoadFile.setVisibility(View.INVISIBLE);
		}
		else{
			btnDownLoadFile.setText(R.string.msgBtnAttachment);
			ll01.addView(btnDownLoadFile);
			btnDownLoadFile.setOnClickListener(this);
		}

		((ReceiveMsgController)msgCtrl).markReaded(_id);

		int i_id = Integer.parseInt(_id);
		removeNotification(i_id);

		//halftale 결제 페이지 ( 삭제해야함 )
		viewSample();

		msgView.setOnClickListener(this);
		mGestureDetector = new GestureDetector(this, new LinearGestureListener());
	}

	private void viewSample() {
		L(" viewSample() ");
		if (ETC == null || ETC.length() == 0) return;

		String code = "";
		//code == 1 이북
		//code == 2 상품
		//code == 3 이벤트
		String price = "";
		String name = "";
		StringBuilder detail = new StringBuilder();

		StringTokenizer st = new StringTokenizer(ETC);
		String token = "";
		while( st.hasMoreTokens() )
		{
			token = st.nextToken();
			if ( token.equalsIgnoreCase("code_") )
			{
				code = st.nextToken();
			}
			else if ( token.equalsIgnoreCase("price_") )
			{
				price = st.nextToken();
			}
			else if ( token.equalsIgnoreCase("name_") )
			{
				name = st.nextToken();	
			}
			else if ( token.equalsIgnoreCase("detail_") )
			{
				detail.append(st.nextToken());
			}else
			{
				detail.append(" ");
				detail.append(token);
			}
		}
		L( "code " + code);
		L( "price " + price);
		L( "name " + name);
		L( "detail " + detail);

		//?? 멍미?
		final String fname = name;
		final String fdetail = detail.toString();
		final String fprice = price;

		if( code.equals("2") ) // 상품
		{
			Button btnBuy = new Button(this);
			btnBuy.setText("구매");
			btnBuy.setBackgroundResource(R.drawable.v2_btn_bg);
			btnBuy.setTextColor(Color.WHITE);
			ll01.addView(btnBuy);

			btnBuy.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View view) 
				{
					Intent ibuy = new Intent( getApplicationContext(), FakeBuyPage.class );
					ibuy.putExtra("name", fname);
					ibuy.putExtra("detail", fdetail);
					ibuy.putExtra("price", fprice);

					startActivity(ibuy);
				}
			});
		}
		if( code.equals("3") ) // 이벤트
		{
			Button btnBuy = new Button(this);
			btnBuy.setText("참가신청");
			btnBuy.setBackgroundResource(R.drawable.v2_btn_bg);
			btnBuy.setTextColor(Color.WHITE);
			ll01.addView(btnBuy);

			btnBuy.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ReceivedMsgActivity.this);

					alert.setTitle( "참가신청" );
					alert.setMessage(  "참가신청하시겠습니까?" );

					alert.setCancelable(false);
					alert.setPositiveButton( "네", new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which) {
							AlertDialog.Builder alert = new AlertDialog.Builder(ReceivedMsgActivity.this);

							alert.setTitle( "참가신청" );
							alert.setMessage(  "참가신청이 완료되었습니다." );
							alert.setPositiveButton("완료", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();   //닫기
								}
							}
							);
							alert.show();
							dialog.dismiss();
						}
					});
					alert.setNegativeButton( "아니요", new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int which) {
							dialog.dismiss();   //닫기
						}
					});

					alert.show();		
				}
			});
		}
		if( code.equals("1")) 
		{
			Button btnBuy = new Button(this);
			btnBuy.setBackgroundResource(R.drawable.v2_btn_bg);
			btnBuy.setTextColor(Color.WHITE);
			btnBuy.setText("구매");

			ll01.addView(btnBuy);

			btnBuy.setOnClickListener(new View.OnClickListener() {

				public void onClick(View arg0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(ReceivedMsgActivity.this);

					alert.setTitle( "이북구매" );
					alert.setMessage(  fname + " - 구매하시겠습니까?" );

					alert.setCancelable(false);
					alert.setPositiveButton( "네", new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which) {
							AlertDialog.Builder alert = new AlertDialog.Builder(ReceivedMsgActivity.this);

							alert.setTitle( "구매완료" );
							alert.setMessage(  "구매신청이 완료되었습니다." );
							alert.setPositiveButton("완료", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();   //닫기

								}
							}
							);
							alert.show();
							dialog.dismiss();
						}
					});
					alert.setNegativeButton( "아니요", new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int which) {
							AlertDialog.Builder alert = new AlertDialog.Builder(ReceivedMsgActivity.this);

							alert.setTitle( "취소" );
							alert.setMessage(  "구매신청이 취소되었습니다." );
							alert.setPositiveButton(R.string.txtDone, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();   //닫기
								}
							});

							dialog.dismiss();   //닫기
						}
					});

					alert.show();		
				}
			});
		}
	}

	private void removeNotification( int msg_id )
	{
		NotificationManager notifier = 
			(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 
		notifier.cancel(msg_id);
		L("removeNotification(" + msg_id + ")");
	}

	protected void parsingSavedMsg(String msg) {
		L("parsingSavedMsg()");
		Scanner sc = new Scanner( msg );

		String tokens = "";
		boolean isText = true;
		boolean isImg = false;
		boolean isMov = false;
		
		int index = 0;
		
		while ( sc.hasNext() )
		{
			// if tokens is EmptyString or '\n', tokens is ignored.
			tokens = sc.nextLine();
			if( tokens != null && tokens.trim().length() < 1 ) continue;

			if ( "text_".equals( tokens )) {
				isText = true;
				isImg = false;
				isMov = false;
			} else if( "img_".equals( tokens )){
				isText = false;
				isImg = true;
				isMov = false;
			} else if( "mov_".equals( tokens )){
				isText = false;
				isImg = false;
				isMov = true;
			} else {
				if ( isText )
				{
					showTxt( tokens +"\n" );
				}else if( isImg )
				{
					L("showImg : " + tokens);
					try {
						showImg( tokens );
						L("Imgindex : " + index);
						image[index] = tokens;
						index++;
					} catch (NullPointerException e) {
						L("showImg error : " + e.getMessage());
					} catch (ArrayIndexOutOfBoundsException be) {
						L("showImg error : " + be.getMessage());						
					}
				}else if( isMov )
				{
					showMov( tokens );
					movieClipUrl = tokens.trim();
					L("Movie CLIP URL = " + movieClipUrl);
				}
			}
		}
		// 2011-06-10 이미지 첨부된 메시지에서 이미지 목록 버튼 추가.
		if (image[0] != AttachController.NO_CONTENTS) {
			btnImageList = new Button(this);
			btnImageList.setText("이미지목록");
			btnImageList.setBackgroundResource(R.drawable.v2_btn_bg);
			btnImageList.setTextColor(Color.WHITE);
			btnImageList.setOnClickListener(this);
			ll01.addView(btnImageList);
		}
	}

	private void showMov( final String url ){

		btnMovieClip = new Button(this);
		btnMovieClip.setBackgroundResource(R.drawable.v2_btn_bg);
		btnMovieClip.setTextColor(Color.WHITE);
		btnMovieClip.setText(R.string.rmShowmov);
		btnMovieClip.setOnClickListener(this);
		ll01.addView(btnMovieClip);
	}

	public void onClick( View view )
	{
		if( view.equals(btnAppInstall))
		{
			L("installApp : " + APPPATH);
			installAPP( APPPATH );			
		}else if( view.equals(btnDownLoadFile))
		{
			// 테스트 코드 입니다.
			Intent intent = new Intent(ReceivedMsgActivity.this, AttachmentListActivity.class);
			intent.putExtra(AttachController.UID, UID);
			startActivity(intent);
		}else if( view.equals(btnImageList)) {
			Intent intent = new Intent(getApplicationContext(), SendAttachListActivity.class);
			for (int j = 0; j < image.length; j++) {
				intent.putExtra("image" + (j+1), image[j]);
			}
			startActivity(intent);
		}else if ( view.equals(btnMovieClip))
		{
			Intent i;
			// Youtube

			if( movieClipUrl.toLowerCase().contains("www.youtube.co"))
			{
				i = new Intent( Intent.ACTION_VIEW );
				i.setData( Uri.parse(movieClipUrl ));
				startActivity(i);
			}
			else
			{
				//우리서버에 있는 경우.
//				i = new Intent(ReceivedMsgActivity.this, MovieClipActivity.class);
				i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setType("video/*");
				i.setData(Uri.parse(movieClipUrl));
				// 2011-06-08 MovieClipActivity 는 사용하지 않음.
//				i.putExtra("URL", movieClipUrl);
				startActivity(i);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		switch( item.getItemId() )
		{
		case R.id.msgDetail:
			showDetailView();
			break;
		case R.id.msgDelete:
			deleteMsg(null);
			break;
		default:
			Log.e(DEBUG_TAG, "Optional Menu selected Default");
		}
		return super.onOptionsItemSelected(item);
	}


	private void showDetailView()
	{
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.msgDetailViewTitle);

		String temp = 
			"GROUP_ID " + GROUP_ID +"<br>" +
			getString(R.string.msgDetailViewSender)+ "<br><b>" + SENDER +"</b><br>" +
			getString(R.string.msgDetailViewCreateDate) + "<br><b>" + TIMEFORMAT.format(new Date(new Long(CREATEDATE))) +"</b><br>" +
			getString(R.string.msgDetailViewReceiveDate) + "<br><b>" + TIMEFORMAT.format(new Date(new Long(RECEIVEDATE))) +"</b>" ;
		builder.setMessage(Html.fromHtml(temp));
		builder.show();

		L("SHOW DETAIL VIEW()");
		L(temp);
	}

	private void installAPP( String path )
	{
		L( "installAPP path = " + path );
		if( path != null && path.length() > 3)
		{
			path = path.trim();
			File apkFile = new File( path );
			if( apkFile.exists() && apkFile.isFile() )
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType( Uri.fromFile(apkFile), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}else{
			if( APKfileItem == null || APKfileItem.size() == 0) return;
			L("action download : " + APKfileItem.get(AttachController.UID));
			Intent intent = new Intent(getApplicationContext(), AttachmentListActivity.class);
			intent.setAction(AttachmentListActivity.ACTION_DOWNLOAD);
			intent.putExtra(AttachController.UID, APKfileItem.get(AttachController.UID));
			startActivity(intent);
		}
	}

	@Override
	protected void setMsgCtrl() {
		if( msgCtrl == null ) msgCtrl = ReceiveMsgController.getInstance(getApplicationContext());
		if( grpCtrl == null ) grpCtrl = GroupController.getInstance(getApplicationContext());
	}

	@Override
	protected void showDetailInfo() {
		StringBuilder msgInfoDetail = new StringBuilder();

		if( GROUP_ID != null)
		{
			String grpName;
			Map<String, String> map = grpCtrl.getGroup(GROUP_ID);
			if (getResources().getConfiguration().locale.getDisplayLanguage().contains("한")) {
				grpName = map.get( GroupController.GROUP_KNAME );
			} else {
				grpName = map.get( GroupController.GROUP_NAME);
			}
			msgInfoDetail.append("<b> ▶ "  + grpName + "</b><br>");
		}
		msgInfoDetail.append( 
				"<b> ▶ "  + getString(R.string.msgDetailViewSender)+ " : " + SENDER +"</b><br>" +
				"<b> ▶ "  + getString(R.string.msgDetailViewCreateDate) + " : " + TIMEFORMAT.format(new Date(new Long(CREATEDATE))) +"</b><br>");
		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(msgInfoDetail.toString()));
		msgView.addView( tv );		
	}
}
