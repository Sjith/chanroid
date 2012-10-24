package kr.co.drdesign.parmtree.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.HotelController;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HotelActivity extends Activity implements c {

	ListView hotelListView;
	ArrayList<Map<String,String>> hotelList;
	
	HotelController hotelCtrl;
	ListAdapter hotelAdapter;
	ListThread hotelThread;
	ListHandler hotelHandler;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_list);
		hotelList = new ArrayList<Map<String,String>>();
		hotelListView = (ListView) findViewById(R.id.hotellist);
		hotelListView.setCacheColorHint(0);
		hotelCtrl = HotelController.getInstance(getApplicationContext());
		hotelThread = new ListThread();
		hotelHandler = new ListHandler();
		load();
	}
	
	void load() {
		hotelThread = new ListThread();
		hotelThread.start();
	}
	
	void getList() {
		hotelList = hotelCtrl.get();
	}
	
	void fillList() {
		hotelAdapter = new ListAdapter(this, hotelList, R.layout.hotel_item, null, null);
		hotelListView.setAdapter(hotelAdapter);
		hotelAdapter.notifyDataSetChanged();
	}
	
	class ListThread extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getList();
			hotelHandler.sendEmptyMessage(0);
		}
		
	}
	
	class ListHandler extends Handler {

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
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

		/* (non-Javadoc)
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.hotel_item, null);
			}
			
			ImageView hotelPic = (ImageView) view.findViewById(R.id.hotelpic);
			
			TextView hotelTitle = (TextView) view.findViewById(R.id.hoteltitle);
			String title = hotelList.get(position).get(ESTNAME);
			hotelTitle.setText(title);
			
			TextView hotelDesc = (TextView) view.findViewById(R.id.hoteldesc);
			String desc = hotelList.get(position).get(DESC);
			hotelDesc.setText(desc);
			
			Button hotelLoc = (Button) view.findViewById(R.id.hotellocbtn);
			hotelLoc.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						double lon = Double.valueOf(hotelList.get(position).get(LON));
						double lat = Double.valueOf(hotelList.get(position).get(LAT));
						Uri location = Uri.parse(String.format("geo:%f,%f", lon, lat));
						startActivity(new Intent(Intent.ACTION_VIEW, location));
					} catch (Exception e) {
						Toast.makeText(getParent().getParent(), e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			return view;
		}
		
		
		
	}

}
