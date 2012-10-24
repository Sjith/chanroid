package kr.co.drdesign.client;

import java.util.List;
import java.util.Map;

import kr.co.drdesign.client.controller.ReceiveMsgController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReceivedMsgListActivity extends MsgListActivity 
{

	protected ReceiveMsgController msgCtrl;

	private static Drawable IC_NEWMSG;
	private static Drawable IC_NEWMSG_ATTACH;
	private static Drawable IC_MSG;
	private static Drawable IC_MSG_ATTACH;
//	private static Drawable cbBG;

//	private GestureDetector mGestureDetector;
	private SimpleTitleAdapter mSimpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if( IC_NEWMSG == null)
			IC_NEWMSG = getResources().getDrawable(R.drawable.v2_ic_new_msg);
		if( IC_NEWMSG_ATTACH == null)
			IC_NEWMSG_ATTACH = getResources().getDrawable(R.drawable.v2_ic_new_msg_attach);
		if( IC_MSG == null)
			IC_MSG = getResources().getDrawable(R.drawable.v2_ic_msg);
		if( IC_MSG_ATTACH == null)
			IC_MSG_ATTACH = getResources().getDrawable(R.drawable.v2_ic_msg_attach);

		msgCtrl = ReceiveMsgController.getInstance(getApplicationContext());
		super.msgCtrl = msgCtrl;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( IC_MSG 		!= null)		IC_MSG.setCallback(null);
		if( IC_NEWMSG 	!= null)		IC_NEWMSG.setCallback(null);
		if( IC_MSG_ATTACH == null)		IC_MSG_ATTACH.setCallback(null);
		if( IC_NEWMSG_ATTACH != null)	IC_NEWMSG_ATTACH.setCallback(null);
	}
	@Override
	protected void fillMsgList()
	{
		msgList = msgCtrl.getMsgListbyGroup(groupIds);
		fillMsgList( msgList );

		//ListView Scrolling
		if ( selectItemPosition >= 0 )
			mlLvMsgList.setSelection(selectItemPosition);
	}
	protected void fillMsgList(List<Map<String,String>> list){
		mSimpleAdapter = new SimpleTitleAdapter(
				this, 
				list, 
				R.layout.msg_item, 
				new String[]{ReceiveMsgController._id, ReceiveMsgController.TITLE}, 
				new int[]{R.id.mlCtvItem, R.id.mlTvTitle});
		//¾î´ðÅÍ µî·Ï
		mlLvMsgList.setAdapter(mSimpleAdapter);
		showNoMsgText();
	}

	class SimpleTitleAdapter extends SimpleAdapter{
		private List<Map<String, String>> list;

		private TextView tvTitle;
		private CheckedTextView ctbItem;
		private ImageView ivMsgState;

		public SimpleTitleAdapter(Context context, 
				List<Map<String, String>> list, int resource, String[] from, int[] to)
		{
			super(context, list, resource, from, to);
			this.list = list;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.msg_item, null);
			}

			final int POSITION = position;
			final String s_id 	= list.get(position).get(ReceiveMsgController._id);
			final Integer i_id 	= Integer.parseInt(s_id);
			String title 		= list.get(position).get(ReceiveMsgController.TITLE);
			String readed 		= list.get(position).get(ReceiveMsgController.READED);
			String attachment 	= list.get(position).get(ReceiveMsgController.ATTACHMENTS);

			tvTitle 	= (TextView)view.findViewById(R.id.mlTvTitle);
			ctbItem 	= (CheckedTextView)view.findViewById(R.id.mlCtvItem);
			ivMsgState 	= (ImageView)view.findViewById(R.id.mlIvMsgState);

			ctbItem.setBackgroundResource(R.drawable.v2_ic_checkbox);
			ctbItem.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {

					if( view.getClass().getName().equalsIgnoreCase("android.widget.CheckedTextView"))
					{
						((CheckedTextView)view).toggle();
						if(((CheckedTextView)view).isChecked()){
							checkedSet.add(i_id);
						}else{
							checkedSet.remove(i_id);
						}
						showButtonMenuByCheckedItem();
					}
				}
			});

			tvTitle.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent( ReceivedMsgListActivity.this, ReceivedMsgActivity.class );
					intent.putExtra(ReceiveMsgController._id, s_id);
					Log.i("DR", "!@#" + "s_id = " + s_id);
					startActivityForResult(intent, Activity.RESULT_OK);
					selectItemPosition = POSITION;
				}
			});
			tvTitle.setText(""+title);
			if ( "0".equals(readed) ){
				if( attachment == null )
					ivMsgState.setBackgroundDrawable(IC_NEWMSG);
				else
					ivMsgState.setBackgroundDrawable(IC_NEWMSG_ATTACH);

			}else{
				if( attachment == null )
					ivMsgState.setBackgroundDrawable(IC_MSG);
				else
					ivMsgState.setBackgroundDrawable(IC_MSG_ATTACH);
			}
			ctbItem.setChecked( checkedSet.contains(i_id) );
			return view;
		}
	}
}