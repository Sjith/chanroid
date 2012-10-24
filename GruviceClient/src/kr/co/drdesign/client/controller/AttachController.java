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
		mDB = mDatabase.getWritableDatabase(); // 헬퍼로부터 쓰기 가능한 데이터베이스 생성
		L( "Create AttachController");
	}

	// 생성자 대신에 이 메서드를 사용해야 한다.
	public static AttachController getInstance( Context ctx ){
		if ( attcon == null )
			attcon = new AttachController(ctx.getApplicationContext());
		return attcon;
	}

	public long insert(Map<String, String> msg)
	// 2011-04-19 호출하는데서 다 집어넣고 보내줘야 한다.
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
		// 2011-04-20 내부 DB에만 집어넣나?

		L("INSERT ITEM :" + "+" + msg.get(UID) + "+" + msg.get(NAME) + "+" + msg.get(URL) + "+" + 
				msg.get(SAVEPATH) + "+" + msg.get(LENGTH) + msg.get(TYPE));
		L( "Insert ITEM " + row + " row.");
		return row;
	}

	public int delete( String uID )
	{
		//여기서 실제파일을 지워야 할 것 같다.
		//ArrayList<Map<String,String>> attMap = getRunnableAttaches(uID);
		//File f = null;
		//for( Map<String,String> map : attMap )
		//{
		//	f = new File( map.get(SAVEPATH));
		//	if( f.exist() ) f.delete();
		//}
		//
		
//		int row = mDB.delete(TABLE_NAME, "UID=?", new String[]{uID});
		// 2011-04-28 그냥 짜증나서 직접 쿼리 때려넣음. 안되면 다시 바꿔야지..
		// 아 역시 쿼리가 진리로구나 아 편하다 싹날라가네
		L("delete with UID : " + uID);
		mDB.execSQL("DELETE FROM MSG_ATTACHMENT WHERE UID=?", new String[]{uID});
		// 내부 DB에서 인자값으로 받은 위치에 있는 행을 삭제한다.
//		L( "DELETE ITEM " + row + " row.");
		return 0 ;
	}

	public ArrayList<Map <String, String>> getRunnableAttaches(){
		return getRunnableAttaches(null);
	}

	public ArrayList<Map <String, String>> getRunnableAttaches(String uID)
	{
		//SAVEPATH가 있는 것들을 다꺼낸다.
		//실제파일이 있는 것들을 다꺼낸다.

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
			while( !mCursor.isAfterLast() ) // 읽은 행의 마지막까지 커서를 돌린다.
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
					// 커서에서 쿼리로 지정한 항목을 읽어옴
				}
				mCursor.moveToNext();
				// 반복문 끝
			}
		}
		mCursor.close(); // 다 쓰고나면 닫아줘야 한다.
		return attachInfo; // 파일 위치 정보를 리턴
	}

	public ArrayList<Map <String, String>> getAttaches()
	{

		//모두 다 꺼내서 
		//SAVEPATH가 있는 것들 중 실제 파일이 저장되어 있는 것들을 빼고 반환한다.
		L( "getAttachges" );
		return getAttachesByUID(null);
	}

	
	// DB로부터 메시지에 첨부된 파일 목록을 읽어옴
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
		// 내부 DB에서 읽어올 항목

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);
		// 내부 DB에서 쿼리문을 실행할 커서 지정
		ArrayList<Map <String, String>> attachInfo = new ArrayList<Map <String, String>>();

		Map <String, String> attachFile;
		if ( mCursor.moveToFirst() ) // 첨부파일이 있을 경우
		{
			int index=0;
			while( !mCursor.isAfterLast() ) 
			{
				attachFile = new HashMap<String, String>();
				attachFile.put(INDEX, "" + index++); // 메시지 내에서의 첨부파일 번호
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
		// 첨부파일에서 .apk 를 제외하고 리턴. 그냥 걸러주는 역할만 하는듯
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
					// 가져온 아이템 중 앱 파일이 있으면 제외
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
		// 첨부파일에서 .apk 만 리턴
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
		//NULL 이 들어올경우 처리를 해주어야 한다.
		
		ContentValues cv = new ContentValues();
		cv.put(UID, uid);
		cv.put(NAME, name);
		cv.put(URL, url);
		cv.put(SAVEPATH, savePath);
		cv.put(TYPE, type);
		cv.put(LENGTH, length);
		return insert( cv ); // 오버라이딩된 메서드로 값을 넘김
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
