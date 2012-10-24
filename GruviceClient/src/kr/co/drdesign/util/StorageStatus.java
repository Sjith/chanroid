package kr.co.drdesign.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
// SDī���� ��밡���� �޸� ������ �˷��ִ� Class
public class StorageStatus {

	static final int ERROR = -1;
	
	static public boolean externalStorageAvailable() {
	    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	static public long getAvailableInternalStorageSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
	
	static public long getTotalInternalStorageSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}
	
	static public long getAvailableExternalStorageSize() {
		if(externalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}
	
	static public long getTotalExternalStorageSize() {
		if(externalStorageAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}
	
	// ����� �þ������ ���� ���ڿ��� �߰����� �ִ� �޼���.
	// 2011-05-16 GB�������� ����.
	static public String formatSize(long size) {
		String suffix = null;
	
		
		if (size >= 1024) {
			suffix = "MB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "GB";
				size /= 1024;
			}
		}
	
		StringBuilder resultBuffer = new StringBuilder(Long.toString(size).substring(0, 3));
	
		int commaOffset = resultBuffer.length() - 2;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, '.');
			commaOffset -= 2;
		}
	
		if (suffix != null)	resultBuffer.append(suffix);
		
		return resultBuffer.toString();
	}
}