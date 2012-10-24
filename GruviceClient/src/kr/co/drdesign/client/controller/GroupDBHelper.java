package kr.co.drdesign.client.controller;

import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GroupDBHelper extends SQLiteOpenHelper implements Loggable{

	private static final String DATABASE_NAME = "dr_m_group.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "GRP_DB";

	public GroupDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		L( "create GroupDBHelper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + 
			GroupController._id 		+ " INTEGER PRIMARY KEY AUTOINCREMENT , " +
			GroupController.GROUP_ID 	+ " TEXT UNIQUE," +
			GroupController.GROUP_NAME 	+ " TEXT," +
			GroupController.GROUP_KNAME + " TEXT," +
			GroupController.GROUP_DESC + " TEXT," +
			GroupController.ETC 		+ " TEXT );";

		L( "Create Table : " + sqlStatement);
		db.execSQL(sqlStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	public void L(char i, String log) {
		if( IS_DEBUG_MODE )
			switch( i )
			{
			case 'd' :
				Log.w(DEBUG_TAG, log);
				break;
			case 'e' :
				Log.e(DEBUG_TAG, log);
				break;
			case 'i' : 
				Log.i(DEBUG_TAG, log);
				break;
			case 'w' :
				Log.w(DEBUG_TAG, log);
				break;
			}
	}

	public void L(String log) {
		if( IS_DEBUG_MODE ) Log.i(DEBUG_TAG, log);
	}
}
