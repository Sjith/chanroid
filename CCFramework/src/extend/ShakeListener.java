package extend;

import iface.OnShakeListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

@SuppressWarnings("deprecation")
public class ShakeListener implements SensorEventListener {
	public static final int DEFAULT_SHAKE_THRESHOLD = 600;
	
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 1000;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 11;

	private int SHAKE_THRESHOLD = DEFAULT_SHAKE_THRESHOLD;
	private SensorManager mSensorMgr;
	private Sensor mSensor;
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	public ShakeListener(Context context) {
		mContext = context;
		resume();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}
	
	public void setThreshold(int threshold) {
		SHAKE_THRESHOLD = threshold;
	}
	
	public int getThreshold() {
		return SHAKE_THRESHOLD;
	}

	public void resume() {
		mSensorMgr = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorMgr
				.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}

		boolean supported = mSensorMgr.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);

		if (!supported) {
			mSensorMgr.unregisterListener(this, mSensor);
		}
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this, mSensor);
			mSensorMgr = null;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() != SensorManager.SENSOR_ACCELEROMETER)
			return;
		long now = System.currentTimeMillis();
		float[] values = event.values;
		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
			float speed = Math.abs(values[SensorManager.DATA_X]
					+ values[SensorManager.DATA_Y]
					+ values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ)
					/ diff * 10000;
			if (speed > SHAKE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT)
						&& (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (mShakeListener != null) {
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = values[SensorManager.DATA_X];
			mLastY = values[SensorManager.DATA_Y];
			mLastZ = values[SensorManager.DATA_Z];
		}
	}

}
