package kr.co.chunhoshop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CartDBController implements ParserTag {

	CartDBHelper mDatabase = null;
	SQLiteDatabase mDB = null;
	Cursor mCursor = null;
	private static CartDBController instance = null;

	private CartDBController(Context c) {
		mDatabase = new CartDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}

	public static CartDBController getInstance(Context c) {
		if (instance == null)
			instance = new CartDBController(c);
		return instance;
	}

	public boolean insert(Map<String, CharSequence> map) {
		ArrayList<Map<String, String>> list = get();
		for (int i = 0; i < list.size(); i++) {
			if (map.get(PNUM).toString().equals(list.get(i).get(PNUM))) {
				return false;
			}
		}

		ContentValues cv = new ContentValues();
		cv.put(PNUM, map.get(PNUM).toString());
		cv.put(PNAME, map.get(PNAME).toString().trim());
		cv.put(PPRICE, map.get(PPRICE).toString());
		cv.put(PIMG, map.get(PIMG).toString());
		cv.put(PPOINT, map.get(PPOINT).toString());
		cv.put(POPTION, map.get(POPTION).toString());
		cv.put(POPTIONNAME, map.get(POPTIONNAME).toString());
		cv.put(POPTIONVALUE, map.get(POPTIONVALUE).toString());
		cv.put(PGIFT, map.get(PGIFT).toString());
		cv.put(PHEALTH, map.get(PHEALTH).toString());
		cv.put(PCOUNT, "1");
		mDB.insert("CART", null, cv);

		return true;
	}

	public boolean delete(int id) {
		String sql = "DELETE FROM CART WHERE _id = " + id + ";";
		mDB.execSQL(sql);
		return true;
	}

	public void count(String count, String id) {
		String sql = "UPDATE CART SET " + PCOUNT + "=" + count + " WHERE _id="
				+ id;
		mDB.execSQL(sql);
	}

	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "SELECT * FROM CART ORDER BY " + PNUM + " DESC ";
		// 전체선택으로 사용할때는 param을 null로 줘도 되는군하.
		mCursor = mDB.rawQuery(sql, null);

		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String, String> map = new HashMap<String, String>();

				map.put(PNUM, mCursor.getString(mCursor.getColumnIndex(PNUM)));
				map.put(PNAME, mCursor.getString(mCursor.getColumnIndex(PNAME)));
				map.put(PPRICE,
						mCursor.getString(mCursor.getColumnIndex(PPRICE)));
				map.put(PIMG, mCursor.getString(mCursor.getColumnIndex(PIMG)));
				map.put(PPOINT,
						mCursor.getString(mCursor.getColumnIndex(PPOINT)));
				map.put(PHEALTH,
						mCursor.getString(mCursor.getColumnIndex(PHEALTH)));
				map.put(PCOUNT,
						mCursor.getString(mCursor.getColumnIndex(PCOUNT)));
				map.put(POPTION,
						mCursor.getString(mCursor.getColumnIndex(POPTION)));
				map.put(POPTIONNAME,
						mCursor.getString(mCursor.getColumnIndex(POPTIONNAME)));
				map.put(POPTIONVALUE,
						mCursor.getString(mCursor.getColumnIndex(POPTIONVALUE)));
				map.put(PGIFT, mCursor.getString(mCursor.getColumnIndex(PGIFT)));
				map.put("_id", mCursor.getString(mCursor.getColumnIndex("_id")));

				list.add(map);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return list;
	}
}
