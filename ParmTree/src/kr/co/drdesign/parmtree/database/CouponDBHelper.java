package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CouponDBHelper extends SQLiteOpenHelper implements c {
	
	public final static String NAME = "coupon.db";
	private final static int VERSION = 1;
	public final static String TABLE = "COUPON";

	public CouponDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String query = 
			"CREATE TABLE " + TABLE + " ( " + 
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COUPONID + " INTEGER NOT NULL, " + 
			ESTID + " TEXT NOT NULL, " + 
			ENABLEDATE + " TEXT NOT NULL ); ";
		// 아이디, 쿠폰id 유효기간
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
