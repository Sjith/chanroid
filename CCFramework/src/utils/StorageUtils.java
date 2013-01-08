package utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class StorageUtils {

	public static boolean isExternalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		return stat.getBlockSize() * stat.getBlockCount();
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		return stat.getBlockSize() * stat.getAvailableBlocks();
	}

	public static long getTotalExternalMemorySize() {
		if (isExternalMemoryAvailable() == true) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			return stat.getBlockSize() * stat.getBlockCount();
		} else {
			return -1;
		}
	}

	public static long getAvailableExternalMemorySize() {
		if (isExternalMemoryAvailable() == true) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			return stat.getBlockSize() * stat.getAvailableBlocks();
		} else {
			return -1;
		}
	}

}
