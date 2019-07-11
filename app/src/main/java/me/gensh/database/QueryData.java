package me.gensh.database;

import android.database.Cursor;

import me.gensh.utils.BasicDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryData {

    //todo maybe we have a better implementation for this method.
    public static boolean haveCoursesImported(DB session) {
        return session.getTimetableDao().getNumberOfRows() != 0;
    }

    public static List<HashMap<String, Object>> getTodayCourse(DB session, int week_num) {
        List<HashMap<String, Object>> mList = new ArrayList<>();
        String[] key = {DBTimetable.TimetableInfo._ID, DBTimetable.TimetableInfo.WEEK_DAY,
                DBTimetable.TimetableInfo.LESSON_NO, DBTimetable.TimetableInfo.COURSE_ID,
                DBTimetable.TimetableInfo.TIME, DBTimetable.TimetableInfo.COURSE_NAME,
                DBTimetable.TimetableInfo.PLACE, DBTimetable.TimetableInfo.TEACHERS};
        Cursor cursor = session.getTimetableDao().loadRawTimetable(week_num, BasicDate.getWeek());

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
    public static List<DBTimetable> getSomedayCourse(DB session, int position) {
        //select * from course_info  where week_day = position{val}
        return session.getTimetableDao().querySomedayCourse(position);
    }

    public static DBTimetable getCourseById(DB session, long id) {
        //select * from course_info  where _id = id{val}
        try {
            List<DBTimetable> tbs = session.getTimetableDao().getUniqueTimetable(id);
            if (tbs.size() != 1) { // make it unique
                return null;
            }
            return tbs.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static DBAccounts queryAccountByType(DB session, int type) {
        try {
            List<DBAccounts> accounts = session.getAccountDao().getUniqueAccountByTag(type);
            if (accounts.size() != 1) {
                return null;
            } else {
                return accounts.get(0);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static List<DBAccounts> queryAllAccount(DB session) {
        return session.getAccountDao().listAll();
    }

    public static boolean hasAccount(DB session, int type) {
        return queryAccountByType(session, type) != null;
    }

}
