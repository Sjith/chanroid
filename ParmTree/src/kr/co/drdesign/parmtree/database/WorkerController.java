package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

public class WorkerController extends DBController {

	private static WorkerController instance = null;
	
	private WorkerController(Context c) {
		mDatabase = new WorkerDBHelper(c);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public static WorkerController getInstance(Context c) {
		if (instance == null) instance = new WorkerController(c);
		return instance;
	}

	public long insert(int msg) {
		// TODO Auto-generated method stub
		// int값만 받아와서 db에 넣는다.
		mDB.execSQL("INSERT INTO WORKER " + WORKERID + " VALUES " + msg);
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
		mDB.execSQL("DELETE FROM WORKER WHERE " + WORKERID + "=" + id);
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
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String sql = "SELECT " + WORKERID + " FROM WORKER"; 
		mCursor = mDB.rawQuery(sql, null);
		
		while (mCursor.isAfterLast()) {
			Map<String,String> map = new HashMap<String,String>();
			map.put(WORKERID, mCursor.getString(1));
			list.add(map);
		}
		
		return list;
	}

	@Override
	public Map<String, String> get(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
