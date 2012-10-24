package kr.co.drdesign.golffinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FavoriteActivity 
extends FinishChainActivity
implements View.OnClickListener
{

	protected ArrayList<Map<String,String>> feList = new ArrayList<Map<String,String>> ();
	protected ArrayList<Map<String,String>> fwList = new ArrayList<Map<String,String>> ();
	protected ArrayList<Map<String,String>> frList = new ArrayList<Map<String,String>> ();
	protected ArrayList<Map<String,String>> frwList = new ArrayList<Map<String,String>> ();

	protected SimpleList simpleFrwList;
	protected SimpleList simpleFwList;
	protected SimpleExcelList simpleFeList;
	protected SimpleExcelList simpleFrList;

	protected Set<View> checkboxes = new HashSet<View> ();
	protected Set<View> icons = new HashSet<View>();

	//TEST
	protected TextView tv1;
	protected TextView tv2;
	protected TextView tv3;

	//Layout
	protected RelativeLayout lExcel;
	protected RelativeLayout lWeb;
	protected RelativeLayout lRecentExcel;
	protected RelativeLayout lRecentWeb;
	protected ViewGroup lDel;

	protected ExcelInfo excelInfo;

	protected FavoriteExcelDBHelper 	feDBHelper;
	protected FavoriteWebDBHelper 		fwDBHelper;
	protected FavoriteRecentDBHelper 	frDBHelper;
	protected FavoriteRecentWebDBHelper frwDBHelper;

	private Button btnDel;
	private Button btnFinish;

	protected LinearLayout flLlExcel;
	protected LinearLayout flLlWeb;
	protected LinearLayout flLlExcelRecent;
	protected LinearLayout flLlWebRecent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.favorite_list);

		tv1 = (TextView) findViewById(R.id.flTvExcelList);
		tv2 = (TextView) findViewById(R.id.flTvWebList);
		tv3 = (TextView) findViewById(R.id.flTvScreenList);

		excelInfo 	= new ExcelInfo(getApplicationContext());
		feDBHelper 	= new FavoriteExcelDBHelper(getApplicationContext());
		fwDBHelper 	= new FavoriteWebDBHelper(getApplicationContext());
		frDBHelper 	= new FavoriteRecentDBHelper(getApplicationContext());
		frwDBHelper = new FavoriteRecentWebDBHelper(getApplicationContext());

		lExcel	= (RelativeLayout) findViewById(R.id.flLayoutExcel);
		lWeb  	= (RelativeLayout) findViewById(R.id.flLayoutWeb);
		lRecentExcel	= (RelativeLayout) findViewById(R.id.flLayoutRecentExcel);
		lRecentWeb 		= (RelativeLayout) findViewById(R.id.flLayoutRecentWeb);
		lDel	= (ViewGroup) findViewById(R.id.flDel);

		btnDel 	= (Button) findViewById(R.id.BtnDel);
		btnDel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				int cnt = 
					simpleFeList.selectedList.size() +
					simpleFwList.selectedList.size() +
					simpleFrList.selectedList.size() +
					simpleFrwList.selectedList.size();

				if( cnt > 0 )
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(FavoriteActivity.this);
					dialog.setTitle("경고");
					dialog.setMessage("선택된 " + cnt + "개의 관심 골프장 리스트가 지워집니다. 계속 하시겠습니까?");
					dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteEditingList();
							
							if( simpleFrwList != null )simpleFrwList.selectedList.clear();
							if( simpleFwList != null )simpleFwList.selectedList.clear();
							if( simpleFeList != null )simpleFeList.selectedList.clear();
							if( simpleFrList != null )simpleFrList.selectedList.clear();
							
							fillAllList();
						}
					});
					dialog.setNegativeButton("아니요", null);
					dialog.show();
				}
			}
		});
		btnFinish 	= (Button) findViewById(R.id.BtnFinish);
		btnFinish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				lDel.setVisibility(View.GONE);
				
				if( simpleFrwList != null )simpleFrwList.selectedList.clear();
				if( simpleFwList != null )simpleFwList.selectedList.clear();
				if( simpleFeList != null )simpleFeList.selectedList.clear();
				if( simpleFrList != null )simpleFrList.selectedList.clear();
				
				for( View cb : checkboxes)
				{
					((CheckBox)cb).setChecked(false);	
				}
				
				showIcons();
			}
		});

		flLlExcelRecent = (LinearLayout)findViewById(R.id.flLlExcelRecent);
		flLlWebRecent 	= (LinearLayout)findViewById(R.id.flLlWebRecent);
		flLlExcel 		= (LinearLayout)findViewById(R.id.flLlExcel);
		flLlWeb 		= (LinearLayout)findViewById(R.id.flLlWeb);

		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnMyGolf);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_mygolf_select);
		btnExcelInfo.setFocusable(false);

		fillAllList();
	}

	@Override
	protected void onResume() 
	{
		lDel.setVisibility(View.GONE);
		super.onResume();
		fillAllList();
		
	}

	private void deleteEditingList()
	{
		for( Map<String,String> item : simpleFeList.selectedList)
		{
			feDBHelper.removeClubByNo(item.get(ExcelInfo.NO));
		}
		for( Map<String,String> item : simpleFwList.selectedList)
		{
			fwDBHelper.removeClubByName(item.get(SearchGCInfo.TELEPHONE));
		}
		for( Map<String,String> item :  simpleFrList.selectedList)
		{
			frDBHelper.removeClubByNo(item.get(ExcelInfo.NO));
		}
		for( Map<String,String> item :  simpleFrwList.selectedList)
		{
			frwDBHelper.removeClubByTelephone(item.get(SearchGCInfo.TELEPHONE));
		}
		Log.i("DR", "simpleFrwList.selectedList = " + simpleFrwList.selectedList.size());

		simpleFeList.selectedList.clear();
		simpleFwList.selectedList.clear();
		simpleFrList.selectedList.clear();
		simpleFrwList.selectedList.clear();
	}
	private void showCheckBoxes()
	{
		lDel.setVisibility(View.VISIBLE);
		for( View v : checkboxes)
		{
			v.setVisibility(View.VISIBLE);	
		}
		for( View v : icons)
		{
			v.setVisibility(View.GONE);	
		}

	}
	private void showIcons()
	{
		lDel.setVisibility(View.GONE);
		for( View v : icons)
		{
			v.setVisibility(View.VISIBLE);	
		}
		for( View v : checkboxes)
		{
			v.setVisibility(View.GONE);	
		}

	}

	private void fillAllList()
	{
		feList = new ArrayList<Map<String, String>>();;
		for( Map<String,String> item : feDBHelper.getFavoriteExcelList() )
		{
			String no = item.get(ExcelInfo.NO);
			feList.add( excelInfo.getItemByNO(no) );
		}

		fwList = fwDBHelper.getFavoriteWebList();
		frList = new ArrayList<Map<String, String>>();
		for( Map<String,String> item : frDBHelper.getFavoriteList() )
		{
			String no = item.get("NO");
			frList.add( excelInfo.getItemByNO(no) );
		}
		frwList = frwDBHelper.getFavoriteList();

		fillExcelList();
		fillWebList();
		fillRecentExcelList();
		fillRecentWebList();
	}
	private final Handler favoriteListHandler = new Handler() {
		public void handleMessage(Message msg) 
		{
			fillAllList();
		};
	};

	/////////////////////////////////////
	protected class SimpleExcelList
	{
		protected List<Map<String, String>> list;
		public List<Map<String, String>> selectedList =  new ArrayList<Map<String,String>> ();;

		LinearLayout parentLayout;
		private String priviousLocation = "";
		boolean showLocationCategory;

		public SimpleExcelList( 
				List<Map<String, String>> list,
				LinearLayout parentLayout,
				boolean showLocationCategory)
		{
			this.list = list;
			this.parentLayout = parentLayout;
			this.showLocationCategory = showLocationCategory;
		}

		public void fillList()
		{
			parentLayout.removeAllViews();
			int i = 0;
			for( Map<String,String> item : list)
			{
				parentLayout.addView( getView1( i, null, null ) );
				i++;
			}
		}

		public View getView1( int position, View view,Object o1 )
		{
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.excel_item, null);
			}
			TextView tvName 	= ( TextView ) view.findViewById(R.id.eiTvName);
			TextView tvAddress 	= ( TextView ) view.findViewById(R.id.eiTvAddress);
			CheckBox cbItem = (CheckBox)view.findViewById(R.id.CheckBox);
			View eiBullet = (View)view.findViewById(R.id.eiBullet);

			if( lDel.getVisibility() == View.GONE )
			{
				eiBullet.setVisibility(View.VISIBLE);
				cbItem.setVisibility(View.GONE);
			}else
			{
				eiBullet.setVisibility(View.GONE);
				cbItem.setVisibility(View.VISIBLE);
			}

			final Map<String,String> item = list.get(position);

			checkboxes.add(cbItem);
			icons.add(eiBullet);

			if( selectedList.contains(item) )
				cbItem.setChecked(true);
			else
				cbItem.setChecked(false);
			cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if( isChecked )
					{
						selectedList.add(item);						
					}else
					{
						selectedList.remove(item);
					}
					Log.i("DR", "selectedList.size() = " + selectedList.size() );
				}
			});

			tvName.setText( item.get(ExcelInfo.NAME_OF_GOLFCLUB));
			tvAddress.setText( item.get(ExcelInfo.ADDRESS));

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent( FavoriteActivity.this, ExcelDetailInfoActivity.class );
					intent.putExtra( ExcelInfo.NO, item.get(ExcelInfo.NO) );
					startActivity( intent );
				}
			});
			return view;
		}

	}
	//////////////////////////////

	protected class SimpleList {
		protected  List<Map<String, String>> list;
		public List<Map<String, String>> selectedList = new ArrayList<Map<String,String>> ();

		private String priviousLocation = "";
		private TextView tvName;

		LinearLayout parentLayout;

		private boolean isShowParentLocation;

		public SimpleList( 
				List<Map<String, String>> list,
				LinearLayout parentLayout, boolean isShowParentLocation)
		{
			this.list = list;
			this.parentLayout = parentLayout;
			this.isShowParentLocation = isShowParentLocation;
		}

		public void fillList(){

			parentLayout.removeAllViews();
			int size = list.size();
			for( int i  = 0 ; i < size ; i++)
			{
				parentLayout.addView( getView1( i, null, null ) );
			}
		}
		public View getView1(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.excel_item, null);
			}
			tvName = ( TextView ) view.findViewById(R.id.eiTvName);
			TextView tvAddress = ( TextView ) view.findViewById(R.id.eiTvAddress);

			CheckBox cbItem = (CheckBox)view.findViewById(R.id.CheckBox);
			View eiBullet = (View)view.findViewById(R.id.eiBullet);

			if( lDel.getVisibility() == View.GONE )
			{
				eiBullet.setVisibility(View.VISIBLE);
				cbItem.setVisibility(View.GONE);
			}else
			{
				eiBullet.setVisibility(View.GONE);
				cbItem.setVisibility(View.VISIBLE);
			}
			final Map<String,String> item = list.get(position);
			checkboxes.add(cbItem);
			icons.add(eiBullet);
			
			if( selectedList.contains(item) )
				cbItem.setChecked(true);
			else
				cbItem.setChecked(false);
			
			cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if( isChecked )
					{
						selectedList.add(item);						
					}else
					{
						selectedList.remove(item);
					}

				}
			});

			tvName.setText( item.get(SearchGCInfo.NAME));
			tvAddress.setText( item.get(SearchGCInfo.ADDRESS));


			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent( getApplicationContext(), GCSimpleMapActivity.class);
					intent.putExtra(SearchGCInfo.LATITUDE, 	item.get(SearchGCInfo.LATITUDE));
					intent.putExtra(SearchGCInfo.LONGITUDE, item.get(SearchGCInfo.LONGITUDE));
					intent.putExtra(SearchGCInfo.NAME, 		item.get(SearchGCInfo.NAME));
					intent.putExtra(SearchGCInfo.ADDRESS,	item.get(SearchGCInfo.ADDRESS));
					intent.putExtra(SearchGCInfo.TELEPHONE,	item.get(SearchGCInfo.TELEPHONE));
					startActivity(intent);
				}
			});
			return view;
		}
	}

	private String normalizingNumber( String number )
	{
		// '/n' 제거
		int idx = 0;
		idx = number.indexOf('\n');
		if( idx > 0 )
			number = number.substring(0, number.indexOf('\n'));

		idx = number.indexOf('~');
		if( idx > 0 )
			number = number.substring(0, number.indexOf('~'));

		idx = number.lastIndexOf('-');
		if( idx > 10 )
			number = number.substring(0, number.lastIndexOf('-'));
		return number;
	}


	public void fillExcelList()
	{
		TextView tv =(TextView) findViewById(R.id.TextView01);
		if( feList != null && feList.size() >= 0)
		{
			simpleFeList = new SimpleExcelList(feList, flLlExcel, false);
			simpleFeList.fillList();
			tv.setVisibility(View.VISIBLE);
		}
		if( feList != null && feList.size() > 0)
		{
			tv.setVisibility(View.GONE);
		}
	}

	public void fillWebList()
	{
		TextView tv =(TextView) findViewById(R.id.TextView02);
		if( fwList != null )
		{
			simpleFwList = new SimpleList(fwList, flLlWeb, false);
			simpleFwList.fillList();
			tv.setVisibility(View.VISIBLE);
		}
		if( fwList != null && fwList.size() > 0)
		{

			tv.setVisibility(View.GONE);
		}
	}

	public void fillRecentExcelList()
	{
		TextView tv =(TextView) findViewById(R.id.TextView03);
		if( frList != null && frList.size() >= 0)
		{
			simpleFrList = new SimpleExcelList(frList, flLlExcelRecent, false);
			simpleFrList.fillList();
			tv.setVisibility(View.VISIBLE);
		}
		if( frList != null && frList.size() > 0)
		{
			tv.setVisibility(View.GONE);
		}
		Log.i("GF", "FRLIST.size() = " + frList.size());
	}

	public void fillRecentWebList()
	{
		TextView tv =(TextView) findViewById(R.id.TextView04);
		if( frwList != null && frwList.size() >= 0)
		{
			simpleFrwList = new SimpleList(frwList, flLlWebRecent, false);
			simpleFrwList.fillList();
			tv.setVisibility(View.VISIBLE);
		}
		if( frwList != null && frwList.size() > 0)
		{
			tv.setVisibility(View.GONE);
		}
		Log.i("GF", "FRWLIST.size() = " + frwList.size());
	}
	public void onClick(View view)
	{
		Intent intent;
		switch( view.getId() )
		{
		case R.id.tmBtnExcelInfo:
			intent = new Intent( getApplicationContext(), ExcelLocationCategoryActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.tmBtnFieldGC:
			intent = new Intent( getApplicationContext(), SearchFieldGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.tmBtnScreenGC:
			intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.tmBtnMyGolf:
			intent = new Intent( getApplicationContext(), FavoriteActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.flTvExcelList:
			if( flLlExcel.getVisibility() == View.VISIBLE )
				flLlExcel.setVisibility(View.GONE);
			else
				flLlExcel.setVisibility(View.VISIBLE);
			break;
		case R.id.flTvScreenList:
			if( flLlExcelRecent.getVisibility() == View.VISIBLE )
				flLlExcelRecent.setVisibility(View.GONE);
			else
				flLlExcelRecent.setVisibility(View.VISIBLE);
			break;
		case R.id.flTvWebList:
		case R.id.Button01:
			if( flLlWeb.getVisibility() == View.VISIBLE )
				flLlWeb.setVisibility(View.GONE);
			else
				flLlWeb.setVisibility(View.VISIBLE);
			break;
		default :
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mygolf_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		Intent intent;
		switch( item.getItemId() )
		{


		case R.id.btnFeildGC:
			intent = new Intent( getApplicationContext(), SearchFieldGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnScreenGC:
			intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnExcelList:
			intent = new Intent( getApplicationContext(), ExcelLocationCategoryActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnHome:
			intent = new Intent( getApplicationContext(), MenuActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity( intent );
			break;
		case R.id.btnDeleteFavorite:

			int cnt  = feList.size() + fwList.size() + frList.size() + frwList.size();
			if( cnt == 0 ) 
				showIcons();
			else if( lDel.getVisibility() == View.VISIBLE)
				showIcons();
			else
				showCheckBoxes();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
