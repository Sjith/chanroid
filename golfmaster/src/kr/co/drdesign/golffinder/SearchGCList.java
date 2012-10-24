package kr.co.drdesign.golffinder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

abstract public class SearchGCList 
extends FinishChainActivity
{

	public static final String LATITUDE		= SearchGCInfo.LATITUDE; 
	public static final String LONGITUDE 	= SearchGCInfo.LONGITUDE;
	public static final String NAME			= SearchGCInfo.NAME;
	public static final String ADDRESS 		= SearchGCInfo.ADDRESS;
	public static final String TELEPHONE 	= SearchGCInfo.TELEPHONE;
	public static final String HOMEPAGE 	= SearchGCInfo.HOMEPAGE;
	public static final String DETAIL_VIEW	= SearchGCInfo.DETAIL_VIEW;
	public static final String PREVIEW		= SearchGCInfo.PREVIEW;

	protected SimpleAdapter mSimpleAdapter;
	protected ListView srchLv;
	protected EditText etNameOfGC;

	protected GeoPoint myGeoPoint;
	protected String searchLocation ;
	protected ProgressDialog progressDialog;

	protected FavoriteWebDBHelper fwDBHelper;
	protected int ContentViewId;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish ) return;

		setContentView(ContentViewId);

		srchLv = (ListView) findViewById(R.id.srchLvResult);

		int latitudeE6 = (int)(getIntent().getDoubleExtra(SearchGCInfo.LATITUDE, 0.0) * 1E6);
		int longitudeE6 = (int)(getIntent().getDoubleExtra(SearchGCInfo.LONGITUDE, 0.0) * 1E6);

		if( latitudeE6 != 0 && longitudeE6 != 0)
			myGeoPoint = new GeoPoint(latitudeE6, longitudeE6);

		etNameOfGC = (EditText) findViewById(R.id.searchEtName);
		etNameOfGC.setOnEditorActionListener( new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				startSearch();
				InputMethodManager imm = (InputMethodManager) getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etNameOfGC.getApplicationWindowToken(), 0);
				return false;
			}
		});
		fwDBHelper = new FavoriteWebDBHelper(getApplicationContext());
	}
	abstract protected void fillMsgList();
	abstract protected void refreshList() throws IOException, URISyntaxException;
	abstract protected void setListItemOnClickListner(View view, String latitude, String longitude);
	abstract protected void startSearch();

	@Override
	protected void onPause() {
		super.onPause();
		etNameOfGC.setText("");
	}
	protected void fillMsgList(List<Map<String,String>> list){

		if( myGeoPoint != null )
		{
			String latitude, longitude;
			Double latitudeE6, longitudeE6;
			GeoPoint geoPoint;
			for( Map<String,String> item : list)
			{
				latitude 		= item.get(LATITUDE);
				longitude		= item.get(LONGITUDE);
				latitudeE6 		= Double.parseDouble( latitude ) * 1E6;
				longitudeE6 		= Double.parseDouble( longitude ) * 1E6;
				geoPoint	= new GeoPoint(latitudeE6.intValue(), longitudeE6.intValue());
				int distance = distanceFrom( myGeoPoint, geoPoint );
				item.put(SearchGCInfo.DISTANCE, distance+"");	
			}

			list = SearchGCInfo.sortList(list, SearchGCInfo.DISTANCE);
		}
		mSimpleAdapter = new SimpleListAdapter(
				this, 
				list, 
				R.layout.gfinder_item, 
				new String[]{NAME, ADDRESS}, 
				new int[]{R.id.srchTvName, R.id.srchTvAddress});

		srchLv.setAdapter(mSimpleAdapter);
		//		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//		srchLv.addFooterView(vi.inflate(R.layout.loadmore_footer, null));
	}

	protected class SimpleListAdapter extends SimpleAdapter
	{
		protected  List<Map<String, String>> list;

		protected  TextView tvName;
		protected  TextView tvAddress;
		protected  TextView tvDistance;
		protected  Button btnCall;
		protected  Button btnLink;
		protected  Button btnDetail;
		protected  Button btnMap;
		protected  ImageView ivPreview;

		public SimpleListAdapter(Context context, 
				List<Map<String, String>> list, int resource, String[] from, int[] to)
		{
			super(context, list, resource, from, to);
			this.list = list;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.gfinder_item, null);
			}

			final String name 		= list.get(position).get(NAME);
			final String number 	= list.get(position).get(TELEPHONE);
			final String link 		= list.get(position).get(HOMEPAGE);
			final String latitude	= list.get(position).get(LATITUDE);
			final String longitude	= list.get(position).get(LONGITUDE);
