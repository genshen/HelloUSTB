package me.gensh.database;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TimetableDao {

    @Query("DELETE FROM " + Config.TIMETABLE_DB_TABLE_NAME + " WHERE 1")
    void deleteAll();

    @Query("DELETE FROM " + Config.TIMETABLE_DB_TABLE_NAME + " WHERE " + DBTimetable.TimetableInfo._ID + " = :id")
    void deleteByKey(long id);

    @Insert
    void insertTimetable(DBTimetable dbTimetable);

    @Update
    void updateTimetable(DBTimetable timetable);

    @Query("SELECT * FROM " + Config.TIMETABLE_DB_TABLE_NAME + " where " + DBTimetable.TimetableInfo._ID + " = :id LIMIT 1")
    List<DBTimetable> getUniqueTimetable(long id);

    @Query("SELECT * FROM  " + DBTimetable.TimetableInfo.TABLE_NAME + " WHERE "
            + DBTimetable.TimetableInfo.WEEK_ID + " >> :week_num&1 = 1 AND "
            + DBTimetable.TimetableInfo.WEEK_DAY + " = :week_day ORDER BY " + DBTimetable.TimetableInfo.LESSON_NO)
    Cursor loadRawTimetable(int week_num, int week_day);

    @Query("SELECT * FROM  " + DBTimetable.TimetableInfo.TABLE_NAME + " WHERE "
            + DBTimetable.TimetableInfo.WEEK_DAY + " = :weekday")
    List<DBTimetable> querySomedayCourse(int weekday);

    @Query("SELECT COUNT(*) FROM " + Config.TIMETABLE_DB_TABLE_NAME + " WHERE 1")
    int getNumberOfRows();
}
