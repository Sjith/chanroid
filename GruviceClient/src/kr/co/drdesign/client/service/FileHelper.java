package kr.co.drdesign.client.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kr.co.drdesign.util.Base64Coder;
import kr.co.drdesign.util.GruviceUtillity;
import kr.co.drdesign.util.Loggable;
import android.content.Context;
import android.util.Log;

public class FileHelper implements Loggable
{
	private static final String CLASS_NAME = "FileHelper"; 

	private static FileHelper fileHelper;
	private File dataDirectory;
	private String dataStoragePath;

	private FileHelper( Context context ){
		//�ܺ� ����Ұ� ���� ��, ó�����ִ� ������ �־�� �Ѵ�.
		dataStoragePath = 
			GruviceUtillity.DATA_STORAGE + "drm/";
		dataDirectory = new File( dataStoragePath );
		if ( !dataDirectory.exists() ) dataDirectory.mkdirs();
		L( "Create " + CLASS_NAME);
	}

	public static FileHelper getInstance(Context context){
		if ( fileHelper == null )
		{
			fileHelper = new FileHelper(context);
		}
		return fileHelper;
	}

	public String getStorageDirectory(){
		return dataDirectory.getAbsolutePath();
	}

	//��ȣȭ�� ���·� ������ �޾Ƽ� ó���Ѵ�.
	public File saveBase64File(String path, String fileName, String buffer){

		File saveFile = null;
		try {
			fileName = fileName.trim();
			//OutputStream os = context.openFileOutput( fileName, 0 );
			//OutputStream os = context.openFileOutput( path + fileName, 0 );
			
			if ( path == null )path = dataStoragePath;
			else path += "/";
			
			saveFile = new File ( path + fileName );
			OutputStream os = new FileOutputStream( saveFile );
			//Log.i("DR", "saveBase64 File is " + fileName);

			// �޸𸮻󿡼� �ٷ� Encode/decode �Ҷ��� Stream�� ������� ����, Base64 Ŭ������ �޼ҵ带 ����Ѵ�.
			//Base64InputStream b64is = new Base64InputStream(
			//		new ByteArrayInputStream(buffer.toString().getBytes()), Base64.DEFAULT);

			
			byte[] decodedBuffer;
			for( String line : buffer.split("\n") ){
				decodedBuffer = Base64Coder.decode(line.trim());
				os.write( decodedBuffer );
			}
//			os.close();
			//
			//100803 halftale
			//byte[] decodedBuffer = kr.co.drdesign.client.aria.Base64Coder.decode(new String(buffer).trim());
			
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return saveFile.getAbsoluteFile();
	}

	public FileOutputStream getFileOutputStream( File File ) throws FileNotFoundException {
		return new FileOutputStream(File);
	}

	public FileOutputStream getFileOutputStream( String filePath ) throws FileNotFoundException{
		return new FileOutputStream(filePath);
	}
	
	public void L(char i, String log) 
	{
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
