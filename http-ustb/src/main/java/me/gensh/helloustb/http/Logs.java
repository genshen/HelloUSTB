package me.gensh.helloustb.http;

/**
 * Created by gensh on 2017/10/11.
 */

public class Logs {
    final static boolean DEBUG = false;

    public static void e(Exception e) {
        if (DEBUG)
            e.printStackTrace();
    }
}
