package kr.co.drdesign.parmtree.message;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.DBController;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public abstract class MsgListActivity extends Activity 
	implements OnItemClickListener, OnItemLongClickListener, OnClickListener, c {

	protected ProgressBar loading;
	protected ListView msgListView;
	
	CheckedTextView allCheck;
	Button delBtn;
	
	
	protected SimpleAdapter listAdapter;
	protected ListHandler listHandler;
	protected ListThread listThread;
	protected DBController msgCtrl; // 하위클래스에서 구현
	
	protected ArrayList<Map<String,String>> msgList;
	protected TreeSet<Integer> checkedSet;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_list);	
		msgListView = (ListView) findViewById(R.id.msglist);
		msgListView.setCacheColorHint(0);
		msgListView.setAlwaysDrawnWithCacheEnabled(false);
		msgListView.setOnItemClickListener(this);
		msgListView.setOnItemLongClickListener(this);
		msgListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		allCheck = (CheckedTextView) findViewById(R.id.msglistcheckall);
		allCheck.setOnClickListener(this);
		delBtn = (Button) findViewById(R.id.msglistdelete);
		delBtn.setOnClickListener(this);
		listHandler = new ListHandler();
		listThread = new ListThread();
		msgList = new ArrayList<Map<String,String>>();
		checkedSet = new TreeSet<Integer>();	
		loading = (ProgressBar) findViewById(R.id.msglistprogress);
	}

	protected boolean showNoMsg() {
		if (msgList == null || msgList.size() < 1) {
			AlertDialog.Builder noMsg = new AlertDialog.Builder(getParent().getParent());
			noMsg.setMessage(R.string.nomsg)
			.setTitle(R.string.alert)
			.setCancelable(false)
			.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
//			.show();
			return false;
		} else return true;
	}
	
	protected final void getList() {
		// DB에 있는 자료를 변수에 할당. 스레드 처리.
		if (!listThread.isAlive()) {
			listThread = new ListThread();
			listThread.start();
		}
	}
	
	protected abstract void fillList();
	
	protected final void showDeleteDialog() {
		AlertDialog.Builder deleteMsg = new AlertDialog.Builder(getParent().getParent());
		deleteMsg.setMessage(R.string.deletemsg)
		.setTitle(R.string.alert)
		.setCancelable(true)
		.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 메시지를 삭제한다.
				dialog.dismiss();
			}
		})
		.show();
	}
	
	protected final void showDeletingDialog() {
		if (loading.getVisibility() == View.INVISIBLE) {
			loading.setVisibility(View.VISIBLE);
		}
	}
	
	protected final void showLoadingDialog() {
		if (loading.getVisibility() == View.INVISIBLE) {
			loading.setVisibility(View.VISIBLE);
		}
	}
	
	protected final void dialogDismiss() {
		if (loading.getVisibility() == View.VISIBLE) {
			loading.setVisibility(View.INVISIBLE);
		}
	}
	
	protected class ListHandler extends Handler {

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			dialogDismiss();
			fillList();
			switch (msg.what) {
			case 0 :
				break;
			case 1 :
				Toast.makeText(getParent().getParent(), R.string.deletesuccess, Toast.LENGTH_SHORT).show();
				break;
			default :
				break;
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getList();
	}

	void checkAll(boolean check) {
		for (int i = 0; i < msgListView.getChildCount(); i++) {
			ViewGroup row = (ViewGroup) msgListView.getChildAt(i);
			for (int j = 0; j < row.getChildCount(); j++) {
				if (row.getChildAt(j) instanceof CheckedTextView) {
					((CheckedTextView) row.getChildAt(0)).setChecked(check);
					checkedSet.add(i);
				}
			}
		}
	}
	
	void deleteAll() {
		for (int i : checkedSet) {
			msgCtrl.delete(msgList.get(i).get(ID));
			msgList.remove(i);
		}
	}
	
	void delete(int id) {
		msgCtrl.delete(msgList.get(id).get(ID));
		msgList.remove(id);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		getParent().onBackPressed();
	}

	protected class ListThread extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			msgList = msgCtrl.get();
			listHandler.sendEmptyMessage(0);
		}
		
	}
	

	boolean allChecked = false;
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.msglistcheckall :
			allChecked = !allChecked;
			checkAll(allChecked);
			allCheck.setChecked(allChecked);
			break;
		case R.id.msglistdelete :
			break;
		}
	}
}
