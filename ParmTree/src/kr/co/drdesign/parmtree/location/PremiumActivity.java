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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PremiumActivity extends Activity 
	implements OnItemClickListener, OnItemLongClickListener, c {

	Button priBtn;
	ListView preListView;
	ArrayList<Map<String,String>> preList;
	EstablishController estCtrl;
	
	ListThread preThread;
	ListHandler preHandler;
	SimpleAdapter preAdapter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pre_list);
		preListView = (ListView) findViewById(R.id.prelist);
		preListView.setCacheColorHint(0);
		preListView.setOnItemClickListener(this);
		preList = new ArrayList<Map<String,String>>();
		preThread = new ListThread();
		preHandler = new ListHandler();
		estCtrl = EstablishController.getInstance(getApplicationContext());
		search();
	}

	
	void search() {
		if (!preThread.isAlive()) {
			preThread = new ListThread();
			preThread.start();
		}
	}
	
	void fillList() {
		preAdapter = new ListAdapter(
			this, preList, R.layout.est_item, null, null);
		preListView.setAdapter(preAdapter);
		
	}
	
	class ListThread extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			preList = estCtrl.getPremi();
			preHandler.sendEmptyMessage(0);
		}
		
	}
	
	class ListHandler extends Handler {

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			fillList();
			switch (msg.what) {
			case 0 :
				break;
			case 1 :
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

		/* (non-Javadoc)
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.est_item, null);
			}

			RelativeLayout estBack;
			
			ImageView estPicture;
			TextView estName;
			ImageView estKind;
			ImageView estPremi;
			TextView estAddr;
			TextView estDesc;
			
			RatingBar estRatingBar;
			TextView estRatingText;
			
			estBack = (RelativeLayout) view.findViewById(R.id.estBack);
			estPicture = (ImageView) view.findViewById(R.id.estPicture);
			estName = (TextView) view.findViewById(R.id.estName);
			estKind = (ImageView) view.findViewById(R.id.estKind);
			estPremi = (ImageView) view.findViewById(R.id.estPremi);
			estAddr = (TextView) view.findViewById(R.id.estAddr);
			estDesc = (TextView) view.findViewById(R.id.estDesc);
			estRatingBar = (RatingBar) view.findViewById(R.id.estRatingBar);
			estRatingText = (TextView) view.findViewById(R.id.estRatingText);
			
			String name = preList.get(position).get(ESTNAME);
			int kind = Integer.valueOf(preList.get(position).get(ESTKIND));
			String addr = preList.get(position).get(ESTADDR)
							.replace("/", " ").replace("¥Î«—πŒ±π ", "");
			CharSequence desc = preList.get(position).get(DESC);
			int premi = Integer.valueOf(preList.get(position).get(PREMI));
			
			estName.setText(name);
			estAddr.setText(addr);
			estDesc.setText(desc);
			estRatingBar.setNumStars(5);
			estRatingBar.setRating(2.50f);
			
			switch (kind) {
			case 0 :
			case 1 :
			case 2 :
			case 3 :
			case 4 :
			case 5 :
				estKind.setImageResource(R.drawable.btn_drink_press);
				break;
			case 6 :
			case 7 :
			case 8 :
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
		i.putExtra(ESTID, preList.get(arg2).get(ESTID));
		startActivity(i);
	}
}
