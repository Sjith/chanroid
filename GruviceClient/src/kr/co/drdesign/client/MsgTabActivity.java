package kr.co.drdesign.client;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MsgTabActivity extends TabActivity {

	private TabHost mTabHost;
	private TabSpec receiveMsg;
	private TabSpec sendMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_list_tab);
		
		mTabHost 	= getTabHost();
		receiveMsg	= mTabHost.newTabSpec("ReceveMsg");
		sendMsg 	= mTabHost.newTabSpec("SendMsg");
		
		receiveMsg.setIndicator( getTextView(R.string.mlReceivedMsgMenu) );
		receiveMsg.setContent(new Intent( this, ReceivedMsgListActivity.class));
		
		sendMsg.setIndicator( getTextView(R.string.mlSendedMsgMenu) );
		sendMsg.setContent(new Intent( this, SendedMsgListActivity.class));
		
		mTabHost.addTab(receiveMsg); 
		mTabHost.addTab(sendMsg);

		mTabHost.setCurrentTab(0);
	}
	
	public void onClick(View view)
	{
		finish();
	}
	private TextView getTextView(int title)
	{
		TextView tv  = new TextView(this);
		tv.setText(title);
		tv.setGravity( Gravity.CENTER);// | Gravity.CENTER_VERTICAL);
		tv.setBackgroundResource(R.drawable.msgtab_bg);
		tv.setTextColor(Color.WHITE);
		return tv;
	}
}