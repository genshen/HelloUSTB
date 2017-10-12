package me.gensh.helloustb.http;

import java.util.ArrayList;

/**
 * Created by gensh on 2015/11/13.
 */
public class ResolvedData {
    public final static int OK = 1;
    public final static int TimeOut = 0;
    public final static int ERROR_RESOLVE = 2;
    public final static int ERROR_PASSWORD = -1;
    public int code = OK;
    public ArrayList<String> data;

    public ResolvedData() {
    }

    public ResolvedData(int code) {
        this.code = code;
    }
}
