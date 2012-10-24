package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReceivedMsgDBHelper extends SQLiteOpenHelper implements c {

	private final static String NAME = "received_msg.db";
	private final static int VERSION = 1;
	public final static String TABLE = "RECEIVED";
	
	public ReceivedMsgDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = 
			"CREATE TABLE " + TABLE + " ( " + 
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			USERID + " TEXT, " + 
			ESTID + " TEXT, " + 
			TITLE + " TEXT, " +
			CONTENTS + " TEXT, " + 
			IMAGE + " TEXT, " + 
			COUPONID + " INTEGER, " + 
			EVENTID + " INTEGER, " + 
			RECEIVEDATE + " TEXT, " +
			READTAG + " INTEGER);";
		// 아이디, 보낸사람, 내용
		db.execSQL(sql);
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
