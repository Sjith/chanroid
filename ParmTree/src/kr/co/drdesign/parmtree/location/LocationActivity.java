package kr.co.drdesign.parmtree.location;

import java.util.ArrayList;
import java.util.Map;

import kr.co.drdesign.parmtree.MainListActivity;
import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.database.FavoriteController;
import kr.co.drdesign.parmtree.est.EstablishActivity;
import kr.co.drdesign.parmtree.util.ParmUtil;
import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * 
 * @author brake-_-
 * LocationActivity
 *
 */

public class LocationActivity extends MapActivity implements LocationListener, OnClickListener, c {

	
	MapView map;
	ImageView currentMarker; // 현재위치 전용 마커.
	Button refreshBtn; // 새로고침 버튼
	Button nearListBtn; // 업체목록 버튼
	RelativeLayout currentBallon;
	MyLocationOverlay mLocation;
	
	MapController mapControl; // 맵뷰에 대한 여러가지 설정이 가능함.
	LocationManager locManager; // 시스템의 위치정보를 가진 장비와 통신하는 객체.
	Location currentLocation;
	GeoPoint currentPoint;	// 현재위치 전용 포인트.
	Criteria criteria;
	
	ParmUtil util;
	EstablishController estCtrl;
	FavoriteController favoCtrl;
	
	ListThread estThread;
	ListHandler estHandler;
	
	ArrayList<Map<String,String>> estList = new ArrayList<Map<String,String>>();
	
	private boolean isMoving = true; // 이동중 자동 위치갱신 여부. 설정값에 의해 조정됨.
	
