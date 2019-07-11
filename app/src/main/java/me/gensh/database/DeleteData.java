package me.gensh.database;

/**
 * Created by gensh on 2016/3/26.
 */
public class DeleteData {

    //todo delete by term
    public static void clearAllTimetable(DB session) {
        session.getTimetableDao().deleteAll();
        //queryBuilder().where(DBTimeTableDao.Properties.Id.eq(1)).buildDelete().executeDeleteWithoutDetachingEntities();
        //  sql = "DELETE FROM " + TableName + " WHERE 1";
    }

    public static void deleteCourseById(DB session, long id) {
        session.getTimetableDao().deleteByKey(id);
//        course_db.delete(TableName, CourseDbHelper.CourseInfoTable._ID + "=" + id, null);
    }

    //DBAccount
    public static boolean deleteAccountByTag(DB session, int tag) {
        session.getAccountDao().deleteByTag(tag);
        return true;
    }

    public static boolean deleteAccountById(DB session, Long key) {
        session.getAccountDao().deleteByKey(key);
        return true;
    }
}
