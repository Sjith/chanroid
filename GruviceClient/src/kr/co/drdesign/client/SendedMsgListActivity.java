package kr.co.drdesign.client;

import java.util.List;
import java.util.Map;

import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.client.controller.SendMsgController;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

// 실제로 메시지를 뿌려주는 작업은 여기서 한다.
public class SendedMsgListActivity extends MsgListActivity {

	protected SendMsgController msgCtrl;

//	private GestureDetector mGestureDetector;
	private SimpleTitleAdapter mSimpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		msgCtrl = SendMsgController.getInstance(getApplicationContext());
		super.msgCtrl = msgCtrl;
	}

	@Override
	protected void fillMsgList() // 메시지를 직접 뿌리지는 않고 준비 후 오버로딩된 메서드에 던져준다.
	{
		msgList = msgCtrl.getMsgListbyGroup(groupIds);
		fillMsgList( msgList );

		//ListView Scrolling
		if ( selectItemPosition >= 0 )
			mlLvMsgList.setSelection(selectItemPosition);
		
		
	}
	
	// 실제로 메시지를 뿌려주는 메서드 2011-3-25
	protected void fillMsgList(List<Map<String,String>> list){
		mSimpleAdapter = new SimpleTitleAdapter(
				this, 
				list, 
				R.layout.msg_item, 
				new String[]{ReceiveMsgController._id, ReceiveMsgController.TITLE}, 
				new int[]{R.id.mlCtvItem, R.id.mlTvTitle});
		//어댑터 등록
		mlLvMsgList.setAdapter(mSimpleAdapter);
		showNoMsgText();
	}

	class SimpleTitleAdapter extends SimpleAdapter{
		private List<Map<String, String>> list;

		private TextView tvTitle;
		private CheckedTextView ctbItem;
//		private ImageView ivMsgState;

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

			final String s_id = list.get(position).get(SendMsgController._id);
			final Integer i_id = Integer.parseInt(s_id);
			String title = list.get(position).get(SendMsgController.TITLE);

			tvTitle = (TextView)view.findViewById(R.id.mlTvTitle);
			//tvTitle.setSelected(true);
			ctbItem = (CheckedTextView)view.findViewById(R.id.mlCtvItem);
//			ivMsgState = (ImageView)view.findViewById(R.id.mlIvMsgState);

			ctbItem.setBackgroundResource(R.drawable.v2_ic_checkbox);
			ctbItem.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {

					if( view.getClass().getName().equalsIgnoreCase("android.widget.CheckedTextView"))
					{
						((CheckedTextView)view).toggle();
						if(((CheckedTextView)view).isChecked()){
							checkedSet.add(i_id);
						} else {
							checkedSet.remove(i_id);
						}
						showButtonMenuByCheckedItem();
					}
				}
			});

			tvTitle.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent( SendedMsgListActivity.this, SendedMsgActivity.class );
					intent.putExtra(SendMsgController._id, s_id);
					startActivityForResult(intent, Activity.RESULT_OK);
				}
			});

			tvTitle.setText(""+title);

			ctbItem.setChecked( checkedSet.contains(i_id) );
			return view;
		}
	}
}