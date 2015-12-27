package com.holo.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.holo.sdcard.SdCardPro;

/**
 * Created by 根深 on 2015/11/20.
 */
public class CourseDbHelper extends SQLiteOpenHelper {
    final String TableName = "Course_info";
    public static final String DATABASE_NAME = SdCardPro.getSDPath() + "/MyUstb/Data/Course/CoursTest.db";
    String SQL_CREATE_COURSE = "create table Course_info(_id integer primary key," +
            "student_num integer," +        //学生数目
            "learn_time integer," +        //学时
            "credit integer," +            //学分
            "week_id integer," +            //第几周
            "week_day integer," +        //周几
            "lesson_no integer," +        //第几节课
            "course_id integer," +        //课程ID
            "course_name varchar," +    //课程名称
            "course_type varchar," +        //课程类型
            "teachers varchar," +        //教师
            "time_place varchar," +        //时间地点
            "place varchar," +            //地点
            "times varchar," +            //上课时间
            "weeks varchar)";            //那几周都有课

    public CourseDbHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COURSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean isTableEmpty() {
        //  SELECT COUNT(*) FROM table_name
        String sql = "SELECT * FROM " + TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean empty = (cursor.getCount() == 0);
        cursor.close();
        return empty;
    }
}
