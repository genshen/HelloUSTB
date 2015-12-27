package com.holo.encrypt;

/**
 * Created by 根深 on 2015/12/10.
 */
public class MailAccount {
    public static  native String getMailAccount();
    public static  native String getMailPassword();
    static {
        System.loadLibrary("Key");
    }
}
