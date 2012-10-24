package kr.co.drdesign.golffinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public abstract class SearchGCActivity 
extends MapActivity{

	protected MapView mapView; //맵뷰 객체
	protected List<Overlay> listOfOverlays; //맵에 표시된 오버레이(레이어)들을 가지고 있는 리스	트
	private String bestProvider; //현재 위치값을 가져오기위한 프로바이더. (network, gps)
	private static LocationManager locM; //위치 매니저
	private static LocationListener locL; //위치 리스너
	protected Location currentLocation; //현재 위치
	private MapController mapController; //맵을 줌시키거나, 이동시키는데 사용될 컨트롤러
	protected MyLocationItemizedOverlay overlayHere; //현재위치 마커가 표시되어질 오버레이
	protected GolfLocationItemizedOverlay overlayBranch; //지점위치 마커들이 표시되어질 오버레이
	private long previousSelectTapTime;

	protected ProgressDialog progressDialog;
	protected SearchGCInfo searchGCInfo;
	public static final String LOG_TAG = "GF";

	protected Button btnMyLocation;
	protected Button btnList;
	protected static ArrayList<Map<String, String>> itemList;

	protected RelativeLayout lastNoteBaloon ;


	protected int initialZoomLevel = 17;
	public static boolean useSatellite = false;

	protected int ContentViewId; 
	private int noteBaloonWidth;
	private int noteBaloonHeight;

	protected EditText etName;
	protected Button btnSearch;

	protected String gcCategory;

	protected RelativeLayout rlLocation;
	protected EditText etSAddr;
	protected EditText etDAddr;
	protected Button btnSAddr;
	protected Button btnDAddr;
	protected Button btnLocationRun;
	protected Map<String,String> destTempItem;
	protected Map<String,String> destItem;

	protected boolean pressdBtnSAddr = false;

	protected void finishChain( Class mClass )
	{
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish )
		{
			finish();
			if( mClass != null )
			{
				Intent intent = new Intent( getApplicationContext(), mClass);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("FINISH", true);
				startActivity( intent );
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish ) return;

		setContentView(ContentViewId);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			createGpsDisabledAlert();
		}
		else{
			setNoteBaloonSize();

			setSearchInfo();

			overlayHere = null;
			overlayBranch = null; //각 오버레이 초기화
			mapView = (MapView) findViewById(R.id.MapView); //맵뷰 객체를 가져온다.
			mapView.setBuiltInZoomControls(false); //줌인,줌아웃 컨트롤을 표시한다.

			mapController = mapView.getController(); //맵컨트롤러를 가져온다.
			mapController.setZoom(initialZoomLevel); //초기 확대는 17정도로..

			btnMyLocation = (Button) findViewById(R.id.mBtnMyLocation);
			btnList = (Button) findViewById(R.id.mBtnList);
			etName = (EditText) findViewById(R.id.searchEtName);
			btnSearch = (Button) findViewById(R.id.searchBtnRun);

			Log.i("GF", "refresh = " + (itemList == null || itemList.size() == 0));
			if( itemList == null || itemList.size() == 0 )
				refreshMyLocation();
			else
				refreshMyLocation(false);

			etSAddr = (EditText) findViewById(R.id.etSAddr);
			etDAddr = (EditText) findViewById(R.id.etDAddr);
			btnSAddr = (Button) findViewById(R.id.btnSAddr);
			btnDAddr = (Button) findViewById(R.id.btnDAddr);
			btnLocationRun 	= (Button) findViewById(R.id.btnLocationRun);
			rlLocation = ( RelativeLayout)findViewById(R.id.RlLocation);

			setOrientationView();

			etName.setOnEditorActionListener( new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					showDialog();
					new Thread( new GetSearchNameResult()).start();
					InputMethodManager imm = (InputMethodManager) getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etName.getApplicationWindowToken(), 0);
					return false;
				}
			});
