package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SendedMsgDBHelper extends SQLiteOpenHelper implements c {

	private final static String NAME = "sended_msg.db";
	private final static int VERSION = 1;
	public final static String TABLE = "SENDED";

	public SendedMsgDBHelper(Context context) {
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
			SENDDATE + " TEXT, " +
			CONTENTS + " TEXT);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
