package kr.co.drdesign.parmtree.est;

import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.R;
import kr.co.drdesign.parmtree.database.EstablishController;
import kr.co.drdesign.parmtree.util.c;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EstIntroduceActivity extends Activity implements OnClickListener,
		c {

	Map<String, String> estMap = new HashMap<String, String>();
	EstablishController estCtrl;
	Intent getIntent;
	EstThread estThread;
	EstHandler estHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.est_introduce);
		estCtrl = EstablishController.getInstance(getApplicationContext());
		getIntent = getIntent();
		estThread = new EstThread();
		estHandler = new EstHandler();
		load();

	}
	
	void load() {
		estThread = new EstThread();
		estThread.start();
	}

	void get() {
		estMap = estCtrl.get(getIntent.getStringExtra(ESTID));
	}
	
	void fill() {
		ImageView estIcon = (ImageView) findViewById(R.id.esticon);

		ImageView estKindpic = (ImageView) findViewById(R.id.estkindpic);

		ImageView estPremipic = (ImageView) findViewById(R.id.estpremipic);

		TextView estTitle = (TextView) findViewById(R.id.estname);
		estTitle.setText(estMap.get(ESTNAME));
		TextView estAddr = (TextView) findViewById(R.id.estlocation);
		estAddr.setText(estMap.get(ESTADDR).replace("/", " ").replace("¥Î«—πŒ±π ", ""));
		TextView estDesc = (TextView) findViewById(R.id.estintroducetext);
		estDesc.setText(estMap.get(DESC));
		TextView estPhone = (TextView) findViewById(R.id.estphoneno);
		estPhone.setText("tel : " + estMap.get(ESTPHONE));
		Button estLocBtn = (Button) findViewById(R.id.estlocationbtn);
		estLocBtn.setOnClickListener(this);
		Button estCallBtn = (Button) findViewById(R.id.estcallbtn);
		estCallBtn.setOnClickListener(this);

		LinearLayout estPicLayout = (LinearLayout) findViewById(R.id.estpicturelayout);
	}
	
	class EstThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			get();
			estHandler.sendEmptyMessage(0);
		}
	}
	
	class EstHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0 :
				fill();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.estlocationbtn:
			break;
		case R.id.estcallbtn:
			AlertDialog.Builder alert = new AlertDialog.Builder(getParent());
			alert.setTitle(R.string.alert);
			alert.setMessage("call : " + estMap.get(ESTPHONE));
			alert.setNegativeButton(R.string.cancel, null);
			alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Intent.ACTION_CALL, 
							Uri.parse("tel:" + estMap.get(ESTPHONE).replace("-", "")));
					startActivity(i);					
				}
			}).show();
			break;
		}
	}

}
