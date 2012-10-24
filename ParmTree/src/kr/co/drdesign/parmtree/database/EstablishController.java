package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.util.l;
import android.content.ContentValues;
import android.content.Context;

public class EstablishController extends DBController {

	private static EstablishController instance = null;
	
	private EstablishController(Context c) {
		mDatabase = new EstablishDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static EstablishController getInstance(Context c) {
		if (instance == null) instance = new EstablishController(c);
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
	public long delete(String id) {
		// TODO Auto-generated method stub
		String sql = 
			"DELETE FROM " + EstablishDBHelper.TABLE +
			" WHERE " + ID + "=" + id + ";";
		mDB.execSQL(sql);
		return 0;
	}

	@Override
	public long delete(int id) {
		// TODO Auto-generated method stub
		String sql = 
			"DELETE FROM " + EstablishDBHelper.TABLE +
			" WHERE " + ID + "=" + id + ";";
		mDB.execSQL(sql);
		return 0;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Map<String,String>> getPremi() {
		ArrayList<Map<String,String>> get = get();
		ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for (int i = 0; i < get.size(); i++) {
			Map<String,String> map = get.get(i);
			if (map.get(PREMI).equals("1")) result.add(map);
		}
		return result;		
	}
	
	public ArrayList<Map<String,String>> search(String name, String region, String kind) {
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if (name == null) name = "";
		if (region == null) region = "";
		if (kind == null) kind = "";
		String sql = "SELECT * FROM " + EstablishDBHelper.TABLE + " WHERE " + 
		 	ESTNAME + " LIKE" + " '%" + name + "%' " + "AND " +
		 	ESTADDR + " LIKE" + " '%" + region + "%' " + "AND " +
		 	ESTKIND + " LIKE" + " '%" + kind + "%' " +
			" ORDER BY " + PREMI + " DESC ";
		l.s("query : " + sql);
	 	mCursor = mDB.rawQuery(sql, null);
		// 전체선택으로 사용할때는 param을 null로 줘도 되는군하.
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();

				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(ESTNAME, mCursor.getString(mCursor.getColumnIndex(ESTNAME)));
				map.put(ESTADDR, mCursor.getString(mCursor.getColumnIndex(ESTADDR)));
				map.put(ESTKIND, mCursor.getString(mCursor.getColumnIndex(ESTKIND)));
				map.put(DESC, mCursor.getString(mCursor.getColumnIndex(DESC)));
				map.put(LON, mCursor.getString(mCursor.getColumnIndex(LON)));
				map.put(LAT, mCursor.getString(mCursor.getColumnIndex(LAT)));
				map.put(PREMI, mCursor.getString(mCursor.getColumnIndex(PREMI)));
				map.put(ESTPHONE, mCursor.getString(mCursor.getColumnIndex(ESTPHONE)));
				
				list.add(map);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return list;
	}
	
	@Override
	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT * FROM " + EstablishDBHelper.TABLE + " ORDER BY " + PREMI + " DESC ";
		// 전체선택으로 사용할때는 param을 null로 줘도 되는군하.
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();

				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(ESTNAME, mCursor.getString(mCursor.getColumnIndex(ESTNAME)));
				map.put(ESTADDR, mCursor.getString(mCursor.getColumnIndex(ESTADDR)));
				map.put(ESTKIND, mCursor.getString(mCursor.getColumnIndex(ESTKIND)));
				map.put(DESC, mCursor.getString(mCursor.getColumnIndex(DESC)));
				map.put(LON, mCursor.getString(mCursor.getColumnIndex(LON)));
				map.put(LAT, mCursor.getString(mCursor.getColumnIndex(LAT)));
				map.put(PREMI, mCursor.getString(mCursor.getColumnIndex(PREMI)));
				map.put(ESTPHONE, mCursor.getString(mCursor.getColumnIndex(ESTPHONE)));
				
				list.add(map);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return list;
	}
	
	public Map<String, String> get(String id) {
		return get(Integer.valueOf(id));
	}

	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		l.s("est get() : " + id);
		Map<String,String> map = new HashMap<String,String>();
		String sql = "SELECT * FROM " + EstablishDBHelper.TABLE +
					" WHERE " + ID + "=" + id;
		mCursor = mDB.rawQuery(sql, null);		
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {

				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(ESTNAME, mCursor.getString(mCursor.getColumnIndex(ESTNAME)));
				map.put(ESTADDR, mCursor.getString(mCursor.getColumnIndex(ESTADDR)));
				map.put(ESTKIND, mCursor.getString(mCursor.getColumnIndex(ESTKIND)));
				map.put(DESC, mCursor.getString(mCursor.getColumnIndex(DESC)));
				map.put(LON, mCursor.getString(mCursor.getColumnIndex(LON)));
				map.put(LAT, mCursor.getString(mCursor.getColumnIndex(LAT)));
				map.put(PREMI, mCursor.getString(mCursor.getColumnIndex(PREMI)));
				map.put(ESTPHONE, mCursor.getString(mCursor.getColumnIndex(ESTPHONE)));

				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return map;
	}

}
