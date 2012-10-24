package kr.co.drdesign.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.co.drdesign.util.Loggable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class SendMsgController implements MsgController, Loggable{


	public static final String _id 			= MsgController._id;
	public static final String TITLE 		= "TITLE";
	public static final String CONTENTS 	= "CONTENTS";
	public static final String GROUP_ID 	= GroupController.GROUP_ID;
	public static final String ETC 			= "ETC";
	public static final String SENDER 		= "SENDER";
	public static final String RECEIVER 	= "RECEIVER";
	public static final String SENDDATE		= "SENDDATE";
	
	
	

	public static final String TABLE_NAME = SendMsgDBHelper.TABLE_NAME;

	protected SendMsgDBHelper mDatabase = null; 
	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;

	private static SendMsgController msgcon = null;

	private SendMsgController(Context ctx){
		mDatabase = new SendMsgDBHelper(ctx);
		mDB = mDatabase.getWritableDatabase();
		L( "SendMsgController." );
	}

	public static SendMsgController getInstance( Context ctx ){
		if ( msgcon == null )
			msgcon = new SendMsgController(ctx.getApplicationContext());
		return msgcon;
	}

	public long insert(Map<String, String> msg)
	{
		Log.i("DR" ,"TITLE = " + msg.get(TITLE));
		Log.i("DR" ,"CONTENTS = " + msg.get(CONTENTS));
		Log.i("DR" ,"GROUP_ID = " + msg.get(GROUP_ID));
		Log.i("DR" ,"RECEIVER = " + msg.get(RECEIVER));
		Log.i("DR" ,"SENDDATE = " + msg.get(SENDDATE));
		Log.i("DR" ,"SENDER = " +  msg.get(SENDER));
		Log.i("DR" ,"ETC = " + msg.get(ETC));
		
		Iterator<String> it = msg.keySet().iterator();
		while( it.hasNext() )
		{
			String key = it.next();
			Log.i("DR", "key : " + key + ", value : " + msg.get(key));
		}
		
		// add the new type to our list
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put(TITLE, msg.get(TITLE));
		typeRecordToAdd.put(CONTENTS,msg.get(CONTENTS));
		typeRecordToAdd.put(GROUP_ID, msg.get(GROUP_ID));
		typeRecordToAdd.put(RECEIVER, msg.get(RECEIVER));
		typeRecordToAdd.put(SENDDATE, msg.get(SENDDATE));
		typeRecordToAdd.put(SENDER, msg.get(SENDER));
		typeRecordToAdd.put(ETC, msg.get(ETC));

		long row = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		L( "INSERT ITEM " + row + "row.");
		return row;
	}

	public long insert(ContentValues cv){
		return mDB.insert(TABLE_NAME, null, cv);
	}

	public int delete( String index )
	{
		int row = mDB.delete(TABLE_NAME, _id + "=?", new String[]{index});
		L( "DELETE ITEM " + row + "row.");
		return row ;
	}

	// return Value, matching Data by _id  
	public Map<String, String> getMsg(String index )
	{

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(SendMsgController._id + "=" + index);

		String asColumnsToReturn[] = 
		{ 		_id, 
				TITLE, 
				CONTENTS, 
				GROUP_ID, 
				ETC, 
				SENDER,
				RECEIVER, 
				SENDDATE
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);

		Map<String, String> map = new HashMap<String, String>();
		if ( mCursor.moveToFirst() ){
			map.put( _id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put( TITLE, 	mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put( CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put( GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put( ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put( SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
			map.put( RECEIVER, 	mCursor.getString(mCursor.getColumnIndex(RECEIVER)));
			map.put( SENDDATE, 	mCursor.getString(mCursor.getColumnIndex(SENDDATE)));
		}
		mCursor.close();
		return map;
	}

	public List<Map<String,String>> getMsgList()
	{
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(SendMsgController.TABLE_NAME);

		// DB ÁúÀÇ¹®
		String asColumnsToReturn[] = 
		{		_id, 
				TITLE, 
				GROUP_ID,
				SENDDATE,
				RECEIVER,
				ETC };
		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn,  null, null, null, null, SendMsgController._id + " DESC");
		List<Map<String,String>> msgTitleList = new ArrayList<Map<String,String>>();

		Map<String, String> map = null;

		String _id, title, senddate, receiver;
		String groupId;
		if ( mCursor != null && mCursor.moveToFirst() )
		{
			while( !mCursor.isAfterLast() ) 
			{
				map = new HashMap<String, String>();

				_id 	= mCursor.getString(mCursor.getColumnIndex(SendMsgController._id));
				title 	= mCursor.getString(mCursor.getColumnIndex(TITLE));
				groupId = mCursor.getString(mCursor.getColumnIndex(GROUP_ID));
				senddate = mCursor.getString(mCursor.getColumnIndex(SENDDATE));
				receiver = mCursor.getString(mCursor.getColumnIndex(RECEIVER));

				map.put(SendMsgController._id, _id);
				map.put(SendMsgController.TITLE, title);
				map.put(SendMsgController.GROUP_ID, groupId);
				map.put(SendMsgController.SENDDATE, senddate);
				map.put(SendMsgController.RECEIVER, receiver);

				msgTitleList.add(map);
				mCursor.moveToNext();
			}
			mCursor.close();
		}

		return msgTitleList;
	}

	public List<Map<String, String>> getMsgListbyGroup(String[] groupIds) {
		return getMsgList();
	}

	public Map<String, String> getBiggerMsg(String index) 
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere( _id + "<" + index);

		String asColumnsToReturn[] = 
		{ _id, 
				TITLE, 
				CONTENTS, 
				SENDDATE,
				GROUP_ID, 
				ETC, 
				SENDER };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, _id + " DESC", " 1");

		Map<String, String> map = new HashMap<String, String>();
		if( mCursor.moveToFirst() )
		{
			map.put(_id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put(TITLE, 		mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put(CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put(GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put(SENDDATE, 	mCursor.getString(mCursor.getColumnIndex(SENDDATE)));
			map.put(ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put(SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));

			
		}
		mCursor.close();

		return map;
	}

	public Map<String, String> getSmallMsg(String index) 
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere( _id + ">" + index);

		String asColumnsToReturn[] = 
		{ 		_id, 
				TITLE, 
				CONTENTS,
				SENDDATE,
				GROUP_ID, 
				ETC, 
				SENDER };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, _id + " ASC", " 1");

		Map<String, String> map = new HashMap<String, String>();
		if( mCursor.moveToFirst() )
		{
			map.put(_id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put(TITLE, 		mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put(CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put(SENDDATE, 	mCursor.getString(mCursor.getColumnIndex(SENDDATE)));
			map.put(GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put(ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put(SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
		}
		mCursor.close();

		return map;
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