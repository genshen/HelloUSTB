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

    @Property(nameInDb = AccountsInfo.TAG)
    int tag;         //标记是哪个网站的密码

    @Property(nameInDb = AccountsInfo.USERNAME)
    String username;   //用户名

    @Property(nameInDb = AccountsInfo.PASSWORD_ENCRYPT)
    String passwordEncrypt;  //加密的密码

    @Property(nameInDb = AccountsInfo.R)
    String r;


    @Generated(hash = 1722719335)
    public DBAccounts(Long id, int tag, String username, String passwordEncrypt,
                      String r) {
        this.id = id;
        this.tag = tag;
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

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
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

    final public static class AccountsInfo {
        final public static String ID = "_id";
        final public static String TAG = "tag";
        final public static String USERNAME = "username";
        final public static String PASSWORD_ENCRYPT = "password_encrypt";
        final public static String R = "r";
    }
}
