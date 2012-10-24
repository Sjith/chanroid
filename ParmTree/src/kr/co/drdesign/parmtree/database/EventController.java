package kr.co.drdesign.parmtree.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.util.c;
import android.content.ContentValues;
import android.content.Context;

public class EventController extends DBController implements c {

	public static EventController instance;

	private EventController(Context c) {
		mDatabase = new EventDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}

	public static EventController getInstance(Context c) {
		if (instance == null)
			instance = new EventController(c);
		return instance;
	}

	public long insert(int msg) {
		// TODO Auto-generated method stub
		// int값만 받아와서 db에 넣는다.
		return 0;
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
		mDB.execSQL("DELETE FROM EVENT WHERE " + ID + "=" + id);
		return 0;
	}

	@Override
	public long delete(String id) {
		// TODO Auto-generated method stub
		delete(Integer.valueOf(id));
		return 0;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		mDB.execSQL("DROP TABLE EVENT");
	}

	@Override
	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date current = new Date();
		String date = format.format(current);				
		// 유효기간이 지난 데이터는 리턴하지 않는다.
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String sql = "SELECT * FROM " + EventDBHelper.TABLE + " WHERE " + ENABLEDATE + ">=" + date + " ORDER BY " + ENABLEDATE + " ASC";
		mCursor = mDB.rawQuery(sql, null);

		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(EVENTID, mCursor.getString(mCursor.getColumnIndex(EVENTID)));
				map.put(ENABLEDATE,	mCursor.getString(mCursor.getColumnIndex(ENABLEDATE)));
				map.put(DESC, mCursor.getString(mCursor.getColumnIndex(DESC)));
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
