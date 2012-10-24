package kr.co.drdesign.parmtree.message;

import java.util.Map;

import kr.co.drdesign.parmtree.database.SendedMsgController;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class SendedMsgActivity extends MsgActivity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		cls = SendedMsgActivity.class;
		msgCtrl = SendedMsgController.getInstance(getApplicationContext());
		setContent();
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void replyMsg() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void setContent() {
		// TODO Auto-generated method stub

		Map<String,String> map = msgCtrl.get(index);
		if (map.size() < 1) return;
		
		String sender = map.get(USERID);
		String estid = map.get(ESTID);
		String senddate = map.get(SENDDATE);
		String content = map.get(CONTENTS);
		String title = map.get(TITLE);
		
		if (estid != null) tvSender.setText(estid);
		if (sender != null) tvSender.setText(sender);
		tvTitle.setText(title);
		tvSenddate.setText(senddate);		
		tvContent.setText(content);
		
		tvContent.setTextColor(Color.BLACK);
		tvSenddate.setTextColor(Color.BLACK);
		tvTitle.setTextColor(Color.BLACK);
		tvSender.setTextColor(Color.BLACK);
		
		llContent.addView(tvContent);
		
	}


}
