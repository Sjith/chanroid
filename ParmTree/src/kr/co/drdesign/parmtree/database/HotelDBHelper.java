package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotelDBHelper extends SQLiteOpenHelper implements c {

	public final static String NAME = "hotel.db";
	private final static int VERSION = 1;
	public final static String TABLE = "HOTEL";

	
	public HotelDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String query = 
			"CREATE TABLE " + TABLE + " ( " + 
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			ESTNAME + " TEXT NOT NULL, " +
			ESTADDR + " TEXT NOT NULL, " +
			DESC + " TEXT, " +
			LON + " FLOAT, " +
			LAT + " FLOAT" + 
			");";
		arg0.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
