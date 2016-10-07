package me.gensh.network;

import java.util.ArrayList;

/**
 * Created by gensh on 2015/11/13.
 */
public class DataInfo {
    public final static int OK = 1;
    public final static int TimeOut = 0;
    public final static int ERROR_PASSWORD = -1;
    public int code;
    public ArrayList<String> data;

    public DataInfo() {
        this.code = OK;
        this.data = null;
    }
}
