package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

public class SendedMsgController extends DBController {

	private static SendedMsgController instance = null;
	
	private SendedMsgController(Context c) {
		mDatabase = new SendedMsgDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static SendedMsgController getInstance(Context c) {
		if (instance == null) instance = new SendedMsgController(c);
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
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT * FROM " + SendedMsgDBHelper.TABLE;
		// 전체선택으로 사용할때는 param을 null로 줘도 되는군하.
		mCursor = mDB.rawQuery(sql, null);
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				Map<String,String> map = new HashMap<String,String>();

				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(USERID, mCursor.getString(mCursor.getColumnIndex(USERID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(CONTENTS, mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
				map.put(TITLE, mCursor.getString(mCursor.getColumnIndex(TITLE)));
				map.put(SENDDATE, mCursor.getString(mCursor.getColumnIndex(SENDDATE)));
				
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
		Map<String,String> map = new HashMap<String,String>();
		String sql = "SELECT * FROM " + SendedMsgDBHelper.TABLE +
					" WHERE " + ID + "=" + id + ";";
		mCursor = mDB.rawQuery(sql, null);		
		
		if (mCursor.moveToFirst()) {
			while (!mCursor.isAfterLast()) {
				map.put(ID, mCursor.getString(mCursor.getColumnIndex(ID)));
				map.put(USERID, mCursor.getString(mCursor.getColumnIndex(USERID)));
				map.put(ESTID, mCursor.getString(mCursor.getColumnIndex(ESTID)));
				map.put(CONTENTS, mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
				map.put(TITLE, mCursor.getString(mCursor.getColumnIndex(TITLE)));
				map.put(SENDDATE, mCursor.getString(mCursor.getColumnIndex(SENDDATE)));
				
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return map;
	}

}
