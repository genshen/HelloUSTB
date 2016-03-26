package com.holo.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 根深 on 2016/3/26.
 */
public class DeleteData {
    SQLiteDatabase course_db;
    public static CourseDbHelper course;
    final String TableName = CourseDbHelper.CourseInfoTable.TableName;

    public DeleteData(Context context) {
        course = new CourseDbHelper(context, 1);
        course_db = course.getReadableDatabase();
    }

    public void close() {
        course.close();
    }

    public void deleteCourseById(int id) {
        course_db.delete(TableName, CourseDbHelper.CourseInfoTable._ID + "=" + id, null);
    }
}
