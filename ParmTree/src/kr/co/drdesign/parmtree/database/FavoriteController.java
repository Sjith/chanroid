package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

public class FavoriteController extends DBController {

	public static FavoriteController instance;
	
	
	private FavoriteController(Context c) {
		mDatabase = new FavoriteDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static FavoriteController getInstance(Context c) {
		if (instance == null) instance = new FavoriteController(c);
		return instance;
	}
	
	@Override
	public long insert(Map<String, String> msg) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long insert(int msg) {
		// TODO Auto-generated method stub
		// int값만 받아와서 db에 넣는다.
		mDB.execSQL("INSERT INTO FAVORITE (" + ESTID + ") VALUES (" + msg + ")");
		return 0;
	}
	
	@Override
	public long insert(ContentValues cv) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long delete(int id) {
		// TODO Auto-generated method stub
		delete(Integer.toString(id));
		return 0;
	}
	@Override
	public long delete(String id) {
		// TODO Auto-generated method stub
		mDB.execSQL("DELETE FROM FAVORITE WHERE " + ESTID + "=" + id);
		return 0;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		mDB.execSQL("DROP TABLE FAVORITE");
	}

	@Override
	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT " + ESTID + " FROM FAVORITE"; 
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();
				map.put(ESTID, mCursor.getString(0));
				list.add(map);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return list;
	}

	@Override
	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		Map<String,String> list = new HashMap<String,String>();
		String sql = "SELECT " + ESTID + " FROM FAVORITE WHERE " + ESTID + "=" + id; 
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				list.put(ESTID, mCursor.getString(0));
				mCursor.moveToNext();
			}
			mCursor.close();
		} else {
			mCursor.close(); 
			return null;
		}
		return list;
	}

}
