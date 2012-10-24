package kr.co.drdesign.golffinder;

import kr.co.drdesign.util.Loggable;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skt.arm.aidl.IArmService;

public class MenuActivity 
extends FinishChainActivity
implements Loggable
{
	private IArmService 				service;
	private ArmServiceConnection 		armCon;
	private String 						resMsg;
	private String						AID = 				"OA00067451";

	private Button btnGC;
	private Button btnExcel;
	private Button btnScreenGC;

	public static int optionMenuColor = Color.argb(150, 0, 0, 0);
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//SKT DRM
		if(!runService())
		{
			resMsg = "ARM 연동에 실패 하였습니다.";
			showDialog(0);
		}
		
		btnGC 		= (Button) findViewById(R.id.mnBtnSearchCC);
		btnExcel 	= (Button) findViewById(R.id.mnBtnExcel);
		btnScreenGC = (Button) findViewById(R.id.mnBtnSearchScreenCC);
	}
	private boolean runService(){
		try{
			if(armCon == null){
				armCon = new ArmServiceConnection();
				boolean conRes = bindService(new Intent(IArmService.class.getName()),  armCon, Context.BIND_AUTO_CREATE);
				if(conRes)					return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		releaseService();
		return false;
	}
	private void releaseService(){
		if(armCon != null){
			unbindService(armCon);
			armCon = null;
			service = null;
		}
	}
	class ArmServiceConnection implements ServiceConnection{
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			if(service == null)
				service = IArmService.Stub.asInterface((IBinder) boundService);
			try{
				int res = service.executeArm(AID);
				resMsg = "결과코드=" + Integer.toHexString(res);
				switch( res )
				{
				case 0xF0000004 :
					resMsg = "일시적인 장애로 라이선스 발급에 실패하였습니다.\n잠시후에 다시 시도해 주십시요.(04)";
					break;
				case 0xF0000008 :
					resMsg = "일시적인 장애로 라이선스 발급에 실패하였습니다.\n잠시후에 다시 시도해 주십시요.(08)";
					break;
				case 0xF000000E :
					resMsg = "일시적인 장애로 라이선스 발급에 실패하였습니다.\n잠시후에 다시 시도해 주십시요.(0E)";
					break;
				case 0xF0000009 :
					resMsg = "상품 구매내역 확인에 실패하였습니다.\n자세한 사항은 고객센터로 문의 바랍니다.(09)";
					break;
				case 0xF000000A :
					resMsg = "Tstore 미가입된 단말입니다.\n가입 후 이용을 해주시기 바랍니다.(0A)";
					break;
				case 0xF000000C :
					resMsg = "일시적인 장애로 라이선스 발급에 실패하였습니다.\n잠시후에 다시 시도해 주십시요.(0C)";
					break;
				case 0xF000000D :
					resMsg = "어플리케이션의 라이선스 정보 확인이 불가능합니다.\n자세한 사항은 고객센터로 문의 바랍니다.(0D)";
					break;
				case 0xF0000011 :
					resMsg = "핸드폰 번호를 확인할 수 없습니다.\nUSIM 장착여부 확인 및 USIM 잠금이 된 경우 해제를 해주시기 바랍니다. (11)";
					break;
				case 0xF0000012 :
					resMsg = "어플리케이션의 정보 확인이 불가능합니다.\n자세한 사항은 고객센터로 문의 바랍니다.(12)";
					break;
				case 0xF0000013 :
					resMsg = "핸드폰에서 데이타통신(3G, WIFI)이 되고 있지 않습니다.\n핸드폰의 데이터 통신 설정부분을 확인 후 재 실행을 해 주십시요. (13)";
					break;
				case 0xF0000014 :
					resMsg = "Tstore 전용프로그램이 설치 되어 있지 않습니다.\nTstore 전용프로그램을 설치하싞 후 재 실행을 해 주십시요. (14)";
					break;
				}
				if( res != 1)
					showDialog(0);
			}catch(Exception e){
				e.printStackTrace();
				releaseService();
				resMsg = "서비스 실행 실패";
				showDialog(0);
				return;
			}
			releaseService();
		}	

		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	}

	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
		.setTitle("알 림")
		.setMessage(resMsg)
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		}).setCancelable(false)
		.create();
	}
	private long previousPressedBackTime = 0;
	@Override
	public void onBackPressed() 
	{
		long duration = 2000;
		long currentPressedBackTime = System.currentTimeMillis();
		if( Math.abs(previousPressedBackTime - currentPressedBackTime) < duration )
		{
			moveTaskToBack(true);
			super.onBackPressed();
			finish();
			System.exit(0);
		}else
		{
			Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 종료 됩니다.", Toast.LENGTH_SHORT).show();
			previousPressedBackTime = System.currentTimeMillis();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public void onClick(View view)
	{
		Intent intent;
		switch( view.getId() )
		{
		case R.id.mnBtnSearchCC :
			intent = new Intent( getApplicationContext(),SearchFieldGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.mnBtnExcel :
			intent = new Intent( getApplicationContext(),ExcelLocationCategoryActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.mnBtnSearchScreenCC :
			intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.mnBtnMyGolf:
			intent = new Intent( getApplicationContext(), FavoriteActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;

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
	}

	@Override
	public void L(String log) {
		Log.i(DEBUG_TAG, log);
	}

}