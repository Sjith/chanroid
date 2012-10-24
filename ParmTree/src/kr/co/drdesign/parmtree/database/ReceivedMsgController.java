package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.parmtree.util.c;
import kr.co.drdesign.parmtree.util.l;
import android.content.ContentValues;
import android.content.Context;

public class ReceivedMsgController extends DBController implements c {

	private static ReceivedMsgController instance = null;
	
	private ReceivedMsgController(Context c) {
		mDatabase = new ReceivedMsgDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static ReceivedMsgController getInstance(Context c) {
		if (instance == null) instance = new ReceivedMsgController(c);
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

	public void updateReadtag(String id) {
		String sql = 
			"UPDATE " + ReceivedMsgDBHelper.TABLE + 
			" SET " + READTAG + "=" + "1" + 
			" WHERE " + ID + "=" + id;
		mDB.execSQL(sql);
		l.s("UPDATE READ TAG!!");
	}
	
	@Override
	public ArrayList<Map<String, String>> get() {
		// TODO Auto-generated method stub
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT * FROM " + ReceivedMsgDBHelper.TABLE;
		// 전체선택으로 사용할때는 param을 null로 줘도 되는군하.
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();

				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(USERID, mCursor.getString(mCursor.getColumnIndex(USERID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(TITLE, mCursor.getString(mCursor.getColumnIndex(TITLE)));
				map.put(CONTENTS, mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
				map.put(IMAGE, mCursor.getString(mCursor.getColumnIndex(IMAGE)));
				map.put(COUPONID, mCursor.getString(mCursor.getColumnIndex(COUPONID)));
				map.put(EVENTID, mCursor.getString(mCursor.getColumnIndex(EVENTID)));
				map.put(RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
				map.put(READTAG, mCursor.getString(mCursor.getColumnIndex(READTAG)));
				
				list.add(map);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return list;
	}

	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<String,String>();
		String sql = "SELECT * FROM " + ReceivedMsgDBHelper.TABLE +
					" WHERE " + ID + "=" + id + ";";
		mCursor = mDB.rawQuery(sql, null);		
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(USERID, mCursor.getString(mCursor.getColumnIndex(USERID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(TITLE, mCursor.getString(mCursor.getColumnIndex(TITLE)));
				map.put(CONTENTS, mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
				map.put(IMAGE, mCursor.getString(mCursor.getColumnIndex(IMAGE)));
				map.put(COUPONID, mCursor.getString(mCursor.getColumnIndex(COUPONID)));
				map.put(EVENTID, mCursor.getString(mCursor.getColumnIndex(EVENTID)));
				map.put(RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
				map.put(READTAG, mCursor.getString(mCursor.getColumnIndex(READTAG)));

				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return map;
	}

}
