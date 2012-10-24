package kr.co.drdesign.client.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.util.Loggable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class AttachController implements Loggable
{
//	private static final String CLASS_NAME = "AttachController";

	public static final String _id 		= "_id";
	public static final String UID 		= "UID";
	public static final String NAME 	= "NAME";
	public static final String URL 		= "URL";
	public static final String SAVEPATH = "SAVEPATH";
	public static final String TYPE 	= "TYPE";
	public static final String LENGTH 	= "LENGTH";
	public static final String INDEX 	= "INDEX";
	
	public static final String NO_CONTENTS = "no contents";

	public static final String TABLE_NAME = "MSG_ATTACHMENT";

	protected AttachDBHelper mDatabase = null; 
	protected Cursor mCursor 		= null;
	protected SQLiteDatabase mDB 	= null;
	private static AttachController attcon = null;

	private AttachController(Context ctx){
		mDatabase = new AttachDBHelper(ctx);
		mDB = mDatabase.getWritableDatabase(); // ���۷κ��� ���� ������ �����ͺ��̽� ����
		L( "Create AttachController");
	}

	// ������ ��ſ� �� �޼��带 ����ؾ� �Ѵ�.
	public static AttachController getInstance( Context ctx ){
		if ( attcon == null )
			attcon = new AttachController(ctx.getApplicationContext());
		return attcon;
	}

	public long insert(Map<String, String> msg)
	// 2011-04-19 ȣ���ϴµ��� �� ����ְ� ������� �Ѵ�.
	{
		// add the new type to our list
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put(UID, msg.get(UID));
		typeRecordToAdd.put(NAME, msg.get(NAME));
		typeRecordToAdd.put(URL, msg.get(URL));
		typeRecordToAdd.put(SAVEPATH, msg.get(SAVEPATH));
		typeRecordToAdd.put(LENGTH, msg.get(LENGTH));
		typeRecordToAdd.put(TYPE, msg.get(TYPE));
		long row = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		// 2011-04-20 ���� DB���� ����ֳ�?

		L("INSERT ITEM :" + "+" + msg.get(UID) + "+" + msg.get(NAME) + "+" + msg.get(URL) + "+" + 
				msg.get(SAVEPATH) + "+" + msg.get(LENGTH) + msg.get(TYPE));
		L( "Insert ITEM " + row + " row.");
		return row;
	}

	public int delete( String uID )
	{
		//���⼭ ���������� ������ �� �� ����.
		//ArrayList<Map<String,String>> attMap = getRunnableAttaches(uID);
		//File f = null;
		//for( Map<String,String> map : attMap )
		//{
		//	f = new File( map.get(SAVEPATH));
		//	if( f.exist() ) f.delete();
		//}
		//
		
//		int row = mDB.delete(TABLE_NAME, "UID=?", new String[]{uID});
		// 2011-04-28 �׳� ¥������ ���� ���� ��������. �ȵǸ� �ٽ� �ٲ����..
		// �� ���� ������ �����α��� �� ���ϴ� �ϳ��󰡳�
		L("delete with UID : " + uID);
		mDB.execSQL("DELETE FROM MSG_ATTACHMENT WHERE UID=?", new String[]{uID});
		// ���� DB���� ���ڰ����� ���� ��ġ�� �ִ� ���� �����Ѵ�.
//		L( "DELETE ITEM " + row + " row.");
		return 0 ;
	}

	public ArrayList<Map <String, String>> getRunnableAttaches(){
		return getRunnableAttaches(null);
	}

	public ArrayList<Map <String, String>> getRunnableAttaches(String uID)
	{
		//SAVEPATH�� �ִ� �͵��� �ٲ�����.
		//���������� �ִ� �͵��� �ٲ�����.

		L( " getRunnableAttaches");
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		if( uID != null ) {
			queryBuilder.appendWhere(UID + "=" + uID + " and ");
		}
		queryBuilder.appendWhere(SAVEPATH +" not NULL");

		String asColumnsToReturn[] = {
				_id,
				UID,
				NAME, 
				URL,
				SAVEPATH,
				TYPE,
				LENGTH 
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);
		ArrayList<Map <String, String>> attachInfo = new ArrayList<Map <String, String>>();

		Map <String, String> attachFile = null;
		if ( mCursor.moveToFirst() )
		{
			int index=0;
			String savePath = null;
			while( !mCursor.isAfterLast() ) // ���� ���� ���������� Ŀ���� ������.
			{

				savePath = mCursor.getString(mCursor.getColumnIndex(SAVEPATH));

				if( savePath != null && !savePath.equalsIgnoreCase("NULL") 
						&& new File(savePath).exists() ) {
					
					attachFile = new HashMap<String, String>();
					attachFile.put(INDEX, "" + index++);
					attachFile.put(_id, mCursor.getString(mCursor.getColumnIndex(_id)));
					attachFile.put(UID, mCursor.getString(mCursor.getColumnIndex(UID)));
					attachFile.put(NAME, mCursor.getString(mCursor.getColumnIndex(NAME)));
					attachFile.put(URL, mCursor.getString(mCursor.getColumnIndex(URL)));
					attachFile.put(SAVEPATH, mCursor.getString(mCursor.getColumnIndex(SAVEPATH)));
					attachFile.put(TYPE, mCursor.getString(mCursor.getColumnIndex(TYPE)));
					attachFile.put(LENGTH, mCursor.getString(mCursor.getColumnIndex(LENGTH)));
					attachInfo.add(attachFile);
					// Ŀ������ ������ ������ �׸��� �о��
				}
				mCursor.moveToNext();
				// �ݺ��� ��
			}
		}
		mCursor.close(); // �� ������ �ݾ���� �Ѵ�.
		return attachInfo; // ���� ��ġ ������ ����
	}

	public ArrayList<Map <String, String>> getAttaches()
	{

		//��� �� ������ 
		//SAVEPATH�� �ִ� �͵� �� ���� ������ ����Ǿ� �ִ� �͵��� ���� ��ȯ�Ѵ�.
		L( "getAttachges" );
		return getAttachesByUID(null);
	}

	
	// DB�κ��� �޽����� ÷�ε� ���� ����� �о��
	public ArrayList<Map <String, String>> getAttachesByUID(String uID )
	{

		L( "getAttachesByUID(" + uID + ")" );
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		if( uID != null ) queryBuilder.appendWhere(UID + "=" + uID );

		String asColumnsToReturn[] = 
		{
				_id,
				UID,
				NAME, 
				URL,
				SAVEPATH,
				TYPE,
				LENGTH 
		};
		// ���� DB���� �о�� �׸�

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);
		// ���� DB���� �������� ������ Ŀ�� ����
		ArrayList<Map <String, String>> attachInfo = new ArrayList<Map <String, String>>();

		Map <String, String> attachFile;
		if ( mCursor.moveToFirst() ) // ÷�������� ���� ���
		{
			int index=0;
			while( !mCursor.isAfterLast() ) 
			{
				attachFile = new HashMap<String, String>();
				attachFile.put(INDEX, "" + index++); // �޽��� �������� ÷������ ��ȣ
				attachFile.put(_id, mCursor.getString(mCursor.getColumnIndex(_id)));
				attachFile.put(UID, mCursor.getString(mCursor.getColumnIndex(UID)));
				attachFile.put(NAME, mCursor.getString(mCursor.getColumnIndex(NAME)));
				attachFile.put(URL, mCursor.getString(mCursor.getColumnIndex(URL)));
				attachFile.put(SAVEPATH, mCursor.getString(mCursor.getColumnIndex(SAVEPATH)));
				attachFile.put(TYPE, mCursor.getString(mCursor.getColumnIndex(TYPE)));
				attachFile.put(LENGTH, mCursor.getString(mCursor.getColumnIndex(LENGTH)));
				attachInfo.add(attachFile);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return attachInfo;
	}
	
	public ArrayList<Map <String, String>> getAttachByUIDWithoutAPP(String uID ) {
		// ÷�����Ͽ��� .apk �� �����ϰ� ����. �׳� �ɷ��ִ� ���Ҹ� �ϴµ�
		L( "getAppByUID(" + uID + ")" );
		ArrayList<Map <String, String>> attachInfo = 
			getAttachesByUID(uID);
		
		int listSize = attachInfo.size();
		Map<String,String> item;
		for( int i = 0; i < listSize; i++)
		{
			item = attachInfo.get(i);
			if (item.get(NAME) != null) {
				if( item.get(NAME).toLowerCase().contains(".apk"))
					// ������ ������ �� �� ������ ������ ����
				{
					L("Contains .apk");
					attachInfo.remove(i);
					i--;
					listSize = attachInfo.size();
				}
			}
		}
		return attachInfo;
	}
	
	public Map <String, String> getAppByUID(String uID ) {
		// ÷�����Ͽ��� .apk �� ����
		L( "getAppByUID(" + uID + ")" );
		ArrayList<Map <String, String>> attachInfo = 
			getAttachesByUID(uID);
		for( Map<String,String> item : attachInfo )
		{
			Log.i("#####   null check", item.get(NAME));
			if( item.get(NAME).toLowerCase().contains(".apk"))
				return item;
		}
		return null;
	}

	public Map <String, String> getAttachesByIndex(String index )
	{
		L( "getAttachesByIndex()" );
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		if( index != null )	queryBuilder.appendWhere(_id + "=" + index );
		
		String asColumnsToReturn[] = 
		{
				_id,
				UID,
				NAME, 
				URL,
				SAVEPATH,
				TYPE,
				LENGTH 
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);

		Map <String, String> attachFile = null;
		if ( mCursor.moveToFirst() )
		{
			int i=0;
			while( !mCursor.isAfterLast() ) 
			{
				attachFile = new HashMap<String, String>();
				attachFile.put(INDEX, "" + i++);
				attachFile.put(_id, mCursor.getString(mCursor.getColumnIndex(_id)));
				attachFile.put(UID, mCursor.getString(mCursor.getColumnIndex(UID)));
				attachFile.put(NAME, mCursor.getString(mCursor.getColumnIndex(NAME)));
				attachFile.put(URL, mCursor.getString(mCursor.getColumnIndex(URL)));
				attachFile.put(SAVEPATH, mCursor.getString(mCursor.getColumnIndex(SAVEPATH)));
				attachFile.put(TYPE, mCursor.getString(mCursor.getColumnIndex(TYPE)));
				attachFile.put(LENGTH, mCursor.getString(mCursor.getColumnIndex(LENGTH)));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return attachFile;
	}

	public long insert(String uid, String name, String url, String savePath, String type, String length) {
		L( "INSERT " + UID + "," + name +"...");
		//NULL �� ���ð�� ó���� ���־�� �Ѵ�.
		
		ContentValues cv = new ContentValues();
		cv.put(UID, uid);
		cv.put(NAME, name);
		cv.put(URL, url);
		cv.put(SAVEPATH, savePath);
		cv.put(TYPE, type);
		cv.put(LENGTH, length);
		return insert( cv ); // �������̵��� �޼���� ���� �ѱ�
	}

	public long insert(ContentValues cv) {
		return mDB.insert(TABLE_NAME, null, cv);
	}

	public void setSavePath( String index, String path )
	{
		ContentValues cv = new ContentValues();
		cv.put( SAVEPATH, path);
		mDB.update(TABLE_NAME, cv, _id + "=?", new String[]{index} );
		L( "update _id = " + index + ", savePath = " + path );
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
