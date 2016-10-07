/**
 *
 */
package me.gensh.helloustb;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;

/**
 * @author gensh
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean CheckNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    public void setUpShortCut() {
        String title="WIFI";
        Intent addIntent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Parcelable icon=Intent.ShortcutIconResource.fromContext(this, R.drawable.guide_wifi); //获取快捷键的图标
//
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(getApplicationContext(), MainActivity.class);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        sendBroadcast(addIntent);
    }
}
