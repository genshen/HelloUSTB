package me.gensh.natives;

/**
 * Created by gensh on 2015/12/10.
 */
public class MailAccount {
    public static native String getMailAccount();

    public static native String getMailPassword();

    static {
        NativeLoader.load();
    }
}
