package com.holo.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicDate {
    public static int getWeek() {
        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK) - 2;
        return week < 0 ? week + 7 : week; //from  0(Mon) to 6(Sun)
    }

    public static String getMyday() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
    }

    public static int getEndYear() {
        Calendar cal = Calendar.getInstance();//ʹ��������
        int to_year = cal.get(Calendar.YEAR);//�õ���
        int to_month = cal.get(Calendar.MONTH) + 1;
        if (to_month < 8)    //���＾ѧ�ڱ���һ��
        {
            to_year--;
        }
        return to_year;
    }

    public static String getTimetableYear() {
//		"2014-2015-3"
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month < 6) {
            return (year - 1) + "-" + year + "-2";
        } else if (month < 8) {
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

}
