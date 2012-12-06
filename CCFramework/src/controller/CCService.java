package controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import model.CCModel;
import view.CCView;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

public abstract class CCService extends Service implements CCController {

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		handleStart(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		handleStart(intent);
		return START_STICKY_COMPATIBILITY;
	}
	
	public abstract void handleStart(Intent intent);
	
	@Override
	public CCModel loadModel(Class<? extends CCModel> cls) {
		// TODO Auto-generated method stub
		try {
			Method method = cls.getMethod("getInstance", Context.class);
			CCModel model = (CCModel) method.invoke(null, this);
			return model;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Deprecated
	/**
	 * 서비스에선 무조건 null
	 */
	public CCView loadView(Class<? extends CCView> cls) {
		// TODO Auto-generated method stub
		return null;
	}

}
