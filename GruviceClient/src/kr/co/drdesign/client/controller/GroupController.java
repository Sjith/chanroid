package kr.co.drdesign.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.drdesign.util.Loggable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class GroupController implements Loggable{

	public static final String _id = "_id";
	public static final String GROUP_ID = "GROUP_ID";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String GROUP_KNAME = "GROUP_KNAME";
	public static final String GROUP_DESC = "GROUP_DESC";
	public static final String ETC = "GROUP_ETC";

	public static final String TABLE_NAME = GroupDBHelper.TABLE_NAME;

	protected GroupDBHelper mDatabase = null; 
	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;
	private static GroupController grpCtrl = null;

	private GroupController(Context ctx){
		mDatabase = new GroupDBHelper(ctx);
		mDB = mDatabase.getWritableDatabase();
		L( "create GroupController()" );
	}

	public static GroupController getInstance( Context ctx ){
		if ( grpCtrl == null )
			grpCtrl = new GroupController(ctx.getApplicationContext());
		return grpCtrl;
	}

	public long insert( Map<String, String> msg )
	{
		L( "SERCHING GROUP ID" );
		//halftale 2.1 2.2 다르게 동작하는 부분..
		// 그룹이 이미 존재하는 경우는 그냥 빠져나온다.
		if ( getGroup( msg.get(GROUP_ID) ).size() != 0 ) return -1;
		// 2011-06-01 서버에서 가져올때 내부 db를 초기화하는 방식으로 변경.
		insert();
		// add the new type to our list
		ContentValues typeRecordToAdd = new ContentValues();
		typeRecordToAdd.put(GROUP_ID, msg.get(GROUP_ID));
		typeRecordToAdd.put(GROUP_NAME, msg.get(GROUP_NAME));
		typeRecordToAdd.put(GROUP_KNAME, msg.get(GROUP_KNAME));
		typeRecordToAdd.put(GROUP_DESC, msg.get(GROUP_DESC));
		long rawId;
		try {
			rawId = mDB.insert(TABLE_NAME, null, typeRecordToAdd);
		} catch (SQLiteConstraintException e) {
			// TODO Auto-generated catch block
			Log.i("Gruvice", "group Insert error.");
			return -1;
		}

		return rawId;
	}
	
	public long insert() {
		ContentValues defGroup = new ContentValues();
		defGroup.put(GROUP_ID, "100");
		defGroup.put(GROUP_NAME, "ToMembers");
		defGroup.put(GROUP_KNAME, "개인");
		defGroup.put(GROUP_DESC, "개인적인 메시지입니다.");		

		mDB.insert(TABLE_NAME, null, defGroup);
		
		return -1;		
	}

	public long insert(ContentValues cv){
		return mDB.insert(TABLE_NAME, null, cv);
	}

	public int delete( String[] groups )
	{
		StringBuilder sb = new StringBuilder();
		sb.append(GROUP_ID).append("=?");
		int length = groups.length;
		for( int i = 1; i < length ;i++){
			sb.append(" OR ").append(GROUP_ID).append("=?");
		}
		String whereClause = sb.toString();
		int row = mDB.delete(TABLE_NAME, whereClause, groups);
		L( "DELETE GROUPS " + row + "row.");
		return row ;
	}
	
	public void deleteAll() {
		mDB.execSQL("DELETE FROM " + TABLE_NAME);
		// db내의 모든 자료 삭제
	}

	public Map <String, String> getGroup(String groupId )
	{
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		String asColumnsToReturn[] = 
		{
				_id,
				GROUP_ID,
				GROUP_NAME, 
				GROUP_KNAME,
				GROUP_DESC,
				ETC
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, GROUP_ID + "=?", new String[]{groupId}, null, null, null);

		Map <String, String> groupList = new HashMap<String, String>();
		if ( mCursor.moveToFirst() )
		{
			while( !mCursor.isAfterLast() ) 
			{
				groupList.put(_id, mCursor.getString(mCursor.getColumnIndex(_id)));
				groupList.put(GROUP_ID, mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
				groupList.put(GROUP_NAME, mCursor.getString(mCursor.getColumnIndex(GROUP_NAME)));
				groupList.put(GROUP_KNAME, mCursor.getString(mCursor.getColumnIndex(GROUP_KNAME)));
				groupList.put(GROUP_DESC, mCursor.getString(mCursor.getColumnIndex(GROUP_DESC)));
				groupList.put(ETC, mCursor.getString(mCursor.getColumnIndex(ETC)));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return groupList;
	}

	public ArrayList<Map <String, String>> getGroupList()
	{
		L( "GET GROUPLIST ");
		ArrayList<Map <String, String>> al = new ArrayList<Map <String, String>>();

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);

		String asColumnsToReturn[] = 
		{
				_id,
				GROUP_ID,
				GROUP_NAME, 
				GROUP_KNAME,
				GROUP_DESC,
				ETC
		};

		mCursor = queryBuilder.query(mDB, asColumnsToReturn, null, null, null, null, null);
		Map <String, String> groupList = null;
		if ( mCursor.moveToFirst() )
		{
			while( !mCursor.isAfterLast() ) 
			{
				groupList = new HashMap<String, String>();
				groupList.put(_id, mCursor.getString(mCursor.getColumnIndex(_id)));
				groupList.put(GROUP_ID, mCursor.getString(mCursor.getColumnIndex(GROUP_ID)));
				groupList.put(GROUP_NAME, mCursor.getString(mCursor.getColumnIndex(GROUP_NAME)));
				groupList.put(GROUP_KNAME, mCursor.getString(mCursor.getColumnIndex(GROUP_KNAME)));
				groupList.put(GROUP_DESC, mCursor.getString(mCursor.getColumnIndex(GROUP_DESC)));
				groupList.put(ETC, mCursor.getString(mCursor.getColumnIndex(ETC)));
				al.add(groupList);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return al;
	}
	
	public ArrayList<Map<String, String>> getAllGroupList() {
		// 어떻게든 가져온 그룹을 표시해 준다.
		ArrayList<Map<String, String>> al = null;
		return al;
	}

	public void updateGroupInfo(String groupId, String groupName) 
	{
		L( "UPDATE GROUP INFO.");
		ContentValues cv = new ContentValues();
		cv.put( GROUP_NAME, groupName );
		mDB.update(TABLE_NAME, cv, GROUP_ID + "=?", new String[]{groupId} );
		mCursor.close();
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
