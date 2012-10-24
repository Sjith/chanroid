package kr.co.drdesign.parmtree.database;

import java.util.ArrayList;
import java.util.Map;

import kr.co.drdesign.parmtree.util.c;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DBController implements c {

	SQLiteOpenHelper mDatabase = null;
	SQLiteDatabase mDB = null;
	Cursor mCursor = null;
	
	public abstract long insert(Map<String, String> msg);
	public abstract long insert(ContentValues cv);
	
	public abstract long delete(int id);
	public abstract long delete(String id);
	public abstract void deleteAll();
	
	public abstract ArrayList<Map<String, String>> get();
	public abstract Map<String, String> get(int id);
	
	
}
