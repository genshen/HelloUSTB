package me.gensh.database;

/**
 * Created by gensh on 2017/9/19.
 */

public class Config {
    final public static boolean DB_ENCRYPTED = false;  //not use encrypted
    final public static String DB_FILE_NAME = "helloustb_v114.db"; // Environment.getExternalStorageDirectory()
    final public static String DB_FILE_NAME_ENCRYPTED = "hello_ustb-encrypted.114.db";
    final public static String DB_ENCRYPTED_PASSWORD = "hdffje2kjJiuW2";//todo DatabaseEncrypted.getDBEncryptedPassword();
    final static String ACCOUNT_DB_TABLE_NAME = "accounts_dbs";
    final static String TIMETABLE_DB_TABLE_NAME = "time_table_dbs";
}

/**
 * more to say: for class {@link:me.gensh.database.CourseDbHelper } Created by gensh on 2015/11/20, and deleted at 2017/9/21 by gensh
 */
