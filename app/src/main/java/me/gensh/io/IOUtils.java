package me.gensh.io;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class IOUtils {
    static final String HELLO_USTB_DOWNLOAD_DIRECTORY = "HelloUSTB";
    static final String HELLO_USTB_DIRECTORY_DOWNLOAD_APK = "HelloUSTB/apk";
    public static final String HELLO_USTB_AVATAR_FILENAME = "avatar.png";
    public static final String CACHE_APK_DIRECTORY = "apk";
    public static final String CACHE_LOGS_DIRECTORY = "logs";
    public static final String FLOW_STORE_FILE_PATH = "flow";  //todo no directory

    /**
     * note: those classes also use I/O:
     * {@link me.gensh.fragments.CampusNetworkFragment#M }
     * {@link me.gensh.helloustb.Account#setAvatar }
     */

    public static String getSDPath() {
        try {
            return Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<File> getFileListInSelfDownloadsDirectory() {
        return getFileList(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), HELLO_USTB_DOWNLOAD_DIRECTORY));
    }

    public static ArrayList<File> getFileList(File path) {
        File[] tempList = path.listFiles();
        ArrayList<File> file_temp = new ArrayList<>();
        for (File aTempList : tempList) {
            if (aTempList.isFile()) {
                file_temp.add(aTempList);
            }
        }
        return file_temp;
    }

    public static File createFile(File parent, String filename) {
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return new File(parent, filename);
    }

    // write data to a file from inputStream,,return true if write is success ,otherwise return false.
    static public boolean writeToStore(File file, InputStream inputStream) {
        try {
            OutputStream outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
            }
            outStream.flush();
            outStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //create file in directory: Downloads
    public static File createFileInSelfDownloadsDirectory(String filename) throws IOException {
        return createFileInSelfDownloadsDirectory(HELLO_USTB_DOWNLOAD_DIRECTORY, filename);
    }

    public static File createFileInSelfDownloadsDirectory(String parent, String filename) throws IOException {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), parent);
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                throw new IOException("create directory failed");
            }
        }
        return new File(directory, filename);
    }

    public static void copy(File src, File dst) throws IOException {
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buf)) > 0) {
            output.write(buf, 0, bytesRead);
        }
        input.close();
        output.close();
    }
}

