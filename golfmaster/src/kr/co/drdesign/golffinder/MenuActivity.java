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
			resMsg = "ARM ������ ���� �Ͽ����ϴ�.";
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
				resMsg = "����ڵ�=" + Integer.toHexString(res);
				switch( res )
				{
				case 0xF0000004 :
					resMsg = "�Ͻ����� ��ַ� ���̼��� �߱޿� �����Ͽ����ϴ�.\n����Ŀ� �ٽ� �õ��� �ֽʽÿ�.(04)";
					break;
				case 0xF0000008 :
					resMsg = "�Ͻ����� ��ַ� ���̼��� �߱޿� �����Ͽ����ϴ�.\n����Ŀ� �ٽ� �õ��� �ֽʽÿ�.(08)";
					break;
				case 0xF000000E :
					resMsg = "�Ͻ����� ��ַ� ���̼��� �߱޿� �����Ͽ����ϴ�.\n����Ŀ� �ٽ� �õ��� �ֽʽÿ�.(0E)";
					break;
				case 0xF0000009 :
					resMsg = "��ǰ ���ų��� Ȯ�ο� �����Ͽ����ϴ�.\n�ڼ��� ������ �����ͷ� ���� �ٶ��ϴ�.(09)";
					break;
				case 0xF000000A :
					resMsg = "Tstore �̰��Ե� �ܸ��Դϴ�.\n���� �� �̿��� ���ֽñ� �ٶ��ϴ�.(0A)";
					break;
				case 0xF000000C :
					resMsg = "�Ͻ����� ��ַ� ���̼��� �߱޿� �����Ͽ����ϴ�.\n����Ŀ� �ٽ� �õ��� �ֽʽÿ�.(0C)";
					break;
				case 0xF000000D :
					resMsg = "���ø����̼��� ���̼��� ���� Ȯ���� �Ұ����մϴ�.\n�ڼ��� ������ �����ͷ� ���� �ٶ��ϴ�.(0D)";
					break;
				case 0xF0000011 :
					resMsg = "�ڵ��� ��ȣ�� Ȯ���� �� �����ϴ�.\nUSIM �������� Ȯ�� �� USIM ����� �� ��� ������ ���ֽñ� �ٶ��ϴ�. (11)";
					break;
				case 0xF0000012 :
					resMsg = "���ø����̼��� ���� Ȯ���� �Ұ����մϴ�.\n�ڼ��� ������ �����ͷ� ���� �ٶ��ϴ�.(12)";
					break;
				case 0xF0000013 :
					resMsg = "�ڵ������� ����Ÿ���(3G, WIFI)�� �ǰ� ���� �ʽ��ϴ�.\n�ڵ����� ������ ��� �����κ��� Ȯ�� �� �� ������ �� �ֽʽÿ�. (13)";
					break;
				case 0xF0000014 :
					resMsg = "Tstore �������α׷��� ��ġ �Ǿ� ���� �ʽ��ϴ�.\nTstore �������α׷��� ��ġ�Ϛ� �� �� ������ �� �ֽʽÿ�. (14)";
					break;
				}
				if( res != 1)
					showDialog(0);
			}catch(Exception e){
				e.printStackTrace();
				releaseService();
				resMsg = "���� ���� ����";
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
		.setTitle("�� ��")
		.setMessage(resMsg)
		.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
			Toast.makeText(this, "�ڷΰ��� ��ư�� �ѹ� �� �����ø� ���� �˴ϴ�.", Toast.LENGTH_SHORT).show();
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