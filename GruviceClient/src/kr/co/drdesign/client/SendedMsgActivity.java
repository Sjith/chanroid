package kr.co.drdesign.client;

import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.SendMsgController;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.widget.TextView;

public class SendedMsgActivity 
extends MsgActivity 
{

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Map<String, String> msgInfo = msgCtrl.getMsg(_id);

		TITLE 		= msgInfo.get(SendMsgController.TITLE);
		CONTENTS 	= msgInfo.get(SendMsgController.CONTENTS);
		GROUP_ID 	= msgInfo.get(SendMsgController.GROUP_ID);
		ETC 		= msgInfo.get(SendMsgController.ETC);
		SENDER 		= msgInfo.get(SendMsgController.SENDER);
		RECEIVER 	= msgInfo.get(SendMsgController.RECEIVER);
		SENDDATE 	= msgInfo.get(SendMsgController.SENDDATE);

		tvTitle = (TextView) findViewById(R.id.msgTvTitle);
		if( TITLE != null)
		tvTitle.setText(TITLE.trim());
		tvTitle.setSelected(true);

		showDetailInfo();
		if ( CONTENTS == null || CONTENTS.length() == 0)
			CONTENTS = "text_\nNo Contents";
		parsingSavedMsg(CONTENTS);

		msgView.setOnLongClickListener(this);
		mGestureDetector = new GestureDetector(this, new LinearGestureListener());

		L("TITLE = " + TITLE);
	}

	protected void parsingSavedMsg(String msg){
		L("parsingSavedMsg()");
		Scanner sc = new Scanner( msg );

		String tokens = "";
		boolean isText = true;
		boolean isImg = false;

		while ( sc.hasNext() )
		{
			// if tokens is EmptyString or '\n', tokens is ignoreed.
			tokens = sc.nextLine();
			if( tokens != null && tokens.trim().length() < 1 ) continue;

			if( "text_".equals( tokens )){
				isText = true;
				isImg = false;
			}else if( "img_".equals( tokens )){
				isText = false;
				isImg = true;
			}else
			{
				if ( isText )
				{
					showTxt( tokens +"\n" );
				}else if( isImg )
				{
//					showImg( tokens );
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		switch( item.getItemId() )
		{
		case R.id.msgDetail:
			L("item01 selected");
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
			//"GROUP_ID " + GROUP_ID +"<br>" +
			getString(R.string.msgDetailViewReceiver)+ "<br><b>" + RECEIVER+"</b><br>" +
			getString(R.string.msgDetailViewCreateDate) + "<br><b>" + TIMEFORMAT.format(new Date(new Long(SENDDATE))) +"</b><br>";// +			
		//			getString(R.string.msgDetailViewReceiveDate) + "<br><b>" + TIMEFORMAT.format(new Date(new Long(RECEIVEDATE))) +"</b>" ;

		builder.setMessage(Html.fromHtml(temp));
		builder.show();
		L("SHOW DETAIL VIEW()");
		L(temp);
	}

	@Override
	protected void setMsgCtrl() {
		if( msgCtrl == null ) msgCtrl = SendMsgController.getInstance(getApplicationContext());
		if( grpCtrl == null ) grpCtrl = GroupController.getInstance(getApplicationContext());
	}
	@Override
	protected void showDetailInfo() {
		StringBuilder msgInfoDetail = new StringBuilder();
		if( GROUP_ID != null )
		{
			Map<String, String> map = grpCtrl.getGroup(GROUP_ID);
			
			String grpName = map.get( GroupController.GROUP_NAME );
			if ( grpName == null || GROUP_ID.equals("100") )
				msgInfoDetail.append(
						"<b> ¢º "  + getString(R.string.msgDetailViewReceiver)+ " : " + RECEIVER +"</b><br>" );
			else
				msgInfoDetail.append(
						"<b> ¢º "  + getString(R.string.msgDetailViewReceiver)+ " : " + grpName +"</b><br>" );

		}
		try {
			msgInfoDetail.append( 
				"<b> ¢º "  + getString(R.string.msgDetailViewCreateDate) + " : " + TIMEFORMAT.format(new Date(new Long(SENDDATE))) +"</b><br>");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		TextView tv = new TextView(this);
		tv.setText(Html.fromHtml(msgInfoDetail.toString()));
		msgView.addView( tv );		
	}
}