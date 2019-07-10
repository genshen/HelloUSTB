package me.gensh.helloustb;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.io.IOUtils;

public class ShareApp extends AppCompatActivity {
    final static String LABEL_DOWNLOAD_URL = "DOWNLOAD_URL";

    ShareAppTask shareAppTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app);
        Toolbar toolbar = findViewById(R.id.toolbar_share_app);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);
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
        if (shareAppTask != null) {
            shareAppTask.cancel(true);
        }
        super.onDestroy();
    }

    @OnClick(R.id.qr_code)
    public void copyDownloadLink() {
        ClipboardManager clipManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(LABEL_DOWNLOAD_URL, getString(R.string.download_link));
        clipManager.setPrimaryClip(clip);
        Toast.makeText(this, R.string.have_copied, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.share_to_friends_layout)
    public void shareToFriends() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File cacheApk = getCacheApkFile();
                shareAppTask = new ShareAppTask(this, cacheApk.exists());
                shareAppTask.execute();
            } else {
                Uri uri = Uri.parse("file://" + getPackageManager().getApplicationInfo(this.getPackageName(), 0).sourceDir);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getString(R.string.share_app_intent_title)));
            }
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
            Toast.makeText(this, R.string.app_send_error, Toast.LENGTH_LONG).show();
        }
    }

    private File getCacheApkFile() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        final String fileName = getString(R.string.app_name) + "-" + packageInfo.versionName + ".apk ";
        return new File(new File(getCacheDir(), IOUtils.CACHE_APK_DIRECTORY), fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class ShareAppTask extends AsyncTask<Void, Void, Boolean> {
        Activity activity;
        boolean haveCopied;

        ShareAppTask(Activity activity, boolean haveFileCopied) {
            this.activity = activity;
            this.haveCopied = haveFileCopied;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (haveCopied) {
                TextView shareText = activity.findViewById(R.id.share_app_text);
                shareText.setText(R.string.preparing_share);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (haveCopied) {
                return false;
            }
            try {
                File sourceApk = new File(getPackageManager().getApplicationInfo(getPackageName(), 0).sourceDir);
                File cacheApk = getCacheApkFile();
                cacheApk.getParentFile().mkdirs();
                IOUtils.copy(sourceApk, cacheApk);
                return false;
            } catch (IOException | PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean hasError) {
            if (haveCopied) {   //resume textView
                TextView shareText = activity.findViewById(R.id.share_app_text);
                shareText.setText(R.string.share_to_friends);
            }
            if (hasError) {
                Toast.makeText(ShareApp.this, R.string.app_send_error, Toast.LENGTH_LONG).show();
            } else {
                try {
                    File cacheApk = getCacheApkFile();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", cacheApk);
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    intent.setType("*/*");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_app_intent_title)));
                } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
                    Toast.makeText(ShareApp.this, R.string.app_send_error, Toast.LENGTH_LONG).show();
                }
            }
            super.onPostExecute(hasError);
        }
    }
}
