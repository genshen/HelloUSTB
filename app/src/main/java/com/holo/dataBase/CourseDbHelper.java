package com.holo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.holo.sdcard.SdCardPro;

/**
 * Created by 根深 on 2015/11/20.
 */
public class CourseDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = SdCardPro.getSDPath() + "/MyUstb/Data/Course/Course_v101.db";
    String SQL_CREATE_COURSE = "create table " + CourseInfoTable.TableName +
            "(_id integer primary key," +   //id
            "student_num integer," +        //学生数目
            "learn_time integer," +        //学时
            "credit integer," +            //学分
            "week_id integer," +           //哪些几周,某一为为1表示该周有课
            "week_day integer," +         //周几
            "lesson_no integer," +        //第几节课
            "course_id integer," +        //课程ID
            "course_name varchar," +      //课程名称
            "course_type varchar," +      //课程类型
            "teachers varchar," +         //教师
            "time_place varchar," +        //时间地点
            "place varchar," +            //地点
            "times varchar," +            //上课时间
            "weeks varchar)";            //哪几周都有课

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
        String sql = "SELECT * FROM " + CourseInfoTable.TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        boolean empty = (cursor.getCount() == 0);
        cursor.close();
        return empty;
    }

    public class CourseInfoTable {
        public static final String TableName = "Course_info";

        public static final String _ID = "_id";
        public static final String STUDENT_NUM = "student_num";
        public static final String LEARN_TIME = "learn_time";
        public static final String CREDIT = "credit";
        public static final String WEEK_ID = "week_id";
        public static final String WEEK_DAY = "week_day";
        public static final String LESSON_NO = "lesson_no";
        public static final String COURSE_ID = "course_id";
        public static final String COURSE_NAME = "course_name";
        public static final String COURSE_TYPE = "course_type";
        public static final String TEACHERS = "teachers";
        public static final String TIME_PLACE = "time_place";
        public static final String PLACE = "place";
        public static final String TIMES = "times";
        public static final String WEEKS = "weeks";
    }
}
