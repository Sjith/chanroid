package controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import view.CCView;

import model.CCModel;
import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class CCFragment extends Fragment implements CCController {

	@Override
	public CCModel loadModel(Class<? extends CCModel> cls) {
		// TODO Auto-generated method stub
		try {
			Method method = cls.getMethod("getInstance", Context.class);
			CCModel model = (CCModel) method.invoke(null, getActivity());
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
	public CCView loadView(Class<? extends CCView> cls) {
		// TODO Auto-generated method stub
		try {
			Constructor<? extends CCView> cons = cls
					.getConstructor(Context.class);
			return (CCView) cons.newInstance(getActivity());
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
		} catch (java.lang.InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
