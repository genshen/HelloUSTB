package com.holo.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.holo.base.BasicDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

;

public class QueryData {
    SQLiteDatabase course_db;
    public CourseDbHelper course;
    final String TableName = "Course_info";

    public QueryData(Context context) {
        course = new CourseDbHelper(context, 1);
        course_db = course.getReadableDatabase();
    }

    public void close() {
        course.close();
    }

    public List<HashMap<String, Object>> getTodayCourse(int week_num) {
        List<HashMap<String, Object>> mList = new ArrayList<>();
        String[] key = {"week_day", "lesson_no", "course_id", "times", "course_name", "place", "teachers"};
        int length = 7;

        String sql = "select * from Course_info " +
                " where week_id >> "+ week_num + "&1 = 1 and week_day = " + BasicDate.getWeek() +" order by lesson_no";
        Cursor cursor = course_db.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HashMap<String, Object> listitem = new HashMap<>();
            for (int i = 0; i < length; i++) {
                listitem.put(key[i], cursor.getString(cursor.getColumnIndex(key[i])));
            }
            listitem.put("lesson_no", cursor.getInt(cursor.getColumnIndex("lesson_no"))+1); //todo ,have done! re save  lesson_no 0-5 -> 1-6
            mList.add(listitem);
            cursor.moveToNext();
        }
        cursor.close();
        return mList;
    }

    //获得某一天的课表
    public Cursor getSomedayCourse(int position) {
        String sql;
        sql = "select * from course_info  where week_day = " + position;
        return course_db.rawQuery(sql, null);
    }

    public  HashMap<String, Object> getCourseById(String id, int week_no, int position) {
        String sql = "select * from course_info  where course_id = " + id + " limit 1";
        Cursor cursor = course_db.rawQuery(sql, null);
        HashMap<String, Object> course_detail = new HashMap<>();

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            String[] key = {"student_num", "learn_time", "credit", "course_name", "course_type",
                    "teachers", "time_place", "place", "times", "weeks"};
            for (int i = 0; i < key.length; i++) {
                if (i < 3) {
                    course_detail.put(key[i], cursor.getInt(cursor.getColumnIndex(key[i])));
                } else {
                    course_detail.put(key[i], cursor.getString(cursor.getColumnIndex(key[i])));
                }
            }
            cursor.close();
            course_db.close();
            return course_detail;
        }
        cursor.close();
        course_db.close();
        return null;
    }
}
