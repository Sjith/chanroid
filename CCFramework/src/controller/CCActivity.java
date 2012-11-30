package controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import view.CCView;

import model.CCModel;
import android.app.Activity;
import android.content.Context;

public abstract class CCActivity extends Activity implements CCController {

	@Override
	public CCModel loadModel(Class<? extends CCModel> cls) {
		// TODO Auto-generated method stub
		try {
			Method method = cls.getMethod("getInstance", Context.class);
			return (CCModel) method.invoke(null, this);
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
	public CCView loadView(Class<? extends CCView> cls) {
		// TODO Auto-generated method stub
		try {
			Constructor<? extends CCView> cons = cls
					.getConstructor(Context.class);
			return (CCView) cons.newInstance(this);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
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

}
