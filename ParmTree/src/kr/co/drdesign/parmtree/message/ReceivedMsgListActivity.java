package kr.co.drdesign.parmtree.message;

import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.ReceivedMsgController;
import kr.co.drdesign.parmtree.util.l;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReceivedMsgListActivity extends MsgListActivity {
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		msgCtrl = ReceivedMsgController.getInstance(getApplicationContext());
		showLoadingDialog();
		getList();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, ReceivedMsgActivity.class);
		i.putExtra(ID, Integer.valueOf(msgList.get(arg2).get(ID)));
		l.s("putExtra : " + i.getIntExtra(ID, 0));
		startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void fillList() {
		// TODO Auto-generated method stub
		listAdapter = new ListAdapter(
			this, msgList, R.layout.msg_item, null, null
		);
		msgListView.setAdapter(listAdapter);
		showNoMsg();
	}

	class ListAdapter extends SimpleAdapter {

		public ListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.msg_item, null);
			}
			CheckedTextView check;
			TextView title;
			TextView sender;
			ImageView readflag;
			
			
			check = (CheckedTextView) view.findViewById(R.id.msgitemcheck);
			title = (TextView) view.findViewById(R.id.msgitemtitle);
			sender = (TextView) view.findViewById(R.id.msgitemsender);
			readflag = (ImageView) view.findViewById(R.id.msgreadflag);
			readflag.setVisibility(View.VISIBLE);
			
			if (msgList.get(position).get(READTAG).equals("0")) {
				readflag.setImageResource(R.drawable.message_02);
			} else {
				readflag.setImageResource(R.drawable.message_01);
			}
			String titleText = msgList.get(position).get(TITLE);
			String senderText = 
				msgList.get(position).get(USERID) + "/" +
				msgList.get(position).get(ESTID) + " [ " +
				msgList.get(position).get(RECEIVEDATE) + " ] ";

			title.setText(titleText);
			title.setSingleLine(true);
			sender.setText(senderText);
			sender.setSingleLine(true);
			
			check.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (((CheckedTextView) v).isChecked()) {
						((CheckedTextView)v).setChecked(false);
						checkedSet.remove(position);
					} else {
						((CheckedTextView)v).setChecked(true);
						checkedSet.add(position);
					}
					
					if (checkedSet.size() < msgList.size()) {
						allCheck.setChecked(false);
					} else if (checkedSet.size() == msgList.size() && msgList.size() != 0) {
						allCheck.setChecked(true);
					}
				}
			});
			
			return view;
		}

	}

	

}
