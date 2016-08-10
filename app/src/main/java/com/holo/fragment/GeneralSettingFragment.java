package com.holo.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.holo.utils.BasicDate;
import com.holo.utils.Const;
import com.holo.helloustb.R;
import com.holo.view.MaterialListPreference;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeneralSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    MaterialListPreference mPreferenceWeekNum, mPreferenceNetSignInMode;
    SharedPreferences preferences;
    /**
     * @see com.holo.utils.BasicDate#MAX_WEEK_NUM
     */
    final static int MAX_WEEK_NUM = 24;
    /**
     * @see com.holo.utils.BasicDate#ONE_DAY
     */
    final static int ONE_DAY = 1000 * 3600 * 24;


    public GeneralSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferenceWeekNum = (MaterialListPreference) getPreferenceScreen().findPreference(Const.Settings.KEY_WEEK_NUM);
        mPreferenceNetSignInMode = (MaterialListPreference) getPreferenceScreen().findPreference(Const.Settings.KEY_NET_SIGN_IN_MODE);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    //    init MaterialListPreference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.Settings.KEY_WEEK_NUM.equals(key)) {
            setWeekStart(Integer.parseInt(mPreferenceWeekNum.getValue()));
            BasicDate.writeLog("changed!\r\n");
            setListSummary(mPreferenceWeekNum, "第一周");
        } else if (Const.Settings.KEY_NET_SIGN_IN_MODE.equals(key)) {
            setListSummary(mPreferenceNetSignInMode, "普通模式");
        }
    }

    private void setListSummary(MaterialListPreference listPreference, String defaultValue) {
        if (listPreference.getValue().isEmpty()) {
            listPreference.setSummary(defaultValue);
        } else {
            listPreference.setSummary(listPreference.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setListSummary(mPreferenceNetSignInMode, "普通模式");

        long week_start_days = preferences.getLong(Const.Settings.KEY_WEEK_START, 0);
        mPreferenceWeekNum.setValue(BasicDate.getWeekNum(week_start_days) + "");
        setListSummary(mPreferenceWeekNum, "第一周");
    }

    private void setWeekStart(int week_num) {
        Calendar calendar = Calendar.getInstance();
        long to_days = calendar.getTimeInMillis() / ONE_DAY;
        int day_of_week = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;// calendar.get(Calendar.DAY_OF_WEEK)-1-1+7)%7
        preferences.edit().putLong(Const.Settings.KEY_WEEK_START, to_days - day_of_week - week_num * 7).apply();
    }

}
