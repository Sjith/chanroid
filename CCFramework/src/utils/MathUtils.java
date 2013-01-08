package utils;

import java.util.ArrayList;

public class MathUtils {

	public static long average(ArrayList<Long> array) {
		long value = 0;
		for (long a : array) {
			value += a;
		}
		return value / array.size();
	}

	/**
	 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
	 * 
	 * @param level
	 * @param scale
	 * @return
	 */
	public static int getPercent(int level, int scale) {
		return getPercent((double) level, (double) scale);
	}

	/**
	 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
	 * 
	 * @param level
	 * @param scale
	 * @return
	 */
	public static int getPercent(float level, float scale) {
		return getPercent((double) level, (double) scale);
	}

	/**
	 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
	 * 
	 * @param level
	 * @param scale
	 * @return
	 */
	public static int getPercent(double level, double scale) {
		return (int) (level / scale * 100);
	}

	/**
	 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
	 * 
	 * @param level
	 * @param scale
	 * @return
	 */
	public static int getPercent(long level, long scale) {
		return getPercent((double) level, (double) scale);
	}

	/**
	 * 2번째 인자값에 대한 1번째 인자값의 퍼센트값을 구함
	 * 
	 * @param level
	 * @param scale
	 * @return
	 */
	public static int getPercent(short level, short scale) {
		return getPercent((double) level, (double) scale);
	}

}
