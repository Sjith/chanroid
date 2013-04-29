package model;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class CCPreferenceModel extends CCModel {

	private SharedPreferences mPref;

	public CCPreferenceModel(Context ctx) {
		mPref = ctx.getSharedPreferences(preferenceName(),
				preferenceMode());
	}

	public abstract String preferenceName();

	public abstract int preferenceMode();

	public SharedPreferences pref() {
		return mPref;
	}

	public SharedPreferences.Editor edit() {
		return mPref.edit();
	}
}
