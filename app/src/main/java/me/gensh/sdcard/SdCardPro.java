package me.gensh.sdcard;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class SdCardPro {

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

    public static String read(String path) {
        //this.path=path;
        try {
            String SDPath = getSDPath();
            FileInputStream fis = new FileInputStream(SDPath + path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder("");
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            // TODO
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<File> getFileList(String path) {
        String SDPath = getSDPath();
        File file = new File(SDPath + path);
        File[] tempList = file.listFiles();

        ArrayList<File> file_temp = new ArrayList<File>();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                file_temp.add(tempList[i]);
            }
        }
        return file_temp;
    }

    public static boolean fileIsExists(String filename) {
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            File f = new File(sdCardDir.getCanonicalPath() + filename);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    //
    public static File createSDFile(String fileName) {
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
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
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


    public static boolean delFile(String fileName) {
        String SDPath = getSDPath();
        File file = new File(SDPath + fileName);
        return file.delete();
    }


}

