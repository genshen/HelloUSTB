package me.gensh.database;

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

    //DBAccount
    public static boolean deleteAccountByTag(DaoSession session, int tag) {
        session.getDBTimetableDao().queryBuilder().where(DBAccountsDao.Properties.Tag.eq(tag)).buildDelete().executeDeleteWithoutDetachingEntities();
        return true;
    }

    public static boolean deleteAccountById(DaoSession session, Long key) {
        session.getDBAccountsDao().deleteByKey(key);
        return true;
    }
}
