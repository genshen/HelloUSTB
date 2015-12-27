package com.holo.dataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class StoreData {
    SQLiteDatabase course_db;
    public CourseDbHelper course;
    final String TableName = "Course_info";

    public StoreData(Context context) {
        course = new CourseDbHelper(context, 1);
        course_db = course.getReadableDatabase();
    }

    public void clearTable() {
        String sql = "DELETE FROM " + TableName + " WHERE 1";
        course_db.execSQL(sql);
    }

    public void close() {
        course.close();
    }

    public static int key = 0;

    public boolean SaveToDb(String msg) {
        try {
            JSONObject dataJson = new JSONObject(msg);
            JSONArray data = dataJson.getJSONArray("selectedCourses");
            for (int i = 0; i < data.length(); i++) {
                JSONObject course = data.getJSONObject(i);
                Object value = course.get("XS");
                if (value != null) {
                    if (!InsertToDB(getStringByKey("SKRS", course, false),//上课人数
                            getStringByKey("XS", course, false),//学时
                            getStringByKey("XF", course, false),//学分
                            getStringByKey("ID", course, false),//ID
                            getStringByKey("KCM", course, true),//课程名称
                            getStringByKey("KCLBM", course, true),//课程类型
                            addJSM(course.getJSONArray("JSM")),    //教师
                            getStringByKey("SKSJDDSTR", course, true),//上课时间地点
                            addSKSJDD(course.getJSONObject("SKSJDD")) //上课详细时间地点
                    )) {
                        return false;
                    }

                    //				System.out.print( v +"\n");
                    //				//配套课
                    //				addPTK(course.getJSONArray("PTK"));
                }
            }
            return true;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    private  boolean InsertToDB(String student_num, String learn_time,
                                      String credit, String course_id,
                                      String course_name, String course_type,
                                      String teachers, String time_place,
                                      List<Map<String, String>> time_place_singel) {
        // TODO Auto-generated method stub
        for (int i = 0; i < time_place_singel.size(); i++) {
            int key = Integer.parseInt(time_place_singel.get(i).get("key"));
            int week_id = (key - 1) / 42 + 1;
            int week_day = (key - 1) % 42 / 6 + 1;
            int lesson_no = (key - 1) % 42 % 6 + 1;
            String place = time_place_singel.get(i).get("place");
            String time = getCourseTime(lesson_no);
            String weeks = time_place_singel.get(i).get("time");

            String sql_sentence = "insert into Course_info values(" +
                    (key++) + "," + student_num + "," + learn_time + "," + credit + "," +
                    week_id + "," + week_day + "," + lesson_no + "," + course_id +
                    ",\'" + course_name + "\',\'" + course_type + "\',\'" + teachers + "\',\'" + time_place +
                    "\',\'" + place + "\',\'" + time + "\',\'" + weeks + "\')";
            course_db.execSQL(sql_sentence);
        }
        return true;
    }

    private static String getCourseTime(int course_no) {
        switch (course_no) {
            case 1:
                return "8:00-9:35";
            case 2:
                return "9:55-11:30";
            case 3:
                return "13:30-15:05";
            case 4:
                return "15:20-16:55";
            case 5:
                return "17:10-18:45";
            case 6:
                return "19:30-21:05";
        }
        return "8:00-9:35";
    }


    private static String getStringByKey(String key, JSONObject data, boolean return_str) {
        Object value = null;
        try {
            value = data.get(key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = value.toString();
        if (s.equals("null") || s.isEmpty()) {
            if (return_str) {
                return "";
            } else {
                return "0";
            }
        }
        return s;
    }

    private static List<Map<String, String>> addSKSJDD(JSONObject jsonObj) {
        // TODO Auto-generated method stub
        List<Map<String, String>> sk = new ArrayList<>();
        Iterator it = jsonObj.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String[] value;
            try {
                value = toStringArray(jsonObj.get(key));
                if (value != null && value.length == 2) {
                    Map<String, String> list = new HashMap<>();
                    list.put("key", key);
                    list.put("place", value[0]);
                    list.put("time", value[1]);
                    sk.add(list);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return sk;
    }

    private static String[] toStringArray(Object object) {
        // TODO Auto-generated method stub
        String array[] = object.toString().split("\"");
        if (array.length == 5) {
            return new String[]{array[1], array[3]};
        }
        return null;
    }

    private static String addJSM(JSONArray jsonArray) {
        // TODO Auto-generated method stub
        String teach_name = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject TEACH = jsonArray.getJSONObject(i);
                teach_name += (i == 0 ? TEACH.get("JSM") : "&" + TEACH.get("JSM"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return teach_name;
    }
}	