//			final String detail 	= list.get(position).get(DETAIL_VIEW);
//			final String previewURL	= list.get(position).get(PREVIEW);
			final String address	= list.get(position).get(ADDRESS);
			final Map<String, String> item = list.get(position);

			tvName 		= (TextView)view.findViewById(R.id.srchTvName);
			tvAddress 	= (TextView)view.findViewById(R.id.srchTvAddress);
			tvDistance 	= (TextView)view.findViewById(R.id.srchTvDistance);

			tvName.setText(name);
			String distance = item.get(SearchGCInfo.DISTANCE);
			if( myGeoPoint != null && distance != null)
			{
				if ( distance.equals("0") )
					tvAddress.setText(Html.fromHtml( address + "  <b><u><color=#000000>" + "1Km이하" + "</color></b></u>" ));
				else
					tvAddress.setText(Html.fromHtml( address + "  <b><u><color=#000000>" + distance + "Km" + "</color></b></u>" ));
			}
			else 
			{
				tvAddress.setText(Html.fromHtml( address ));
			}
			btnCall 	= (Button)view.findViewById(R.id.srchBtnCall);
			btnLink 	= (Button)view.findViewById(R.id.srchBtnLink);
			btnDetail 	= (Button)view.findViewById(R.id.srchBtnDetail);
			btnMap 		= (Button)view.findViewById(R.id.srchBtnLocation);
			ivPreview 	= (ImageView)view.findViewById(R.id.scrhIV);

			if( number != null ) 
			{
				btnCall.setVisibility(View.VISIBLE);
				btnCall.setOnClickListener(new SearchScreenGCInfo.Call(getApplicationContext(), number));
			}else
			{
				btnCall.setVisibility(View.GONE);
			}

//			if( detail != null )
//			{
//				btnDetail.setVisibility(View.VISIBLE);
//				btnDetail.setOnClickListener( new SearchScreenGCInfo.OpenLink(getApplicationContext(), detail, item));
//			}else
//			{
//				btnDetail.setVisibility(View.GONE);
//			}

			if( link != null )
			{
				btnLink.setVisibility(View.VISIBLE);
				btnLink.setOnClickListener( new SearchScreenGCInfo.OpenLink(getApplicationContext(), link, item));
				btnDetail.setVisibility(View.GONE);
			}else
			{
				btnLink.setVisibility(View.GONE);
			}

			btnLink.setVisibility(View.GONE);
			btnDetail.setVisibility(View.GONE);

			if( myGeoPoint != null && myGeoPoint.getLatitudeE6() > 0 && myGeoPoint.getLongitudeE6() > 0)
			{
				//tvDistance.setVisibility(View.VISIBLE);
				//tvDistance.setText( distanceFrom( myGeoPoint, geoPoint ));
			}else
			{
				tvDistance.setVisibility(View.GONE);
			}

//			Log.i("GF", "previewURL = " + previewURL);
//			if( previewURL != null)
//			{
//				try{
//					URL imageURL = new URL("http://" + previewURL );
//					HttpURLConnection conn = (HttpURLConnection)imageURL.openConnection();             
//					BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 10240);
//					Bitmap bm = BitmapFactory.decodeStream(bis);
//					ivPreview.setImageBitmap(bm);
//					bis.close();
//				}catch( IOException ioe )
//				{
//					Log.e("GF", ioe.getMessage());
//				}
//			}else
//			{
//				ivPreview.setVisibility(View.GONE);
//			}


