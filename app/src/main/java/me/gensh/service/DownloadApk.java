package me.gensh.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import me.gensh.helloustb.R;
import me.gensh.sdcard.SdCardPro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author gensh
 */

public class DownloadApk extends Service {
    static boolean downloading = false;
    NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;
    int mNotificationId = 0x001;
    long size = 0;
    long download_size = 0;
    int download_percent = 0;

    @Override
    public void onCreate() {
        mBuilder = new NotificationCompat.Builder(DownloadApk.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.update_download))
                .setTicker(getString(R.string.startDownload))
                .setProgress(0, 0, true)
                .setContentText(getString(R.string.update_downloading));

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String latestVersion = intent.getStringExtra("latestVersion");
        size = intent.getLongExtra("size", 0);

        if (!downloading && size != 0) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        downloading = true;
                        URL url = new URL(getString(R.string.downloadURL));//下载地址
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setConnectTimeout(3000);
                        size = Long.parseLong(urlConn.getHeaderField("Content-Length"));
                        InputStream inputStream = urlConn.getInputStream();
                        SdCardPro.checkDirExit();

                        handler.sendEmptyMessage(0x001);
                        File resultFile = writeToSDfromInput("/MyUstb/Apk", "/helloustb" + latestVersion + ".apk", inputStream);
                        if (resultFile == null) {
                            System.out.print("error");
                            downloading = false;
                            size = 0;
                            download_size = 0;
                            download_percent = 0;
                        } else {
                            handler.sendEmptyMessage(0x002);

                            File file = SdCardPro.createSDFile("/MyUstb/Apk/helloustb" + latestVersion + ".apk");
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            String type = "application/vnd.android.package-archive";
                            intent.setDataAndType(Uri.fromFile(file), type);
                            startActivity(intent);
                            downloading = false;
                            size = 0;
                            download_size = 0;
                            download_percent = 0;
                        }
                    } catch (IOException e) {
                        System.out.print("error");
                    }
                }
            }.start();
        }
        return startId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated catch block
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    mNotifyManager.notify(mNotificationId, mBuilder.build());
                    break;
                case 0x002:
                    mNotifyManager.cancel(mNotificationId);
                    break;
                case 0x003:
                    mBuilder.setProgress(100, download_percent, false)
                            .setContentText(download_percent + "%");
                    mNotifyManager.notify(mNotificationId, mBuilder.build());
                    break;
            }
        }
    };

    public File writeToSDfromInput(String path, String fileName, InputStream inputStream) {
        File file = SdCardPro.createSDFile(path + fileName);
        OutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
                upeadataNotification(count);
            }
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void upeadataNotification(int count) {
        download_size += count;
        int percent = (int) (download_size / (size / 100));
        if (download_percent != percent) {
//            System.out.println("percent is:" + percent + "\tdownload_size" + download_size + "\tsize" + size);
            download_percent = percent > 100 ? 100 : percent;
            handler.sendEmptyMessage(0x003);
        }
    }

}
