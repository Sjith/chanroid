package kr.co.drdesign.golffinder;

import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

abstract public class GCActivity
extends MapActivity
{
	protected MapView mapView; //맵뷰 객체
	protected List<Overlay> listOfOverlays; //맵에 표시된 오버레이(레이어)들을 가지고 있는 리스	트
	protected MapController mapController; //맵을 줌시키거나, 이동시키는데 사용될 컨트롤러
	protected long previousSelectTapTime;

	public static final String LOG_TAG = "GF";

	protected Button btnMyLocation;
	protected Button btnList;

	protected RelativeLayout lastNoteBaloon ;

	protected int radius = 5; // 검색 범위
	protected int initialZoomLevel = 14;
	public static boolean useSatellite = false;

	protected int noteBaloonWidth;
	protected int noteBaloonHeight;


	protected void finishChain( Class mClass )
	{
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish )
		{
			finish();
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

	private void setNoteBaloonSize()
	{
		Resources r = getResources(); 
		noteBaloonWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, r.getDisplayMetrics());
		noteBaloonHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onClick(View view)
	{
		int zoomLevel = 0;
		switch( view.getId() )
		{
		case R.id.mBtnMyLocation:
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
		default:
			break;
		}
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
		params.width = zoomPanel.getWidth() -(zoomPanel.getWidth() * zoomLevel / 23 );
	}

}