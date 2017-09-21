package me.gensh.helloustb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import me.gensh.network.VersionCheckerTask;

public class About extends AppCompatActivity {
    private VersionCheckerTask checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
            checker.cancel(true);
            checker = null;
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
        AppCompatTextView version = findViewById(R.id.this_version);
        AppCompatTextView builtTime = findViewById(R.id.app_build_time);
        builtTime.setText(getString(R.string.build_time_format, BuildConfig.APP_BUILD_TIME));
        version.setText(getString(R.string.version_base, getString(R.string.app_name), versionName));
    }

    public void clickHandle(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.check_update:
                checker = new VersionCheckerTask(getString(R.string.UpdateAddress), this, false);
                checker.execute();
                break;
            case R.id.open_source:
                Intent open_source = new Intent(this, OpenSource.class);
                this.startActivity(open_source);
                break;
            case R.id.app_web:
                Browser.openBrowserWithUrl(this, getString(R.string.app_website));
                break;
        }
    }
}
