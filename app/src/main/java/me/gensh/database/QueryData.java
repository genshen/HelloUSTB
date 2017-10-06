package me.gensh.database;

import android.database.Cursor;

import me.gensh.utils.BasicDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryData {

    //todo maybe we have a better implementation  for this method.
    public static boolean haveCoursesImported(DaoSession session) {
        return session.getDBTimetableDao().count() != 0;
    }

    public static List<HashMap<String, Object>> getTodayCourse(DaoSession session, int week_num) {
        List<HashMap<String, Object>> mList = new ArrayList<>();
        String[] key = {DBTimetable.TimetableInfo._ID, DBTimetable.TimetableInfo.WEEK_DAY,
                DBTimetable.TimetableInfo.LESSON_NO, DBTimetable.TimetableInfo.COURSE_ID,
                DBTimetable.TimetableInfo.TIME, DBTimetable.TimetableInfo.COURSE_NAME,
                DBTimetable.TimetableInfo.PLACE, DBTimetable.TimetableInfo.TEACHERS};
        String sql = "select * from  " + DBTimetable.TimetableInfo.TABLE_NAME + " where " + DBTimetable.TimetableInfo.WEEK_ID + " >> " +
                week_num + "&1 = 1 and " + DBTimetable.TimetableInfo.WEEK_DAY + " = " + BasicDate.getWeek() + " order by " + DBTimetable.TimetableInfo.LESSON_NO;
        Cursor cursor = session.getDatabase().rawQuery(sql, null);

        cursor.moveToFirst();
        int length = key.length;
        while (!cursor.isAfterLast()) {
            HashMap<String, Object> listItem = new HashMap<>();
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    listItem.put(key[i], cursor.getLong(cursor.getColumnIndex(key[i])));
                } else if (i == 2) {
                    listItem.put(key[i], cursor.getInt(cursor.getColumnIndex(key[i])) + 1);
                } else if (i < 4) {
                    listItem.put(key[i], cursor.getInt(cursor.getColumnIndex(key[i])));
                } else {
                    listItem.put(key[i], cursor.getString(cursor.getColumnIndex(key[i])));
                }
            }
            mList.add(listItem);
            cursor.moveToNext();
        }
        cursor.close();
        return mList;
    }

    //获得某一天的课表
    public static List<DBTimetable> getSomedayCourse(DaoSession session, int position) {
        //select * from course_info  where week_day = position{val}
        return session.getDBTimetableDao().queryBuilder().where(DBTimetableDao.Properties.WeekDay.eq(position)).list();
    }

    public static DBTimetable getCourseById(DaoSession session, long id) {
        //select * from course_info  where _id = id{val}
        try {
            return session.getDBTimetableDao().queryBuilder().where(DBTimetableDao.Properties.Id.eq(id)).uniqueOrThrow();
        } catch (Exception e) {
            return null;
        }
    }

    public static DBAccounts queryAccountByTag(DaoSession session, int tag) {
        try {
            return session.getDBAccountsDao().queryBuilder().where(DBAccountsDao.Properties.Tag.eq(tag)).uniqueOrThrow();  //can also be null
        } catch (Exception e) {
            return null;
        }
    }

    public static List<DBAccounts> queryAllAccount(DaoSession session) {
        return session.getDBAccountsDao().queryBuilder().list();
    }

    public static boolean hasAccount(DaoSession session, int tag) {
        return queryAccountByTag(session, tag) != null;
    }

}
