package me.gensh.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.gensh.helloustb.BuildConfig;
import me.gensh.helloustb.R;
import me.gensh.io.ApkDownloader;
import me.gensh.utils.NotificationUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ApkDownloadIntentService extends IntentService {
    private static final String ACTION_DOWNLOAD_APK = "me.gensh.service.action.download_apk";

    private static final String EXTRA_VERSION_NAME = "me.gensh.service.extra.VERSION_NAME";
    private static final String EXTRA_PACKAGE_SIZE = "me.gensh.service.extra.PACKAGE_SIZE";

    final static int mNotificationId = 0x001;

    public ApkDownloadIntentService() {
        super("ApkDownloadIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @param context     android context
     * @param packageSize latest version application apk package size(byte)
     * @param versionCode version code for latest version application
     * @param versionName version name for latest version application
     */
    public static void startActionDownloadApk(Context context, long packageSize, int versionCode, String versionName) {
        Intent intent = new Intent(context, ApkDownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_APK);
        intent.putExtra(EXTRA_VERSION_NAME, versionName);
        intent.putExtra(EXTRA_PACKAGE_SIZE, packageSize);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //todo (bug or feature?)but this can cause multiple downloads,one by one download.
        if (intent != null) {
            final String action = intent.getAction();  //can also add another action.
            if (ACTION_DOWNLOAD_APK.equals(action)) {
                final String versionName = intent.getStringExtra(EXTRA_VERSION_NAME);
                final long packageSize = intent.getLongExtra(EXTRA_PACKAGE_SIZE, 0);
                handleActionDownloadApk(versionName, packageSize);
            }
        }
    }

    private void handleActionDownloadApk(String versionName, long packageSize) {
        if (packageSize != 0) {
            try {
                URL url = new URL(getString(R.string.downloadURL));//下载地址
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(3000);
                if (urlConn.getHeaderField("Content-Length") != null) {
                    packageSize = Long.parseLong(urlConn.getHeaderField("Content-Length"));
                }
                InputStream inputStream = urlConn.getInputStream();

                final NotificationUtils notificationUtils = new NotificationUtils(this);
                final Notification.Builder mBuilder = notificationUtils.getDefaultNotification(getString(R.string.update_download),
                        getString(R.string.start_download), getString(R.string.update_downloading));
                mBuilder.setProgress(0, 0, true);
                notificationUtils.notify(mNotificationId, mBuilder);

                ApkDownloader downloader = new ApkDownloader(packageSize, versionName, getString(R.string.app_name));
                downloader.setOnDownloadProgressChanged(new ApkDownloader.OnDownloadProgressChanged() {
                    @Override
                    public void onChanged(int percent) {
                        mBuilder.setProgress(100, percent, false).setContentText(percent + "%");
                        notificationUtils.notify(mNotificationId, mBuilder);
                    }
                });
                File file = downloader.writeToSDFromInputStream(inputStream);
                downloader.close();

                if (file != null && file.exists()) {
                    notificationUtils.cancel(mNotificationId);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
//                        System.out.println(contentUri.toString());  // content://me.gensh.helloustb.provider/external/Download/HelloUSTB/HelloUSTB2.1.0-alpha2.apk
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {  // for pre-N version
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.update_download_error, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.print("error");
            }
        }
    }
}
