/*
 * $Id$
 */

package kr.co.drdesign.client.argos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class ConnectionLog
{
	private String mPath;
	private Writer mWriter;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("[HH:mm:ss] ");

	// 클래스 생성자
	public ConnectionLog()
	  throws IOException
	{
		// 클래스와 같이 생성되는 객체
		File sdcard = Environment.getExternalStorageDirectory();
        File logDir = new File(sdcard, "gruvice/log/argos");
        if (!logDir.exists()) {
        	logDir.mkdirs();
        	// do not allow media scan
            new File(logDir, ".nomedia").createNewFile();
        }        
		
        // /push_ 폴더에 현재 날짜 + .log 이름을 가진  파일 생성
		open(logDir.getAbsolutePath() + "/push_" + getTodayString() + ".log");
	}

	public ConnectionLog(String basePath) throws IOException
	{
		open(basePath);
	}

	protected void open(String basePath)
	  throws IOException
	{
		File f = new File(basePath);
		mPath = f.getAbsolutePath();
		mWriter = new BufferedWriter(new FileWriter(mPath, true), 2048);

		println("Opened log.");
	}

	private static String getTodayString()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		return df.format(new Date());
	}

	public String getPath()
	{
		return mPath;
	}

	public void println(String message)
	  throws IOException
	{
		mWriter.write(TIMESTAMP_FMT.format(new Date()));
		mWriter.write(message);
		mWriter.write('\n');
		mWriter.flush();
	}

	public void close()
	  throws IOException
	{
		mWriter.close();
	}
}
