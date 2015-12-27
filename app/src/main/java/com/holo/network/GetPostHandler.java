package com.holo.network;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by cgs on 2015/11/12.
 */
public class GetPostHandler {

    private final static int TIMEOUT = 8000;
    private static String tag_now = "";

    public static void HtmlOut(BufferedReader br) {
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param handler  handler that message send to
     * @param url      url
     * @param tag      tag of website
     * @param feedback message.what
     * @param id       process id
     * @param code     witch decides how to encode return stream
     */
    public static void handlerGet(final Handler handler, final String url, final String tag,
                                  final int feedback, final int id, final String code) {
        new Thread() {
            @Override
            public void run() {
                tag_now = tag;  // tag_now can be changed by other thread!
                Message msg = new Message();
                msg.what = feedback;
                msg.obj = getData(url, tag, code, id);
                if (tag_now.equals(tag)) {
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * @param handler     handler that message send to
     * @param url         url
     * @param tag         tag of website
     * @param feedback    message.what
     * @param id          process id
     * @param code        witch decides how to encode return stream
     * @param post_params post data
     */
    public static void handlerPost(final Handler handler, final String url, final String tag, final int feedback,
                                   final int id, final String code, final Map<String, String> post_params) {
        new Thread() {
            @Override
            public void run() {
                tag_now = tag;  // tag_now can be changed by other thread!
                Message msg = new Message();
                msg.what = feedback;
                msg.obj = postData(url, post_params, tag, code, id);
                if (tag_now.equals(tag)) {
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * @param url  url
     * @param tag  website url
     * @param code witch decides how to encode return stream, such as utf-8 or gbk...
     * @param id   process id
     * @return it will return the data processed
     */
    private static DataInfo getData(String url, String tag, String code, int id) {
//        HttpClients.close();
        DataInfo data_info = new DataInfo();
        BufferedReader getBr = HttpClients.get(url, tag, code, TIMEOUT, TIMEOUT);
        if (getBr == null) {  // connection timeout error
            data_info.code = DataInfo.TimeOut;
            return data_info;
        }
        data_info.data = GetProcess.MainProcess(getBr, id);
//        HttpClients.close();
        return data_info;
    }

    /**
     *
     * @param url url
     * @param post_params post data
     * @param tag website tag
     * @param code  witch decides how to encode return stream, such as utf-8 or gbk...
     * @param id   process id
     * @return data  processed
     */
    private static DataInfo postData(String url, Map<String, String> post_params, String tag, String code, int id) {
//        HttpClients.close();
        BufferedReader getBr = HttpClients.post(url, post_params, tag, code, TIMEOUT, TIMEOUT);
        if (getBr == null) {  // connection timeout error
            DataInfo data_info = new DataInfo();
            data_info.code = DataInfo.TimeOut;
            return data_info;
        }
        return  PostProcess.MainProcess(getBr, id);
//        HttpClients.close();
    }

}
