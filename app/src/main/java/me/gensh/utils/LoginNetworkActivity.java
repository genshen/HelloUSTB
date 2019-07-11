package me.gensh.utils;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import me.gensh.database.DBAccounts;
import me.gensh.database.QueryData;
import me.gensh.database.StoreData;
import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.helloustb.http.HttpClients;

/**
 * Created by genshen on 2016/7/13.
 */
public abstract class LoginNetworkActivity extends NetWorkActivity {
    protected String username, password;  // if one login does not finish, another new login can overwrite the username ,password and userType
    private int userTag;
    boolean canSave = false;

    public void Login(final LoginDialog ld, final String tag, final int feedback) {
        userTag = ld.userType;
        if (!QueryData.hasAccount(((MyApplication) getApplication()).getDaoSession(), userTag)) {
            canSave = true;
            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
            boolean numberOnly = pre.getBoolean(Const.Settings.KEY_STU_NO_NUMBER_ONLY, false);
            final View enter = getLayoutInflater().inflate(R.layout.dialog_sign_in, null);
            TextInputEditText account = enter.findViewById(R.id.account);
            if (numberOnly) {  //default is text type.
                //use different layout based on value of key KEY_STU_NO_NUMBER_ONLY
                account.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            new AlertDialog.Builder(this)
                    .setTitle(ld.dialog_title)
                    .setView(enter)
                    .setNegativeButton(R.string.alert_cancel, null)
                    .setPositiveButton(R.string.alert_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            username = ((TextView) enter.findViewById(R.id.account)).getText().toString();
                            password = ((TextView) enter.findViewById(R.id.pass)).getText().toString();
                            if (!username.isEmpty() && !password.isEmpty()) {
                                ld.setAccount(username, password);
                                attemptHttpRequest(HttpClients.HTTP_POST, LoginNetworkActivity.this.getString(ld.post_address),
                                        tag, feedback, ld.verify_id, HttpClients.CHARSET_BTF8, ld.post_params, true);//todo charset
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            canSave = false;
            DBAccounts account = QueryData.queryAccountByType(((MyApplication) getApplication()).getDaoSession(), userTag);
            assert account != null;  // account is not null
            String password = StringUtils.decryptWithIv(account.getPasswordEncrypt(), Base64.decode(account.getR(), Base64.DEFAULT));
            ld.setAccount(account.getUsername(), password);
            attemptHttpRequest(HttpClients.HTTP_POST, this.getString(ld.post_address),
                    tag, feedback, ld.verify_id, HttpClients.CHARSET_BTF8, ld.post_params, true); //todo charset
        }
    }

    public void savePassword() {
        if (canSave) {
            StoreData.storeAccount(((MyApplication) getApplication()).getDaoSession(), username, password, userTag);
            // todo: 2017/9/23 see{@link me.gensh.fragment.ELearningCategory}
        }
    }
}
