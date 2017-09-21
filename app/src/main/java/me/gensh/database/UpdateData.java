package me.gensh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import me.gensh.utils.BasicDate;

/**
 * Created by gensh on 2016/3/26.
 */
public class UpdateData {
    //todo
    public static boolean submitCourseInfoEdit(DaoSession session, long id, String name, String teachers, String place, String weeks,
                                               int week_id, int week_day, int lesson_id) {
        try {
            //select * from course_info  where _id = id{val}
            DBTimetable timetable = session.getDBTimetableDao().queryBuilder().where(DBTimetableDao.Properties.Id.eq(id)).uniqueOrThrow();
            timetable.setCourseName(name);
            timetable.setTeachers(teachers);
            timetable.setPlace(place);
            timetable.setWeeks(weeks);
            timetable.setWeekId(week_id);
            timetable.setWeekDay(week_day);
            timetable.setLessonNo(lesson_id);   //todo need update lesson time.
            session.getDBTimetableDao().update(timetable);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
