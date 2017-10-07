package me.gensh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;

public class VersionChecker {
    public final static int VERSION_CHECKER_TO_MAIN_ACTIVITY = 0x220;
    public final static int VERSION_CHECKER_TO_GUIDE_ACTIVITY = 0x221;
    private boolean sameVersionCode = true, sameOpenDate = true;
    private int oldVersionCode, currentVersionCode;
    private String oldOpenDate, currentOpenDate;
    private SharedPreferences preferences;

    /**
     * construct method, get old versionCode anf old open-log(e.g. when do we open this app) from  SharedPreferences.
     *
     * @param context android Context
     */
    public VersionChecker(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        oldVersionCode = preferences.getInt(Const.SharedPreferences.KEY_VERSION_CODE, 0);
        currentVersionCode = getCurrentVersionCode(context, 0);

        oldOpenDate = preferences.getString(Const.SharedPreferences.KEY_OPEN_DATE, null);
        currentOpenDate = BasicDate.getMyDay();
    }

    /**
     * check version code and open-log.And renew to current versioncode and current open-log.
     */
    public void check() {
        SharedPreferences.Editor editor = preferences.edit();
        if (oldVersionCode != currentVersionCode) {
            sameVersionCode = false;
            editor = editor.putInt(Const.SharedPreferences.KEY_VERSION_CODE, currentVersionCode);
        }
        if (oldOpenDate == null || !oldOpenDate.equals(currentOpenDate)) {
            sameOpenDate = false;
            editor.putString(Const.SharedPreferences.KEY_OPEN_DATE, currentOpenDate);
        }
        if (!sameVersionCode || !sameOpenDate) {  //if has put data to editor.
            editor.apply();
        }
    }

    public void waitFor(final Handler handler) {
        if (!sameVersionCode) {
            handler.sendEmptyMessageDelayed(VERSION_CHECKER_TO_GUIDE_ACTIVITY, 2000);
        } else if (!sameOpenDate) {
            handler.sendEmptyMessageDelayed(VERSION_CHECKER_TO_MAIN_ACTIVITY, 2000);
        } else {  //same version and same open date(it is not first time to open this app today)
            handler.sendEmptyMessage(VERSION_CHECKER_TO_MAIN_ACTIVITY);
        }
    }

    /**
     * get current package/application version code
     *
     * @param context            android Context
     * @param defaultVersionCode default value for versionCode
     * @return package version code.
     */
    private int getCurrentVersionCode(Context context, int defaultVersionCode) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultVersionCode;
    }


}
