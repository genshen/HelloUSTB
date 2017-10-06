package me.gensh.io;

import me.gensh.utils.BasicDate;

public class VersionUpdate {
    public static boolean Renew(int versionCode, String versionName) {
        // TODO
        int OldVersionCode = 0;
        if (SdCardPro.fileIsExists("/MyUstb/version.xml")) {
            String vcode = SdCardPro.read("/MyUstb/version.xml");
            String code[] = vcode.split("_");
            OldVersionCode = Integer.parseInt(code[1]);
        }

//        if (OldVersionCode < 87) {//
//            SdCardPro.delFile("/MyUstb/Data/Course/Course.db");
//            SdCardPro.delFile("/MyUstb/Data/Course/Course.db-journal");
//        }

        if (OldVersionCode != versionCode) {
            if (SdCardPro.fileIsExists("/MyUstb/version.xml")) {
                SdCardPro.delFile("/MyUstb/version.xml");
            }
            SdCardPro.createSDFile("/MyUstb/version.xml");
            SdCardPro.write("<myustb>\n<versionCode>_" + versionCode + "_</versionCode>\n"
                    + "<versionName>_v" + versionName + "_</versionName>\n</myustb>", "/MyUstb/version.xml");
            return false;
        }
        return true;
    }

    public static boolean compareDate() {
        // TODO
        String todayDate = BasicDate.getMyday();
        if (SdCardPro.fileIsExists("/MyUstb/openlog.log")) {
            if (todayDate.equals(SdCardPro.read("/MyUstb/openlog.log"))) {
                return true;
            } else {
                SdCardPro.delFile("/MyUstb/openlog.log");
                SdCardPro.write(todayDate, "/MyUstb/openlog.log");
                return false;
            }
        } else {
            SdCardPro.write(todayDate, "/MyUstb/openlog.log");  // TODO: remove 2017/9/29
            return false;
        }
    }

}
