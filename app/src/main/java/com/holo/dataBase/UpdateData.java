package com.holo.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.holo.base.BaseDate;

/**
 * Created by 根深 on 2016/3/26.
 */
public class UpdateData {
    SQLiteDatabase course_db;
    public static CourseDbHelper course;
    final String TableName = CourseDbHelper.CourseInfoTable.TableName;

    public UpdateData(Context context) {
        course = new CourseDbHelper(context, 1);
        course_db = course.getReadableDatabase();
    }

    public void close() {
        course.close();
    }

    public void submitCourseInfoEdit(int id, String name, String teachers, String place, String weeks,
                                     int week_id, int week_day, int lesson_id) {
        ContentValues cv = new ContentValues();
        cv.put(CourseDbHelper.CourseInfoTable.COURSE_NAME, name);
        cv.put(CourseDbHelper.CourseInfoTable.TEACHERS, teachers);
        cv.put(CourseDbHelper.CourseInfoTable.PLACE, place);
        cv.put(CourseDbHelper.CourseInfoTable.WEEKS, weeks);
        cv.put(CourseDbHelper.CourseInfoTable.WEEK_ID, week_id);
        cv.put(CourseDbHelper.CourseInfoTable.WEEK_DAY, week_day);
        cv.put(CourseDbHelper.CourseInfoTable.LESSON_NO, lesson_id);
        cv.put(CourseDbHelper.CourseInfoTable.TIMES, BaseDate.getCourseTime(lesson_id));
        course_db.update(TableName, cv, CourseDbHelper.CourseInfoTable._ID + "=" + id, null);
    }
}
