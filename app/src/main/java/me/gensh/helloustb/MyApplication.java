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

import org.greenrobot.greendao.database.Database;

import me.gensh.database.Config;
import me.gensh.database.DaoMaster;
import me.gensh.database.DaoSession;

/**
 * @author gensh
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Config.DB_ENCRYPTED ? Config.DB_FILE_NAME_ENCRYPTED : Config.DB_FILE_NAME);
        Database db = Config.DB_ENCRYPTED ? helper.getEncryptedWritableDb(Config.DB_ENCRYPTED_PASSWORD) : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
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
