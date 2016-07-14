package com.holo.base;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.holo.helloustb.MyApplication;
import com.holo.helloustb.R;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 根深 on 2016/7/13.
 */
public abstract class NetWorkActivity extends AppCompatActivity {
    public abstract void RequestResultHandler(int what, ArrayList<String> data);

    public abstract void setProcessDialog();

    public abstract void onNetworkDisabled();

    protected interface ErrorHandler {
        void onPasswordError();

        void onTimeoutError();
    }

    ErrorHandler errorHandler = null;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;
            //error password and timeout process
            if (data_info.code == DataInfo.TimeOut) {
                if (errorHandler != null) {
                    errorHandler.onTimeoutError();
                } else {
                    Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                }
                return;
            } else if (data_info.code == DataInfo.ERROR_PASSWORD) {
                if (errorHandler != null) {
                    errorHandler.onPasswordError();
                } else {
                    Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
                }
                return;
            }
            ArrayList<String> str_msg = data_info.data;
            RequestResultHandler(msg.what, str_msg);
        }
    };

    public void get(String url, String tag, int feedback, int id, String code, boolean setdialog) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (setdialog) {
                setProcessDialog();
            }
            GetPostHandler.handlerGet(handler, url, tag, feedback, id, code);
        } else {
            onNetworkDisabled();
        }
    }

    public void post(String url, String tag, int feedback, int id, String code,
                     Map<String, String> post_params, boolean setdialog) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (setdialog) {
                setProcessDialog();
            }
            GetPostHandler.handlerPost(handler, url, tag, feedback, id, code, post_params);
        } else {
            onNetworkDisabled();
        }
    }

    public void setErrorHandler(ErrorHandler callback) {
        errorHandler = callback;
    }

}
