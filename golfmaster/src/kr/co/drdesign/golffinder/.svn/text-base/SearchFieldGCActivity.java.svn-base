package kr.co.drdesign.golffinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.widget.Button;

public class SearchFieldGCActivity  
extends SearchGCActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContentViewId = R.layout.gfinder_mapview;
		super.onCreate(savedInstanceState);
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish ) return;
		
		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnFieldGC);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_field_gc_select);
		btnExcelInfo.setFocusable(false);
	}

	@Override
	protected void setSearchInfo() {
		if( searchGCInfo == null ) 
			searchGCInfo = new SearchFieldGCInfo(getApplicationContext());
		itemList = SearchFieldGCInfo.itemList;
	}

	@Override
	protected void onPause() {
		SearchFieldGCInfo.itemList = itemList;
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		itemList = SearchFieldGCInfo.itemList;
		super.onResume();
		
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish )
		{
			finish();
		}
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch( view.getId() )
		{
		case R.id.mBtnList :
			if( itemList.size() ==0 ) break;
			Intent intent  = new Intent( getApplicationContext(), SearchFieldGCList.class);
			if( currentLocation != null )
			{
				intent.putExtra(SearchGCInfo.LATITUDE, currentLocation.getLatitude());
				intent.putExtra(SearchGCInfo.LONGITUDE, currentLocation.getLongitude());
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.field_gc_list_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		Intent intent;
		switch( item.getItemId() )
		{
		case R.id.btnHome:
			intent = new Intent( getApplicationContext(), MenuActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity( intent );
			break;
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
		case R.id.btnFavorite:
			intent = new Intent( getApplicationContext(), FavoriteActivity.class );
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
//		case R.id.btnLocation:
//			showLocation(null);		
//			if( rlLocation.getVisibility() == View.VISIBLE )
//				rlLocation.setVisibility(View.GONE);
//			else{
//				rlLocation.setVisibility(View.VISIBLE);
//			}
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}