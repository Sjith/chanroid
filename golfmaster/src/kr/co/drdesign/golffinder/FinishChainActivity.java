package kr.co.drdesign.golffinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FinishChainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	protected boolean  finishChain( Class mClass )
	{
		
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( mClass != null)
		{
			Log.i("DR", "FinishChain " + mClass.getName() + isFinish);
		}
		else
			{
			Log.i("DR", "FinishChain " + mClass +  isFinish);
			}
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
		return isFinish;
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		Log.i("DR", "onNewIntent " +  isFinish);
	}
}
