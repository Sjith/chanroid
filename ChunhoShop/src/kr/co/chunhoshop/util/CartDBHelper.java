package kr.co.chunhoshop.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CartDBHelper extends SQLiteOpenHelper implements ParserTag {

	private final static String NAME = "cart.db";
	private final static int VERSION = 1;
	private final static String TABLE = "CART";

	public CartDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String sql = 
			"CREATE TABLE " + TABLE + " ( " + 
			"_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			PNUM + " TEXT, " +
			PNAME + " TEXT, " +
			PPRICE + " TEXT, " +
			PIMG + " TEXT, " +
			PGIFT + " TEXT, " + 
			PHEALTH + " TEXT, " + // 건강기능식품
			PCOUNT + " TEXT, " +
			POPTION + " TEXT, " +
			POPTIONNAME + " TEXT, " + 
			POPTIONVALUE + " TEXT, " +
			PPOINT + " TEXT);";
		// 아이디, 업체명, 주소, 전화번호, 업종, 설명, 위도, 경도, 프리미엄여부
		arg0.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
