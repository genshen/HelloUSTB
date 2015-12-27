package com.holo.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.os.Bundle;

import com.holo.helloustb.R;
import com.holo.view.MaterialListPreference;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeneralSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    MaterialListPreference week_num;
    final String WEEK_NUM = "week_num";

    public GeneralSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_preferences);

        week_num = (MaterialListPreference) getPreferenceScreen().findPreference("week_num");
    }

//    init MaterialListPreference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(WEEK_NUM)) {
            InitListSummary();
        }
    }

    private void  InitListSummary(){
        if(week_num.getValue().equals("")){
            week_num.setSummary("第1周");
        }else{
            week_num.setSummary(week_num.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        InitListSummary();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

}
