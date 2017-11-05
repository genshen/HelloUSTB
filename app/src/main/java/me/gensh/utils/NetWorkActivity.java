package me.gensh.utils;

import android.support.v7.app.AppCompatActivity;

import java.util.Map;

import me.gensh.helloustb.MyApplication;
import me.gensh.network.HttpRequestTask;

/**
 * Created by gensh on 2016/7/13.
 * Big update by gensh on 2017/09/22
 */
public abstract class NetWorkActivity extends AppCompatActivity {

    public abstract void showProgressDialog();

    public abstract void dismissProgressDialog();

    public abstract void onNetworkDisabled();

    HttpRequestTask httpRequestTask;

    protected void attemptHttpRequest(int requestTypePost, String url, String tag, int feedback, int id, String code, Map<String, String> params, boolean showProgress) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (showProgress) {
                showProgressDialog();
            }
            if (this instanceof HttpRequestTask.OnTaskFinished) {
                if (httpRequestTask != null) {   //cancel task if it is not null,to make sure there is one task running in a activity or fragment.
                    httpRequestTask.cancel(true);
                }
                httpRequestTask = new HttpRequestTask(this, requestTypePost, url, tag, feedback, id, code, params);
                if (this instanceof HttpRequestTask.CustomRequestBackgroundTask) {  //set CustomRequestTask.
                    httpRequestTask.setCustomRequestBackgroundTask((HttpRequestTask.CustomRequestBackgroundTask) this);
                }
                httpRequestTask.setOnTaskFinished((HttpRequestTask.OnTaskFinished) this);
                httpRequestTask.execute();
            } else {
                throw new RuntimeException(this.toString() + " must implement HttpRequestTask.OnTaskFinished");
            }
        } else {
            onNetworkDisabled();
        }
    }

    @Override
    protected void onDestroy() {
        if (httpRequestTask != null) {
            httpRequestTask.cancel(true);
        }
        super.onDestroy();
    }

}
