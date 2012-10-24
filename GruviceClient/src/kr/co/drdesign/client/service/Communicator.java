package kr.co.drdesign.client.service;

import kr.co.drdesign.client.controller.AttachController;
import kr.co.drdesign.client.controller.GroupController;
import kr.co.drdesign.client.controller.ReceiveMsgController;
import kr.co.drdesign.client.controller.ReceiveMsgDBHelper;
import kr.co.drdesign.util.GruviceUtillity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Communicator implements Runnable
{
	public static Context context;

	// 접속 서버 URL
	public static final String SERVERURL="http://121.254.228.178:8080/xmlmsgsys/devicemsg/";
	
	protected ReceiveMsgDBHelper mDatabase = null; 
	protected Cursor mCursor = null;
	protected SQLiteDatabase mDB = null;
	
	public static ReceiveMsgController msgController;
	public static AttachController attController;
	public static GroupController grpController;
	public static FileHelper fileHelper;
	
	public static String clientID;
	
	public Communicator ( Context ctx ){
		Communicator.context = ctx;
		msgController = ReceiveMsgController.getInstance(ctx);
		attController = AttachController.getInstance(ctx);
		grpController = GroupController.getInstance(ctx);
		fileHelper = FileHelper.getInstance(ctx);
	}
	
	public void run(){
		
		clientID = GruviceUtillity.getInstance(context).getClientId();
		new Thread( new MsgReceiver(SERVERURL, clientID, null, false)).start();
	}
	
}