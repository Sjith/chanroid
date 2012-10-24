package kr.co.drdesign.client.controller;

import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AttachDBHelper extends SQLiteOpenHelper implements Loggable{

	private static final String DATABASE_NAME = "dr_m_attach.db"; // 내부 DB
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "MSG_ATTACHMENT"; // 관련 테이블명

	// 여기선 테이블을 만들어주기만 하는것 같다.
	public AttachDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// 이렇게 하면 데이터베이스 파일이 만들어진다.
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + AttachController._id 	+ " INTEGER PRIMARY KEY AUTOINCREMENT , " +
			AttachController.UID  	+ " TEXT," + AttachController.NAME	+ " TEXT," +
			AttachController.URL  	+ " TEXT," + AttachController.LENGTH + " INETGER," +
			AttachController.SAVEPATH + " TEXT," + AttachController.TYPE + " TEXT );";
		// 테이블 생성 쿼리문. _id 값은 기본키, 자동증가로 설정

		L( "Create Table : " + sqlStatement);
		db.execSQL(sqlStatement); // 테이블 생성
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
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

