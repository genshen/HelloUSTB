package me.gensh.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.gensh.network.VersionCheckerTask;
import me.gensh.service.DownloadApk;

public class About extends AppCompatActivity {
    private VersionCheckerTask checker;

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
        TextView version = findViewById(R.id.this_version);
        version.setText(getString(R.string.version_base, versionName));
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
