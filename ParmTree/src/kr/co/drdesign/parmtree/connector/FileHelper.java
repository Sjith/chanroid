package kr.co.drdesign.parmtree.connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kr.co.drdesign.parmtree.aria.Base64Coder;
import kr.co.drdesign.parmtree.util.ParmUtil;
import android.content.Context;

public class FileHelper
{

	private static FileHelper fileHelper;
	private File dataDirectory;
	private static String dataStoragePath;

	private FileHelper( Context context ){
		//�ܺ� ����Ұ� ���� ��, ó�����ִ� ������ �־�� �Ѵ�.
		dataStoragePath = 
			ParmUtil.TEMP + "drm/";
		dataDirectory = new File( dataStoragePath );
		if ( !dataDirectory.exists() ) dataDirectory.mkdirs();
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
	public static File saveBase64File(String path, String fileName, String buffer){

		File saveFile = null;
		try {
			fileName = fileName.trim();
			//OutputStream os = context.openFileOutput( fileName, 0 );
			//OutputStream os = context.openFileOutput( path + fileName, 0 );
			
			if ( path == null ) path = dataStoragePath;
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
	
}
