package kr.co.drdesign.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * 
 * @author brake-_-
 * ������ �����ΰ���.
 * �ƴ� ����
 * ���� �Ǵ°� �����鼭 �ȵǰ� �ű��ϳ�
 *
 */

public class SvcSettingActivity extends PreferenceActivity {
	SharedPreferences mainPreference;
	PreferenceCategory category1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		mainPreference = PreferenceManager.getDefaultSharedPreferences(this); 
	}


	// Preference���� Ŭ�� �߻��� ȣ��Ǵ� call back
	// Parameters:
	//  - PreferenceScreen : �̺�Ʈ�� �߻��� Preference�� root
	//  - Preference : �̺�Ʈ�� �߻���Ų Preference �׸�
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		// sub_checkbox�� Ű�� ������ �ִ� Preference�׸��� �̺�Ʈ �߻� �� ���� 
		//if(preference.equals((CheckBoxPreference)findPreference("sub_checkbox"))) {
		// Preference ������ ������ "sub_checkbox" Ű�� ����� boolean ���� ����
		// category1 (ListPreference, RingtonePreference ����)�� Ȱ��ȭ/��Ȱ��ȭ
		//category1.setEnabled(mainPreference.getBoolean("sub_checkbox", false));
		//}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}	
}
