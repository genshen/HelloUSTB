package com.holo.base;

import android.database.Cursor;

import com.holo.dataBase.CourseDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeTableList {

    ArrayList<ArrayList<Map<String, Object>>> course;
    private short MaxCourse = 6;

    public TimeTableList(Cursor cursor) {
        course = new ArrayList<>();
        for (short i = 0; i < MaxCourse; i++) {
            ArrayList<Map<String, Object>> c = new ArrayList<>();
            course.add(c);
        }

        if (cursor.moveToFirst()) {
            addCourse(cursor);
        }
        while (cursor.moveToNext()) {
            addCourse(cursor);
        }
    }

    private void addCourse(Cursor cursor) {
        int position = cursor.getInt(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.LESSON_NO));

        HashMap<String, Object> course_detail = new HashMap<>();
        course_detail.put(CourseDbHelper.CourseInfoTable._ID, cursor.getInt(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable._ID)));
       // course_detail.put("course_id", cursor.getString(cursor.getColumnIndex("course_id")));
        course_detail.put(CourseDbHelper.CourseInfoTable.COURSE_NAME, cursor.getString(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.COURSE_NAME)));
        course_detail.put(CourseDbHelper.CourseInfoTable.PLACE, cursor.getString(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.PLACE)));
        course_detail.put(CourseDbHelper.CourseInfoTable.TEACHERS, cursor.getString(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.TEACHERS)));
        course_detail.put(CourseDbHelper.CourseInfoTable.WEEKS, cursor.getString(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.WEEKS)));
        course_detail.put(CourseDbHelper.CourseInfoTable.COURSE_TYPE, cursor.getString(cursor.getColumnIndex(CourseDbHelper.CourseInfoTable.COURSE_TYPE)));

        (course.get(position)).add(course_detail);
    }

    public ArrayList<Map<String, Object>> getCourseList(int lesson_id) {
        return course.get(lesson_id);
    }

}
