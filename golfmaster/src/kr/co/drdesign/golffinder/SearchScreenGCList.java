package kr.co.drdesign.golffinder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.widget.Button;
import android.widget.Toast;

public class SearchScreenGCList  
extends SearchGCList
{
	public static ArrayList<Map<String,String>> itemList = SearchScreenGCInfo.itemList;
	protected SearchScreenGCInfo mGCInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ContentViewId = R.layout.gfinder_list;
		mGCInfo = new SearchScreenGCInfo(getApplicationContext());
		itemList = SearchScreenGCInfo.itemList;
		super.onCreate(savedInstanceState);

		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish ) return;

		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnScreenGC);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_screen_gc_select);
		btnExcelInfo.setFocusable(false);
	}

	protected void fillMsgList() {
		fillMsgList( itemList );
	}

	@Override
	protected void refreshList() throws IOException, URISyntaxException {
		itemList = mGCInfo.getGCList( searchLocation , SearchGCInfo.radius, 0);
	}

	@Override
	protected void onPause() {
		SearchScreenGCInfo.itemList = itemList;
		super.onPause();
		etNameOfGC.setText("");
	}

	@Override
	protected void onResume() {

		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);

		if( itemList != null && itemList.equals(SearchScreenGCInfo.itemList) && isFinish == false )
		{
			itemList = SearchScreenGCInfo.itemList;
			fillMsgList();
		}
		else if( isFinish == false )
		{
			itemList = SearchScreenGCInfo.itemList;
		}
		super.onResume();
		//		if( etNameOfGC != null && etNameOfGC.getText() != null){
		//			String text = etNameOfGC.getText().toString();
		//			//if( text == null || text.length() == 0 || text.equals("��������Է�") ) 
		//			{
		//				etNameOfGC.setText("��������Է�");
		//				etNameOfGC.setTextColor(Color.GRAY);
		//				etNameOfGC.setSelection(3);
		//			}
		//		}
	}

	protected void setListItemOnClickListner(View view, 
			final String latitude, 
			final String longitude)
	{
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(SearchGCInfo.LATITUDE, latitude);
				intent.putExtra(SearchGCInfo.LONGITUDE, longitude);
				startActivity( intent );
			}
		});
	}

	@Override
	protected void startSearch() {
		// TODO Auto-generated method stub
		final String searchName = etNameOfGC.getText().toString();
		
		// searchName.length() ==  0�� ��, ������ �� �˻� �� ����� ����
		if( searchName.length() != 0 && searchName.length() < 2 )
		{
			Toast.makeText(getApplicationContext(), "�˻�� 2���� �̻� ��Ȯ�� �Է��� �ּ���. ����)123 ����(����)��/��ũ�� ������ �˻� �� 123 ��ȣ�� ��Ȯ�� �Է�.", Toast.LENGTH_SHORT).show();
		}
		else if( searchName.length() > 0 && searchName.equals("��������Է�") == false )
		{
			showDialog();
			new Thread(
					new Runnable() {
						@Override
						public void run() {
							ArrayList<Map<String,String>> al = new ArrayList<Map<String,String>>();
							Bundle bundle = new Bundle();
							try {
								al = mGCInfo.getGCList("���ѹα�", searchName, 500);
								if ( al == null || al.size() == 0)
								{
									bundle.putBoolean("SUCCESS_KEY", false); //�����ϸ� false �̴�.
								}else
								{
									bundle.putBoolean("SUCCESS_KEY", true); //�����ϸ� false �̴�.
									mGCInfo.itemList = itemList = al;
								}
							} catch (IOException e) {
								bundle.putBoolean("SUCCESS_KEY", false); //�����ϸ� false �̴�.
								e.printStackTrace();
							} catch (URISyntaxException e) {
								bundle.putBoolean("SUCCESS_KEY", false); //�����ϸ� false �̴�.
								e.printStackTrace();
							}

							Message msg = getMapdataHandler.obtainMessage();
							msg.setData(bundle);
							getMapdataHandler.sendMessage(msg);
						}
					}
			).start();
		}else
		{
			showSelectLocalDialog();
		}
	}
}