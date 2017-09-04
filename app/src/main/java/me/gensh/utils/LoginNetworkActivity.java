package me.gensh.utils;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import me.gensh.helloustb.R;
import me.gensh.sdcard.SdCardPro;

/**
 * Created by 根深 on 2016/7/13.
 */
public abstract class LoginNetworkActivity extends NetWorkActivity {
    protected static String username, password;
    static String passFileName;    //发送post请求之前传给变量,如果密码正确,则保存至本地
    boolean canWrite = false;

    public void Login(final LoginDialog ld, final String tag, final int feedback) {
        passFileName = ld.passFileName;
        if (!SdCardPro.fileIsExists(ld.passFileName)) {
            canWrite = true;
            final View enter = getLayoutInflater().inflate(R.layout.dialog_enter, null);
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
                                post(LoginNetworkActivity.this.getString(ld.post_address), tag, feedback, ld.verify_id, "GB2312", ld.post_params, true);
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            canWrite = false;
            String myaccount[] = StrUtils.ReadWithEncryption(passFileName).split("@");
            username = myaccount[0];
            password = myaccount[1];
            ld.setAccount(username, password);
            post(this.getString(ld.post_address), tag, feedback, ld.verify_id, "GB2312", ld.post_params, true);
        }
    }

    public void savePasswordToLocal() {
        if (canWrite) {
            SdCardPro.checkDirExit();
            StrUtils.WriteWithEncryption(username + "@" + password, passFileName);
        }
    }
}
