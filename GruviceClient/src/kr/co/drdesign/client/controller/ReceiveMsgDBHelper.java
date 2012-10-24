package kr.co.drdesign.client.controller;

import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReceiveMsgDBHelper extends SQLiteOpenHelper implements Loggable{

	private static final String DATABASE_NAME = "receivemsg.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "MSG_DB";

	public ReceiveMsgDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + 
			ReceiveMsgController._id 		+ " INTEGER PRIMARY KEY AUTOINCREMENT , " +
			ReceiveMsgController.UID  		+ " TEXT UNIQUE," +
			ReceiveMsgController.TITLE 		+ " TEXT," +
			ReceiveMsgController.CONTENTS 	+ " TEXT," +
			ReceiveMsgController.APPPATH 	+ " TEXT," +
			ReceiveMsgController.ATTACHMENTS + " TEXT," +
			ReceiveMsgController.READED 	+ " TEXT," +
			ReceiveMsgController.GROUP_ID 	+ " TEXT," +
			ReceiveMsgController.ETC 		+ " TEXT," +
			ReceiveMsgController.SENDER 	+ " TEXT," +
			ReceiveMsgController.CREATEDATE + " TEXT," +
			ReceiveMsgController.RECEIVEDATE + " TEXT);";

		L("ReceivedMsgDBHelper.");
		L("Create Table : " + sqlStatement);
		db.execSQL(sqlStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
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