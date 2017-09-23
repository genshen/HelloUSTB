package me.gensh.database;

import android.os.Environment;

import me.gensh.natives.DatabaseEncrypted;
import me.gensh.sdcard.SdCardPro;

/**
 * Created by gensh on 2017/9/19.
 */

public class Config {
    final public static boolean DB_ENCRYPTED = false;  //not use encrypted
    final public static String DB_FILE_NAME ="helloustb_v111.db"; // Environment.getExternalStorageDirectory() todo "hello_ustb.db";
    final public static String DB_FILE_NAME_ENCRYPTED = "hello_ustb-encrypted.db";
    final public static String DB_ENCRYPTED_PASSWORD = "hdffje2kjJiuW2";//todo DatabaseEncrypted.getDBEncryptedPassword();
}
/**
 * more to say: for class {@link:me.gensh.database.CourseDbHelper } Created by gensh on 2015/11/20, and deleted at 2017/9/21 by gensh
 */
