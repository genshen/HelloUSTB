package me.gensh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import me.gensh.utils.BasicDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gensh on 2015/11/20.
 */

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
            for (int i = data.length() - 1; i >= 0; i--) {
                JSONObject course = data.getJSONObject(i);
                if (course != null) {
                    if (!InsertToDB(getValueByKey("SKRS", course, false),//上课人数
                            getValueByKey("XS", course, false),//学时
                            getValueByKey("XF", course, false),//学分
                            getValueByKey("ID", course, false),//ID
                            getValueByKey("KCM", course, true),//课程名称
                            getValueByKey("KCLBM", course, true),//课程类型
                            addJSM(course.getJSONArray("JSM")),    //教师
                            getValueByKey("SKSJDDSTR", course, true),//上课时间地点
                            addSKSJDD(course.getJSONObject("SKSJDD")) //上课详细时间地点
                    )) {
                        return false;
                    }
                    if (!addPTK(course.getJSONArray("PTK"))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    //配套课(如实验课)
    private boolean addPTK(JSONArray ptks) {
        try {
            for (int i = ptks.length() - 1; i >= 0; i--) {
                JSONObject ptk = ptks.getJSONObject(i);
                if (ptk != null) {
                    if (!InsertToDB(getValueByKey("SKRS", ptk, false),//上课人数
                            getValueByKey("XS", ptk, false),//学时
                            getValueByKey("XF", ptk, false),//学分
                            getValueByKey("ID", ptk, false),//ID
                            getValueByKey("KCM", ptk, true),//课程名称
                            "未知",     //课程类型 null
                            addJSM(ptk.getJSONArray("JSM")),    //教师
                            getValueByKey("SKSJDDSTR", ptk, true),//上课时间地点
                            addSKSJDD(ptk.getJSONObject("SKSJDD")) //上课详细时间地点
                    )) {
                        return false;
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean InsertToDB(String student_num, String learn_time,
                               String credit, String course_id,
                               String course_name, String course_type,
                               String teachers, String time_place,
                               HashMap<LessonPlaceTime, Integer> time_place_single) {
        for (Map.Entry<LessonPlaceTime, Integer> entry : time_place_single.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            LessonPlaceTime lesson = entry.getKey();
            int week_id = entry.getValue();
            String time = BasicDate.getCourseTime(lesson.lesson_no);

            String sql_sentence = "insert into Course_info values(" +
                    (key++) + "," + student_num + "," + learn_time + "," + credit + "," +
                    week_id + "," + lesson.week_day + "," + lesson.lesson_no + "," + course_id +
                    ",\'" + course_name + "\',\'" + course_type + "\',\'" + teachers + "\',\'" + time_place +
                    "\',\'" + lesson.class_place + "\',\'" + time + "\',\'" + lesson.weeks + "\')";
            course_db.execSQL(sql_sentence);
        }
        return true;
    }


    private static String getValueByKey(String key, JSONObject data, boolean return_str) {
        Object value;
        try {
            value = data.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (return_str) {
            return "";
        } else {
            return "0";
        }
    }

    private static HashMap<LessonPlaceTime, Integer> addSKSJDD(JSONObject jsonObj) {
        HashMap<LessonPlaceTime, Integer> lessonSet = new HashMap<>();
        Iterator it = jsonObj.keys();
        while (it.hasNext()) {
            try {
                String key = (String) it.next();
                JSONArray arr = jsonObj.getJSONArray(key);
                if (arr != null && arr.length() == 2) {
                    LessonPlaceTime lessonKey = new LessonPlaceTime(Integer.parseInt(key), arr.getString(0), arr.getString(1));
                    Integer val_obj = lessonSet.get(lessonKey);
                    int val = val_obj == null ? 0 : val_obj;
                    val = val | (1 << lessonKey.week_id); // eg: val = 001010 means we have course at week 2 and week 4
                    lessonSet.put(lessonKey, val);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lessonSet;
    }

    private static String[] toStringArray(Object object) {
        String array[] = object.toString().split("\"");
        if (array.length == 5) {
            return new String[]{array[1], array[3]};
        }
        return null;
    }

    private static String addJSM(JSONArray jsonArray) {
        String teach_name = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject TEACH = jsonArray.getJSONObject(i);
                teach_name += (i == 0 ? TEACH.get("JSM") : "&" + TEACH.get("JSM"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return teach_name;
    }

    public static class LessonPlaceTime {
        int week_id, week_day, lesson_no;
        String class_place, weeks;

        public LessonPlaceTime(int id, String class_place, String weeks) {
            week_id = (id - 1) / 42;
            week_day = (id - 1) % 42 / 6;
            lesson_no = (id - 1) % 42 % 6;
            this.class_place = class_place;
            this.weeks = weeks;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LessonPlaceTime &&
                    this.class_place.equals(((LessonPlaceTime) obj).class_place) &&
                    this.week_day == ((LessonPlaceTime) obj).week_day &&
                    this.lesson_no == ((LessonPlaceTime) obj).lesson_no;
        }

        @Override
        public int hashCode() {
            return class_place.hashCode() + 6 * week_day + lesson_no;
        }
    }
}	
