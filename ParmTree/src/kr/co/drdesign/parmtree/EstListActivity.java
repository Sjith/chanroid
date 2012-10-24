package kr.co.drdesign.parmtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.database.FavoriteController;
import kr.co.drdesign.parmtree.est.EstablishActivity;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class EstListActivity extends Activity 
	implements OnItemClickListener, OnItemLongClickListener, c {

	ProgressDialog loading;
	
	ListView favoriteListView;
	ArrayList<Map<String,String>> favoriteList;
	FavoriteAdapter favoriteAdapter;
	FavoriteThread favoriteThread;
	FavoriteHandler favoriteHandler;
	FavoriteController favoriteCtrl;
	EstablishController estCtrl;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_list);
		favoriteList = new ArrayList<Map<String,String>>();
		favoriteListView = (ListView) findViewById(R.id.favoritelist);
		favoriteListView.setCacheColorHint(0);
		favoriteListView.setOnItemLongClickListener(this);
		favoriteListView.setOnItemClickListener(this);
		favoriteThread = new FavoriteThread();
		favoriteHandler = new FavoriteHandler();
		favoriteCtrl = FavoriteController.getInstance(getApplicationContext());
		estCtrl = EstablishController.getInstance(getApplicationContext());
		load();
	}

	void getList() {
		ArrayList<Map<String,String>> temp = favoriteCtrl.get();
		if (favoriteList.size() > 0) favoriteList.clear();
		for (int i = 0; i < temp.size(); i++) {
			favoriteList.add(estCtrl.get(Integer.parseInt(temp.get(i).get(ESTID))));
		}
	}
	
	void fillList() {
		favoriteAdapter = new FavoriteAdapter(
				this, favoriteList, R.layout.est_item, null, null);
		favoriteListView.setAdapter(favoriteAdapter);
	}

	class FavoriteAdapter extends SimpleAdapter {

		public FavoriteAdapter(Context context,
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

//			RelativeLayout estBack = (RelativeLayout) view.findViewById(R.id.estBack);
//			RelativeLayout estPicture = (ImageView) view.findViewById(R.id.estPicture);
			TextView estName = (TextView) view.findViewById(R.id.estName);
			ImageView estKind = (ImageView) view.findViewById(R.id.estKind);
			ImageView estPremi = (ImageView) view.findViewById(R.id.estPremi);
			TextView estAddr = (TextView) view.findViewById(R.id.estAddr);
			TextView estDesc = (TextView) view.findViewById(R.id.estDesc);
			
			String name = favoriteList.get(position).get(ESTNAME);
			String addr = favoriteList.get(position).get(ESTADDR).replace("/", " ").replace("대한민국 ", "");
			CharSequence desc = favoriteList.get(position).get(DESC);
			int kind = Integer.valueOf(favoriteList.get(position).get(ESTKIND));
			int premi = Integer.valueOf(favoriteList.get(position).get(PREMI));
			
			estName.setText(name);
			estAddr.setText(addr);
			estDesc.setText(desc);
			
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
	
	void load() {
		if (!favoriteThread.isAlive()) {
			favoriteThread = new FavoriteThread();
			favoriteThread.start();
		}
	}
	void showDeleteDialog(final String id) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getParent().getParent());
		alert.setTitle(R.string.danger)
		.setMessage(R.string.removelist)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				favoriteCtrl.delete(favoriteList.get(Integer.valueOf(id)).get(ESTID)); // 임시값. 차후 수정
				dialog.dismiss();
				load();
			}
		})
		.setNegativeButton(R.string.no, null)
		.show();
	}
	
	void showLoadingDialog() {
		loading = new ProgressDialog(getParent().getParent());
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.setMessage(getString(R.string.searchTxtResponseToServer));
		loading.show();
	}
	
	void showLongClickDialog(final String id) {

		AlertDialog.Builder alert = new AlertDialog.Builder(getParent().getParent());
		alert.setItems(
				new String[]{ getString(R.string.detail), getString(R.string.delete) }, 
		new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0 : break;
				case 1 : showDeleteDialog(id); break;
				default : break;
				}
				dialog.dismiss();
			}
		});
		alert.show();
	}
	
	void dismiss() {
		if (loading.isShowing()) loading.dismiss();
	}
	
	
	class FavoriteThread extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			getList();
			favoriteHandler.sendEmptyMessage(0);
		}
		
	}
	
	class FavoriteHandler extends Handler {

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
			default :
				break;
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		load();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		showLongClickDialog(Integer.toString(arg2));
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, EstablishActivity.class);
		i.putExtra(ESTID, favoriteList.get(arg2).get(ESTID));
		startActivity(i);		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.getParent().onBackPressed();
	}

}
