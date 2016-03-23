package com.holo.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.holo.base.BasicDate;
import com.holo.helloustb.R;
import com.holo.view.MaterialListPreference;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class GeneralSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    MaterialListPreference week_num;
    SharedPreferences preferences;
    final static String WEEK_NUM = "week_num";
    final static String WEEK_START = " week_start";
    /**
     *@see com.holo.base.BasicDate#MAX_WEEK_NUM
     */
    final static int MAX_WEEK_NUM = 24;
    /**
     *@see com.holo.base.BasicDate#ONE_DAY
     */
    final static int ONE_DAY = 1000 * 3600 * 24;


    public GeneralSettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        week_num = (MaterialListPreference) getPreferenceScreen().findPreference("week_num");
    }

    //    init MaterialListPreference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(WEEK_NUM)) {
            setWeekStart(Integer.parseInt(week_num.getValue()));
            InitListSummary();
        }
    }

    private void InitListSummary() {
       if (week_num.getValue().equals("")) {
            week_num.setSummary("第1周");
        } else {
            week_num.setSummary(week_num.getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        long week_start_days = preferences.getLong(WEEK_START, 0);
        week_num.setValue(BasicDate.getWeekNum(week_start_days) + "");

        InitListSummary();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }



    private void setWeekStart(int week_num) {
        Calendar calendar = Calendar.getInstance();
        long to_days = System.currentTimeMillis() / ONE_DAY;
        int day_of_week = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;// calendar.get(Calendar.DAY_OF_WEEK)-1-1+7)%7
        preferences.edit().putLong(WEEK_START, to_days - day_of_week - week_num * 7).apply();
    }

}
