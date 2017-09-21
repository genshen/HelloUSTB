package me.gensh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by gensh on 2016/3/26.
 */
public class DeleteData {

    //todo delete by term
    public static void clearAllTimetable(DaoSession session) {
        session.getDBTimetableDao().deleteAll();
        //queryBuilder().where(DBTimeTableDao.Properties.Id.eq(1)).buildDelete().executeDeleteWithoutDetachingEntities();
        //  sql = "DELETE FROM " + TableName + " WHERE 1";
    }

    public static void deleteCourseById(DaoSession session, long id) {
        session.getDBTimetableDao().deleteByKey(id);
//        course_db.delete(TableName, CourseDbHelper.CourseInfoTable._ID + "=" + id, null);
    }
}
