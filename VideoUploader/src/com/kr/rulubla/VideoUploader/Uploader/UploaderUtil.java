package com.kr.rulubla.VideoUploader.Uploader;

import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UploaderUtil {


	public static Bitmap getImageURL(String path) {
		try {
			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			conn.connect();
			FlushedInputStream bis = new FlushedInputStream(
					conn.getInputStream());
			Bitmap b = BitmapFactory.decodeStream(bis);
			bis.close();
//			Log.i("Uploader", "getImage : " + path);
			return b;
		} catch (Exception e) {
			return null;
		}
	}
	
}
