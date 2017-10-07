package me.gensh.helloustb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginNetworkActivity;

public class CampusCardConsumption extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_card_consumption);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {

    }

    @Override
    public void onPasswordError() {

    }

    @Override
    public void onTimeoutError() {

    }
}
