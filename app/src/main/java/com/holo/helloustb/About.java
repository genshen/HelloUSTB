package com.holo.helloustb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.holo.network.VersionChecker;

public class About extends AppCompatActivity {
    private VersionChecker checker;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (checker != null) {
            checker.setOnUpdate(null);
        }

    }

    private void setVersion() {
        String versionName = "1.1.0";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(About.this.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = (TextView) findViewById(R.id.this_version);
        version.setText(getString(R.string.version_base, versionName));
    }

    public void clickHandle(View view) {
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
        checker = new VersionChecker(getString(R.string.UpdateAddress), this);
        checker.setOnUpdate(new VersionChecker.Update() {
            @Override
            public void onUpdate(boolean latestVersion) {
                if (latestVersion) {
                    handler.sendEmptyMessage(0x110);
                } else {
                    handler.sendEmptyMessage(0x111);
                }
            }
        });
        checker.check(((MyApplication) getApplication()).CheckNetwork());
    }

    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    new MaterialDialog.Builder(About.this)
                            .title(R.string.versionUpdate)
                            .content(getString(R.string.updateRequest, checker.version_name_new, checker.version_describe))
                            .positiveText(R.string.updateNow)
                            .negativeText(R.string.sayLater)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                    Toast.makeText(About.this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(About.this, DownloadApk.class);
                                    intent.putExtra("latestVersion", checker.version_name_new);
                                    intent.putExtra("size", checker.package_size);
                                    startService(intent);
                                }
                            })
                            .show();
                    break;
                case 0x111:
                    Toast.makeText(About.this, R.string.haveUpdated, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
