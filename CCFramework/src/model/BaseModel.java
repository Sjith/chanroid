package model;

import iface.BaseModelCallback;

import java.util.ArrayList;

/**
 * 
 * <PRE>
 * 1. ClassName : 
 * 2. FileName  : BaseModel.java
 * 3. Package  : model
 * 4. Comment  : CC Framework Base Model
 * 				(싱글톤 사용시 상속받아서 구현할것)
 * 5. 작성자   : 박찬우
 * 6. 작성일   : 2012. 10. 15. 오후 2:45:48
 * </PRE>
 */
public class BaseModel {
	
	private ArrayList<BaseModelCallback> callback = new ArrayList<BaseModelCallback>();

	public boolean addCallback(BaseModelCallback cb) {
		return callback.add(cb);
	}

	public boolean removeCallback(BaseModelCallback cb) {
		return callback.remove(cb);
	}

	public void update() {
		for (BaseModelCallback cb : callback)
			cb.onModelUpdate(this);
	}

}