//			etName.setFocusable(true);
//			etName.setClickable(true);
//			etName.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) 
//				{
//					etName.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//					String text = etName.getText().toString();
//					if( text == null || text.length() == 0 || text.equals("골프장명입력") ) 
//						etName.setText("");
//					etName.setTextColor(Color.BLACK);
//				}
//			});
		}
	}

	@Override
	protected void onDestroy() {
		if( progressDialog != null ) progressDialog.dismiss();
		progressDialog = null;
		super.onDestroy();
	}


	protected void setOrientationView()
	{
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
	protected void refreshMyLocation()
	{
		refreshMyLocation(true);
	}
	protected void refreshMyLocation(boolean isResearchInfo )
	{
		//위치 매니저를 시스템으로부터 받아온다.
		if( locM == null )
			locM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//사용가능한 적절한 프로바이더를 받아온다.
		//network (보통 3G망,Wifi AP 위치정보)또는 gps 둘중 하나로 설정된다.
		bestProvider = locM.getBestProvider(new Criteria(), true);
		//bestProvider = locM.GPS_PROVIDER;
		//halftale
		Log.i("GF", "bestProvider is " + bestProvider);
		//기기에 가지고 있는 마지막 위치정보로 현재위치를 초기 설정한다.
		currentLocation = locM.getLastKnownLocation(bestProvider);

		//위치 리스너 초기화
		if( locL == null )
			locL = new MyLocationListener();
		//위치 매니저에 위치 리스너를 셋팅한다.
		//위치 리스너에서 10000ms (10초) 마다 100미터 이상 이동이 발견되면 업데이트를 하려한다.
		locM.requestLocationUpdates(bestProvider, 5000, 100, locL);

		//맵뷰에 그려준다.
		if(isResearchInfo )
		{
			updateMyOverlay(currentLocation, true);
			Log.i("GF", "re??");
			showDialog();
			new Thread( new GetSearchResult()).start();
		}
	}


	abstract protected void setSearchInfo( );

	private void setNoteBaloonSize()
	{
		Resources r = getResources(); 
		noteBaloonWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
		noteBaloonHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 115, r.getDisplayMetrics());
	}

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			currentLocation = location;
			updateMyOverlay(location, false);
		}
		@Override
		public void onProviderDisabled(String provider) {
			Log.d(LOG_TAG, "GPS disabled : " + provider);
		}
		@Override
		public void onProviderEnabled(String provider) {
			Log.d(LOG_TAG, "GPS Enabled : " + provider);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(LOG_TAG, "onStatusChanged : " + provider + " & status = "
					+ status);
		}
	}
	@Override
	protected void onPause() {
		if( locM != null ) locM.removeUpdates(locL);
		if( etName != null ) etName.setText("");
		super.onPause();
	}

	protected GeoPoint getGeoPoint(Location location) {
		if (location == null) {
			return null;
		}
		Double lat = location.getLatitude() * 1E6;
		Double lng = location.getLongitude() * 1E6;
		return new GeoPoint(lat.intValue(), lng.intValue());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(itemList.size() != 0)
			updateItemOverlay();
		
		Intent intent 	= getIntent();
		String sLatitude = intent.getStringExtra(SearchGCInfo.LATITUDE);
		String sLongitude = intent.getStringExtra(SearchGCInfo.LONGITUDE);

		Log.i("GF", "sLatitude = " + sLatitude);
		Log.i("GF", "sLongitude = " + sLongitude);
		if( sLatitude != null && sLongitude != null )
		{
			Double latitude = Double.parseDouble( sLatitude ) * 1E6;
			Double longitude = Double.parseDouble( sLongitude ) * 1E6;
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(),longitude.intValue());
			mapController.animateTo(geoPoint);
		}
	}

	private String showNowHere(double lat, double lng , boolean showOption){
		StringBuilder geoString = new StringBuilder();

		try {
			Geocoder goecoder = new Geocoder(getApplicationContext(), Locale.getDefault());
			Address adr = goecoder.getFromLocation(lat,lng, 1).get(0);

			geoString.append(adr.getLocality());
			geoString.append("\n");
			geoString.append(adr.getThoroughfare());
			geoString.append("\n");
			geoString.append(adr.getFeatureName());
			geoString.append("\n");			

			if (adr.getLocality()== null) 
				geoString.append(adr.getLocality()).append(" ");
			if (adr.getThoroughfare() == null) 
				geoString.append(adr.getThoroughfare());

		} catch (IndexOutOfBoundsException e) { 
			e.printStackTrace();
			Log.e("GF", e.getMessage() + "");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("GF", e.getMessage() + "");
		}

		geoString.append("위도 : ").append(lat).append(" ,경도 : ").append(lng);

		if (showOption){
			Toast.makeText(getApplicationContext(), geoString.toString(),
					Toast.LENGTH_SHORT).show();
		}

		return geoString.toString();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	public void onClick(View view)
	{
		InputMethodManager imm;
		int zoomLevel = 0;
		switch( view.getId() )
		{
		case R.id.mBtnMyLocation:
			etName.setText("");
			mapView.removeAllViews();
			mapController.setZoom(initialZoomLevel);
			refreshMyLocation();
			imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etName.getApplicationWindowToken(), 0);
			break;
		case R.id.mBtnZoomIn:
			mapController.zoomIn();
			zoomLevel = mapView.getZoomLevel();
			setZoomPanel(zoomLevel);
			break;
		case R.id.mBtnZoomOut:
			mapController.zoomOut();
			zoomLevel = mapView.getZoomLevel();
			setZoomPanel(zoomLevel);
			break;
		case R.id.mBtnRefresh:
			etName.setText("");
			GeoPoint centerPoint = mapView.getProjection().fromPixels(mapView.getWidth()/2, mapView.getBottom()/2);
			showDialog();
			new Thread( new GetSearchResult( centerPoint )).start();
			imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etName.getApplicationWindowToken(), 0);
			break;
		case R.id.searchBtnRun:
			showDialog();
			new Thread( new GetSearchNameResult()).start();
			imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etName.getApplicationWindowToken(), 0);
			break;

		case R.id.btnDAddr:
			showSelectLocalDialog();
			break;
		case R.id.btnSAddr:
			pressdBtnSAddr = true;
			etSAddr.setText("");
			Toast.makeText(getApplicationContext(), "지도에서 위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnLocationRun:
			String sAddr = etSAddr.getText().toString();
			String dAddr = etDAddr.getText().toString();

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(
					Uri.parse("http://maps.google.com/maps?" 
							+ "saddr=" + sAddr + "&daddr=" + dAddr));
			Log.i("GF", ("http://maps.google.com/maps?" 
					+ "saddr=@" + sAddr + "&daddr=" + dAddr));
			startActivityForResult(intent, 0);

			rlLocation.setVisibility(View.GONE);
			break;
		default:
			break;
		}

		Intent intent;
		switch( view.getId() )
		{
		case R.id.tmBtnExcelInfo:
			intent = new Intent( getApplicationContext(), ExcelLocationCategoryActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		case R.id.tmBtnFieldGC:
			intent = new Intent( getApplicationContext(), SearchFieldGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		case R.id.tmBtnScreenGC:
			intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		case R.id.tmBtnMyGolf:
			intent = new Intent( getApplicationContext(), FavoriteActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(intent, 0);
			break;
		default :
			break;
		}
	}

	protected ProgressDialog showDialog()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("지도를 불러오는 중입니다...");
		progressDialog.show();
		return progressDialog;
	}
	private void setZoomPanel(int zoomLevel)
	{
		if( initialZoomLevel <= 16 ) 
			mapView.setSatellite(useSatellite);
		else 	
			mapView.setSatellite(false);

		RelativeLayout zoomPanel = (RelativeLayout) findViewById(R.id.ZoomPanel);

		ImageView plusBar = (ImageView)zoomPanel.findViewById(R.id.ImageView01);
		RelativeLayout.LayoutParams params = null;
		params = (RelativeLayout.LayoutParams) plusBar.getLayoutParams();
		params.width = zoomPanel.getWidth()-(zoomPanel.getWidth() * zoomLevel / 23 );
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
		mapView.removeView(lastNoteBaloon);
	}
	protected void updateMyOverlay(Location location, boolean moveToLocation) {

		//기존에 화면에 찍어둔 오버레이 (마커들)을 싹 지운다.
		clearOverlays();
		if ( location == null ) return;
		//Location 객체를 가지고 GeoPoint 객체를 얻어내는 메소드
		GeoPoint geoPoint = getGeoPoint(location);
		//현재위치를 표시할 이미지
		Drawable marker;
		//실제 운영소스엔 분기하여 현재위치와 선택위치 이미지를 변경하게 되어있다.
		marker = getResources().getDrawable(R.drawable.ic_mylocation); 
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		//LocationItemizedOverlay 를 이용하여 현재위치 마커를 찍을 오버레이를 생성한다.
		overlayHere = new MyLocationItemizedOverlay(marker);
		//touch event 의 null pointer 버그를 방지하기 위해 마커를 찍고 바로 populate 시켜준다.
		overlayHere.mPopulate();

		//현재위치를 GeoCoder 를 이용하여 대략주소와 위,경도를 Toast 를 통하여 보여준다.
		//String geoString = showNowHere(location.getLatitude(), location.getLongitude() , false);
		//현재위치 마커 정의
		OverlayItem overlayItem = new OverlayItem(geoPoint, "here", "나의 위치");

		//현재위치 오버레이 리스트에 현재위치 마커를 넣는다.
		overlayHere.addOverlay(overlayItem); 

		// 맵뷰에서 터치이벤트를 받을 오버레이를 추가한다.
		// 특정지점을 오래 눌렀을때 특정 지점 기준으로 재검색을 하기 위하여 터치이벤트를 받아와야한	다.
		//mapView.getOverlays().add(new MapTouchDetectorOverlay());
		// 마지막으로 생성된 오버레이레이어를 맵뷰에 추가한다.
		mapView.getOverlays().add(overlayHere);
		if( moveToLocation )
			mapView.getController().animateTo(geoPoint); //현재위치로 화면을 이동한다.
		mapView.postInvalidate(); //맵뷰를 다시 그려준다.
	}

	private void updateItemOverlay(){
		updateMyOverlay( currentLocation, false );
		// draw branches
		Drawable branchMarker;
		branchMarker = getResources().getDrawable(R.drawable.ic_golf);
		branchMarker.setBounds(0, 0, branchMarker.getIntrinsicWidth(),
				branchMarker.getIntrinsicHeight());
		//지점 마커들을 그려줄 오버레이를 준비한다.
		overlayBranch = new GolfLocationItemizedOverlay(branchMarker);
		overlayBranch.mPopulate();
		//반복문을 돌면서 마커들을 오버레이에 추가한다.
		//나중에 마커를 눌렀을때 다이얼로그에 지점 정보를 보여주기위해 스니펫에 몇가지 정보를
		//string 으로 전달한다.
		Map<String, String> closistItem = null;
		for (Map<String, String> map : itemList) 
		{
			Double latitude = Double.parseDouble( map.get(SearchGCInfo.LATITUDE)) * 1E6;
			Double longitude = Double.parseDouble( map.get(SearchGCInfo.LONGITUDE)) * 1E6;
			GeoPoint branchGeoPoint = new GeoPoint(latitude.intValue(),longitude.intValue());

			// Create new overlay with marker at geoPoint
			OverlayItem overlayItem = new OverlayItem(branchGeoPoint, "branch", map.get(SearchGCInfo.NAME));
			overlayBranch.addOverlay(overlayItem);
			//overlayBranch.mPopulate();

			if( currentLocation != null )
			{
				if( closistItem == null ) closistItem = map;

				int distance = distanceFrom( currentLocation, branchGeoPoint );
				String sdis = closistItem.get(SearchGCInfo.DISTANCE);

				if ( sdis == null )
				{
					closistItem.put(SearchGCInfo.DISTANCE, distance + "");
				}else
				{
					int cdistance = Integer.valueOf( sdis );
					if( distance < cdistance )
					{
						closistItem = map;
						closistItem.put(SearchGCInfo.DISTANCE, distance + "");
					}
				}
			}
		}

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
		if( currentLocation != null 
				&& closistItem != null 
				&& (etName.getText() != null 
						&& etName.getText().toString().length() != 0 
						&& etName.getText().toString().equals("골프장명입력")== false) )
		{
			Double latitude = Double.parseDouble( closistItem.get(SearchGCInfo.LATITUDE)) * 1E6;
			Double longitude = Double.parseDouble( closistItem.get(SearchGCInfo.LONGITUDE)) * 1E6;
			GeoPoint branchGeoPoint = new GeoPoint(latitude.intValue(),longitude.intValue());
			mapController.animateTo(branchGeoPoint);
		}
	}
	private final Handler getMapdataHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.getData().getBoolean("SUCCESS_KEY")) {
				updateItemOverlay();
			}
			if (msg.getData().getBoolean("SHORT_KEYWORD")) {
				Toast.makeText(getApplicationContext(), "검색어를 2글자 이상 정확히 입력해 주세요. 예시)123 골프(연습)장/스크린 골프장 검색 시 123 상호를 정확히 입력.", Toast.LENGTH_SHORT).show();
			}

			if( progressDialog != null )
				progressDialog.dismiss();

			Intent intent 	= getIntent();
			String sLatitude = intent.getStringExtra(SearchGCInfo.LATITUDE);
			String sLongitude = intent.getStringExtra(SearchGCInfo.LONGITUDE);
			if( (sLatitude == null || sLongitude == null) )
			{

			}
		};
	};

	public int distanceFrom(Location p1, GeoPoint p2)
	{
		double p1Longitude 	= p1.getLongitude();
		double p1Latitude 	= p1.getLatitude();
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
		if ( distance == 0 )
			return 0;
		else
			return distance;
	}
	protected class GetSearchNameResult implements Runnable
	{
		public GetSearchNameResult(){}

		@Override
		public void run() {
			Bundle bundle = new Bundle();

			ArrayList<Map<String,String>> al = new ArrayList<Map<String,String>>();
			String keyword = etName.getText().toString();
			if( keyword == null || keyword.length() < 2 || keyword.equals("골프장명입력"))
			{
				bundle.putBoolean("SHORT_KEYWORD", true);
				Message msg = getMapdataHandler.obtainMessage();
				msg.setData(bundle);
				getMapdataHandler.sendMessage(msg);
				return;
			}
			try {
				al = searchGCInfo.getGCList("대한민국", keyword , 500);
				itemList = al;
				bundle.putBoolean("SUCCESS_KEY", true); //실패하면 false 이다.
				//mapController.setZoom(9);
			} catch (IOException e) {
				bundle.putBoolean("SUCCESS_KEY", false); //실패하면 false 이다.
				e.printStackTrace();
			} catch (URISyntaxException e) {
				bundle.putBoolean("SUCCESS_KEY", false); //실패하면 false 이다.
				e.printStackTrace();
			}

			bundle.putBoolean("SHORT_KEYWORD", false);
			Message msg = getMapdataHandler.obtainMessage();
			msg.setData(bundle);
			getMapdataHandler.sendMessage(msg);
		}
	}

	protected class GetSearchResult implements Runnable
	{
		private Location location;
		private GeoPoint geoPoint;

		public GetSearchResult(){}

		public GetSearchResult( Location location )
		{
			this.location = location;
		}
		public GetSearchResult( GeoPoint geoPoint )
		{
			this.geoPoint = geoPoint;
		}
		@Override
		public void run() {
			Bundle bundle = new Bundle();
			try {
				if( location != null )
					itemList = searchGCInfo.getGCList(location);
				else if ( geoPoint != null )
					itemList = searchGCInfo.getGCList(geoPoint);
				else if( currentLocation != null )
					itemList = searchGCInfo.getGCList(currentLocation);
				else 
				{
					throw new IOException();
				}

				Log.i("GF", "List.size() = " + itemList.size());
				bundle.putBoolean("SUCCESS_KEY", true); //실패하면 false 이다.
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Log.e("DR", "" + e.getMessage());
			}
			catch (IOException e) {
				e.printStackTrace();
				Log.e("DR", "" + e.getMessage());
				bundle.putBoolean("SUCCESS_KEY", false); //실패하면 false 이다.
			} 
			Message msg = getMapdataHandler.obtainMessage();
			msg.setData(bundle);
			getMapdataHandler.sendMessage(msg);
		}
	}

	protected class MyLocationItemizedOverlay
	extends LocationItemizedOverlay 
	{
		public MyLocationItemizedOverlay(Drawable defaultMarker) {
			super(defaultMarker);
		}

		@Override
		protected boolean onTap(int index) 
		{
			previousSelectTapTime = System.currentTimeMillis();

			Log.i("GF" , "onTap(" + index +")");
			//mapController.zoomIn();
			//마커를 눌렀을때 발생시킬 이벤트 메소드이다.
			//현재 위치일 경우 간단한 토스트 메세지를 보여준다.
			Toast.makeText(getApplicationContext(),
					overlays.get(index).getSnippet(),
					Toast.LENGTH_SHORT)
					.show();
			mapController.animateTo(overlays.get(index).getPoint());

			return false;
		}
	}

	protected class GolfLocationItemizedOverlay
	extends LocationItemizedOverlay 
	{
		public GolfLocationItemizedOverlay(Drawable defaultMarker) {
			super(defaultMarker);
		}

		// 빈화면을 터치하면 풍선 정보창이 사라진다.
		@Override
		public boolean onTap(GeoPoint p, MapView mapView) 
		{
			if( pressdBtnSAddr )
			{
				double lat = ((double)p.getLatitudeE6()) / 1E6;
				double lon =((double)p.getLongitudeE6()) / 1E6;

				StringBuilder sb = new StringBuilder();
				sb.append(""+ lat).append(',')
				.append("" + lon);
				etSAddr.setText(sb);
				pressdBtnSAddr = false;
				Toast.makeText(getApplicationContext(), "위치가 선택되었습니다.", Toast.LENGTH_SHORT).show();
			}else
			{
				mapView.removeView(lastNoteBaloon);
			}
			return super.onTap(p, mapView);
		}


		protected void addNoteBaloon(int index)
		{
			mapView.removeView(lastNoteBaloon);
			noteBaloon.setVisibility(View.VISIBLE);

			final Map<String, String> item = itemList.get(index); 

			final String name = item.get(SearchGCInfo.NAME);
			final String link = item.get(SearchGCInfo.HOMEPAGE);
			final String number = item.get(SearchGCInfo.TELEPHONE);
			final String detail = item.get(SearchGCInfo.DETAIL_VIEW);
			final String address = item.get(SearchGCInfo.ADDRESS);

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

			Button btnDetail = (Button) noteBaloon.findViewById(R.id.mbBtnDetail);
			if( detail != null)
			{
				btnDetail.setVisibility(View.VISIBLE);
				btnDetail.setClickable(true);
				btnDetail.setFocusable(true);
				btnDetail.setOnClickListener(new SearchScreenGCInfo.OpenLink(getApplicationContext(), detail, item));
			}else if( link != null )
			{
				Button btnHome = (Button) noteBaloon.findViewById(R.id.mbBtnHome);
				//btnHome.setVisibility(View.GONE);
				btnHome.setVisibility(View.VISIBLE);
				btnDetail.setVisibility(View.GONE);
				btnHome.setClickable(true);
				btnHome.setFocusable(true);
				btnHome.setOnClickListener(new SearchScreenGCInfo.OpenLink(getApplicationContext(), link, item));
			}

			btnDetail.setVisibility(View.GONE);

			final FavoriteWebDBHelper fwDBHelper = new FavoriteWebDBHelper(getApplicationContext());
			Button btnFavorite = (Button) noteBaloon.findViewById(R.id.mbBtnFavorite);

			if( fwDBHelper.isExistFavoriteWebByIndex(item.get(SearchGCInfo.TELEPHONE)) )
			{
				btnFavorite.setBackgroundResource(R.drawable.btn_bookmark_done);
			}else
			{
				btnFavorite.setBackgroundResource(R.drawable.btn_bookmark);
			}
			btnFavorite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if( fwDBHelper.isExistFavoriteWebByIndex(item.get(SearchGCInfo.TELEPHONE)) == false)
					{
						if(  fwDBHelper.addClub(item) )
						{
							view.setBackgroundResource(R.drawable.btn_bookmark_done);
						}
						Toast.makeText(getApplicationContext(), "관심 골프장으로 등록되었습니다 .", Toast.LENGTH_SHORT).show();
					}else
					{
						fwDBHelper.removeClubByName(item.get(SearchGCInfo.TELEPHONE));
						view.setBackgroundResource(R.drawable.btn_bookmark);
						Toast.makeText(getApplicationContext(), "관심 골프장이 해제되었습니다", Toast.LENGTH_SHORT).show();
					}
				}
			});
			Button btnLocation = (Button) noteBaloon.findViewById(R.id.mbBtnLocation);
			btnLocation.setOnClickListener( new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) {
					showLocation(item);		
					rlLocation.setVisibility(View.VISIBLE);
				}
			});
			final FavoriteRecentWebDBHelper frwDBHelper = new FavoriteRecentWebDBHelper(getApplicationContext());
			frwDBHelper.addClub(item);
			mapView.addView(noteBaloon, new MapView.LayoutParams(noteBaloonWidth, noteBaloonHeight,lastTap,MapView.LayoutParams.BOTTOM_CENTER));
			lastNoteBaloon = noteBaloon;
		}

		@Override
		protected boolean onTap(int index) 
		{
			if( pressdBtnSAddr )
			{
				String sAddr = overlays.get(index).getSnippet();
				if( sAddr.equals("나의 위치"))
				{
					StringBuilder sb = new StringBuilder();
					sb.append("현재위치").append('@');
					sb.append(""+ currentLocation.getLatitude()).append(',')
					.append("" + currentLocation.getLongitude());
					etSAddr.setText(sb);
					Toast.makeText(getApplicationContext(), "현재 위치가 선택되었습니다.", Toast.LENGTH_SHORT).show();
				}
				pressdBtnSAddr = false;
				return false;
			}
			previousSelectTapTime = System.currentTimeMillis();

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

	abstract public class LocationItemizedOverlay 
	extends	ItemizedOverlay<OverlayItem> 
	{

		protected GeoPoint lastTap;
		protected RelativeLayout noteBaloon;
		protected List<OverlayItem> overlays;

		public LocationItemizedOverlay(Drawable defaultMarker) 
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
	}

	protected void showLocation( Map<String,String> item )
	{
		if( item != null )
		{
			String name = item.get(searchGCInfo.NAME);
			String sLatitude = item.get(searchGCInfo.LATITUDE);
			String sLongitude = item.get(searchGCInfo.LONGITUDE);
			etDAddr.setText( name+'@'+sLatitude+','+sLongitude);
		}

		if( currentLocation != null )
		{
			StringBuilder sb = new StringBuilder();
			sb.append("현재위치").append('@');
			sb.append(""+ currentLocation.getLatitude()).append(',')
			.append("" + currentLocation.getLongitude());
			etSAddr.setText(sb);
		}
	}

	private void showSelectLocalDialog()
	{	
		if(itemList == null || itemList.size() == 0)
		{
			return;
		}

		String destTemp;
		final String[] items = new String[itemList.size()];
		int size = itemList.size();
		Map<String,String> item ;
		for( int i = 0; i < size ; i++)
		{
			item = itemList.get(i);
			items[i] = item.get(SearchGCInfo.NAME);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("도착위치");

		builder.setSingleChoiceItems(items, 0,  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				destTempItem = itemList.get(which);
			}
		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 새롭게 검색해서 목록을 받아오는 녀석이 필요함.
				StringBuilder sb = new StringBuilder() ;
				sb.append( destTempItem.get(SearchGCInfo.NAME) ).append('@');
				sb.append( destTempItem.get(SearchGCInfo.LATITUDE)).append(',');
				sb.append( destTempItem.get(SearchGCInfo.LONGITUDE));
				etDAddr.setText(sb.toString());
			}
		});
		builder.setNegativeButton("cancel", null);
		builder.create().show();
	}


	// GPS Disabled Alert
	private void createGpsDisabledAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("GPS가 꺼져있습니다. GPS를 활성화 시키겠습니까?")
		.setCancelable(false)
		.setPositiveButton("GPS 설정",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showGpsOptions();
				finish();
			}
		})
		.setNegativeButton("이전화면",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// show GPS Options
	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

}