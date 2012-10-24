package kr.co.drdesign.parmtree.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.parmtree.MainListActivity;
import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.est.EstablishActivity;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, c {

	private Button searchBtn;
	ListView estListView;
	ProgressBar estLoading;

	Button searchNameBtn;
	Button searchRegionBtn;
	Button searchKindBtn;

	String searchName;
	String searchRegion;
	String searchKind;

	String[] regionArray;
	String[] kindArray;
	String[] kindValues = new String[] { null, "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9" };

	ArrayList<Map<String, String>> estList;
	EstablishController estCtrl;

	ListThread estThread;
	ListHandler estHandler;
	ListAdapter estAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.club_search);

		searchBtn = (Button) findViewById(R.id.searchButton1);

		searchNameBtn = (Button) findViewById(R.id.searchestname);
		searchRegionBtn = (Button) findViewById(R.id.searchregionname);
		searchKindBtn = (Button) findViewById(R.id.searchestkind);
		estLoading = (ProgressBar) findViewById(R.id.searchloading);

		estListView = (ListView) findViewById(R.id.searchlist1);
		estListView.setCacheColorHint(0);
		estListView.setOnItemClickListener(this);

		searchBtn.setOnClickListener(this);

		searchNameBtn.setOnClickListener(this);
		searchRegionBtn.setOnClickListener(this);
		searchKindBtn.setOnClickListener(this);

		estListView.setOnItemClickListener(this);
		estListView.setOnItemLongClickListener(this);

		regionArray = getResources().getStringArray(R.array.RegionTypeValues);
		kindArray = getResources().getStringArray(R.array.EstkindValues);
		estCtrl = EstablishController.getInstance(getApplicationContext());
		estList = new ArrayList<Map<String, String>>();

		estThread = new ListThread();
		estHandler = new ListHandler();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		startActivity(new Intent(this, MainListActivity.class));
	}

	/*
	 * custom methods.
	 */

	void search() {
		if (!estThread.isAlive()) {
			estThread = new ListThread();
			estThread.start();
		}
	}

	void init() {
		searchName = "";
		searchRegion = "";
		searchKind = "";
		Toast.makeText(getParent().getParent(), R.string.searchinit,
				Toast.LENGTH_SHORT).show();
	}

	void fillList() {
		estAdapter = new ListAdapter(this, estList, R.layout.est_item, null,
				null);
		estListView.setAdapter(estAdapter);
	}

	class ListThread extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			estList = estCtrl.search(searchName, searchRegion, searchKind);
			estHandler.sendEmptyMessage(0);
		}

	}

	class ListHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				estLoading.setVisibility(View.GONE);
				fillList();
				break;
			case 1:
				break;
			}
		}

	}

	/*
	 * listener methods.
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.searchButton1:
			search();
			break;
		case R.id.searchinit:
			init();
			break;
		case R.id.searchestname:
			LinearLayout ll = (LinearLayout) View.inflate(this,
					R.layout.searchname_dialog, null);
			final EditText text = (EditText) ll.findViewById(R.id.editText1);

			AlertDialog.Builder name = new AlertDialog.Builder(getParent()
					.getParent());
			name.setTitle(R.string.estname);
			name.setView(ll);
			name.setNegativeButton(R.string.cancel, null);
			name.setPositiveButton(R.string.done,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							searchName = text.getText().toString();
							l.s("searchname : " + searchName);
						}
					});
			name.show();

			break;
		case R.id.searchregionname:
			AlertDialog.Builder regionAlert = new AlertDialog.Builder(
					getParent().getParent()).setTitle(R.string.region)
					.setSingleChoiceItems(regionArray, -1,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									searchRegion = regionArray[which];
									if (searchRegion.equals("전체"))
										searchRegion = "";
									l.s("searchregion : " + searchRegion);
									dialog.dismiss();
								}
							});
			regionAlert.show();
			break;
		case R.id.searchestkind:
			AlertDialog.Builder kindAlert = new AlertDialog.Builder(getParent()
					.getParent()).setTitle(R.string.estkind)
					.setSingleChoiceItems(kindArray, -1,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									searchKind = kindValues[which];
									l.s("searchkind : " + searchKind);
									dialog.dismiss();
								}
							});
			kindAlert.show();
			break;
		default:
			break;
		}

	}

	class ListAdapter extends SimpleAdapter {

		public ListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

			String name = estList.get(position).get(ESTNAME);
			int kind = Integer.valueOf(estList.get(position).get(ESTKIND));
			String addr = estList.get(position).get(ESTADDR).replace("/", " ")
					.replace("대한민국 ", "");
			CharSequence desc = estList.get(position).get(DESC);
			int premi = Integer.valueOf(estList.get(position).get(PREMI));
			estPremi.setImageResource(R.drawable.btn_premium_unpress);

			estName.setText(name);
			estAddr.setText(addr);
			estDesc.setText(desc);
			estRatingBar.setNumStars(5);
			estRatingBar.setRating(2.50f);

			switch (kind) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				estKind.setImageResource(R.drawable.btn_drink_press);
				break;
			case 6:
			case 7:
			case 8:
			case 9:
				estKind.setImageResource(R.drawable.btn_food_press);
				break;
			}

			switch (premi) {
			case 0:
				estPremi.setVisibility(View.GONE);
				break;
			case 1:
				estPremi.setVisibility(View.VISIBLE);
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
