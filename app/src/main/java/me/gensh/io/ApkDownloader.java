package me.gensh.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by gensh on 2017/10/3.
 */

public class ApkDownloader {
    private OnDownloadProgressChanged listener;
    private long totalSize = 0;
    private int downloadPercent = 0;
    final private String versionName, appName;

    //we store apk file in Downloads directory,
    public ApkDownloader(long packageSize, String versionName, String appName) {
        this.totalSize = packageSize;
        this.versionName = versionName;
        this.appName = appName;
    }

    public File writeToSDFromInputStream(InputStream inputStream) {
        long downloadSize = 0;
        try {
            File file = SdCardPro.createFileInDownloadsDirectory(appName + versionName + ".apk");
            OutputStream outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
                downloadSize += count;
                int percent = (int) (downloadSize / (totalSize / 100));
                if (downloadPercent != percent) {
//            System.out.println("percent is:" + percent + "\tdownload_size" + download_size + "\tsize" + size);
                    downloadPercent = percent > 100 ? 100 : percent;
                    if (listener != null) {
                        listener.onChanged(downloadPercent);
                    }
                }
            }
            outStream.flush();
            outStream.close();
            inputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        listener = null;
    }

    public void setOnDownloadProgressChanged(OnDownloadProgressChanged callback) {
        listener = callback;
    }

    public interface OnDownloadProgressChanged {
        void onChanged(int percent);
    }
}
