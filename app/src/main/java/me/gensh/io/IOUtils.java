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
    static final String HELLO_USTB_DIRECTORY = "HelloUSTB";
    static final String HELLO_USTB_DIRECTORY_APK = "HelloUSTB/apk";
    public static final String HELLO_USTB_AVATAR_NAME = "avatar.png";
    public static final String CACHE_APK_DIR = "apk";
    public static final String CACHE_LOGS = "logs";
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

    public static ArrayList<File> getFileList(String path) {
        String SDPath = getSDPath();
        File file = new File(SDPath + path);
        File[] tempList = file.listFiles();

        ArrayList<File> file_temp = new ArrayList<>();
        for (File aTempList : tempList) {
            if (aTempList.isFile()) {
                file_temp.add(aTempList);
            }
        }
        return file_temp;
    }

    private static File createFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // write data to a file from inputStream,if write is success return true,otherwise return false.
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
        return createFileInSelfDownloadsDirectory(HELLO_USTB_DIRECTORY, filename);
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

