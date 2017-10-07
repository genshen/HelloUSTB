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
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class IOUtils {
    final static String HELLO_USTB_DIRECTORY = "/HelloUSTB";
    final static String HELLO_USTB_DIRECTORY_APK = "/HelloUSTB/apk";
    final public static String HELLO_USTB_AVATAR_NAME = "avatar.png";
    final public static String CACHE_APK_DIR = "/apk/";

    /**
     * note: those classes also use I/O:
     * {@link me.gensh.fragments.CampusNetworkFragment#FILE_PATH }
     * {@link me.gensh.fragments.CampusNetworkFragment#M }
     * {@link me.gensh.helloustb.Account#setAvatar }
     */

    //     if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
    public static void checkDirExit() {
        createSDCardDir("/MyUstb");
        createSDCardDir("/MyUstb/Pass_store");
        createSDCardDir("/MyUstb/Apk");
        createSDCardDir("/MyUstb/Data");
        createSDCardDir("/MyUstb/Data/Info");
        createSDCardDir("/MyUstb/Data/Info/images");
        createSDCardDir("/MyUstb/Data/Course");
        createSDCardDir("/MyUstb/DownloadFile");
    }

    //
    private static void createSDCardDir(String dirName) {
        String path = getSDPath() + dirName;
        File newDir = new File(path);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

    public static void write(String content, String file_name) {
        try {
            String SDPath = getSDPath();
            File targetFile = new File(SDPath + file_name);
            RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
            raf.seek(targetFile.length());
            raf.write(content.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    //
    private static File createSDFile(String fileName) {
        String SDPath = getSDPath();
        File file = new File(SDPath + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File writeToSDfromInput(String path, String fileName, InputStream inputStream) {
        File file = createSDFile(path + fileName);
        try {
            OutputStream outStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
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

    //create file in directory: Downloads
    public static File createFileInDownloadsDirectory(String filename) throws IOException {
        return createFileInDownloadsDirectory(HELLO_USTB_DIRECTORY, filename);
    }

    public static File createFileInDownloadsDirectory(String parent, String filename) throws IOException {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + parent);
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

