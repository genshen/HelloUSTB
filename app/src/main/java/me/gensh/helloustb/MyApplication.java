/**
 *
 */
package me.gensh.helloustb;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.room.Room;

import me.gensh.database.Config;
import me.gensh.database.DB;

/**
 * @author gensh
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */

    private DB dbSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        dbSession = Room.databaseBuilder(getApplicationContext(),
                DB.class, Config.DB_ENCRYPTED ? Config.DB_FILE_NAME_ENCRYPTED : Config.DB_FILE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public DB getDaoSession() {
        return dbSession;
    }

    public static Context getMyApplication() {
        return instance;
    }

    /**
     * @return return true if has network
     */
    public boolean CheckNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

}