//			btnMap.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent();
//					intent.setAction(Intent.ACTION_VIEW);
//					intent.addCategory(Intent.CATEGORY_BROWSABLE);
//					if( myGeoPoint != null && myGeoPoint.getLatitudeE6() > 0 && myGeoPoint.getLongitudeE6() > 0)
//					{
//						double slatitude 	= (double)myGeoPoint.getLatitudeE6() / 1E6;
//						double slongitude 	= (double)myGeoPoint.getLongitudeE6() / 1E6;
//						intent.setData(
//								Uri.parse("http://maps.google.com/maps?" 
//										+ "saddr=현재위치@" + slatitude + "," + slongitude + "&daddr=" + address + "@" + latitude + "," + longitude));
//						Log.i("GF", ("http://maps.google.com/maps?" 
//								+ "saddr=@" + slatitude + "," + slongitude + "&daddr=" + address + "@" + latitude + "," + longitude));
//					}
//					else
//					{
//						intent.setData(Uri.parse("http://maps.google.com/maps?" + "daddr=" + address + "@" + latitude + "," + longitude));	
//					}
//					startActivity(intent);
//				}
//			});


			final Button btnFavorite = (Button) view.findViewById(R.id.srchBtnFavorite);
			if( fwDBHelper.isExistFavoriteWebByIndex(number) )
			{
				btnFavorite.setBackgroundResource(R.drawable.btn_bookmark_ok);
			}
			else
			{
				btnFavorite.setBackgroundResource(R.drawable.btn_bookmark);
			}
			btnFavorite.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickFavorite( item, v );
				}
			});

			setListItemOnClickListner( view,  latitude, longitude);
			return view;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		etNameOfGC.setText("");
	}
	protected void onClickFavorite(Map<String, String> item, View view) {
		// TODO Auto-generated method stub
		if( fwDBHelper.isExistFavoriteWebByIndex(item.get(TELEPHONE)) == false)
		{
			if(  fwDBHelper.addClub(item) )
			{
				view.setBackgroundResource(R.drawable.btn_bookmark_ok);
			}
			Toast.makeText(getApplicationContext(), "관심 골프장으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
		}else
		{
			fwDBHelper.removeClubByName(item.get(SearchGCInfo.TELEPHONE));
			view.setBackgroundResource(R.drawable.btn_bookmark);
			Toast.makeText(getApplicationContext(), "관심 골프장이 해제되었습니다" , Toast.LENGTH_SHORT).show();
		}

	}

	public int distanceFrom(Location p1, GeoPoint p2)
	{
		double p1Longitude 	= p1.getLongitude();
		double p1Latitude 	= p1.getLatitude();
		double p2Longitude 	= ((double)p2.getLongitudeE6())/1E6;
		double p2Latitude 	= ((double)p2.getLatitudeE6())/1E6;

		return distanceFrom(p1Latitude, p1Longitude, p2Latitude, p2Longitude);
	}
	public int distanceFrom(GeoPoint p1, GeoPoint p2)
	{
		double p1Longitude 	= ((double)p1.getLongitudeE6())/1E6;
		double p1Latitude 	= ((double)p1.getLatitudeE6())/1E6;
		double p2Longitude 	= ((double)p2.getLongitudeE6())/1E6;
		double p2Latitude 	= ((double)p2.getLatitudeE6())/1E6;

		return distanceFrom(p1Latitude, p1Longitude, p2Latitude, p2Longitude);
	}

	public int distanceFrom(double p1Latitude, double p1Longitude, double p2Latitude, double p2Longitude)
	{
		double R 	= 6371; // Radius of the earth in km
		double dLat = Math.abs(p2Latitude-p1Latitude);  // Javascript functions in radians
		double dLon = Math.abs(p2Longitude-p1Longitude);
		dLat = Math.toRadians(dLat);
		dLon = Math.toRadians(dLon);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.cos( Math.toRadians(p1Latitude) ) * Math.cos(Math.toRadians(p2Latitude)) * 
		Math.sin(dLon/2) * Math.sin(dLon/2); 

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c; // Distance in km
		int distance = (int)Math.round(d);
		return distance;
	}

	private class SelectLocalDialogListener 
	implements DialogInterface.OnClickListener
	{
		private int selectItemPostion;
		@Override
		public void onClick(DialogInterface dialog, int which) 
		{
			switch( which )
			{
			case DialogInterface.BUTTON_POSITIVE :
				String[] city = getResources().getStringArray(R.array.city);
				showSelectLocalDialog( city[selectItemPostion] );
				break;
			case DialogInterface.BUTTON_NEGATIVE :
				break;
			default:
				selectItemPostion = which;
			}
		}
	};
	protected void showSelectLocalDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("지역별 골프장 상세 검색");
		SelectLocalDialogListener sldialogListener = new SelectLocalDialogListener();
		builder.setSingleChoiceItems(R.array.city, 0, sldialogListener);
		builder.setPositiveButton("OK", 	sldialogListener);
		builder.setNegativeButton("cancel",	sldialogListener);
		builder.create().show();
	}


	private void showSelectLocalDialog(String location)
	{	
		String[] city 			= getResources().getStringArray(R.array.gu);
		ArrayList<String> al 	= new ArrayList<String>();
		for( String str : city )
			if ( str.contains(location) )
				al.add(str);
		final String[] detailLocations = new String[al.size()];
		al.toArray(detailLocations);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("지역별 골프장 상세 검색");
		searchLocation = detailLocations[0];
		builder.setSingleChoiceItems(detailLocations, 0,  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				searchLocation = detailLocations[which];
				Log.i("DR", "searchLocation = " + searchLocation + " " + which);
			}
		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 새롭게 검색해서 목록을 받아오는 녀석이 필요함.
				Toast.makeText(getApplicationContext(), searchLocation + "으로 다시 검색", Toast.LENGTH_SHORT).show();
				try {
					refreshList();
					fillMsgList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton("cancel", null);
		builder.create().show();
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
		default :
			break;
		}
		switch(view.getId())
		{
		case R.id.searchBtnRun:
			startSearch();
			InputMethodManager imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etNameOfGC.getApplicationWindowToken(), 0);
			break;
		}
	}

	protected ProgressDialog showDialog()
	{
		if( progressDialog == null ) 
			progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("결과를 불러오는 중입니다...");
		progressDialog.show();
		return progressDialog;
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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected final Handler getMapdataHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.getData().getBoolean("SUCCESS_KEY") == false) {
				Toast.makeText(getApplicationContext(),
						"검색결과가 없거나 통신장애 입니다.\n 잠시후 다시 검색해 주세요.",
						Toast.LENGTH_LONG).show();
			}
			else
			{
				fillMsgList();
			}
			progressDialog.dismiss();
		};
	};
}