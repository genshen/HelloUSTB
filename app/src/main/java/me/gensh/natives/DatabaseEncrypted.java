package me.gensh.natives;

/**
 * Created by gensh on 2017/9/19.
 */

public class DatabaseEncrypted {
    public static native String getDBEncryptedPassword();

    static {
        NativeLoader.load();
    }
}
