package kr.co.drdesign.parmtree.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

public class CouponController extends DBController {

	private static CouponController instance;

	private CouponController(Context c) {
		mDatabase = new CouponDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}

	public static CouponController getInstance(Context c) {
		if (instance == null)
			instance = new CouponController(c);
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
		return 0;
	}

	@Override
	public long delete(int id) {
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
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date current = new Date();
		int date = Integer.valueOf(format.format(current));				
		// 유효기간이 지난 데이터는 리턴하지 않는다.
		String sql = "SELECT * FROM " + CouponDBHelper.TABLE + " WHERE " + ENABLEDATE + ">=" + date + " ORDER BY " + ENABLEDATE + " ASC";
		mCursor = mDB.rawQuery(sql, null);
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {				
				Map<String,String> map = new HashMap<String,String>();
				map.put(COUPONID, mCursor.getString(mCursor.getColumnIndex(COUPONID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(ENABLEDATE, mCursor.getString(mCursor.getColumnIndex(ENABLEDATE)));
				list.add(map);
				mCursor.moveToNext();
			}
		}
		return list;
	}

	@Override
	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECE * FROM " + CouponDBHelper.TABLE + " WHERE "
				+ ESTID + "=" + id;
		mCursor = mDB.rawQuery(sql, null);

		if (mCursor.moveToFirst()) {
			map.put(COUPONID,
					mCursor.getString(mCursor.getColumnIndex(COUPONID)));
			map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
			map.put(ENABLEDATE,
					mCursor.getString(mCursor.getColumnIndex(ENABLEDATE)));
		}

		return map;
	}

}
