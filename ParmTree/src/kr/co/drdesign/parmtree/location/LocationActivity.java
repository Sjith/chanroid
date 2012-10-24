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
	ImageView currentMarker; // ������ġ ���� ��Ŀ.
	Button refreshBtn; // ���ΰ�ħ ��ư
	Button nearListBtn; // ��ü��� ��ư
	RelativeLayout currentBallon;
	MyLocationOverlay mLocation;
	
	MapController mapControl; // �ʺ信 ���� �������� ������ ������.
	LocationManager locManager; // �ý����� ��ġ������ ���� ���� ����ϴ� ��ü.
	Location currentLocation;
	GeoPoint currentPoint;	// ������ġ ���� ����Ʈ.
	Criteria criteria;
	
	ParmUtil util;
	EstablishController estCtrl;
	FavoriteController favoCtrl;
	
	ListThread estThread;
	ListHandler estHandler;
	
	ArrayList<Map<String,String>> estList = new ArrayList<Map<String,String>>();
	
	private boolean isMoving = true; // �̵��� �ڵ� ��ġ���� ����. �������� ���� ������.
	
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
		criteria = new Criteria(); // GPS�� ������ �ִ� ��ü
		estThread = new ListThread();
		estHandler = new ListHandler();
		
		if (util.isCheckGPS()) checkGPS(); // GPS Ȯ��
		if (util.isSatellite()) map.setSatellite(true); // ����������뿩��
		else map.setSatellite(false);
		
		nearListBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);		

		map.setBuiltInZoomControls(true); // ��
		mapControl = map.getController();
		mapControl.setZoom(18);
		
		// GPS������ �������� �����ѵ�. ���͸� �Ҹ� ũ�� ������ �������� �ٲ� �� �ֵ��� �Ѵ�.
		// ��Ȯ���� �����ϸ� �ӵ��� �ʹ� ���̰� ���� ���Ƿ� �� ���ؿ��� �����Ѵ�.
		
		String bestProvider = locManager.getBestProvider(criteria, true);
//		String bestProvider = LocationManager.GPS_PROVIDER;
		locManager.requestLocationUpdates(bestProvider, 1000, 0, this);
		// ��ġ �ڵ����� �ֱ� ����.
		
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
		if (util.isSatellite()) map.setSatellite(true); // ����������뿩��. �׽�Ʈ��
		else map.setSatellite(false);
		
		switch (Character.codePointAt(util.getGPSSensitive(), 0)) { // GPS ���� ����.
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
		// ���� ǥ�õǴ� �߿� ��� ȣ��Ǵ°� ����.
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		l.s("Location Changed.");
		// TODO Auto-generated method stub
		if (isMoving) {
			refreshLocation(location);
			// �������� �о �ڵ���ġ���� Ȱ��ȭ ���¸� ��� ����
			// ��Ȱ��ȭ �����̸� isMoving �� false �� �ٲ�
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
	 * ���� ��ġ�� �������ε� �ڵ����ε�
	 * ���ΰ�ħ�� �� ȣ���ϴ� �޼���.
	 * 
	 */
	private void refreshLocation(Location location) {
		
		if (location == null) return;
		
		currentLocation = location; // ������ġ ����.
		map.removeView(currentMarker); // ��Ŀ ��ø�� ���� ���� ���� ��Ŀ ����.
		l.s("Refreshing your location...");
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		// ������ġ�� ����, �浵�� �ʿ信 ���� ����� �߰��� �� �ִ�.
		
		currentPoint = new GeoPoint((int)(lat * 1E6), (int)(lon*1E6));
		// �ʿ��ٰ� ���� �������� ���� �浵���� �׻� 1E6�� ������� �Ѵ�.
		
		MapView.LayoutParams mapMarkerParams = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, currentPoint,
				MapView.LayoutParams.BOTTOM_CENTER);
		currentMarker.setImageResource(R.drawable.btn_mine);
		map.removeView(currentBallon);
		map.addView(currentMarker, mapMarkerParams);
		
		// ���ΰ�ħ�� ��ġ�� ���缭 ��ü ��Ŀ�鵵 ��������� �Ѵ�.
		
		// 2011-08-04 �׽�Ʈ�� Mylocationoverlay
//		map.getOverlays().remove(mLocation);
//		mLocation = new MyLocationOverlay(this, map);
//		mLocation.enableMyLocation();
//		mLocation.enableCompass();
//		map.getOverlays().add(mLocation);
		moveToCurrent(); // ���ΰ�ħ�� ��ġ�� �̵��Ѵ�.		
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
						// ǥ�ø� ���� ����
						
						TextView title = (TextView) currentBallon.findViewById(R.id.ballontitle);
						TextView addr = (TextView) currentBallon.findViewById(R.id.ballonaddrtext);
						TextView tel = (TextView) currentBallon.findViewById(R.id.ballontelnumtext);
						TextView desc = (TextView) currentBallon.findViewById(R.id.ballondesctext);
						
						final String phoneNum = estList.get(j).get(ESTPHONE);
						
						title.setText(estList.get(j).get(ESTNAME));
						addr.setText("�ּ� : " + 
								estList.get(j).get(ESTADDR).replace("/", " ").replace("���ѹα� ", ""));
						tel.setText("��ȭ��ȣ : " + phoneNum);
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
	 * ���� ��ġ�� ��Ŀ���� �̵�.
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
	 * GPS�� ���������� Ȯ��â�� ����Ѵ�.
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
		// ������ locationProvider�� ��Ȱ��ȭ �Ǹ� ȣ��ȴ�.
		l.s("Provider Disabled.");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		// ������ locationProvider�� Ȱ��ȭ �Ǹ� ȣ��ȴ�.
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
			refreshLocation(currentLocation); // ������ġ ���� �� �̵�.
			Toast.makeText(this, R.string.mapTxtRefreshLoc, Toast.LENGTH_SHORT).show();
			break;
		case R.id.nearLocation :
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < estList.size(); i++) {
				list.add(estList.get(i).get(ESTID));
			}
			
			Intent i = new Intent(this, NearFieldActivity.class);
			i.putExtra(ESTID, list);
			startActivity(i); // �ֺ���ü ��� ����
		default :
			break;
		}
	}

}
