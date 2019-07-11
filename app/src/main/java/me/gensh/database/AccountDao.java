package me.gensh.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AccountDao {

    @Query("DELETE FROM " + Config.ACCOUNT_DB_TABLE_NAME + " WHERE " + DBAccounts.AccountsInfo.ID + " = :id")
    void deleteByKey(long id);

    @Query("DELETE FROM " + Config.ACCOUNT_DB_TABLE_NAME + " WHERE " + DBAccounts.AccountsInfo.TYPE + " = :type")
    void deleteByTag(int type);

    @Insert
    void insertAccount(DBAccounts account);

    @Update
    void updateAccount(DBAccounts account);

    @Query("SELECT * FROM " + Config.ACCOUNT_DB_TABLE_NAME + " where " + DBAccounts.AccountsInfo.TYPE + " = :type LIMIT 1")
    List<DBAccounts> getUniqueAccountByTag(long type);

    @Query("SELECT * FROM " + Config.ACCOUNT_DB_TABLE_NAME)
    List<DBAccounts> listAll();
}
