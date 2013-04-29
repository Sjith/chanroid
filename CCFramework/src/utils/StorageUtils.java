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
		return (long) stat.getBlockSize() * (long) stat.getBlockCount();
	}

	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
	}

	public static long getTotalExternalMemorySize() {
		if (isExternalMemoryAvailable() == true) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			return (long) stat.getBlockSize() * (long) stat.getBlockCount();
		} else {
			return -1l;
		}
	}

	public static long getAvailableExternalMemorySize() {
		if (isExternalMemoryAvailable() == true) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			return (long) stat.getBlockSize()
					* (long) stat.getAvailableBlocks();
		} else {
			return -1l;
		}
	}

}
