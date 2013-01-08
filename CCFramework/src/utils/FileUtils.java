package utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtils {

	/**
	 * 파일 이름이나 경로에서 파일 확장자를 구함
	 * 
	 * @param path
	 *            파일 이름이나 경로
	 * @return 확장자명
	 */
	public static String getExtension(String path) {
		int pos = path.lastIndexOf(".");
		return path.substring(pos + 1);
	}

	/**
	 * 지정 파일에서 확장자가 바뀐 "파일명"을 리턴(파일 아님, 실제 존재하는 경로 아닐 수 있음)
	 * 
	 * @param path
	 * @param changeExtension
	 * @return
	 */
	public static String changeExtension(String path, String changeExtension) {
		String[] filePathsplit = path.split("/");
		String fileName = filePathsplit[filePathsplit.length - 1];
		path = path.replace(fileName, "");
		String realName = fileName.split("\\.")[0];
		String extension = "smi";
		return path + realName + "." + extension;
	}

	/**
	 * CP를 통해 이미지의 Uri를 얻어온 경우 이를 이용해 실제 파일 경로 반환
	 * 
	 * @param context
	 *            호출한 액티비티
	 * @param uri
	 *            CP를 통해 반환받은 Uri
	 * @return
	 */
	public static String getMediaPathfromUri(Context context, Uri uri) {
		Cursor c = context.getContentResolver().query(uri, null, null,
				null, null);
		if (c.moveToNext())
			return c.getString(c
					.getColumnIndex(MediaStore.MediaColumns.DATA));
		return null;
	}
}