	/* 
	 * Activity methods.
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.location_map);
		
		util = ParmUtil.getInstance(getApplicationContext());
		estCtrl = EstablishController.getInstance(getApplicationContext());
		favoCtrl = FavoriteController.getInstance(getApplicationContext());
		currentMarker = new ImageView(this);
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		map = (MapView) findViewById(R.id.MapView);
		refreshBtn = (Button) findViewById(R.id.refreshCurrentLocation);
		nearListBtn = (Button) findViewById(R.id.nearLocation);
		criteria = new Criteria(); // GPS를 관리해 주는 객체
		estThread = new ListThread();
		estHandler = new ListHandler();
		
		if (util.isCheckGPS()) checkGPS(); // GPS 확인
		if (util.isSatellite()) map.setSatellite(true); // 위성사진사용여부
		else map.setSatellite(false);
		
		nearListBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);		

		map.setBuiltInZoomControls(true); // 줌
		mapControl = map.getController();
		mapControl.setZoom(18);
		
		// GPS세팅은 이정도가 적절한듯. 배터리 소모가 크기 때문에 설정으로 바꿀 수 있도록 한다.
		// 정확도는 조절하면 속도가 너무 차이가 많이 나므로 이 수준에서 유지한다.
		
		String bestProvider = locManager.getBestProvider(criteria, true);
//		String bestProvider = LocationManager.GPS_PROVIDER;
		locManager.requestLocationUpdates(bestProvider, 1000, 0, this);
		// 위치 자동갱신 주기 설정.
		
		estThread.start();
		
	}
	
	class ListThread extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			estList = estCtrl.get();
			estHandler.sendEmptyMessage(0);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		startActivity(new Intent(this, MainListActivity.class));
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
				makeEstMarker();
				break;
			case 1 :
				break;
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		l.s("onResume");
		if (util.isSatellite()) map.setSatellite(true); // 위성사진사용여부. 테스트중
		else map.setSatellite(false);
		
		switch (Character.codePointAt(util.getGPSSensitive(), 0)) { // GPS 감도 설정.
		case 1 : 
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_HIGH); 
			break;
		case 2 :
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_MEDIUM); 
			break;
		default :
			break;
		}
	}
	


	/*
	 * MapActivity methods.
	 */



	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		// 뭔가 표시되는 중에 계속 호출되는것 같다.
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		l.s("Location Changed.");
		// TODO Auto-generated method stub
		if (isMoving) {
			refreshLocation(location);
			// 설정값을 읽어서 자동위치갱신 활성화 상태면 계속 진행
			// 비활성화 상태이면 isMoving 을 false 로 바꿈
			if (util.isAR()) {
				isMoving = true;
			} else {
				isMoving = false;
			}
		}
	}
	
	/*
	 * customized Methods.
	 */
	
	/**
	 * 
	 * @param location
	 * 현재 위치를 수동으로든 자동으로든
	 * 새로고침할 때 호출하는 메서드.
	 * 
	 */
	private void refreshLocation(Location location) {
		
		if (location == null) return;
		
		currentLocation = location; // 현재위치 갱신.
		map.removeView(currentMarker); // 마커 중첩을 막기 위해 이전 마커 삭제.
		l.s("Refreshing your location...");
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		// 현재위치의 위도, 경도는 필요에 따라 멤버로 추가할 수 있다.
		
		currentPoint = new GeoPoint((int)(lat * 1E6), (int)(lon*1E6));
		// 맵에다가 뭔가 찍을때는 위도 경도값에 항상 1E6을 곱해줘야 한다.
		
		MapView.LayoutParams mapMarkerParams = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, currentPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		currentMarker.setImageResource(R.drawable.btn_mine);
		map.removeView(currentBallon);
		map.addView(currentMarker, mapMarkerParams);
		
		// 새로고침한 위치에 맞춰서 업체 마커들도 갱신해줘야 한다.
		
		// 2011-08-04 테스트용 Mylocationoverlay
//		map.getOverlays().remove(mLocation);
//		mLocation = new MyLocationOverlay(this, map);
//		mLocation.enableMyLocation();
//		mLocation.enableCompass();
//		map.getOverlays().add(mLocation);
		moveToCurrent(); // 새로고침한 위치로 이동한다.		
	}
	
	
	void makeEstMarker() {
		for (int i = 0; i < estList.size(); i++) {
			final int j = i;
			Button marker = new Button(this);
			Double lat = Double.parseDouble(estList.get(i).get(LAT)) * 1E6;
			Double lng = Double.parseDouble(estList.get(i).get(LON)) * 1E6;
			
			if (ParmUtil.getDistanceBetween(lat, lng, 
					currentLocation.getLatitude(), currentLocation.getLongitude())
					< 200.0f) {
				final GeoPoint markPoint = new GeoPoint(lat.intValue(), lng.intValue());
				int kind = Integer.parseInt(estList.get(i).get(ESTKIND));
				int premi = Integer.parseInt(estList.get(i).get(PREMI));
				
				final MapView.LayoutParams param = new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						markPoint, MapView.LayoutParams.CENTER);
	
				currentBallon = (RelativeLayout) 
					getLayoutInflater().inflate(R.layout.map_ballon_item, null, false);
				
				marker.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						map.removeView(currentBallon);
						mapControl.animateTo(markPoint);
						// 표시를 위한 세팅
						
						TextView title = (TextView) currentBallon.findViewById(R.id.ballontitle);
						TextView addr = (TextView) currentBallon.findViewById(R.id.ballonaddrtext);
						TextView tel = (TextView) currentBallon.findViewById(R.id.ballontelnumtext);
						TextView desc = (TextView) currentBallon.findViewById(R.id.ballondesctext);
						
						final String phoneNum = estList.get(j).get(ESTPHONE);
						
						title.setText(estList.get(j).get(ESTNAME));
						addr.setText("주소 : " + 
								estList.get(j).get(ESTADDR).replace("/", " ").replace("대한민국 ", ""));
						tel.setText("전화번호 : " + phoneNum);
						desc.setText(estList.get(j).get(DESC));
						
						ImageView kind = (ImageView) currentBallon.findViewById(R.id.ballonkindicon);
						ImageView premi = (ImageView) currentBallon.findViewById(R.id.ballonpremiicon);
	
						switch (Integer.valueOf(estList.get(j).get(ESTKIND))) {
						case 0 :
						case 1 :
						case 2 :
						case 3 :
						case 4 :
						case 5 :
							kind.setBackgroundResource(R.drawable.btn_drink);
							break;
						case 6 :
						case 7 :
						case 8 :
						case 9 :
							kind.setBackgroundResource(R.drawable.btn_food);
							break;
						}
						
						switch (Integer.valueOf(estList.get(j).get(PREMI))) {
						case 0 :
							premi.setBackgroundDrawable(Drawable.createFromPath(null));
							break;
						case 1 :
							premi.setBackgroundResource(R.drawable.btn_premium);
							break;
						}
						
						Button call = (Button) currentBallon.findViewById(R.id.balloncallbtn);
						Button detail = (Button) currentBallon.findViewById(R.id.ballondetailbtn);
						Button favorite = (Button) currentBallon.findViewById(R.id.ballonfavobtn);
						Button close = (Button) currentBallon.findViewById(R.id.ballonclosebtn);
						
						View.OnClickListener click = new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								switch (v.getId()) {
								case R.id.balloncallbtn :
									AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
									builder.setTitle(R.string.alert);
									builder.setMessage("call : " + phoneNum);
									builder.setNegativeButton(R.string.cancel, null);
									builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											String tel = "tel:" + phoneNum.replace("-", "");
											startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(tel)));
										}
									})
									.show();
									break;
								case R.id.ballondetailbtn :
									Intent i = new Intent(LocationActivity.this, EstablishActivity.class);
									i.putExtra(ESTID, estList.get(j).get(ESTID));
									startActivity(i);
									break;
								case R.id.ballonfavobtn :
									addFavorite(estList.get(j).get(ESTID));
									break;
								case R.id.ballonclosebtn :
									map.removeView(currentBallon);
									break;
								}
							}
						};
						
						call.setOnClickListener(click);
						detail.setOnClickListener(click);
						favorite.setOnClickListener(click);
						close.setOnClickListener(click);
						
						map.addView(currentBallon, param);
					}
				});
				
				switch (kind) {
				case 0 :
				case 1 :
				case 2 :
				case 3 :
				case 4 :
				case 5 :
					marker.setBackgroundResource(R.drawable.btn_drink);
					break;
				case 6 :
				case 7 :
				case 8 :
				case 9 :
					marker.setBackgroundResource(R.drawable.btn_food);
					break;
				}
				
				switch (premi) {
				case 0 :
	//				Resources r = getResources(); 
	//				float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, r.getDisplayMetrics()); 
	//				marker.setWidth((int) px);
	//				marker.setHeight((int) px);
					break;
				case 1 :
					marker.setBackgroundResource(R.drawable.btn_premium);
					break;
				}
				
				map.addView(marker, param);
			}			
		}
	}
	/**
	 * 현재 위치로 포커스를 이동.
	 */
	private void moveToCurrent() {
		if (currentPoint != null) mapControl.animateTo(currentPoint);
	}
	
	void addFavorite(String id) {
		if (favoCtrl.get(Integer.valueOf(id)) == null || 
				favoCtrl.get(Integer.valueOf(id)).size() < 1) {
			favoCtrl.insert(Integer.valueOf(id));
			Toast.makeText(getParent(), R.string.addfavorite, Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(getParent(), R.string.alreadyaddedest, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * GPS가 꺼져있으면 확인창을 출력한다.
	 */
	private void checkGPS() {
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.alert)
				.setCancelable(false)
				.setMessage
					(R.string.mapTxtAlertGPS)
				.setNegativeButton(R.string.no, null)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), 
								R.string.setTxtRefreshClick, Toast.LENGTH_LONG).show();
						Intent gpsIntent = 
							new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(gpsIntent);
					}
			}).show();
		}
	}

	
	/*
	 * LocationListener methods. 
	 */
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		// 지정된 locationProvider가 비활성화 되면 호출된다.
		l.s("Provider Disabled.");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		// 지정된 locationProvider가 활성화 되면 호출된다.
		l.s("Provider Enabled.");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		l.s("Location Status Changed.");
	}

	/*
	 * OnClickListener method.
	 */
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.refreshCurrentLocation :
			refreshLocation(currentLocation); // 현재위치 갱신 후 이동.
			Toast.makeText(this, R.string.mapTxtRefreshLoc, Toast.LENGTH_SHORT).show();
			break;
		case R.id.nearLocation :
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < estList.size(); i++) {
				list.add(estList.get(i).get(ESTID));
			}
			
			Intent i = new Intent(this, NearFieldActivity.class);
			i.putExtra(ESTID, list);
			startActivity(i); // 주변업체 목록 열기
		default :
			break;
		}
	}

}
