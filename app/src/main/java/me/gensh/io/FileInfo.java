package me.gensh.io;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.gensh.helloustb.R;


/**
 * @author gensh
 */
public class FileInfo {
    public static String getFileDate(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        return sdf.format(file.lastModified());
    }

    public static String getFileSize(File file) {
        String[] length_str = {"B", "KB", "MB", "GB", "TB"};
        long file_length = file.length();
        int radix = 1024;
        int next = 0;
        int i = 0;

        while (file_length / radix > 0) {
            next = (int) (file_length % radix);
            file_length = file_length / radix;
            i++;
        }

        if (i == 0) {
            return file_length + length_str[i];
        } else {
            return file_length + "." + ((next >= 1000 ? 1 : next / 100) + "" + next / 10 % 10) + length_str[i];
        }
    }

    public static String getEnd(File file) {
        String file_name = file.getName();

        int dotIndex = file_name.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        return file_name.substring(dotIndex, file_name.length()).toLowerCase();
    }

    private static Map<String, Integer> FileIconMap;
    private static Map<String, String> MatchAppMap;

    static {
        Map<String, Integer> map = new HashMap<>();
        map.put(".doc", R.drawable.filesystem_icon_word);
        map.put(".apk", R.drawable.filesystem_icon_apk);
        map.put(".bmp", R.drawable.filesystem_icon_photo);
        map.put(".docx", R.drawable.filesystem_icon_word);
        map.put(".xls", R.drawable.filesystem_icon_excel);
        map.put(".xlsx", R.drawable.filesystem_icon_excel);
        map.put(".gif", R.drawable.filesystem_icon_photo);
//	    map.put( ".gtar",R.drawable.filesystem_icon_photo);
        map.put(".gz", R.drawable.filesystem_icon_rar);
        map.put(".htm", R.drawable.filesystem_icon_web);
        map.put(".html", R.drawable.filesystem_icon_web);
        map.put(".jpeg", R.drawable.filesystem_icon_photo);
        map.put(".jpg", R.drawable.filesystem_icon_photo);
//		map.put( ".js",R.drawable.filesystem_icon_photo);
        map.put(".log", R.drawable.filesystem_icon_text);
        map.put(".mp3", R.drawable.filesystem_icon_music);
        map.put(".mp4", R.drawable.filesystem_icon_movie);
        map.put(".pdf", R.drawable.filesystem_icon_pdf);
        map.put(".png", R.drawable.filesystem_icon_photo);
        map.put(".ppt", R.drawable.filesystem_icon_ppt);
        map.put(".pptx", R.drawable.filesystem_icon_ppt);
//		map.put( ".rmvb",R.drawable.filesystem_icon_photo);
//		map.put( ".rtf",R.drawable.filesystem_icon_photo);
        map.put(".tar", R.drawable.filesystem_icon_rar);
        map.put(".tgz", R.drawable.filesystem_icon_rar);
        map.put(".txt", R.drawable.filesystem_icon_text);
        map.put(".wav", R.drawable.filesystem_icon_music);
        map.put(".wmv", R.drawable.filesystem_icon_movie);
        map.put(".wps", R.drawable.filesystem_icon_word);
//		map.put( ".xml",R.drawable.filesystem_icon_photo);
//		map.put( ".z",R.drawable.filesystem_icon_photo);
        map.put(".zip", R.drawable.filesystem_icon_rar);
        FileIconMap = Collections.unmodifiableMap(map);

        Map<String, String> mapApp = new HashMap<>();
        mapApp.put(".doc", "application/msword");
//		mapApp.put( ".3gp": return "video/3gpp");
        mapApp.put(".apk", "application/vnd.android.package-archive");
//	            {".asf",    "video/x-ms-asf"},
//	            {".avi",    "video/x-msvideo"},
//	            {".bin",    "application/octet-stream"},
        mapApp.put(".bmp", "image/bmp");
//	            {".c",  "text/plain"},
//	            {".class",  "application/octet-stream"},
//	            {".conf",   "text/plain"},
//	            {".cpp",    "text/plain"},
//		".doc",    "application/msword"},
        mapApp.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mapApp.put(".xls", "application/vnd.ms-excel");
        mapApp.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//	            {".exe",    "application/octet-stream"},
        mapApp.put(".gif", "image/gif");
        mapApp.put(".gtar", "application/x-gtar");

        mapApp.put(".gz", "application/x-gzip");
//	            {".h",  "text/plain"},
        mapApp.put(".htm", "text/html");
        mapApp.put(".html", "text/html");
//	            {".jar",    "application/java-archive"},
//	            {".java",   "text/plain"},
        mapApp.put(".jpeg", "image/jpeg");
        mapApp.put(".jpg", "image/jpeg");
        mapApp.put(".js", "application/x-javascript");
        mapApp.put(".log", "text/plain");
//	            {".m3u",    "audio/x-mpegurl"},
//	            {".m4a",    "audio/mp4a-latm"},
//	            {".m4b",    "audio/mp4a-latm"},
//	            {".m4p",    "audio/mp4a-latm"},
//	            {".m4u",    "video/vnd.mpegurl"},
//	            {".m4v",    "video/x-m4v"},
//	            {".mov",    "video/quicktime"},
//	            {".mp2",    "audio/x-mpeg"},
        mapApp.put(".mp3", "audio/x-mpeg");
        mapApp.put(".mp4", "video/mp4");
//	            {".mpc",    "application/vnd.mpohun.certificate"},
//	            {".mpe",    "video/mpeg"},
//	            {".mpeg",   "video/mpeg"},
//	            {".mpg",    "video/mpeg"},
//	            {".mpg4",   "video/mp4"},
//	            {".mpga",   "audio/mpeg"},
//	            {".msg",    "application/vnd.ms-outlook"},
//	            {".ogg",    "audio/ogg"},
        mapApp.put(".pdf", "application/pdf");
        mapApp.put(".png", "image/png");
//	            {".pps",    "application/vnd.ms-powerpoint"},
        mapApp.put(".ppt", "application/vnd.ms-powerpoint");
        mapApp.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
//	            {".prop",   "text/plain"},
//	            {".rc", "text/plain"},
        mapApp.put(".rmvb", "audio/x-pn-realaudio");
        mapApp.put(".rtf", "application/rtf");
//	            {".sh", "text/plain"},
        mapApp.put(".tar", "application/x-tar");
        mapApp.put(".tgz", "application/x-compressed");
        mapApp.put(".txt", "text/plain");
        mapApp.put(".wav", "audio/x-wav");
//	            {".wma",    "audio/x-ms-wma"},
        mapApp.put(".wmv", "audio/x-ms-wmv");
        mapApp.put(".wps", "application/vnd.ms-works");
        mapApp.put(".xml", "text/plain");
        mapApp.put(".z", "application/x-compress");
        mapApp.put(".zip", "application/x-zip-compressed");
        MatchAppMap = Collections.unmodifiableMap(mapApp);
    }

    public static int getFileIcon(String end) {
        if (end != null) {
            Integer res = FileIconMap.get(end);
            if (res != null) {
                return res;
            }
        }
        return R.drawable.filesystem_icon_default;
    }

    public static String matchApp(String name) {
        if (name != null) {
            return MatchAppMap.get(name);
        } else {
            return null;
        }
    }
}
