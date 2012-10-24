package kr.co.drdesign.parmtree.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.est.EstablishActivity;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NearFieldActivity extends Activity 
	implements OnItemClickListener, OnItemLongClickListener, c {
	
	ListView estListView;
	ArrayList<Map<String,String>> estList;
	EstablishController estCtrl;
	Intent getIntent;
	
	ListThread estThread;
	ListHandler estHandler;
	ListAdapter estAdapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.club_nearlist);
		
		getIntent = getIntent();
		estCtrl = EstablishController.getInstance(getApplicationContext());
		
		estListView = (ListView) findViewById(R.id.nearlistView1);
		estListView.setCacheColorHint(0);
		estList = new ArrayList<Map<String,String>>();
		
		estListView.setOnItemClickListener(this);
		estListView.setOnItemLongClickListener(this);
		
		estThread = new ListThread();
		estHandler = new ListHandler();
		load();
	}
	
	void load() {
		estThread = new ListThread();
		estThread.start();
	}

	@SuppressWarnings("unchecked")
	void getList() {
		ArrayList<String> getlist = (ArrayList<String>) getIntent.getSerializableExtra(ESTID);
		for(int i = 0; i < getlist.size(); i++) {
			estList.add(estCtrl.get(getlist.get(i)));
		}
	}
	
	void fillList() {
		estAdapter = new ListAdapter(this, estList, R.layout.est_item, null, null);
		estListView.setAdapter(estAdapter);
	}
	
	class ListThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getList();
			estHandler.sendEmptyMessage(0);
		}
	}
	
	class ListHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0 :
				fillList();
				break;
			}
		}
	}
	
	class ListAdapter extends SimpleAdapter {

		public ListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated constructor stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.est_item, null);
			}

			ImageView estPicture = (ImageView) view.findViewById(R.id.estPicture);
			TextView estName = (TextView) view.findViewById(R.id.estName);
			ImageView estKind = (ImageView) view.findViewById(R.id.estKind);
			ImageView estPremi = (ImageView) view.findViewById(R.id.estPremi);
			TextView estAddr = (TextView) view.findViewById(R.id.estAddr);
			TextView estDesc = (TextView) view.findViewById(R.id.estDesc);
			
			String name = estList.get(position).get(ESTNAME);
			String addr = estList.get(position).get(ESTADDR).replace("/", " ").replace("¥Î«—πŒ±π ", "");
			String desc = estList.get(position).get(DESC);
			
			estName.setText(name);
			estAddr.setText(addr);
			estDesc.setText(desc);

			int kind = Integer.valueOf(estList.get(position).get(ESTKIND));
			int premi = Integer.valueOf(estList.get(position).get(PREMI));
			
			switch (kind) {
			case 0 : case 1 : case 2 : case 3 :	case 4 :
			case 5 :
				estKind.setImageResource(R.drawable.btn_drink_press);
				break;
			case 6 : case 7 : case 8 :
			case 9 :
				estKind.setImageResource(R.drawable.btn_food_press);
				break;
			}
			
			switch (premi) {
			case 0 :
				break;
			case 1 :
				estPremi.setImageResource(R.drawable.btn_premium_unpress);
				break;
			}
			
			return view;
		}
		
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
		Intent i = new Intent(this, EstablishActivity.class);
		i.putExtra(ESTID, estList.get(arg2).get(ESTID));
		startActivity(i);
	}


}
