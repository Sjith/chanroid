package controller;

import model.CCModel;
import view.CCView;

public interface CCController {
	public CCModel loadModel(Class<? extends CCModel> cls);
	public CCView loadView(Class<? extends CCView> cls);
}
