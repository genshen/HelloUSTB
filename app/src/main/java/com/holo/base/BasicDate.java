package com.holo.base;

import com.holo.sdcard.SdCardPro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicDate {
    final static int MAX_WEEK_NUM = 24;
    final static int ONE_DAY = 1000 * 3600 * 24;

    public static int getWeek() {
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK) - 2;
        return week < 0 ? week + 7 : week; //from  0(Mon) to 6(Sun)
    }

    // pretend the first day of a week is monday!
    public static int getWeekNum(long week_start_days) {
        long to_days = System.currentTimeMillis() / ONE_DAY;
        writeLog("start:" + week_start_days + ";to" + to_days + ";weeks:" + (to_days - week_start_days) / 7 % MAX_WEEK_NUM + "\r\n");
        if (week_start_days > to_days || week_start_days == 0) {
            return 0;
        }
        return (int) (to_days - week_start_days) / 7 % MAX_WEEK_NUM; // mod 24
    }

    public static void writeLog(String log) {
        String filename = "/MyUstb/log.log";
        if (SdCardPro.fileIsExists(filename)) {
            SdCardPro.createSDFile(filename);
        }
        SdCardPro.write(log, filename);
    }

    public static String getMyday() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
    }

    public static int getweek() {//蔡勒公式
        Calendar cal = Calendar.getInstance();
//		Date date =new Date();
        int year = cal.get(Calendar.YEAR);//date.getYear();
        int month = cal.get(Calendar.MONTH) + 1;//date.getMonth();
        int day = cal.get(Calendar.DATE);//date.getDay();
        //System.out.println(year+"-"+month+"-"+day+"=");
        if (month == 1 || month == 2) {
            year--;
            month += 12;
        }
        int c = year / 100;
        int y = year % 100;
        int week = (c / 4) - 2 * c + (y + y / 4) + (13 * (month + 1)) / 5 + day - 1;
        week %= 7;
        if (week <= 0) {
            week += 7;
        }
        ;
        return week;
    }

    /*成绩查询中生成结束年级*/
    public static int getEndYear() {
        Calendar cal = Calendar.getInstance();//使用日历类
        int to_year = cal.get(Calendar.YEAR);//得到年
        int to_month = cal.get(Calendar.MONTH) + 1;
        if (to_month < 8)    //和秋季学期保持一致
        {
            to_year--;
        }
        return to_year;
    }


    /*导入课表生成年份*/
    public static String getTimetableYear() {
//		"2014-2015-3"
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 6)    //6月开始就可以导入小学期课表
        {
            return (year - 1) + "-" + year + "-2";
        } else if (month < 8) //8月开始就可以导入下学期课表
        {
            return (year - 1) + "-" + year + "-3";
        } else {
            return (year) + "-" + (year + 1) + "-1";
        }
    }

    public static int getStartYear(ArrayList<String> score) {
        if (score.size() >= 3) {
            String y = score.get(2);    //2013-2014-1
            return Integer.parseInt(y.split("-")[0]);
        }
        return 2013;
    }

    public static List<List<Map<String, Object>>> getScoredSplit(ArrayList<String> record) {
        int length = record.size() / 8;
        String year = "";
        List<List<Map<String, Object>>> score_list = new ArrayList<>();
        List<Map<String, Object>> score_li = new ArrayList<>();

        for (int i = 0; i < length; ) {
            if (record.get(2 + 8 * i).equals(year)) {    //get year
                Map<String, Object> listitem = new HashMap<>();
                listitem.put("course", record.get(8 * i + 4));
                listitem.put("course_class", record.get(8 * i + 5));
                listitem.put("learn_time", record.get(8 * i + 7));
                listitem.put("myscore", record.get(8 * i + 9));
                score_li.add(listitem);
                i++;
            } else {    //new year list
                if (i != 0) score_list.add(score_li); // don't add when the first time
                score_li = new ArrayList<>();
                year = record.get(2 + 8 * i);
            }
        }
        if (length != 0) score_list.add(score_li);
        return score_list;
    }

    public static String getCourseTime(int lesson_no) {
        switch (lesson_no) {
            case 0:
                return "8:00-9:35";
            case 1:
                return "9:55-11:30";
            case 2:
                return "13:30-15:05";
            case 3:
                return "15:20-16:55";
            case 4:
                return "17:10-18:45";
            case 5:
                return "19:30-21:05";
        }
        return "8:00-9:35";
    }

}
