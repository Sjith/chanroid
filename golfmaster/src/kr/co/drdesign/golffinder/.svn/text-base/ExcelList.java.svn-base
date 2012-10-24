package kr.co.drdesign.golffinder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ExcelList extends FinishChainActivity
{
	public static final String LATITUDE		= SearchGCInfo.LATITUDE; 
	public static final String LONGITUDE 	= SearchGCInfo.LONGITUDE;
	public static final String NAME			= SearchGCInfo.NAME;
	public static final String ADDRESS 		= SearchGCInfo.ADDRESS;
	public static final String TELEPHONE 	= SearchGCInfo.TELEPHONE;
	public static final String HOMEPAGE 	= SearchGCInfo.HOMEPAGE;
	public static final String DETAIL_VIEW	= SearchGCInfo.DETAIL_VIEW;
	public static final String PREVIEW		= SearchGCInfo.PREVIEW;

	private FavoriteRecentDBHelper frDBHelper;

	private ExcelInfo excelInfo;
	protected SimpleAdapter mSimpleAdapter;
	protected ListView exGolfLv;
	private EditText etNameOfGC;
	protected Button btnSearch;

	protected String[] locationCategory ;
	public ArrayList<Map<String,String>> al;
	private static ArrayList<String> nameOfGC = new ArrayList<String>();

	private int VOICE_RECOGNITION_REQUEST_CODE = 8754;

	protected int ContentViewId;
	/** Called when the activity is first created. */

	@Override
	protected void onPause() {
		super.onPause();
		etNameOfGC.setText("");
	}

	private int num = 0;
	private void setCordinate2CG()
	{
		ArrayList<Map<String,String>> al = new ArrayList<Map<String,String>>();
		al = excelInfo.getGCListByCondition("");

		ArrayList<Map<String,String>> al2 = new ArrayList<Map<String,String>>();
		al2 = excelInfo.getGCListByCondition("");

		Map<String,String[]> coordinateList = excelInfo.getCoordinateList();

		FileOutputStream fos;
		try {
			fos = openFileOutput("coordinate" + (num++) + ".xml", 0);

			String name, latitude = null, longitude = null;

			for( int i  = 0 ; al2.size() > 10 && i <4  ; i++ )
			for( Map<String,String> item : al )
			{
				name = item.get(ExcelInfo.NAME_OF_GOLFCLUB);
				if(  coordinateList.containsKey(name)  == false )
				{
					try {
						Address address = excelInfo.address2Location( item.get(ExcelInfo.ADDRESS) );
						if( address == null )
						{
							Log.i("DR", "ADDRESS IS NULL " + item.get(ExcelInfo.NUMBER)) ;
							continue;
						}
						latitude = String.valueOf(address.getLatitude());
						longitude= String.valueOf(address.getLongitude());
					} catch (IOException e) {
						e.printStackTrace();
					}
					fos.write("<Placemark>\n<name>".getBytes());
					fos.write(name.replace(" ", "_").getBytes());
					fos.write("</name>".getBytes());
					fos.write("<Point>\n<coordinates>".getBytes());
					fos.write(longitude.getBytes());
					fos.write(",".getBytes());
					fos.write(latitude.getBytes());
					fos.write(",0.00</coordinates>\n</Point>\n</Placemark>\n".getBytes());
					al2.remove(item);
					Log.i("DR", "al2.size = " + al2.size());
					Log.i("DR", "al.size = " + al.size());
				}
				al2.remove(item);
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		boolean isFinish = getIntent().getBooleanExtra("FINISH", false);
		if( isFinish )
			return;

		excelInfo 	= new ExcelInfo( getApplicationContext());
		frDBHelper 	= new FavoriteRecentDBHelper(getApplicationContext());

		setContentView(R.layout.excel_list);

		exGolfLv = (ListView) findViewById(R.id.elLvResult);
		etNameOfGC = (EditText) findViewById(R.id.elActvNameOfGC);

		etNameOfGC.setOnEditorActionListener( new TextView.OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				String condition = etNameOfGC.getText().toString();
				if( condition.length() > 0 &&  condition.length() < 2)
				{
					Toast.makeText(getApplicationContext(), "검색어가 짧습니다. 2글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
				else if( condition.length() > 0 && condition.equals("골프장명입력") == false )
				{
					final ArrayList<Map<String,String>> list = excelInfo.getGCListByCondition(condition);

					if( list == null || list.size() == 0 )
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(ExcelList.this);
						builder.setTitle("알림");
						builder.setMessage("골프장 명이 틀리거나 없는 골프장입니다.");
						builder.setNegativeButton("닫기", null);
						builder.show();
					}else
					{
						al = list;
						fillMsgList();
					}
				}
				else
				{
					searchAllList();
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etNameOfGC.getApplicationWindowToken(), 0);
				return false;
			}
		});


		al = excelInfo.getList();
		Intent intent = getIntent();
		if( intent != null )
		{
			locationCategory = intent.getStringArrayExtra(ExcelInfo.PARENT_ADDRESS);
			if( locationCategory != null)
			{
				al = new ArrayList<Map<String,String>>();

				for( String lc : locationCategory)
				{
					if( lc != null ) 
						al.addAll( excelInfo.getGCListByLocationCategory(lc) );	
				}
			}
		}

		fillMsgList();

		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnExcelInfo);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_excel_info_select);
		btnExcelInfo.setFocusable(false);
		
		Button btn01 = (Button) findViewById(R.id.Button01);
		btn01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCordinate2CG();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		finishChain(ExcelLocationCategoryActivity.class);
	}
	protected void fillMsgList(){
		fillMsgList ( al );
	}
	//	void refreshList() throws IOException, URISyntaxException;

	protected void fillMsgList(List<Map<String,String>> list){
		mSimpleAdapter = new SimpleListAdapter(
				this, 
				list, 
				R.layout.excel_item, 
				new String[]{ ExcelInfo.NAME_OF_GOLFCLUB, ExcelInfo.ADDRESS}, 
				new int[]{R.id.eiTvName, R.id.eiTvAddress});

		exGolfLv.setAdapter(mSimpleAdapter);
	}

	protected class SimpleListAdapter extends SimpleAdapter{

		protected  List<Map<String, String>> list;

		private TextView tvName;
		private TextView tvAddress;
		private Button btnHomePage;
		private Button btnCall;
		private Button btnMap;

		private int resourceID;

		public SimpleListAdapter(Context context, 
				List<Map<String, String>> list, int resource, String[] from, int[] to)
		{
			super(context, list, resource, from, to);
			this.list = list;
			resourceID = resource;
			Log.i("GF", "list.size() = "  + list.size());
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(resourceID, null);
			}

			tvName 		= (TextView) view.findViewById(R.id.eiTvName);
			tvAddress 	= (TextView) view.findViewById(R.id.eiTvAddress);
			btnCall 	= (Button) view.findViewById(R.id.eiBtnCall);
			btnHomePage = (Button) view.findViewById(R.id.eiBtnHomePage);
			btnMap 		= (Button) view.findViewById(R.id.eiBtnMap);

			final Map<String, String> item = list.get(position);
			tvName.setText( item.get(ExcelInfo.NAME_OF_GOLFCLUB));
			tvAddress.setText( item.get(ExcelInfo.ADDRESS));

			final String no = item.get(ExcelInfo.NO);
			view.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) 
				{
					frDBHelper.addClub(item);
					Intent intent = new Intent(getApplicationContext(), ExcelDetailInfoActivity.class);
					intent.putExtra(ExcelInfo.NO, no);
					startActivity(intent);
				}
			});
			return view;
		}
	}
	//	private String normalizingNumber( String number )
	//	{
	//		// '/n' 제거
	//		int idx = 0;
	//		idx = number.indexOf('\n');
	//		if( idx > 0 )
	//			number = number.substring(0, number.indexOf('\n'));
	//
	//		idx = number.indexOf('~');
	//		if( idx > 0 )
	//			number = number.substring(0, number.indexOf('~'));
	//
	//		idx = number.lastIndexOf('-');
	//		if( idx > 10 )
	//			number = number.substring(0, number.lastIndexOf('-'));
	//		return number;
	//	}

	public void onClick( View view )
	{
		Intent intent;
		switch( view.getId() )
		{
		case R.id.elBtnSearch:
			String condition = etNameOfGC.getText().toString();
			if( condition.length() > 0 &&  condition.length() < 2)
			{
				Toast.makeText(getApplicationContext(), "검색어가 짧습니다. 2글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
			}
			else if( condition.length() > 0 && condition.equals("골프장명입력") == false )
			{
				final ArrayList<Map<String,String>> list = excelInfo.getGCListByCondition(condition);

				if( list == null || list.size() == 0 )
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("알림");
					builder.setMessage("골프장 명이 틀리거나 없는 골프장입니다.");
					builder.setNegativeButton("닫기", null);
					builder.show();
				}else
				{
					al = list;
					fillMsgList();
				}
			}
			else
			{
				searchAllList();
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etNameOfGC.getApplicationWindowToken(), 0);
			break;
		default:
			break;
		}

		switch( view.getId() )
		{
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

	public void searchAllList()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("경고");
		builder.setMessage("전체 목록을 새로고침 합니다. 진행 하시겠습니까?");
		builder.setPositiveButton("예", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				al = excelInfo.getGCListByCondition("");
				fillMsgList();
			}
		});
		builder.setNegativeButton("아니요", null);
		builder.show();
	}


	//=---
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exceinfo_location_category_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/* Creates the menu items */
		Intent intent;
		switch( item.getItemId() )
		{
		case R.id.btnHome:
			intent = new Intent( getApplicationContext(), MenuActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity( intent );
			break;
		case R.id.btnFeildGC:
			intent = new Intent( getApplicationContext(), SearchFieldGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnScreenGC:
			intent = new Intent( getApplicationContext(), SearchScreenGCActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnFavorite:
			intent = new Intent( getApplicationContext(), FavoriteActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		case R.id.btnExcelList:
			intent = new Intent( getApplicationContext(), ExcelLocationCategoryActivity.class );
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity( intent );
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}