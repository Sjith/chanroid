package kr.co.drdesign.client.controller;

import java.util.List;
import java.util.Map;

import android.content.ContentValues;

public interface MsgController {
	public static final String _id 			= "_id";
	
	public long insert(Map<String, String> msg);
	public long insert(ContentValues cv);
	public int delete( String index );
	public Map<String, String> getMsg(String index );
	public List<Map<String,String>> getMsgList();
	public List<Map<String,String>> getMsgListbyGroup(String[] groupIds );
	public Map<String,String> getBiggerMsg(String _id);
	public Map<String,String> getSmallMsg(String _id);
}
