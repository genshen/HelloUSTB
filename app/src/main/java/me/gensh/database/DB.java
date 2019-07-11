package me.gensh.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DBAccounts.class, DBTimetable.class}, version = 2)
public abstract class DB extends RoomDatabase {

    public abstract AccountDao getAccountDao();

    public abstract TimetableDao getTimetableDao();
}
