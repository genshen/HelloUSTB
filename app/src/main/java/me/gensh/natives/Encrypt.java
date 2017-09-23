package me.gensh.natives;

/**
 * Created by gensh on 2017/9/23.
 */

public class Encrypt {
    //encrypt a text of byte array format,and returns encrypted byte array.
    public static native byte[] nativeEncrypt(byte[] plainText, byte[] iv);

    //decrypt a text of byte array format,and returns decrypted byte array.
    public static native byte[] nativeDecrypt(byte[] cipherText, byte[] iv);

    static {
        NativeLoader.load();
    }
}
