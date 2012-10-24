package kr.co.drdesign.golffinder;

import kr.co.drdesign.util.Utillity;
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

public class SearchScreenGCActivity 
extends SearchGCActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContentViewId = R.layout.gfinder_mapview;
		super.onCreate(savedInstanceState);

		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish ) return;
		
		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnScreenGC);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_screen_gc_select);
		btnExcelInfo.setFocusable(false);
	}

	@Override
	protected void setSearchInfo() {
		if( searchGCInfo == null ) 
			searchGCInfo = new SearchScreenGCInfo(getApplicationContext());
		itemList = SearchScreenGCInfo.itemList;
	}

	@Override
	protected void onPause() {
		SearchScreenGCInfo.itemList = itemList;
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i("DR", "RESUME = " + getIntent().getBooleanExtra("FINISH", false));
		
		if( Utillity.searchGCActivity )
		{
			Utillity.searchGCActivity = false;
			finish();
			Intent intent = new Intent( getApplicationContext(), MenuActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity( intent );
			SearchScreenGCInfo.itemList.clear(); 
		}
		itemList = SearchScreenGCInfo.itemList;
		super.onResume();
		
		finishChain(SearchFieldGCActivity.class);
	}

	@Override
	public void onClick(View view) {

		super.onClick(view);
		switch( view.getId() ){
		case R.id.mBtnList :
			if( itemList.size() ==0 ) break;
			Intent intent  = new Intent( getApplicationContext(), SearchScreenGCList.class);
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
		inflater.inflate(R.menu.screen_gc_list_menu, menu);

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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
