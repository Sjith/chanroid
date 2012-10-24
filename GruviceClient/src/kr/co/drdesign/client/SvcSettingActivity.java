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
 * 이쪽이 설정인갑다.
 * 아님 말고
 * 적용 되는거 같으면서 안되고 신기하네
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


	// Preference에서 클릭 발생시 호출되는 call back
	// Parameters:
	//  - PreferenceScreen : 이벤트가 발생한 Preference의 root
	//  - Preference : 이벤트를 발생시킨 Preference 항목
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		// sub_checkbox란 키를 가지고 있는 Preference항목이 이벤트 발생 시 실행 
		//if(preference.equals((CheckBoxPreference)findPreference("sub_checkbox"))) {
		// Preference 데이터 파일중 "sub_checkbox" 키와 연결된 boolean 값에 따라
		// category1 (ListPreference, RingtonePreference 포함)을 활성화/비활성화
		//category1.setEnabled(mainPreference.getBoolean("sub_checkbox", false));
		//}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}	
}
