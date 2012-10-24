package kr.co.drdesign.golffinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.Browser.SearchColumns;
import android.util.Log;

public class FavoriteWebDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "golf_finder_field.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "FavoritFieldGC";

	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;

	public FavoriteWebDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + 
			"_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
			SearchGCInfo.LATITUDE + " TEXT," +
			SearchGCInfo.LONGITUDE + " TEXT," +
			SearchGCInfo.NAME + " TEXT," +
			SearchGCInfo.ADDRESS  + " TEXT," +
			SearchGCInfo.TELEPHONE + " TEXT UNIQUE," +
			SearchGCInfo.HOMEPAGE + " TEXT," +
			SearchGCInfo.DETAIL_VIEW + " TEXT," +
			SearchGCInfo.PREVIEW + " TEXT," +
			"TIME TEXT UNIQUE);";
		db.execSQL(sqlStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public ArrayList<Map<String,String>> getFavoriteWebList()
	{
		mDB = getWritableDatabase();
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		// DB 질의문
		String asColumnsToReturn[] = 
		{
				"_id",
				SearchGCInfo.LATITUDE,
				SearchGCInfo.LONGITUDE,
				SearchGCInfo.NAME,
				SearchGCInfo.ADDRESS,
				SearchGCInfo.TELEPHONE,
				SearchGCInfo.HOMEPAGE,
				SearchGCInfo.DETAIL_VIEW,
				SearchGCInfo.PREVIEW,
				"TIME"
		};

		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn,  null, null, null, null, "TIME DESC limit 3");

		ArrayList<Map<String,String>> itemList = new ArrayList<Map<String,String>>();

		Map<String, String> map = null;

		String temp;
		if ( mCursor.moveToFirst() )
			while( !mCursor.isAfterLast() ) 
			{
				map = new HashMap<String, String>();

				temp = mCursor.getString(mCursor.getColumnIndex("_id"));
				map.put("_id", temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.LATITUDE));
				map.put(SearchGCInfo.LATITUDE, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.LONGITUDE));
				map.put(SearchGCInfo.LONGITUDE, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.NAME));
				map.put(SearchGCInfo.NAME, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.ADDRESS));
				map.put(SearchGCInfo.ADDRESS, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.TELEPHONE));
				map.put(SearchGCInfo.TELEPHONE, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.HOMEPAGE));
				map.put(SearchGCInfo.HOMEPAGE, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.DETAIL_VIEW));
				map.put(SearchGCInfo.DETAIL_VIEW, temp);

				temp = mCursor.getString(mCursor.getColumnIndex(SearchGCInfo.PREVIEW));
				map.put(SearchGCInfo.PREVIEW, temp);

				itemList.add(map);
				mCursor.moveToNext();
			}
		mCursor.close();
		mDB.close();
		return itemList;
	}

	public boolean isExistFavoriteWebByIndex(String telNumber)
	{
		if(telNumber == null) return false;

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
				asColumnsToReturn, SearchGCInfo.TELEPHONE+"=?", new String[]{telNumber}, null, null, "TIME DESC");
		int cnt =  mCursor.getCount();
		mCursor.close();
		mDB.close();
		return ( cnt > 0 )? true: false;
	}

	public boolean addClub ( Map<String, String> item )
	{
		mDB = getWritableDatabase();
		ContentValues typeRecordToAdd = new ContentValues();
		Iterator<String> it = item.keySet().iterator();
		
		String key = null;
		while( it.hasNext() )
		{
			key = it.next();
			if( key.equals(SearchGCInfo.DISTANCE)) continue;
			typeRecordToAdd.put(key, item.get(key));
		}
		typeRecordToAdd.put("TIME", System.currentTimeMillis()+"");

		long row = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		Log.i("GF", "row is " + row );
		
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
	public boolean removeClubByName( String telNumber )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, SearchGCInfo.TELEPHONE+ "=?", new String[]{telNumber});
		mDB.close();
		return (row>0)? true: false;
	}

	public boolean removeClubByIndex( String _id )
	{
		mDB = getWritableDatabase();
		long row = mDB.delete(TABLE_NAME, "_id" + "<?", new String[]{_id});
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
