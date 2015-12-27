package com.holo.helloustb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;

import java.util.ArrayList;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setVersion();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setVersion() {
        // TODO Auto-generated method stub
        String versionName = "1.1.0";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(About.this.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextView version = (TextView) findViewById(R.id.this_version);
        version.setText(getString(R.string.version_base) + versionName);
    }

    public void clickHander(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.check_update:
                CheckUpdate();
                break;
            case R.id.feedback:
                Intent feedback = new Intent(this, Feedback.class);
                startActivity(feedback);
                break;
            case R.id.app_web:
                Bundle data = new Bundle();
                data.putSerializable("url", getString(R.string.app_website));
                Intent browser = new Intent(this, Browser.class);
                browser.putExtras(data);
                startActivity(browser);
                break;
        }
    }

    public void CheckUpdate() {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            GetPostHandler.handlerGet(handler, getString(R.string.UpdateAddress), "MYWEB", 0x110, 10, "utf-8");
        } else {
            Toast.makeText(getApplicationContext(), R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.ERROR_PASSWORD) {
//                cancelProcessDialog();
                Toast.makeText(About.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
                return;
            } else if (data_info.code == DataInfo.TimeOut) {
//                cancelProcessDialog();
                Toast.makeText(About.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<String> str_msg = data_info.data;

            switch (msg.what) {
                case 0:
                    Toast.makeText(About.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                    break;
                case 0x110:
                    int versionCode = 0;
//					String spiltArr[] = str_msg.split("@");
                    if (str_msg == null || str_msg.size() != 4) {
                        Toast.makeText(About.this, R.string.request_error, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    final String latestVersionCode = str_msg.get(0);
                    final String latestVersionName = str_msg.get(1);
                    final long apk_size = Long.parseLong(str_msg.get(3));
                    try {
                        PackageInfo info = About.this.getPackageManager().getPackageInfo(About.this.getPackageName(), 0);
                        versionCode = info.versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//					Toast.makeText(Settings.this, versionCode+"@"+latestVersionCode, Toast.LENGTH_SHORT).show();
                    if (versionCode < Integer.parseInt(latestVersionCode)) {//
                        new MaterialDialog.Builder(About.this)
                                .title(R.string.versionUpdate)
                                .content(getString(R.string.updateRequest) + "v" + latestVersionName + "\n" + str_msg.get(2))
                                .positiveText(R.string.updateNow)
                                .negativeText(R.string.sayLater)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                        Toast.makeText(About.this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(About.this, DownloadApk.class);
                                        intent.putExtra("latestVersion", latestVersionName);
                                        intent.putExtra("size", apk_size);
                                        startService(intent);
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(About.this, R.string.haveUpdated, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
