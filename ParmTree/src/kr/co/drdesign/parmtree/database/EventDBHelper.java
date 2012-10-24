package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDBHelper extends SQLiteOpenHelper implements c {

	private final static String NAME = "event.db";
	private final static int VERSION = 1;
	public final static String TABLE = "EVENT";

	public EventDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + TABLE + " ( " + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + EVENTID
				+ " INTEGER NOT NULL, " + ESTID + " TEXT NOT NULL, " + DESC
				+ " TEXT NOT NULL, " + ENABLEDATE + " TEXT NOT NULL);";
		arg0.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
