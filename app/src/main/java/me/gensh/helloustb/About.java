package me.gensh.helloustb;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

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

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startDownload(long packageSize, int newVersionCode, String newVersionName) {
        Toast.makeText(this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
        ApkDownloadIntentService.startActionDownloadApk(this, packageSize, newVersionCode, newVersionName);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForApkDownload(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_download_apk_rationale)
                .setPositiveButton(R.string.permission_button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AboutPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    final static private short TURN = 4;
    final static private short ICON_COUNT = 8;
    final static private int[] icons = {R.drawable.play_1, R.drawable.play_2, R.drawable.play_3,
            R.drawable.play_4, R.drawable.play_5, R.drawable.play_6, R.drawable.play_7, R.drawable.play_8};
    private short iconClickCounter = 0;
    Random random = new Random();

    public void clickHandle(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.about_header_container:
                iconClickCounter++;
                if (iconClickCounter == TURN) {
                    ImageView appIcon = findViewById(R.id.app_icon);
                    int index = random.nextInt(ICON_COUNT);  //ic_launcher is also needed ?
                    appIcon.setImageResource(icons[index]); // 0 to ICON_COUNT-1
                    iconClickCounter = 0;
                } /*else { //1,2,3,TURN-1
                    Snackbar.make(view, getString(R.string.about_play_snack, TURN - iconClickCounter), Snackbar.LENGTH_SHORT).show();
                }*/
                break;
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
