package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EstablishDBHelper extends SQLiteOpenHelper implements c{
	
	private final static String NAME = "est.db";
	private final static int VERSION = 1;
	public final static String TABLE = "ESTABLISH";

	public EstablishDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = 
			"CREATE TABLE " + TABLE + " ( " + 
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			ESTID + " TEXT NOT NULL, " +
			ESTNAME + " TEXT NOT NULL, " +
			ESTADDR + " TEXT NOT NULL, " +
			ESTKIND + " TEXT NOT NULL, " +
			DESC + " TEXT, " + 
			LON + " FLOAT NOT NULL, " + 
			LAT + " FLOAT NOT NULL, " + 
			PREMI + " INTEGER NOT NULL, " +
			ESTPHONE + " TEXT NOT NULL);";
		// 아이디, 업체명, 주소, 전화번호, 업종, 설명, 위도, 경도, 프리미엄여부
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
