package kr.co.drdesign.client.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import kr.co.drdesign.util.Loggable;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class ReceiveMsgController implements MsgController, Loggable{

	public static final String _id 			= MsgController._id;
	public static final String UID 			= "UID";
	public static final String TITLE 		= "TITLE";
	public static final String CONTENTS 	= "CONTENTS";
	public static final String APPPATH 		= "APPPATH";
	public static final String ATTACHMENTS 	= "ATTACHMENTS";
	public static final String READED 		= "READED";
	public static final String GROUP_ID 	= GroupController.GROUP_ID;
	public static final String ETC 			= "ETC";
	public static final String SENDER 		= "SENDER";
	public static final String CREATEDATE 	= "CREATEDATE";
	public static final String RECEIVEDATE	= "RECEIVEDATE";

	public static final String TABLE_NAME = ReceiveMsgDBHelper.TABLE_NAME;

	protected ReceiveMsgDBHelper mDatabase = null; 
	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;

	private static ReceiveMsgController msgcon = null;
	private AttachController attCtrl;
//	private GroupController grpCtrl;
	private NotificationManager notifier;

	private ReceiveMsgController(Context ctx){
		ctx = ctx.getApplicationContext();
		mDatabase = new ReceiveMsgDBHelper(ctx);
		mDB = mDatabase.getWritableDatabase();
		attCtrl = AttachController.getInstance(ctx);
//		grpCtrl = GroupController.getInstance(ctx);
		notifier = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		L( "Create Receive Message Controller.");
	}

	public static ReceiveMsgController getInstance( Context ctx ){
		
		if ( msgcon == null )
			msgcon = new ReceiveMsgController(ctx);
		return msgcon;
	}

	public long insert(Map<String, String> msg)
	{
		// add the new type to our list
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put(UID, msg.get(UID));
		typeRecordToAdd.put(TITLE, msg.get(TITLE));
		typeRecordToAdd.put(CONTENTS,msg.get(CONTENTS));
		typeRecordToAdd.put(APPPATH, msg.get(APPPATH));
		typeRecordToAdd.put(ATTACHMENTS, msg.get(ATTACHMENTS));
		typeRecordToAdd.put(GROUP_ID, msg.get(GROUP_ID));
		typeRecordToAdd.put(READED, "0");
		typeRecordToAdd.put(SENDER, msg.get(SENDER));
		typeRecordToAdd.put(CREATEDATE, msg.get(CREATEDATE));
		typeRecordToAdd.put(RECEIVEDATE, msg.get(RECEIVEDATE));
		typeRecordToAdd.put(ETC, msg.get(ETC));
		
		long row = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		return row;
	}

	public long insert(ContentValues cv){
		return mDB.insert(TABLE_NAME, null, cv);
	}


	public int delete( String index )
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController._id + "=" + index);

		String asColumnsToReturn[] = 
		{ 		
				ReceiveMsgController.CONTENTS, 
				ReceiveMsgController.APPPATH, 
				ReceiveMsgController.ATTACHMENTS,
				ReceiveMsgController.UID
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);
//		ArrayList<String> msgDetailInfo = new ArrayList<String>();

		if ( mCursor.moveToFirst() ){
			String contents = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.CONTENTS));
			if( contents != null )
				deleteContents(contents);

			String appPath = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.APPPATH));

			if( appPath != null ){
				File f = new File(appPath);
				if( f.exists() )f.delete();
			}

			String uID = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.UID));

			attCtrl.delete(uID);
			//mCursor.getString(mCursor.getColumnIndex(MsgController.ATTACHMENTS));
		}else{
			Log.i("DR", "mCursor.getCnt() = " + mCursor.getCount() );
		}

		mCursor.close();
		int row = mDB.delete(TABLE_NAME, _id + "=?", new String[]{index});

		//If Device have Notification, This code will clear Notification of deleted Message.
		notifier.cancel(Integer.valueOf(index));
		L( "Delete Item " + row + "row.");
		return row ;
	}

	private void deleteContents( String contents ){
		Scanner sc = new Scanner( contents );
		String line = null;
		boolean isText = true;
		while( sc.hasNext() ){
			line = sc.next();
			Log.i("DR", line);
			if ( "img_".equalsIgnoreCase(line) )
			{
				isText = false;
			}else if( "text_".equalsIgnoreCase(line) )
			{
				isText = true;
			}
			if( isText ) continue;
			else{
				try{
					new File( line ).delete();
				}catch( Exception e){
					Log.e("DR", e.getMessage());
				}
			}
		}
		L( "DELETE Contents");
	}

	public void markReaded( String _id )
	{

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere( ReceiveMsgController._id + "=" + _id);

		String asColumnsToReturn[] = 
		{ READED };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);

		mCursor.moveToFirst();
		int numberOfReaded = mCursor.getInt(mCursor.getColumnIndex(READED));
		//Integer.parseInt( mCursor.getString(mCursor.getColumnIndex("READED")) );

		ContentValues cv = new ContentValues();
		cv.put( READED, String.valueOf(++numberOfReaded) );
		mDB.update(TABLE_NAME, cv, ReceiveMsgController._id + "=?", new String[]{_id} );
		mCursor.close();
		L( "Update Mark Readed");
	}
	public void markReaded( int _id )
	{
		markReaded( "" + _id );
	}

	// return Value, matching Data by _id  
	public Map<String, String> getMsg(String index )
	{
		L( "getMessage(" + index + ")");
		Log.i("DR", "getMessage(" + index + ")");
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController._id + "=" + index);

		String asColumnsToReturn[] = 
		{ ReceiveMsgController._id, ReceiveMsgController.UID, ReceiveMsgController.TITLE, 
				ReceiveMsgController.CONTENTS, ReceiveMsgController.APPPATH, ReceiveMsgController.ATTACHMENTS, 
				ReceiveMsgController.READED, ReceiveMsgController.GROUP_ID, ReceiveMsgController.ETC, 
				ReceiveMsgController.SENDER, ReceiveMsgController.CREATEDATE, ReceiveMsgController.RECEIVEDATE};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);

		Map<String, String> map = new HashMap<String, String>();
		if ( mCursor.moveToFirst() ){
			map.put( _id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put( UID, 		mCursor.getString(mCursor.getColumnIndex(UID)));
			map.put( TITLE, 	mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put( CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put( APPPATH, 	mCursor.getString(mCursor.getColumnIndex(APPPATH)));
			map.put( ATTACHMENTS, mCursor.getString(mCursor.getColumnIndex(ATTACHMENTS)));
			map.put( READED, 	mCursor.getString(mCursor.getColumnIndex(READED)));
			map.put( GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put( ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put( SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
			map.put( CREATEDATE, mCursor.getString(mCursor.getColumnIndex(CREATEDATE)));
			map.put( RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
		}
		mCursor.close();
		return map;
	}

	public List<Map<String,String>> getMsgListbyGroup(String[] groupIds )
	{
		L("getMsgListByGroup() ");
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ReceiveMsgDBHelper.TABLE_NAME);

		// DB 질의문
		String asColumnsToReturn[] = 
		{ 		ReceiveMsgController._id, 
				ReceiveMsgController.UID, 
				ReceiveMsgController.TITLE, 
				ReceiveMsgController.READED, 
				ReceiveMsgController.GROUP_ID,
				ReceiveMsgController.ATTACHMENTS,
				ReceiveMsgController.ETC };
		String selection = null;
		if( groupIds == null ){

		}else if ( groupIds.length == 1){
			selection = GROUP_ID + " =? ";
		}else{
			selection = GROUP_ID + " =? ";

			StringBuilder sb = new StringBuilder(selection);
			int length = groupIds.length;
			for( int i = 1; i < length ; i++ )
			{
				sb.append(" OR ").append(selection);
			}
			selection = sb.toString();
		}
		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn,  selection, groupIds, null, null, ReceiveMsgController._id + " DESC");

		List<Map<String,String>> msgTitleList = new ArrayList<Map<String,String>>();

		Map<String, String> map = null;

		String _id, title, readed, attach;
		String groupId;
		if ( mCursor.moveToFirst() )
			while( !mCursor.isAfterLast() ) 
			{
				map = new HashMap<String, String>();

				_id = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController._id));
				title = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.TITLE));
				groupId = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.GROUP_ID));
				readed = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.READED));
				attach = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.ATTACHMENTS));

				map.put(ReceiveMsgController._id, _id);
				map.put(ReceiveMsgController.TITLE, title);
				map.put(ReceiveMsgController.READED, readed);
				map.put(ReceiveMsgController.GROUP_ID, groupId);
				map.put(ReceiveMsgController.ATTACHMENTS, attach);


				msgTitleList.add(map);
				mCursor.moveToNext();
			}
		mCursor.close();
		return msgTitleList;
	}
	//public List<Map<String,String>> getMsgList()
	//{
	//	return getMsgListbyGroup(null);
	//}

	public List<Map<String, String>> getMsgList() {
		L( "getMsgList()");
		// SQL Query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ReceiveMsgDBHelper.TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController.READED + "=0");

		// DB 질의문
		String asColumnsToReturn[] = 
		{ ReceiveMsgController._id, ReceiveMsgController.UID, ReceiveMsgController.TITLE, 
				ReceiveMsgController.CONTENTS, ReceiveMsgController.APPPATH, ReceiveMsgController.ATTACHMENTS, 
				ReceiveMsgController.READED, ReceiveMsgController.ETC };

		mCursor = queryBuilder.query(mDB, 
				asColumnsToReturn, null, null, null, null, ReceiveMsgController._id + " DESC");

		List<Map<String,String>> msgTitleList = new ArrayList<Map<String,String>>();

		Map<String, String> map = new HashMap<String, String>();

		String _id, title, readed, attach;
		if ( mCursor.moveToFirst() )
			while( !mCursor.isAfterLast() ) 
			{
				map = new TreeMap<String, String>();

				_id = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController._id));
				title = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.TITLE));
				readed = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.READED));
				attach = mCursor.getString(mCursor.getColumnIndex(ReceiveMsgController.ATTACHMENTS));

				map.put(ReceiveMsgController._id, _id);
				map.put(ReceiveMsgController.TITLE, title);
				map.put(ReceiveMsgController.ATTACHMENTS, attach);
				map.put(ReceiveMsgController.READED, readed);

				msgTitleList.add(map);
				mCursor.moveToNext();
			}
		mCursor.close();
		return msgTitleList;
	}

	public int cntRecordByGroup(String[] groupIds)
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ReceiveMsgDBHelper.TABLE_NAME);

		String [] selectColumns = {"count(*)as cnt"};
		mCursor = queryBuilder.query(mDB, 
				selectColumns, GROUP_ID + " = ? ", groupIds, null, null, null);

		mCursor.moveToFirst();

		int rtn = mCursor.getInt(mCursor.getColumnIndex("cnt"));
		mCursor.close();
		return rtn;	
	}

	public int cntRecordByGroup(String groupId)
	{
		return cntRecordByGroup( new String[]{ groupId } );	
	}

	public int updateAPPPath( String UID, String APPPath )
	{
//		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		ContentValues cv = new ContentValues();
		cv.put( APPPATH,APPPath );
		int cnt = mDB.update(TABLE_NAME, cv, ReceiveMsgController.UID + "=?", new String[]{UID} );
		mCursor.close();
		return cnt;
	}
	
	public Map<String, String> getMsgbyUID(String uid) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController.UID + " = " + uid);

		String asColumnsToReturn[] = 
		{ ReceiveMsgController._id, ReceiveMsgController.UID, ReceiveMsgController.TITLE, 
				ReceiveMsgController.CONTENTS, ReceiveMsgController.APPPATH, ReceiveMsgController.ATTACHMENTS, 
				ReceiveMsgController.READED, ReceiveMsgController.GROUP_ID, ReceiveMsgController.ETC, 
				ReceiveMsgController.SENDER, ReceiveMsgController.CREATEDATE, ReceiveMsgController.RECEIVEDATE };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null, " 1");

		Map<String, String> map = new HashMap<String, String>();
		if( mCursor.moveToFirst() )
		{
			map.put(_id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put(UID, 		mCursor.getString(mCursor.getColumnIndex(UID)));
			map.put(TITLE, 		mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put(CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put(APPPATH, 	mCursor.getString(mCursor.getColumnIndex(APPPATH)));
			map.put(ATTACHMENTS, mCursor.getString(mCursor.getColumnIndex(ATTACHMENTS)));
			map.put(READED, 	mCursor.getString(mCursor.getColumnIndex(READED)));
			map.put(GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put(ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put(SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
			map.put(CREATEDATE, mCursor.getString(mCursor.getColumnIndex(CREATEDATE)));
			map.put(RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
		}
		mCursor.close();

		return map;
	}
	
	public Map<String, String> getBiggerMsg(String index) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController._id + "<" + index);

		String asColumnsToReturn[] = 
		{ ReceiveMsgController._id, ReceiveMsgController.UID, ReceiveMsgController.TITLE, 
				ReceiveMsgController.CONTENTS, ReceiveMsgController.APPPATH, ReceiveMsgController.ATTACHMENTS, 
				ReceiveMsgController.READED, ReceiveMsgController.GROUP_ID, ReceiveMsgController.ETC, 
				ReceiveMsgController.SENDER, ReceiveMsgController.CREATEDATE, ReceiveMsgController.RECEIVEDATE };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, ReceiveMsgController._id + " DESC", " 1");

		Map<String, String> map = new HashMap<String, String>();
		if( mCursor.moveToFirst() )
		{
			map.put(_id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put(UID, 		mCursor.getString(mCursor.getColumnIndex(UID)));
			map.put(TITLE, 		mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put(CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put(APPPATH, 	mCursor.getString(mCursor.getColumnIndex(APPPATH)));
			map.put(ATTACHMENTS, mCursor.getString(mCursor.getColumnIndex(ATTACHMENTS)));
			map.put(READED, 	mCursor.getString(mCursor.getColumnIndex(READED)));
			map.put(GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put(ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put(SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
			map.put(CREATEDATE, mCursor.getString(mCursor.getColumnIndex(CREATEDATE)));
			map.put(RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
		}
		mCursor.close();

		return map;
	}

	public Map<String, String> getSmallMsg(String index) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		queryBuilder.appendWhere(ReceiveMsgController._id + ">" + index);

		String asColumnsToReturn[] = 
		{ ReceiveMsgController._id, ReceiveMsgController.UID, ReceiveMsgController.TITLE, 
				ReceiveMsgController.CONTENTS, ReceiveMsgController.APPPATH, ReceiveMsgController.ATTACHMENTS, 
				ReceiveMsgController.READED, ReceiveMsgController.GROUP_ID, ReceiveMsgController.ETC, 
				ReceiveMsgController.SENDER, ReceiveMsgController.CREATEDATE, ReceiveMsgController.RECEIVEDATE };

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, ReceiveMsgController._id + " ASC", " 1");

		Map<String, String> map = new HashMap<String, String>();
		if( mCursor.moveToFirst() )
		{
			map.put(_id, 		mCursor.getString(mCursor.getColumnIndex(_id)));
			map.put(UID, 		mCursor.getString(mCursor.getColumnIndex(UID)));
			map.put(TITLE, 		mCursor.getString(mCursor.getColumnIndex(TITLE)));
			map.put(CONTENTS, 	mCursor.getString(mCursor.getColumnIndex(CONTENTS)));
			map.put(APPPATH, 	mCursor.getString(mCursor.getColumnIndex(APPPATH)));
			map.put(ATTACHMENTS, mCursor.getString(mCursor.getColumnIndex(ATTACHMENTS)));
			map.put(READED, 	mCursor.getString(mCursor.getColumnIndex(READED)));
			map.put(GROUP_ID, 	mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
			map.put(ETC, 		mCursor.getString(mCursor.getColumnIndex(ETC)));
			map.put(SENDER, 	mCursor.getString(mCursor.getColumnIndex(SENDER)));
			map.put(CREATEDATE, mCursor.getString(mCursor.getColumnIndex(CREATEDATE)));
			map.put(RECEIVEDATE, mCursor.getString(mCursor.getColumnIndex(RECEIVEDATE)));
			mCursor.close();
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