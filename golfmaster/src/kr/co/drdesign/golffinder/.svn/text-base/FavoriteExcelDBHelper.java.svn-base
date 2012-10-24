package kr.co.drdesign.golffinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class FavoriteExcelDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "golf_finder.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "Favorit";

	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;

	public FavoriteExcelDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + 
			"_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
			"NAME_OF_GOLFCLUB TEXT UNIQUE," +
			"ADDRESS TEXT," +
			"TIME TEXT," +
			"NO TEXT UNIQUE);";
		db.execSQL(sqlStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public ArrayList<Map<String,String>> getFavoriteExcelList()
	{
		mDB = getWritableDatabase();
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		// DB 질의문
		String asColumnsToReturn[] = 
		{
				"ADDRESS",
				"NAME_OF_GOLFCLUB",
				"NO"
		};

		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn,  null, null, null, null, "TIME DESC limit 3");

		ArrayList<Map<String,String>> itemList = new ArrayList<Map<String,String>>();

		Map<String, String> map = null;

		String name, address, no;
		if ( mCursor.moveToFirst() )
			while( !mCursor.isAfterLast() ) 
			{
				map = new HashMap<String, String>();

				name = mCursor.getString(mCursor.getColumnIndex("NAME_OF_GOLFCLUB"));
				address = mCursor.getString(mCursor.getColumnIndex("ADDRESS"));
				no = mCursor.getString(mCursor.getColumnIndex("NO"));

				map.put(ExcelInfo.NAME_OF_GOLFCLUB, name);
				map.put(ExcelInfo.ADDRESS, address);
				map.put(ExcelInfo.NO, no);

				itemList.add(map);
				mCursor.moveToNext();
			}
		mCursor.close();
		mDB.close();
		return itemList;
	}

	public boolean isExistFavoriteExcelByNO(String no)
	{

		mDB = getWritableDatabase();
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		// DB 질의문
		String asColumnsToReturn[] = 
		{
				"ADDRESS",
				"NAME_OF_GOLFCLUB",
				"NO"
		};

		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn, "NO=?", new String[]{no}, null, null, "ADDRESS DESC ");
		int cnt =  mCursor.getCount();
		mCursor.close();
		mDB.close();
		return ( cnt > 0 )? true: false;
	}

	public boolean addClub ( Map<String, String> item )
	{
		mDB = getWritableDatabase();
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put("NAME_OF_GOLFCLUB", item.get(ExcelInfo.NAME_OF_GOLFCLUB));
		typeRecordToAdd.put("ADDRESS", item.get(ExcelInfo.ADDRESS));
		typeRecordToAdd.put("NO", item.get(ExcelInfo.NO));
		typeRecordToAdd.put("TIME", System.currentTimeMillis()+"");

		long row = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		
		int cnt = cntClub(3);
		if( cnt > 3)
		{
			removeClubByIndex( String.valueOf(cnt));
		}
		mDB.close();
		return (row>0)? true: false;
	}
	
	private int cntClub(int index)
	{
		mDB = getWritableDatabase();
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		// DB 질의문
		String asColumnsToReturn[] = 
		{
				"_id"
		};

		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn, null, null, null, null, "_id DESC");
		int cnt =  mCursor.getCount();
		if( cnt >= index )
		{
			mCursor.move(index);
			int rtn = mCursor.getInt(0);
			return rtn;
		}
		mDB.close();
		return cnt;
	}
	public boolean removeClubByName( String name )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, "NAME_OF_GOLFCLUB"+ "=?", new String[]{name});
		mDB.close();
		return (row>0)? true: false;
	}
	
	public boolean removeClubByIndex( String _id )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, "_id"+ "<?", new String[]{_id});
		mDB.close();
		return (row>0)? true: false;
	}
	
	public boolean removeClubByNo( String no )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, "NO" + "=?", new String[]{no});
		mDB.close();
		return (row>0)? true: false;
	}
	public boolean removeAll( )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, null, null);
		mDB.close();
		return (row>0)? true: false;
	}
}
