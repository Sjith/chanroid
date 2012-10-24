package kr.co.drdesign.parmtree;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

public class SettingActivity extends PreferenceActivity {
	SharedPreferences mainPreference;
	PreferenceCategory category1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.setting);
		mainPreference = PreferenceManager.getDefaultSharedPreferences(this); 
	}
}
