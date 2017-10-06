package me.gensh.network;

import android.content.Context;
import android.os.AsyncTask;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import me.gensh.helloustb.http.DataInfo;
import me.gensh.helloustb.http.GetProcess;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.PostProcess;

/**
 * Created by gensh on 2015/11/12.
 * Rewritten with OkHttp and AsyncTask by gensh on 2017/9/22.
 */

public class HttpRequestTask extends AsyncTask<Void, Integer, DataInfo> {
    final public static int REQUEST_TYPE_GET = 1, REQUEST_TYPE_POST = 2;
    private final static int TIMEOUT = 8000;

    private final String url, tag, charset;
    private final int requestType, feedback, id;
    private Map<String, String> params;

    private Context context;
    private static HttpClients httpClients = HttpClients.newInstance(TIMEOUT, TIMEOUT);
    private OnTaskFinished responseHandler;

    /**
     * @param context     android context
     * @param requestType http request type, post or get
     * @param url         request url
     * @param tag         website tag
     * @param feedback    tag to distinguish every http progress results
     * @param id          it will progress different http response  by different way via this id
     * @param charset     witch decides how to encode return stream
     * @param params      http POST params or http GET params
     */
    public HttpRequestTask(Context context, int requestType, String url, String tag, int feedback, int id, String charset, Map<String, String> params) {
        this.context = context;
        this.requestType = requestType;
        this.url = url;
        this.feedback = feedback;
        this.tag = tag;
        this.id = id;
        this.charset = charset;
        this.params = params;
    }

    public static void HtmlOut(BufferedReader br) {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DataInfo doInBackground(Void... voids) {
        if (requestType == REQUEST_TYPE_GET) {  //process get request
            BufferedReader br = httpClients.get(url, charset);
            DataInfo data_info = new DataInfo();
            if (br == null) {  // connection timeout error
                data_info.code = DataInfo.TimeOut;
                return data_info;
            }
            data_info.data = GetProcess.MainProcess(br, id);
            return data_info;
        } else if (requestType == REQUEST_TYPE_POST) {  //process post request
            BufferedReader br = httpClients.post(url, params, charset);
            if (br == null) {
                DataInfo data_info = new DataInfo();
                data_info.code = DataInfo.TimeOut;
                return data_info;
            }
            return PostProcess.MainProcess(br, id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(DataInfo datainfo) {
        if (responseHandler != null) {
            if (datainfo == null || datainfo.data == null || datainfo.code == DataInfo.TimeOut) {
//                System.out.println(">>>>>>timeout>>>>");
                responseHandler.onTimeoutError();
            } else if (datainfo.code == DataInfo.ERROR_PASSWORD) {
                responseHandler.onPasswordError();
            } else {  //ok!
                ArrayList<String> data = datainfo.data;
                responseHandler.onOk(feedback, data);
            }
        }
        super.onPostExecute(datainfo);
    }

    public void setOnTaskFinished(OnTaskFinished callback) {
        responseHandler = callback;
    }

    public interface OnTaskFinished {
        //all things is ok(there is no error in http request and http response progress),
        void onOk(int what, @NonNull ArrayList<String> data);

        //when login into a website,if your password is not right,this method will be called.
        void onPasswordError();

        //when request timeout or read timeout happens,this method will be called.
        void onTimeoutError();
    }
}
