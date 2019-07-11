package me.gensh.helloustb;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Map;

import me.gensh.helloustb.http.ResolvedData;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginNetworkActivity;

public class USTB_VPNActivity extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished, HttpRequestTask.CustomRequestBackgroundTask {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustb__vpn);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public ResolvedData OnCustomDoInBackground(String url, int id, int requestType, String charset, Map<String, String> params) {
        Toast.makeText(this, "None", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {

    }

    @Override
    public void onPasswordError() {

    }

    @Override
    public void onTimeoutError() {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void onNetworkDisabled() {

    }

}
