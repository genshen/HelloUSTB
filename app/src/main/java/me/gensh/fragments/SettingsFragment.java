package me.gensh.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.Calendar;

import me.gensh.helloustb.R;
import me.gensh.utils.BasicDate;
import me.gensh.utils.Const;
import me.gensh.views.MaterialListPreference;

/**
 * A placeholder fragment containing a simple view.
 *
 * * Updated by gensh on 2027/09/14
 * *.Created by gensh on  2016/10/7
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    MaterialListPreference mPreferenceWeekNum, mPreferenceNetSignInMode;
    SharedPreferences preferences;
    /**
     * @see me.gensh.utils.BasicDate#MAX_WEEK_NUM
     */
    final static int MAX_WEEK_NUM = 24;
    /**
     * @see me.gensh.utils.BasicDate#ONE_DAY
     */
    final static int ONE_DAY = 1000 * 3600 * 24;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        setListSummary(mPreferenceNetSignInMode, "普通模式");

        long week_start_days = preferences.getLong(Const.Settings.KEY_WEEK_START, 0);
        mPreferenceWeekNum.setValue(BasicDate.getWeekNum(week_start_days) + "");
        //have a think: setValue will cause a call of function onSharedPreferenceChanged. But it have no effect.todo think.
        setListSummary(mPreferenceWeekNum, "第一周");
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    //    init MaterialListPreference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.Settings.KEY_WEEK_NUM.equals(key)) {
            setWeekStart(Integer.parseInt(mPreferenceWeekNum.getValue()));
//            BasicDate.writeLog("changed!\r\n");
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

    private void setWeekStart(int week_num) {
        Calendar calendar = Calendar.getInstance();
        long to_days = calendar.getTimeInMillis() / ONE_DAY;
        int day_of_week = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;// calendar.get(Calendar.DAY_OF_WEEK)-1-1+7)%7
        preferences.edit().putLong(Const.Settings.KEY_WEEK_START, to_days - day_of_week - week_num * 7).apply();
    }

}
