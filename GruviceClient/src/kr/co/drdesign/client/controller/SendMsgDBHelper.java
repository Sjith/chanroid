package kr.co.drdesign.client.controller;

import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SendMsgDBHelper extends SQLiteOpenHelper implements Loggable{

	private static final String DATABASE_NAME = "sendmsg.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "SENDMSG";

	public SendMsgDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		L( "Create Message DB Helper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + 
			SendMsgController._id 		+ " INTEGER PRIMARY KEY AUTOINCREMENT ," +
			SendMsgController.TITLE 	+ " TEXT," +
			SendMsgController.CONTENTS 	+ " TEXT," +
			SendMsgController.GROUP_ID 	+ " TEXT," +
			SendMsgController.ETC 		+ " TEXT," +
			SendMsgController.SENDER 	+ " TEXT," +
			SendMsgController.RECEIVER 	+ " TEXT," +
			SendMsgController.SENDDATE 	+ " TEXT);";

		L( "Create Table : " + sqlStatement);
		db.execSQL(sqlStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void L(char i, String log) 
	{
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