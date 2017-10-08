package me.gensh.natives;

/**
 * Created by gensh on 2015/12/10.
 */
public class MailAccount {
    public static native String getMailAccount();

    static {
        NativeLoader.load();
    }
}
