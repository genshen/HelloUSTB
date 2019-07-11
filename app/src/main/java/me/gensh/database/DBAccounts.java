package me.gensh.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by gensh on 2017/9/22.
 */

@Entity(
        tableName = Config.ACCOUNT_DB_TABLE_NAME
)
public class DBAccounts {
    @PrimaryKey
    @ColumnInfo(name = AccountsInfo.ID)
    private Long id; //id

    @ColumnInfo(name = AccountsInfo.TYPE)
    int type;         //标记是哪个网站的密码

    @ColumnInfo(name = AccountsInfo.USERNAME)
    String username;   //用户名

    @ColumnInfo(name = AccountsInfo.PASSWORD_ENCRYPT)
    String passwordEncrypt;  //加密的密码

    @ColumnInfo(name = AccountsInfo.R)
    String r;

    public DBAccounts(Long id, int type, String username, String passwordEncrypt,
                      String r) {
        this.id = id;
        this.type = type;
        this.username = username;
        this.passwordEncrypt = passwordEncrypt;
        this.r = r;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordEncrypt() {
        return this.passwordEncrypt;
    }

    public void setPasswordEncrypt(String passwordEncrypt) {
        this.passwordEncrypt = passwordEncrypt;
    }

    public String getR() {
        return this.r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    final public static class AccountsInfo {
        final public static String ID = "_id";
        final public static String TYPE = "type";
        final public static String USERNAME = "username";
        final public static String PASSWORD_ENCRYPT = "password_encrypt";
        final public static String R = "r";
    }
}
