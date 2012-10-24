package kr.co.drdesign.parmtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.database.EventController;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class EventActivity extends Activity 
	implements OnItemClickListener, OnItemLongClickListener, c{

	ListView eventList;
	ProgressDialog loading;
	
	ArrayList<Map<String,String>> eventArray = new ArrayList<Map<String,String>>();

	EventController eventCtrl;
	EventThread eventThread;
	EventHandler eventHandler;
	EventAdapter eventAdapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		eventList = (ListView) findViewById(R.id.eventlist);
		eventList.setCacheColorHint(0);
		eventCtrl = EventController.getInstance(getApplicationContext());
		eventThread = new EventThread();
		eventHandler = new EventHandler();
		load();
	}
	
	void load() {
		showLoadingDialog();
		eventThread = new EventThread();
		eventThread.start();
	}
	
	protected void getList() {
		eventArray = eventCtrl.get();
	}
	
	class EventThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getList();
			eventHandler.sendEmptyMessage(0);
		}
	}
	
	class EventHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			dismiss();
			fillList();
		}
	}
	
	protected void fillList() {
		eventAdapter = new EventAdapter
				(this, eventArray, R.layout.event_item_2, null, null);
		eventList.setAdapter(eventAdapter);
	}
	
	class EventAdapter extends SimpleAdapter {

		public EventAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.event_item_2, null);
			}
			
//			ImageView eventPic = (ImageView) view.findViewById(R.id.eventBanner);
//			TextView eventEst = (TextView) view.findViewById(R.id.eventEstname);
//			TextView eventDate = (TextView) view.findViewById(R.id.eventEnabledate);
//			
//			String estName = eventArray.get(position).get(ESTNAME);
//			String enableDate = eventArray.get(position).get(ENABLEDATE);
//			
//			eventEst.setText(estName);
//			eventDate.setText(enableDate);
			
			return view;
		}
		
	}
	
	protected void showLoadingDialog() {
		loading = new ProgressDialog(getParent());
		loading.setCancelable(false);
		loading.setMessage(getString(R.string.searchTxtResponseToServer));
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.show();
	}
	
	protected void dismiss() {
		if (loading.isShowing()) loading.dismiss();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.getParent().onBackPressed();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// 이벤트 상세정보 팝업
	}

}
