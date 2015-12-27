package com.holo.sdcard;

import com.holo.helloustb.R;

import java.io.File;
import java.text.SimpleDateFormat;


/**
 * @author cgs
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

        String end = file_name.substring(dotIndex, file_name.length()).toLowerCase();
        if (end.isEmpty()) {
            return null;
        }
        return end;
    }

    public static int getFileIcon(String end) {
        if (end != null) {
            switch (end) {
                case ".doc":
                    return R.drawable.filesystem_icon_word;
                case ".apk":
                    return R.drawable.filesystem_icon_apk;
                case ".bmp":
                    return R.drawable.filesystem_icon_photo;
                case ".docx":
                    return R.drawable.filesystem_icon_word;
                case ".xls":
                    return R.drawable.filesystem_icon_excel;
                case ".xlsx":
                    return R.drawable.filesystem_icon_excel;
                case ".gif":
                    return R.drawable.filesystem_icon_photo;
//			case ".gtar":
//				return R.drawable.filesystem_icon_photo; 
                case ".gz":
                    return R.drawable.filesystem_icon_rar;
                case ".htm":
                    return R.drawable.filesystem_icon_web;
                case ".html":
                    return R.drawable.filesystem_icon_web;
                case ".jpeg":
                    return R.drawable.filesystem_icon_photo;
                case ".jpg":
                    return R.drawable.filesystem_icon_photo;
//			case ".js": 
//				return R.drawable.filesystem_icon_photo; 
                case ".log":
                    return R.drawable.filesystem_icon_text;
                case ".mp3":
                    return R.drawable.filesystem_icon_music;
                case ".mp4":
                    return R.drawable.filesystem_icon_movie;
                case ".pdf":
                    return R.drawable.filesystem_icon_pdf;
                case ".png":
                    return R.drawable.filesystem_icon_photo;
                case ".ppt":
                    return R.drawable.filesystem_icon_ppt;
                case ".pptx":
                    return R.drawable.filesystem_icon_ppt;
//			case ".rmvb":
//				return R.drawable.filesystem_icon_photo; 
//			case ".rtf": 
//				return R.drawable.filesystem_icon_photo; 
                case ".tar":
                    return R.drawable.filesystem_icon_rar;
                case ".tgz":
                    return R.drawable.filesystem_icon_rar;
                case ".txt":
                    return R.drawable.filesystem_icon_text;
                case ".wav":
                    return R.drawable.filesystem_icon_music;
                case ".wmv":
                    return R.drawable.filesystem_icon_movie;
                case ".wps":
                    return R.drawable.filesystem_icon_word;
//			case ".xml":
//				return R.drawable.filesystem_icon_photo; 
//			case ".z":
//				return R.drawable.filesystem_icon_photo; 
                case ".zip":
                    return R.drawable.filesystem_icon_rar;
                default:
                    return R.drawable.filesystem_icon_default;
            }
        } else {
            return R.drawable.filesystem_icon_default;
        }
    }

    public static String matchApp(String name) {
        switch (name) {
            case ".doc":
                return "application/msword";
//		case ".3gp": return "video/3gpp";  
            case ".apk":
                return "application/vnd.android.package-archive";
//	            {".asf",    "video/x-ms-asf"},  
//	            {".avi",    "video/x-msvideo"},  
//	            {".bin",    "application/octet-stream"},  
            case ".bmp":
                return "image/bmp";
//	            {".c",  "text/plain"},  
//	            {".class",  "application/octet-stream"},  
//	            {".conf",   "text/plain"},  
//	            {".cpp",    "text/plain"},  
//		".doc",    "application/msword"},  
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//	            {".exe",    "application/octet-stream"},  
            case ".gif":
                return "image/gif";
            case ".gtar":
                return "application/x-gtar";

            case ".gz":
                return "application/x-gzip";
//	            {".h",  "text/plain"},  
            case ".htm":
                return "text/html";
            case ".html":
                return "text/html";
//	            {".jar",    "application/java-archive"},  
//	            {".java",   "text/plain"},  
            case ".jpeg":
                return "image/jpeg";
            case ".jpg":
                return "image/jpeg";
            case ".js":
                return "application/x-javascript";
            case ".log":
                return "text/plain";
//	            {".m3u",    "audio/x-mpegurl"},  
//	            {".m4a",    "audio/mp4a-latm"},  
//	            {".m4b",    "audio/mp4a-latm"},  
//	            {".m4p",    "audio/mp4a-latm"},  
//	            {".m4u",    "video/vnd.mpegurl"},  
//	            {".m4v",    "video/x-m4v"},   
//	            {".mov",    "video/quicktime"},  
//	            {".mp2",    "audio/x-mpeg"},  
            case ".mp3":
                return "audio/x-mpeg";
            case ".mp4":
                return "video/mp4";
//	            {".mpc",    "application/vnd.mpohun.certificate"},        
//	            {".mpe",    "video/mpeg"},    
//	            {".mpeg",   "video/mpeg"},    
//	            {".mpg",    "video/mpeg"},    
//	            {".mpg4",   "video/mp4"},     
//	            {".mpga",   "audio/mpeg"},  
//	            {".msg",    "application/vnd.ms-outlook"},  
//	            {".ogg",    "audio/ogg"},  
            case ".pdf":
                return "application/pdf";
            case ".png":
                return "image/png";
//	            {".pps",    "application/vnd.ms-powerpoint"},  
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
//	            {".prop",   "text/plain"},  
//	            {".rc", "text/plain"},  
            case ".rmvb":
                return "audio/x-pn-realaudio";
            case ".rtf":
                return "application/rtf";
//	            {".sh", "text/plain"},  
            case ".tar":
                return "application/x-tar";
            case ".tgz":
                return "application/x-compressed";
            case ".txt":
                return "text/plain";
            case ".wav":
                return "audio/x-wav";
//	            {".wma",    "audio/x-ms-wma"},  
            case ".wmv":
                return "audio/x-ms-wmv";
            case ".wps":
                return "application/vnd.ms-works";
            case ".xml":
                return "text/plain";
            case ".z":
                return "application/x-compress";
            case ".zip":
                return "application/x-zip-compressed";
            default:
                return null;
        }
    }
}
