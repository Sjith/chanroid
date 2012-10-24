package kr.co.drdesign.golffinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.drdesign.golffinder.SearchGCActivity.GetSearchResult;
import kr.co.drdesign.golffinder.SearchGCActivity.GolfLocationItemizedOverlay;
import kr.co.drdesign.golffinder.SearchGCActivity.LocationItemizedOverlay;
import kr.co.drdesign.golffinder.SearchGCActivity.MyLocationItemizedOverlay;
import kr.co.drdesign.golffinder.SearchGCActivity.MyLocationListener;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ExcelActivity 
extends GCActivity
{
	protected Location currentLocation; //현재 위치
	protected MyLocationItemizedOverlay overlayHere; //현재위치 마커가 표시되어질 오버레이
	protected GolfLocationItemizedOverlay1 overlayBranch; //지점위치 마커들이 표시되어질 오버레이
	protected long previousSelectTapTime;

	protected SearchGCInfo searchGCInfo;
	public static final String LOG_TAG = "GF";

	protected Button btnMyLocation;
	protected Button btnList;

	protected RelativeLayout lastNoteBaloon ;

	protected int radius = 5; // 검색 범위
	protected int initialZoomLevel = 17;
	public static boolean useSatellite = SearchGCActivity.useSatellite;

	private int noteBaloonWidth;
	private int noteBaloonHeight;

	private ArrayList<Map<String,String>> itemList = new ArrayList<Map<String,String>>();
	private Map<String,String> item ;
	private ExcelInfo excelInfo;
	
	// NO를 받아와서 리스트의 정보를 가져온다. 지도에 표시되는 것은 한개 아니면 두개.
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.excel_mapview2);

		Intent intent = getIntent();
		String[] NOs = getIntent().getStringArrayExtra(ExcelInfo.NO);
		if( NOs == null ) return;
		
		excelInfo = new ExcelInfo(getApplicationContext());
		setNoteBaloonSize();

		overlayBranch = null; //각 오버레이 초기화
		mapView = (MapView) findViewById(R.id.MapView); //맵뷰 객체를 가져온다.
		mapView.setBuiltInZoomControls(false); //줌인,줌아웃 컨트롤을 표시한다.
		if( initialZoomLevel <= 16 ) 
			mapView.setSatellite(useSatellite);
		else 	
			mapView.setSatellite(false);

		mapController = mapView.getController(); //맵컨트롤러를 가져온다.
		mapController.setZoom(initialZoomLevel); //초기 확대는 17정도로..

		btnMyLocation = (Button) findViewById(R.id.mBtnMyLocation);
		btnList = (Button) findViewById(R.id.mBtnList);

		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnExcelInfo);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_excel_info_select);
		btnExcelInfo.setFocusable(false);

		for( String NO : NOs)
		{
			for( Map<String, String> item : ExcelInfo.al)
			{
				if( item.get(ExcelInfo.NO).equals(NO) )
				{
					this.item = item;
					Log.i("GF", "ITEM = " + item.get(ExcelInfo.NUMBER));
					String[] coordinate = excelInfo.getCoordinate(item.get(ExcelInfo.NAME_OF_GOLFCLUB));
					if( coordinate != null && coordinate.length >= 2 )
					{
						this.item.put( ExcelInfo.LONGITUDE, coordinate[0]);
						this.item.put( ExcelInfo.LATITUDE, coordinate[1]);
					}
					itemList.add(item);
				}
			}
		}
		setOrientationView();
		new Thread( new GetGCLocation()).start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		finishChain( ExcelDetailInfoActivity.class );
	}
	protected void setOrientationView()
	{
//		 int rotation = getWindowManager().getDefaultDisplay().getOrientation();
//		 Log.i("DR", "rotation = " + rotation);
		 
		 int screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight(); 
		 int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		 int btnWidth = screenWidth/4;
		 if (screenWidth > screenHeight) 
		 {
			Button tmBtnExcelInfo	= (Button)findViewById(R.id.tmBtnExcelInfo);
			Button tmBtnFieldGC 	= (Button)findViewById(R.id.tmBtnFieldGC);
			Button tmBtnScreenGC 	= (Button)findViewById(R.id.tmBtnScreenGC);
			Button tmBtnMyGolf 		= (Button)findViewById(R.id.tmBtnMyGolf);
			 
			tmBtnExcelInfo.setWidth(btnWidth);
			tmBtnFieldGC.setWidth(btnWidth);
			tmBtnScreenGC.setWidth(btnWidth);
			tmBtnMyGolf.setWidth(btnWidth);
		 }
	}
	
	private void setNoteBaloonSize()
	{
		Resources r = getResources(); 
		noteBaloonWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
		noteBaloonHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 115, r.getDisplayMetrics());
	}


	public void onClick(View view)
	{
		switch( view.getId() )
		{
		case R.id.mBtnMyLocation:
			
			break;
		case R.id.mBtnList:
			finish();
			break;
		default:
			super.onClick(view);
			break;
		}
		
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
			SearchFieldGCInfo.itemList.clear();
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
	}

	protected void clearOverlays()
	{
		//기존에 화면에 찍어둔 오버레이 (마커들)을 싹 지운다.
		listOfOverlays = mapView.getOverlays(); //맵뷰에서 오버레이 리스트를 가져온다.
		if (listOfOverlays.size() > 0) {
			listOfOverlays.clear(); //오버레이가 있을때 싹 지워준다.
			Log.d(LOG_TAG, "clear overlays : " + listOfOverlays.size());
		} else {
			Log.d(LOG_TAG, "empty overlays");
		}
	}
	private void updateItemOverlay(){
		// draw branches
		Drawable branchMarker;
		branchMarker = getResources().getDrawable(R.drawable.ic_golf);
		branchMarker.setBounds(0, 0, branchMarker.getIntrinsicWidth(),
				branchMarker.getIntrinsicHeight());
		overlayBranch = new GolfLocationItemizedOverlay1(branchMarker);
		overlayBranch.mPopulate();
		GeoPoint branchGeoPoint = getItemLocation( item );

		OverlayItem overlayItem 
		= new OverlayItem(branchGeoPoint, "branch", item.get(ExcelInfo.NAME_OF_GOLFCLUB));
		overlayBranch.addOverlay(overlayItem);
		overlayBranch.mPopulate();


		//마커 찍은것이 없으면 오류 메세지를 토스트로 보여준다.
		if (overlayBranch.size() < 1){
			Toast.makeText(getApplicationContext(),
					"검색결과가 없거나 통신장애 입니다.\n 잠시후 다시 검색해 주세요.",
					Toast.LENGTH_LONG).show();
		}

		//지점 오버레이를 맵뷰 오버레이에 최종적으로 추가해준다.
		if (overlayBranch != null) {
			mapView.getOverlays().add(overlayBranch);
			mapView.postInvalidate();
		}
	}

	private GeoPoint getItemLocation( Map<String,String> item)
	{

		String sLatitude = item.get( ExcelInfo.LATITUDE );
		String sLongitude = item.get( ExcelInfo.LONGITUDE );
		if( sLatitude != null && sLongitude != null)
		{
			try{
				Double latitudeE6 = Double.parseDouble(sLatitude) * 1E6;
				Double longitudeE6 = Double.parseDouble(sLongitude) *1E6;
				return new GeoPoint(latitudeE6.intValue(), longitudeE6.intValue());
			}
			catch( NumberFormatException e)
			{
				Log.e("GF", "" + e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			String sAddress = item.get(ExcelInfo.ADDRESS);
			if( sAddress == null ) return null;

			Address address = excelInfo.address2Location(sAddress);
			if ( address != null)
			{
				item.put(ExcelInfo.LATITUDE, Double.toString(address.getLatitude()));
				item.put(ExcelInfo.LONGITUDE, Double.toString(address.getLongitude()));
				int latitudeE6 = (int)(address.getLatitude()* 1E6);
				int longitudeE6 = (int)(address.getLongitude() * 1E6);
				return new GeoPoint( latitudeE6, longitudeE6 );
			}else
			{
				//	String[] locationCategory = item.get(ExcelInfo.CsAddress.split(" ", 2);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("DR", "IOEXCEPTION");
		}
		//Thread로 가져오는 방법..
		return null;
	}

	private final Handler getMapdataHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.getData().getBoolean("SUCCESS_KEY")) {
				updateItemOverlay();
				moveToItemLocation( item );
			}else
			{
				Toast.makeText(getApplicationContext(), "주소를 가져올 수 없거나 잘못된 주소 입니다.", Toast.LENGTH_SHORT).show();
			}
		};
	};

	protected class GetGCLocation implements Runnable
	{
		public void run() {
			Bundle bundle = new Bundle();
			try 
			{
				for( Map<String,String> item : itemList)
				{
					GeoPoint geoPoint = getItemLocation( item );
					if (geoPoint == null) 
						{
						Log.i("DR", "GEOPOINT is NULL.");
						throw new IOException();
						}
				}
				bundle.putBoolean("SUCCESS_KEY", true); //실패하면 false 이다.
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				bundle.putBoolean("SUCCESS_KEY", false); //실패하면 false 이다.
			} 
			Message msg = getMapdataHandler.obtainMessage();
			msg.setData(bundle);
			getMapdataHandler.sendMessage(msg);
		}
	}

	protected class GolfLocationItemizedOverlay1
	extends	ItemizedOverlay<OverlayItem>
	{
		protected GeoPoint lastTap;
		protected RelativeLayout noteBaloon;
		protected List<OverlayItem> overlays;

		public GolfLocationItemizedOverlay1(Drawable defaultMarker) 
		{ 
			//오버레이 생성자
			//마커 이미지의 가운데 아랫부분이 마커에서 표시하는 포인트가 되게 한다.
			super(boundCenterBottom(defaultMarker));
			overlays = new ArrayList<OverlayItem>();
			noteBaloon = (RelativeLayout) getLayoutInflater().inflate(R.layout.map_note_info, null, false);
		}

		@Override
		protected OverlayItem createItem(int i) {
			return overlays.get(i);
		}

		@Override
		public int size() {
			return overlays.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);

		}

		public void addOverlay(OverlayItem overlay) {
			overlays.add(overlay);
			//null pointer 버그때문에 오버레이 아이템 추가후 가능한 빨리 populate 해줘야한다.
			populate();
		}

		//외부에서 마커의 populate 를 해주기 위한 메소드.
		public void mPopulate() {
			populate();
		}

		// 빈화면을 터치하면 풍선 정보창이 사라진다.
		@Override
		public boolean onTap(GeoPoint p, MapView mapView) 
		{
			Log.i("GF" , ""+previousSelectTapTime);
			Log.i("GF" , "inteval " + (System.currentTimeMillis() - previousSelectTapTime)) ;
			if( System.currentTimeMillis() - previousSelectTapTime > 1000)
			{
				Log.i("GF" , "onTap(GeoPoint " + p.getLatitudeE6() + "," + p.getLongitudeE6() +")");
				mapView.removeView(lastNoteBaloon);
			}
			return super.onTap(p, mapView);
		}


		protected void addNoteBaloon(int index)
		{
			mapView.removeView(lastNoteBaloon);
			noteBaloon.setVisibility(View.VISIBLE);

			final String name = item.get(ExcelInfo.NAME_OF_GOLFCLUB);
			final String link = item.get(ExcelInfo.HOMEPAGE);
			final String number = item.get(ExcelInfo.NUMBER);
			final String detail = null;
			final String address = item.get(ExcelInfo.ADDRESS);

			final Button btnFavorite = (Button) noteBaloon.findViewById(R.id.mbBtnFavorite);
			btnFavorite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FavoriteExcelDBHelper feDBHelper = new FavoriteExcelDBHelper(getApplicationContext());
					
					boolean isFavorite = feDBHelper.isExistFavoriteExcelByNO(item.get(ExcelInfo.NO));
					if( isFavorite )
					{
						btnFavorite.setBackgroundResource(R.drawable.btn_bookmark);
						feDBHelper.removeClubByNo(item.get(ExcelInfo.NO));
						Toast.makeText(getApplicationContext(), "관심 골프장이 해제되었습니다.", Toast.LENGTH_SHORT).show();
					}
					else
					{
						btnFavorite.setBackgroundResource(R.drawable.btn_bookmark_ok);
						feDBHelper.addClub(item);
						Toast.makeText(getApplicationContext(), "관심 골프장으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			TextView textmsg = (TextView) noteBaloon.findViewById(R.id.mbTvName);
			textmsg.setText(name);
			textmsg.setVisibility(View.VISIBLE);

			TextView tvAddress = (TextView) noteBaloon.findViewById(R.id.mbTvAddress);
			tvAddress.setText(address);
			tvAddress.setVisibility(View.VISIBLE);

			TextView tvTelnumber = (TextView) noteBaloon.findViewById(R.id.mbTvTel);
			tvTelnumber.setText(number);
			tvTelnumber.setVisibility(View.VISIBLE);


			if( number != null )
			{
				Button btnCall = (Button) noteBaloon.findViewById(R.id.mbBtnCall);
				btnCall.setVisibility(View.VISIBLE);
				btnCall.setClickable(true);
				btnCall.setFocusable(true);
				btnCall.setOnClickListener(new SearchScreenGCInfo.Call(getApplicationContext(), number));
			}
			if( link != null )
			{
				Button btnHome = (Button) noteBaloon.findViewById(R.id.mbBtnHome);
				//btnHome.setVisibility(View.VISIBLE);
				btnHome.setClickable(true);
				btnHome.setFocusable(true);
				btnHome.setOnClickListener(new SearchScreenGCInfo.OpenLink(getApplicationContext(), link));
			}
			if( detail != null)
			{
				Button btnDetail = (Button) noteBaloon.findViewById(R.id.mbBtnDetail);
				//btnDetail.setVisibility(View.VISIBLE);
				btnDetail.setClickable(true);
				btnDetail.setFocusable(true);
				btnDetail.setOnClickListener(new SearchScreenGCInfo.OpenLink(getApplicationContext(), detail));
			}

			LinearLayout linearLayout01 = (LinearLayout) noteBaloon.findViewById(R.id.LinearLayout01);
			LinearLayout linearLayout03 = (LinearLayout) noteBaloon.findViewById(R.id.LinearLayout03);

			mapView.addView(noteBaloon, new MapView.LayoutParams(noteBaloonWidth, noteBaloonHeight,lastTap,MapView.LayoutParams.BOTTOM_CENTER));

			lastNoteBaloon = noteBaloon;
		}

		@Override
		protected boolean onTap(int index) 
		{
			previousSelectTapTime = System.currentTimeMillis();

			Log.i("GF" , "onTap(" + index +")");
			mapView.getController().animateTo(overlays.get(index).getPoint());
			//마커를 눌렀을때 발생시킬 이벤트 메소드이다.
			Toast.makeText(getApplicationContext(),
					overlays.get(index).getSnippet(),
					Toast.LENGTH_SHORT)
					.show();

			lastTap = overlays.get(index).getPoint();

			// popup을 띄워서 이것저것 보여준다.
			addNoteBaloon(index);
			return false;
		}
	}
	private void moveToItemLocation( Map<String, String> item)
	{
		mapController.animateTo(getItemLocation( item ));
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exceinfo_location_category_menu, menu);
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