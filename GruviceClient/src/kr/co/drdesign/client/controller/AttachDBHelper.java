package kr.co.drdesign.client.controller;

import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AttachDBHelper extends SQLiteOpenHelper implements Loggable{

	private static final String DATABASE_NAME = "dr_m_attach.db"; // ���� DB
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "MSG_ATTACHMENT"; // ���� ���̺��

	// ���⼱ ���̺��� ������ֱ⸸ �ϴ°� ����.
	public AttachDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// �̷��� �ϸ� �����ͺ��̽� ������ ���������.
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStatement = 
			"Create Table " + TABLE_NAME + 
			" (  " + AttachController._id 	+ " INTEGER PRIMARY KEY AUTOINCREMENT , " +
			AttachController.UID  	+ " TEXT," + AttachController.NAME	+ " TEXT," +
			AttachController.URL  	+ " TEXT," + AttachController.LENGTH + " INETGER," +
			AttachController.SAVEPATH + " TEXT," + AttachController.TYPE + " TEXT );";
		// ���̺� ���� ������. _id ���� �⺻Ű, �ڵ������� ����

		L( "Create Table : " + sqlStatement);
		db.execSQL(sqlStatement); // ���̺� ����
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

