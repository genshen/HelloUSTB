package com.holo.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holo.account.LoginDialog;
import com.holo.base.StrPro;
import com.holo.fragment.VolunteerHomeFragment;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.holo.sdcard.SdCardPro;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.Map;

public class Volunteer extends AppCompatActivity {
    Boolean islogin = false;
    String passFileName, account, password;
    Boolean canWrite = false;

    GoogleProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_bar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        showLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.volunteer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.vol_search:
                Intent search_intent = new Intent(this,VolunteerSearch.class);
                startActivity(search_intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;

            //error password and timeout process
            if (data_info.code == DataInfo.ERROR_PASSWORD) {
                progress_bar.setVisibility(View.INVISIBLE);
                setVolunteerMessage(R.string.errorPassword, R.string.login_vol_button_again);
                return;
            } else if (data_info.code == DataInfo.TimeOut) {
                progress_bar.setVisibility(View.INVISIBLE);
                setVolunteerMessage(R.string.connectionTimeout, R.string.login_vol_button_again);
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            switch (msg.what) {
                case 0x401:    //志愿者服务网登录成功
//						Toast.makeText(Volunteer.this, str_msg, Toast.LENGTH_SHORT).show();
                    islogin = true;
                    savePass();
                    get(getString(R.string.volunteer_home), "VOL", 0x402, 15, "utf-8", false);
                    break;
                case 0x402://home of volunteer home Page;
                    progress_bar.setVisibility(View.INVISIBLE);
                    ((RelativeLayout) findViewById(R.id.volunteer_container)).removeAllViews();

                    VolunteerHomeFragment fragment_volunteer_home = VolunteerHomeFragment.newInstance(str_msg);
                    getFragmentManager().beginTransaction().replace(R.id.volunteer_container, fragment_volunteer_home).commit();
                    break;
                case 0x403:
                    progress_bar.setVisibility(View.INVISIBLE);
                    Bundle vol_list =new Bundle();
                    vol_list.putStringArrayList("list",str_msg);
                    Intent list=new Intent(Volunteer.this,MyVolunteerList.class);
                    list.putExtras(vol_list);
                    startActivity(list);
                default:
            }
        }

    };

    public void volClick(View view) {
        switch (view.getId()) {
            case R.id.for_detail:
                get(getString(R.string.my_volunteer_list),"VOL",0x403,16,"utf-8",true);
                break;
            case R.id.vol_login_button:
                showLogin();
                break;
        }
    }

    private void showLogin() {
//show login dialog，Or  use local username and password;
        final LoginDialog vol_login = new LoginDialog(LoginDialog.LoginVol);
        passFileName = vol_login.passFileName;
        final String title = getString(R.string.alert_title_volunteer);
        final String postAddress = getString(R.string.vol_login);
        final int feedback = 0x401;

        if (!SdCardPro.fileIsExists(passFileName)) {
            canWrite = true;
            final View enter = getLayoutInflater().inflate(R.layout.dialog_enter, null);

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setView(enter)
                    .setNegativeButton(R.string.alert_cancel, null)
                    .setPositiveButton(R.string.alert_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            account = ((TextView) enter.findViewById(R.id.account)).getText().toString();
                            password = ((TextView) enter.findViewById(R.id.pass)).getText().toString();
                            vol_login.setAccount(account, password);
                            post(postAddress, "VOL", feedback, vol_login.verify_id, "utf-8", vol_login.post_params, true);
                        }
                    })
                    .show();
        } else {
            String myaccount[] = StrPro.ReadWithEncryption(passFileName).split("@");
            account = myaccount[0];
            password = myaccount[1];
            vol_login.setAccount(account, password);
            post(postAddress, "VOL", feedback, vol_login.verify_id, "gb2312", vol_login.post_params, true);
        }
    }

    private void savePass() {
        if (canWrite) {
            SdCardPro.checkDirExit();
            StrPro.WriteWithEncryption(account + "@" + password, passFileName);
        }
    }

    private void setVolunteerMessage(int msg_text, int msg_btn) {
        Button btn = (Button) findViewById(R.id.vol_login_button);
        TextView text = (TextView) findViewById(R.id.volunteer_message);
        if (btn != null && text != null) {
            btn.setText(msg_btn);
            text.setText(msg_text);
        } else {
            Toast.makeText(this, msg_text, Toast.LENGTH_SHORT).show();
        }
    }

    // just used to login
    private void post(String url, String tag, int feedback, int id, String code, Map<String, String> post_params, Boolean progress) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (progress) progress_bar.setVisibility(View.VISIBLE);
            GetPostHandler.handlerPost(handler, url, tag, feedback, id, code, post_params);
        } else {
//            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            setVolunteerMessage(R.string.NoNetwork, R.string.login_vol_button_again);
        }
    }

    private void get(String url, String tag, int feedback, int id, String code, Boolean progress) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (progress) progress_bar.setVisibility(View.VISIBLE);
            GetPostHandler.handlerGet(handler, url, tag, feedback, id, code);
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

}
