package kr.co.chan.util.Classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * 서비스같은데서 다이얼로그 띄울때 씀
 * 모든 인터페이스가 implements 되어 있으므로
 * 리스너 설정할땐 그냥 다 this로 때려박으면 됨.
 * 단, 버튼에 대한 리스너 객체는 각각 이름으로
 * 자동완성 호출하면 튀어나오도록 되어 있음.
 * 
 * @author chanroid
 */
public abstract class DialogActivity extends Activity implements OnClickListener,
		OnCancelListener, OnDismissListener, OnShowListener, OnKeyListener,
		OnMultiChoiceClickListener {
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		super.onCreate(savedInstanceState);
		mDialog = buildDialog();
		show();
	}
	
	public void show() {
		mDialog.show();
	}
	
	public void dismiss() {
		mDialog.dismiss();
		finish();
	}
	
	final OnClickListener positiveListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			onPositiveClick();
		}
	};

	final OnClickListener neutralListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			onNeutralClick();
		}
	};

	final OnClickListener negativeListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			onNegativeClick();
		}
	};
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	};

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShow(DialogInterface dialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub

	}

	public abstract AlertDialog buildDialog();

	public abstract void onPositiveClick();

	public abstract void onNeutralClick();

	public abstract void onNegativeClick();
}
