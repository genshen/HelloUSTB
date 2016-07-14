package com.holo.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cgs on 2015/11/15.
 */
public class HttpClients {

    static HttpClient httpclient = new DefaultHttpClient();
    public static HttpResponse response;
    public static boolean connected = false;

    /**
     * @param url           request url
     * @param tag           website tag
     * @param code          witch decides how to encode return stream
     * @param timeout_con   connection limit time(ms)
     * @param timeout_cread read html limit time(ms)
     * @return bufferedReader returned from website the url pointed to
     */
    public static BufferedReader get(String url, String tag, String code, int timeout_con, int timeout_cread) {
        httpclient.getParams().setParameter("http.connection.timeout", timeout_con);
        httpclient.getParams().setParameter("http.socket.timeout", timeout_cread);

        BufferedReader br = null;
        HttpGet httpget = new HttpGet(url);

        try {
            connected = true;
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                br = new BufferedReader(new InputStreamReader(entity.getContent(), code));
            }
//            response.close();
            return br;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param url           request url
     * @param post_params   post data
     * @param tag           website tag
     * @param code          witch decides how to encode return stream
     * @param timeout_con   connection limit time(ms)
     * @param timeout_cread read html limit time(ms)
     * @return bufferedReader returned from website the url pointed to
     */
    public static BufferedReader post(String url, Map<String, String> post_params, String tag,
                                      String code, int timeout_con, int timeout_cread) {
        httpclient.getParams().setParameter("http.connection.timeout", timeout_con);
        httpclient.getParams().setParameter("http.socket.timeout", timeout_cread);
        HttpPost httppost = new HttpPost(url);
        BufferedReader br = null;
        UrlEncodedFormEntity uefEntity;
        try {
            connected = true;
            uefEntity = new UrlEncodedFormEntity(getNameValuePairs(post_params), code);
            httppost.setEntity(uefEntity);
            response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                br = new BufferedReader(new InputStreamReader(entity.getContent(), code));
            }
//            response.close();
            return br;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * change post data form(Map) to name-value-pair
     *
     * @param post_params post data
     * @return name-value-pair
     */
    private static List<NameValuePair> getNameValuePairs(Map<String, String> post_params) {
        List<NameValuePair> name_valuepair = new ArrayList<>();
        for (Map.Entry<String, String> entry : post_params.entrySet()) {
            name_valuepair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return name_valuepair;
    }

    /**
     * close httpclient connect
     *
     * @return never use this function!
     */
    public static boolean close() {
        if (connected) {
            httpclient.getConnectionManager().shutdown();    //关闭正在使用的连接
            httpclient = new DefaultHttpClient();
            connected = true;
        }
        return false;
    }
}
