package me.gensh.network;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;

import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.PendingException;
import me.gensh.helloustb.http.ResolvedData;
import me.gensh.helloustb.http.resolver.ResponseResolveException;

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
        HttpClients httpClients = HttpClients.newInstance(TIMEOUT, TIMEOUT);
        try {
            // get a nonNull ResolvedData(resolve method returns nonNull data)
            return httpClients.request(url, requestType, charset, params).resolve(requestType, id);
        } catch (PendingException e) {  //for timeout
            e.printStackTrace();
            return new ResolvedData(ResolvedData.TimeOut);
        } catch (ResponseResolveException e) { //for bad response/process.
            e.printStackTrace();
            return new ResolvedData(ResolvedData.ERROR_RESOLVE);
        }
    }

    @Override
    protected void onPostExecute(@NonNull ResolvedData resolvedData) {
        if (responseHandler != null) {
            if (resolvedData.code == ResolvedData.TimeOut || resolvedData.code == ResolvedData.ERROR_RESOLVE) { //todo
                responseHandler.onTimeoutError();
            } else if (resolvedData.code == ResolvedData.ERROR_PASSWORD) {
                responseHandler.onPasswordError();
            } else {  //ok,or right password
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
