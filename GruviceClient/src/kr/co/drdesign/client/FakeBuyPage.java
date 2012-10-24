package kr.co.drdesign.client;

import kr.co.drdesign.util.GruviceUtillity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FakeBuyPage extends Activity {

	
	private Drawable sBackGround;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fakebuy);

		// 인텐트에 실려온 extra값을 받아서 뷰에 뿌려줌
		Intent intent 	= getIntent();
		String name 	= intent.getStringExtra("name");
		String detail 	= intent.getStringExtra("detail");
		String price 	= intent.getStringExtra("price");
		TextView tvName = (TextView) findViewById(R.id.tvName);
		TextView tvDetail = (TextView) findViewById(R.id.tvDetail);
		TextView tvPrice = (TextView) findViewById(R.id.tvPrice);

		String skinType = GruviceUtillity.getInstance(this).getSkinType();
		String[] SKIN_TYPES = GruviceUtillity.getInstance(this).getSKIN_TYPES();

		if( skinType.equals( SKIN_TYPES[0]) )
		{
			sBackGround = getResources().getDrawable(R.drawable.v1_ml_bg);
		}
		else
		{
			sBackGround = getResources().getDrawable(R.drawable.v2_ml_bg);
		}

		LinearLayout ll00 = (LinearLayout) findViewById(R.id.LinearLayout02);
		ll00.setBackgroundDrawable(sBackGround);

		tvName.append(name); 
		tvDetail.append(detail);
		tvPrice.append(price);

		Button btnBuy = (Button) findViewById(R.id.Button01);
		btnBuy.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder alert = new AlertDialog.Builder(FakeBuyPage.this);

				alert.setTitle( "확인" );
				alert.setMessage(  "구매하시겠습니까?" );

				alert.setCancelable(false);
				alert.setPositiveButton( "네", new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int which) {
						dialog.dismiss();   //닫기
						finish();
					}
				});
				alert.setNegativeButton( "아니요", new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int which) {
						dialog.dismiss();   //닫기
					}
				});

				alert.show();
			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( sBackGround != null ) sBackGround.setCallback(null);
	}
}
