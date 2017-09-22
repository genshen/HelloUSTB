package me.gensh.utils;

import android.app.Fragment;

import java.util.Map;

import me.gensh.helloustb.MyApplication;
import me.gensh.network.HttpRequestTask;

/**
 * Created by gensh on 2017/8/31.
 * <p>
 * this class is the same as {@link me.gensh.utils.NetWorkActivity}
 * </p>
 */

public abstract class NetWorkFragment extends Fragment {

    public abstract void showProgressDialog();

    public abstract void dismissProgressDialog();

    public abstract void onNetworkDisabled();

    HttpRequestTask httpRequestTask;

    protected void attemptHttpRequest(int requestTypePost, String url, String tag, int feedback, int id, String code, Map<String, String> params, boolean showProgress) {
        if (((MyApplication) getActivity().getApplication()).CheckNetwork()) {
            if (showProgress) {
                showProgressDialog();
            }
            if (this instanceof HttpRequestTask.OnTaskFinished) {
                if (httpRequestTask != null) {   //cancel task if it is not null,to make sure there is one task running in a activity or fragment.
                    httpRequestTask.cancel(true);
                }
                httpRequestTask = new HttpRequestTask(getActivity(), requestTypePost, url, tag, feedback, id, code, params);
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
    public void onDestroy() {
        if (httpRequestTask != null) {
            httpRequestTask.cancel(true);
        }
        super.onDestroy();
    }
}
