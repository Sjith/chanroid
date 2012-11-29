package model;

import iface.Clearable;
import iface.Destroyable;
import iface.Initializeable;
import android.content.Context;

public abstract class CCModel implements Initializeable, Destroyable, Clearable {

	private Context mContext;

	protected CCModel(Context ctx) {
		mContext = ctx;
		init(0);
	}

	public Context getContext() {
		return mContext;
	}

}
