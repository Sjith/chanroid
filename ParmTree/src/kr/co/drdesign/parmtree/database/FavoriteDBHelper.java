package kr.co.drdesign.parmtree.database;

import kr.co.drdesign.parmtree.util.c;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDBHelper extends SQLiteOpenHelper implements c {

	private final static String NAME = "favorite.db";
	private final static int VERSION = 1;
	private final static String TABLE = "FAVORITE";

	public FavoriteDBHelper(Context context) {
		super(context, NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String query = 
			"CREATE TABLE " + TABLE + " ( " + 
			ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			ESTID + " INTEGER UNIQUE NOT NULL );";
		arg0.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
