package me.gensh.network;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;

import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.ResolvedData;

/**
 * Created by gensh on 2015/11/12.
 * Rewritten with OkHttp and AsyncTask by gensh on 2017/9/22.
 */

public class HttpRequestTask extends AsyncTask<Void, Integer, ResolvedData> {
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
     * @param hostTag     website tag
     * @param feedback    tag to distinguish every http progress results
     * @param id          it will process different http response via this id
     * @param charset     witch decides how to encode return stream
     * @param params      http POST params or http GET params
     */
    public HttpRequestTask(Context context, int requestType, String url, String hostTag, int feedback, int id, String charset, Map<String, String> params) {
        this.context = context;
        this.requestType = requestType;
        this.url = url;
        this.feedback = feedback;
        this.tag = hostTag;
        this.id = id;
        this.charset = charset;
        this.params = params;
    }

    @Override
    protected ResolvedData doInBackground(Void... voids) {
        return httpClients.request(url, requestType, id, charset, params);
    }

    @Override
    protected void onPostExecute(ResolvedData resolvedData) {
        if (responseHandler != null) {
            if (resolvedData == null || resolvedData.data == null || resolvedData.code == ResolvedData.TimeOut) {
//                System.out.println(">>>>>>timeout>>>>");
                responseHandler.onTimeoutError();
            } else if (resolvedData.code == ResolvedData.ERROR_PASSWORD) {
                responseHandler.onPasswordError();
            } else {  //ok!
                responseHandler.onOk(feedback, resolvedData.data);
            }
        }
        super.onPostExecute(resolvedData);
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
