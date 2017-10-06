package me.gensh.helloustb;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import me.gensh.network.VersionCheckerTask;
import me.gensh.service.ApkDownloadIntentService;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class About extends AppCompatActivity implements VersionCheckerTask.OnNewVersionListener {
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

    //methods for updating
    @Override
    public void onAttemptToDownload(long packageSize, int newVersionCode, String newVersionName) {
        AboutPermissionsDispatcher.startDownloadWithPermissionCheck(this, packageSize, newVersionCode, newVersionName);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startDownload(long packageSize, int newVersionCode, String newVersionName) {
        Toast.makeText(this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
        ApkDownloadIntentService.startActionDownloadApk(this, packageSize, newVersionCode,newVersionName);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForApkDownload(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_download_apk_rationale)
                .setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AboutPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
