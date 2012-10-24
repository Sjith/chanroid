package kr.co.drdesign.golffinder;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ExcelDetailInfoActivity extends FinishChainActivity
{
	protected FavoriteExcelDBHelper feDBHelper;
	private FavoriteRecentDBHelper frDBHelper;
	protected ExcelInfo excelInfo;

	protected Map<String,String> item;
	protected Button btnFavorite;

	protected TextView tvClassification;
	protected TextView tvFeeMemberWeekday;
	protected TextView tvFeeMemberWeekend;
	protected TextView tvFeeWDMemberWeekday;
	protected TextView tvFeeWDMemberWeekend;
	protected TextView tvFeeNormalWeekday;
	protected TextView tvFeeNormalWeekend;
	protected TextView tvFeeGreen;

	protected TextView tvAddress;
	protected TextView tvParentAddress;
	protected TextView tvRestDay;
	protected TextView tvBookingTeam;
	protected TextView tvHomePage;
	protected TextView tvBookingWeekDay;
	protected TextView tvBookingWeekEnd;

	protected TextView tvFeeCaddy;
	protected TextView tvFeeCart;
	protected TextView tvDayOfMember;
	protected TextView tvNameOfGC;
	protected TextView tvTelNumber;
	protected TextView tvHoleNumber;
	protected TextView tvETC;

	protected ImageView ivLine01;
	protected ImageView ivLine02;
	protected ImageView ivLine03;
	protected ImageView ivLine04;
	protected ImageView ivLine05;
	protected ImageView ivLine06;
	protected ImageView ivLine07;
	protected ImageView ivLine08;
	protected ImageView ivLine09;
	protected ImageView ivLine10;
	protected ImageView ivLine11;
	protected ImageView ivLine12;

	protected Button btnHomePage;
	protected Button btnTelephone;

	protected EditText etName;

	public static final String NO_TEXT = "전화문의";
	protected static boolean isFavorite = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String no = getIntent().getStringExtra(ExcelInfo.NO);
		if( no == null ) return;
		Log.i("GF", "NO = " +no);
		
		setContentView(R.layout.excel_detail);

		Button btnExcelInfo = (Button) findViewById(R.id.tmBtnExcelInfo);
		btnExcelInfo.setBackgroundResource(R.drawable.topmenu_excel_info_select);
		btnExcelInfo.setFocusable(false);

		feDBHelper = new FavoriteExcelDBHelper(getApplicationContext());
		frDBHelper = new FavoriteRecentDBHelper(getApplicationContext());



		excelInfo = new ExcelInfo(getApplicationContext());

		item = excelInfo.getItemByNO(no);
		frDBHelper.addClub(item);

		tvAddress			=(TextView) findViewById(R.id.TvAddress);
		tvClassification	=(TextView) findViewById(R.id.TvCategory);
		//tvFeeMemberWeekday	=(TextView) findViewById(R.id.TvCategory);
		//tvFeeMemberWeekend	=(TextView) findViewById(R.id.TvCategory);
		//tvFeeNormalWeekday	=(TextView) findViewById(R.id.TvCategory);
		//tvFeeNormalWeekend	=(TextView) findViewById(R.id.TvCategory);
		//tvParentAddress		=(TextView) findViewById(R.id.TvCategory);

		tvRestDay			=(TextView) findViewById(R.id.TvHollyday);
		tvBookingTeam		=(TextView) findViewById(R.id.TvTeamReservation);
		tvHomePage			=(TextView) findViewById(R.id.TvHomePage);
		tvBookingWeekDay	=(TextView) findViewById(R.id.TvReservationWeekday);
		tvBookingWeekEnd	=(TextView) findViewById(R.id.TvReservationWeekend);
		tvFeeCaddy			=(TextView) findViewById(R.id.TvCaddyFee);
		tvFeeCart			=(TextView) findViewById(R.id.TvCartFee);
		//tvDayOfMember		=(TextView) findViewById(R.id.TvCategory);
		//tvNameOfGC			=(TextView) findViewById(R.id.TvCategory);
		tvTelNumber			=(TextView) findViewById(R.id.TvTelNumber);
		tvHoleNumber		=(TextView) findViewById(R.id.TvHall);

		btnHomePage			=(Button) findViewById(R.id.btnHome);
		btnTelephone		=(Button) findViewById(R.id.btnCall);

		etName				=(EditText) findViewById(R.id.searchEtName);
		etName.setText( item.get(ExcelInfo.NAME_OF_GOLFCLUB));

		ivLine01 = (ImageView) findViewById(R.id.ImageView07);
		ivLine02 = (ImageView) findViewById(R.id.ImageView08);
		ivLine03 = (ImageView) findViewById(R.id.ImageView09);
		ivLine04 = (ImageView) findViewById(R.id.ImageView10);
		ivLine05 = (ImageView) findViewById(R.id.ImageView11);

		ivLine06 = (ImageView) findViewById(R.id.ImageView13);
		ivLine07 = (ImageView) findViewById(R.id.ImageView14);
		ivLine08 = (ImageView) findViewById(R.id.ImageView15);
		ivLine09 = (ImageView) findViewById(R.id.ImageView16);

		ivLine10 = (ImageView) findViewById(R.id.ImageView17);
		ivLine11 = (ImageView) findViewById(R.id.ImageView18);
		ivLine12 = (ImageView) findViewById(R.id.ImageView19);

		if( item.containsKey(ExcelInfo.ADDRESS))
		{
			tvAddress.setText( item.get(ExcelInfo.ADDRESS) );
			ivLine01.setVisibility(View.VISIBLE);
		}
		else
		{
			tvAddress.setText( NO_TEXT );
//			TableRow tbAddress = (TableRow)findViewById(R.id.TableRow02);
//			tbAddress.setVisibility(View.GONE);
//			ivLine01.setVisibility(View.GONE);
		}

		if( item.containsKey(ExcelInfo.CLASSIFICATION))
		{
			tvClassification.setText( item.get(ExcelInfo.CLASSIFICATION));
			ivLine02.setVisibility(View.VISIBLE);
		}
		else
		{
			tvClassification.setText( NO_TEXT );
//			TableRow tbClassification = (TableRow)findViewById(R.id.TableRow03);
//			tbClassification.setVisibility(View.GONE);
//			ivLine02.setVisibility(View.GONE);
		}

		if( item.containsKey(ExcelInfo.HOLE_NUMBER))
		{
			tvHoleNumber.setText(item.get(ExcelInfo.HOLE_NUMBER));
			ivLine03.setVisibility(View.VISIBLE);
		}
		else
		{
			tvHoleNumber.setText( NO_TEXT );
//			TableRow tbHoleNumber = (TableRow)findViewById(R.id.TableRow04);
//			tbHoleNumber.setVisibility(View.GONE);
//			ivLine03.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.NUMBER))
		{
			tvTelNumber.setText(item.get(ExcelInfo.NUMBER));
			ivLine04.setVisibility(View.VISIBLE);
		}
		else
		{
			TableRow tbTelNumber = (TableRow)findViewById(R.id.TableRow05);
			tbTelNumber.setVisibility(View.GONE);
			ivLine04.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.HOMEPAGE))
		{
			tvHomePage.setText(Html.fromHtml("<u>" + item.get(ExcelInfo.HOMEPAGE) + "</u>"));
			ivLine05.setVisibility(View.VISIBLE);
		}
		else
		{
			tvHomePage.setText(NO_TEXT);
//			TableRow tbHomePage = (TableRow)findViewById(R.id.TableRow06);
//			tbHomePage.setVisibility(View.GONE);
//			ivLine05.setVisibility(View.GONE);
		}
		//-----
		boolean hasUseInfo = false;
		TableRow tbRestDay = (TableRow)findViewById(R.id.TableRow08);
		if( item.containsKey(ExcelInfo.RESTDAY))
		{
			tvRestDay.setText(item.get(ExcelInfo.RESTDAY));
			hasUseInfo=true;
			tbRestDay.setVisibility(View.VISIBLE);
			ivLine06.setVisibility(View.VISIBLE);
		}
		else
		{
			tvRestDay.setText(NO_TEXT);
//			tbRestDay.setVisibility(View.GONE);
//			ivLine06.setVisibility(View.GONE);
		}

		TableRow tbBookingWeekDay = (TableRow)findViewById(R.id.TableRow09);
		if( item.containsKey(ExcelInfo.BOOKING_WEKKDAY))
		{
			tvBookingWeekDay.setText(item.get(ExcelInfo.BOOKING_WEKKDAY));
			hasUseInfo=true;
			tbBookingWeekDay.setVisibility(View.VISIBLE);
			ivLine07.setVisibility(View.VISIBLE);
		}
		else
		{
			tvBookingWeekDay.setText(NO_TEXT);
//			tbBookingWeekDay.setVisibility(View.GONE);
//			ivLine07.setVisibility(View.GONE);
		}

		TableRow tbAddress = (TableRow)findViewById(R.id.TableRow10);
		if( item.containsKey(ExcelInfo.BOOKING_WEKKEND))
		{
			tvBookingWeekEnd.setText(item.get(ExcelInfo.BOOKING_WEKKEND));
			hasUseInfo=true;
			tbAddress.setVisibility(View.VISIBLE);
			ivLine08.setVisibility(View.VISIBLE);
		}
		else
		{
			tvBookingWeekEnd.setText(NO_TEXT);
//			tbAddress.setVisibility(View.GONE);
//			ivLine08.setVisibility(View.GONE);
		}

		TableRow tbBookingTeam = (TableRow)findViewById(R.id.TableRow11);
		if( item.containsKey(ExcelInfo.BOOKING_TEAM))
		{
			tvBookingTeam.setText(item.get(ExcelInfo.BOOKING_TEAM));
			hasUseInfo=true;
			tbBookingTeam.setVisibility(View.VISIBLE);
			ivLine09.setVisibility(View.VISIBLE);
		}
		else
		{
			tvBookingTeam.setText(NO_TEXT);
//			tbBookingTeam.setVisibility(View.GONE);
//			ivLine09.setVisibility(View.GONE);
		}
		TableLayout tlUseInfo = ( TableLayout)findViewById(R.id.tlUseInfo);
		if( hasUseInfo )
		{
		}
		else
		{
		}
		//-----
		tvFeeMemberWeekday 		= (TextView) findViewById(R.id.TvGreenFeeMwmberWD);
		tvFeeMemberWeekend		= (TextView) findViewById(R.id.TvGeenFeeMemberWE);
		tvFeeWDMemberWeekday	= (TextView) findViewById(R.id.TvGreenFeeWEMemberWD);
		tvFeeWDMemberWeekend	= (TextView) findViewById(R.id.TvGreenFeeWEMemberWE);
		tvFeeNormalWeekday 		= (TextView) findViewById(R.id.TvGreenFeeNormalWD);
		tvFeeNormalWeekend 		= (TextView) findViewById(R.id.TvGreenFeeNormalWE);
		boolean hasGreenFee = false;
		//-------------
		if( item.containsKey(ExcelInfo.FEE_MEMBER_WEEKDAY))
		{
			tvFeeMemberWeekday.append(item.get(ExcelInfo.FEE_MEMBER_WEEKDAY));
			hasGreenFee = true;
		}
		else
		{
			tvFeeMemberWeekday.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.FEE_MEMBER_WEEKEND))
		{
			tvFeeMemberWeekend.append(item.get(ExcelInfo.FEE_MEMBER_WEEKEND));
			hasGreenFee = true;
		}
		else
		{
			tvFeeMemberWeekend.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.FEE_WEMEMBER_WEEKDAY))
		{
			tvFeeWDMemberWeekday.append(item.get(ExcelInfo.FEE_WEMEMBER_WEEKDAY));
			hasGreenFee = true;
		}
		else
		{
			tvFeeWDMemberWeekday.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.FEE_WEMEMBER_WEEKEND))
		{
			tvFeeWDMemberWeekend.append(item.get(ExcelInfo.FEE_WEMEMBER_WEEKEND));
			hasGreenFee = true;
		}
		else
		{
			tvFeeWDMemberWeekend.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.FEE_NORMAL_WEEKDAY))
		{
			tvFeeNormalWeekday.append(item.get(ExcelInfo.FEE_NORMAL_WEEKDAY));
			hasGreenFee = true;
		}
		else
		{
			tvFeeNormalWeekday.setVisibility(View.GONE);
		}
		if( item.containsKey(ExcelInfo.FEE_NORMAL_WEEKEND))
		{
			tvFeeNormalWeekend.append(item.get(ExcelInfo.FEE_NORMAL_WEEKEND));
			hasGreenFee = true;
		}
		else
		{
			//tvFeeNormalWeekend.setVisibility(View.GONE);
		}

		if( hasGreenFee == false )
		{
			tvFeeNormalWeekend.setText(NO_TEXT);
//			TableRow tbGreenFee = (TableRow)findViewById(R.id.TableRow13);
//			tbGreenFee.setVisibility(View.GONE);
//			ivLine10.setVisibility(View.GONE);
		}

		boolean hasCaddyFee = false;
		TableRow tbCaddyFee = (TableRow)findViewById(R.id.TableRow14);
		if( item.containsKey(ExcelInfo.FEE_CADDY))
		{
			tvFeeCaddy.setText(item.get(ExcelInfo.FEE_CADDY));
			tbCaddyFee.setVisibility(View.VISIBLE);
			ivLine11.setVisibility(View.VISIBLE);
			hasCaddyFee = true;
		}
		else
		{
			tvFeeCaddy.setText(NO_TEXT);
//			tbCaddyFee.setVisibility(View.GONE);
//			ivLine11.setVisibility(View.GONE);
			hasCaddyFee = false;
		}

		boolean hasCartFee = false;
		TableRow tbCartFee = (TableRow)findViewById(R.id.TableRow15);
		if( item.containsKey(ExcelInfo.FEE_CART))
		{
			tvFeeCart.setText(item.get(ExcelInfo.FEE_CART));
			tbCartFee.setVisibility(View.VISIBLE);
			ivLine12.setVisibility(View.VISIBLE);
			hasCartFee = true;
		}
		else
		{
			tvFeeCart.setText(NO_TEXT);
//			tbCartFee.setVisibility(View.GONE);
//			ivLine12.setVisibility(View.GONE);
			hasCartFee = false;
		}

		
		if( hasCaddyFee == false && hasGreenFee == false && hasCartFee == false )
		{
		}else
		{
		}
		LinearLayout lladdress = (LinearLayout) findViewById(R.id.LlAddress);
		lladdress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ExcelActivity.class);
				intent.putExtra(ExcelInfo.NO, new String[]{no});
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});
		Button btnAddress = (Button)findViewById(R.id.Button01);
		btnAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ExcelActivity.class);
				intent.putExtra(ExcelInfo.NO, new String[]{no});
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});
		final String gcurl = item.get(ExcelInfo.HOMEPAGE);
		tvHomePage.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				if( gcurl.contains("http://") )
					intent.setData(Uri.parse(gcurl));	
				else
					intent.setData(Uri.parse("http://" + gcurl));
				startActivity(intent);
			}
		});

		final String number = normalizingNumber(item.get(ExcelInfo.NUMBER));
		tvTelNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
				dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialIntent);
			}
		});
		btnTelephone.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
				dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(dialIntent);
			}
		});
	}

	private String normalizingNumber( String number )
	{
		// '/n' 제거
		int idx = 0;
		idx = number.indexOf('\n');
		if( idx > 0 )
			number = number.substring(0, number.indexOf('\n'));

		idx = number.indexOf('~');
		if( idx > 0 )
			number = number.substring(0, number.indexOf('~'));

		idx = number.lastIndexOf('-');
		if( idx > 10 )
			number = number.substring(0, number.lastIndexOf('-'));
		return number;
	}

	public void onClick(View view)
	{
		Intent intent;
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

		//Intent intent = new Intent(getApplicationContext(), ExcelActivity.class);
		//		intent.putExtra(ExcelInfo.NO, new String[]{no});
		//		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.excel_detail_info_menu, menu);

		MenuItem menuItem = (MenuItem) menu.findItem( R.id.btnAddFavorite );
		isFavorite = feDBHelper.isExistFavoriteExcelByNO(item.get(ExcelInfo.NO));
		if( isFavorite )
		{
			menuItem.setTitle("관심골프장 해제");

		}
		else
		{
			menuItem.setTitle("관심골프장 등록");
		}
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
		case R.id.btnAddFavorite:
			onClickFavorite( item );
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClickFavorite(MenuItem item)
	{
		if( isFavorite == false)
		{
			boolean result = feDBHelper.addClub(this.item);
			Log.i("GF", "RESULT = " + result);
			Toast.makeText(this, "관심골프장으로 등록 되었습니다.", Toast.LENGTH_SHORT).show();
			item.setTitle("관심골프장 해제");
			isFavorite = true;
		}
		else
		{
			boolean result = feDBHelper.removeClubByNo(this.item.get(ExcelInfo.NO));
			Log.i("GF", "RESULT = " + result);
			Toast.makeText(this, "관심골프장이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
			item.setTitle("관심골프장 등록");
			isFavorite = false;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		finishChain(ExcelList.class);
	}
}