package kr.co.drdesign.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.util.Loggable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RunnableAttachListActivity 
extends ListActivity 
implements Loggable{

	private static final String CLASS_NAME ="RunnableAttachListActivity";
	
	private static final String INDEX = "INDEX";
//	private TextView alTvNoMsg;

	private ArrayList < Map<String, String>> al;
	private AttachController attCtrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L( CLASS_NAME + " onCreate()");
		attCtrl = AttachController.getInstance(RunnableAttachListActivity.this);
		al = attCtrl.getRunnableAttaches();
		//setContentView(R.layout.attachment_list);

		SimpleAdapter adapter = new SimpleFileListAdapter(
				this, 
				al, 
				R.layout.attachment_item,
				new String[]{AttachController.NAME, INDEX},
				new int[]{R.id.alAttachName, R.id.alCtvItem
				}
		);

		setListAdapter(adapter);
		showNoAttachText();
	}

	private void showNoAttachText(){
		if( al != null && al.size() == 0 ){ 
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(  R.string.txtAlret );
			alert.setMessage( R.string.ralTxtNoAttach );

			alert.setCancelable(false);
			alert.setPositiveButton(  R.string.txtDone, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int which) {
					dialog.dismiss();   //닫기
					finish();
				}
			});

			alert.show();
		}else{
		}
	}

	class SimpleFileListAdapter extends SimpleAdapter{

		private TextView tvFileName;
		private CheckedTextView ctbItem;
		private Button btnRunApp;
		private String mimeType;
		private String savepath;

		public SimpleFileListAdapter(Context context, 
				List<Map<String, String>> list, int resource, String[] from, int[] to)
		{
			super(context, list, resource, from, to);
			//this.checkedItem = new HashMap<CompoundButton, Boolean>();
			if(IS_DEBUG_MODE) Log.i(DEBUG_TAG, "List.size()" + list.size());
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.attachment_item, null);
			}

			tvFileName = (TextView)view.findViewById(R.id.alAttachName);
			String temp = al.get(position).get(AttachController.NAME);
			tvFileName.setText(temp);

			mimeType = al.get(position).get(AttachController.TYPE);
			savepath = al.get(position).get(AttachController.SAVEPATH);

			ctbItem = (CheckedTextView)view.findViewById(R.id.alCtvItem);
			btnRunApp = (Button)view.findViewById(R.id.alBtnRunAPP);
			btnRunApp.setVisibility(View.VISIBLE);

			final File file = new File(savepath).getAbsoluteFile();
			btnRunApp.setOnClickListener(new View.OnClickListener() {

				public void onClick(View view) {
					//savepath에 잘못된 값이 들어 있는 경우에 처리해주는 코드를 만들어야 한다.
					if(IS_DEBUG_MODE) Log.i(DEBUG_TAG, "savePath = " + savepath );
					if( file.exists() )
					{
						Uri uri = Uri.fromFile(file);
						Intent intent = new Intent();
						L( "mimeType : " + mimeType);
						intent.setDataAndType(uri, mimeType);
						startActivity(intent);
					}
				}
			});
			ctbItem.setVisibility(View.INVISIBLE);
			return view ;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ral_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() )
		{
		case R.id.ralRemoveAll:
//			AttachController attCtrl = AttachController.getInstance(this);
//			attCtrl.deleteAll();
			break;
		default:
			Log.e(DEBUG_TAG, "Option Menu Selected default.");
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void L(char i, String log) {
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}