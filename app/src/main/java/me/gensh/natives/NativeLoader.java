package me.gensh.natives;

/**
 * Created by gensh on 2017/9/20.
 */

public class NativeLoader {
    private static boolean done = false;

    protected static synchronized void load() {
        if (done)
            return;
        System.loadLibrary("native-lib");
        done = true;
    }
}