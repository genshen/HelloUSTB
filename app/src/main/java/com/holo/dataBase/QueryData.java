package com.holo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.holo.utils.BasicDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryData {
    SQLiteDatabase course_db;
    public CourseDbHelper course;
    final String TableName = CourseDbHelper.CourseInfoTable.TableName;

    public QueryData(Context context) {
        course = new CourseDbHelper(context, 1);
        course_db = course.getReadableDatabase();
    }

    public void close() {
        course.close();
    }

    public List<HashMap<String, Object>> getTodayCourse(int week_num) {
        List<HashMap<String, Object>> mList = new ArrayList<>();
        String[] key = {CourseDbHelper.CourseInfoTable._ID, CourseDbHelper.CourseInfoTable.WEEK_DAY,
                CourseDbHelper.CourseInfoTable.LESSON_NO, CourseDbHelper.CourseInfoTable.COURSE_ID,
                CourseDbHelper.CourseInfoTable.TIMES, CourseDbHelper.CourseInfoTable.COURSE_NAME,
                CourseDbHelper.CourseInfoTable.PLACE, CourseDbHelper.CourseInfoTable.TEACHERS};
        String sql = "select * from Course_info " + " where week_id >> " +
                week_num + "&1 = 1 and week_day = " + BasicDate.getWeek() + " order by lesson_no";
        Cursor cursor = course_db.rawQuery(sql, null);

        cursor.moveToFirst();
        int length = key.length;
        while (!cursor.isAfterLast()) {
            HashMap<String, Object> listitem = new HashMap<>();
            for (int i = 0; i < length; i++) {
                if (i == 2) {
                    listitem.put(key[i], cursor.getInt(cursor.getColumnIndex(key[i])) + 1);
                } else if (i < 4) {
                    listitem.put(key[i], cursor.getInt(cursor.getColumnIndex(key[i])));
                } else {
                    listitem.put(key[i], cursor.getString(cursor.getColumnIndex(key[i])));
                }
            }
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

    public HashMap<String, Object> getCourseById(int id) {
        String sql = "select * from course_info  where _id = " + id;
        Cursor cursor = course_db.rawQuery(sql, null);
        HashMap<String, Object> course_detail = new HashMap<>();

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            String[] key = {"student_num", "learn_time", "credit", "week_id", "week_day", "lesson_no", "course_id",
                    "course_name", "course_type", "teachers", "time_place", "place", "times", "weeks"};
            for (int i = 0; i < key.length; i++) {
                if (i < 7) {
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
