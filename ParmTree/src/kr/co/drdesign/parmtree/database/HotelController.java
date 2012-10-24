package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.util.c;
import android.content.ContentValues;
import android.content.Context;

public class HotelController extends DBController implements c {

	public static HotelController instance;
	
	private HotelController(Context c) {
		mDatabase = new HotelDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static HotelController getInstance(Context c) {
		if (instance == null) instance = new HotelController(c);
		return instance;
	}
	
	@Override
	public long insert(Map<String, String> msg) {
		// TODO Auto-generated method stub
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
		return 0;
	}

	@Override
	public long delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT * FROM " + HotelDBHelper.TABLE;
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();
				map.put(ESTNAME, mCursor.getString(mCursor.getColumnIndex(ESTNAME)));
				map.put(ESTADDR, mCursor.getString(mCursor.getColumnIndex(ESTADDR)));
				map.put(DESC, mCursor.getString(mCursor.getColumnIndex(DESC)));
				map.put(LON, mCursor.getString(mCursor.getColumnIndex(LON)));
				map.put(LAT, mCursor.getString(mCursor.getColumnIndex(LAT)));
				list.add(map);
				mCursor.moveToNext();
			}
		}
		
		return list;
	}

	@Override
	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
