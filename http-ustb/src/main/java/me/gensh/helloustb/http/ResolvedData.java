package me.gensh.helloustb.http;

import java.util.ArrayList;

/**
 * Created by gensh on 2015/11/13.
 */
public class ResolvedData {
    public final static int OK = 1;
    public final static int TimeOut = 0;
    public final static int ERROR_PASSWORD = -1;
    public int code;
    public ArrayList<String> data;

    public ResolvedData() {
        this.code = OK;
        this.data = null;
    }
}
