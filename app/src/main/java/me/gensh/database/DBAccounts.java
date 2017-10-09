package me.gensh.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by gensh on 2017/9/22.
 */

@Entity(
        nameInDb = "accounts_dbs"
)
public class DBAccounts {
    @Id
    @Property(nameInDb = AccountsInfo.ID)
    private Long id; //id

    @Property(nameInDb = AccountsInfo.TYPE)
    int type;         //标记是哪个网站的密码

    @Property(nameInDb = AccountsInfo.USERNAME)
    String username;   //用户名

    @Property(nameInDb = AccountsInfo.PASSWORD_ENCRYPT)
    String passwordEncrypt;  //加密的密码

    @Property(nameInDb = AccountsInfo.R)
    String r;


    @Generated(hash = 32811746)
    public DBAccounts(Long id, int type, String username, String passwordEncrypt,
            String r) {
        this.id = id;
        this.type = type;
        this.username = username;
        this.passwordEncrypt = passwordEncrypt;
        this.r = r;
    }

    @Generated(hash = 640312663)
    public DBAccounts() {
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
