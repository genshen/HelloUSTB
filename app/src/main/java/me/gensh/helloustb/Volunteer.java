package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.gensh.fragment.VolunteerHomeFragment;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;
import me.gensh.utils.NetWorkActivity;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;

public class Volunteer extends LoginNetworkActivity {
    Boolean isLogin = false;
//    String passFileName, account, password;
//    Boolean canWrite = false;

    GoogleProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progress_bar);
        setErrorHandler(new NetWorkActivity.ErrorHandler() {
            @Override
            public void onPasswordError() {
                dismissProgressDialog();
                setVolunteerMessage(R.string.errorPassword, R.string.login_vol_button_again);
            }

            @Override
            public void onTimeoutError() {
                dismissProgressDialog();
                setVolunteerMessage(R.string.connectionTimeout, R.string.login_vol_button_again);
            }
        });

        final LoginDialog vol_login = new LoginDialog(LoginDialog.LoginVol);
        Login(vol_login, "VOL", 0x401);
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
                Intent search_intent = new Intent(this, VolunteerSearch.class);
                startActivity(search_intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void volClick(View view) {
        switch (view.getId()) {
            case R.id.for_detail:
                get(getString(R.string.my_volunteer_list), "VOL", 0x403, 16, "utf-8", true);
                break;
            case R.id.vol_login_button:
                final LoginDialog vol_login = new LoginDialog(LoginDialog.LoginVol);
                Login(vol_login, "VOL", 0x401);
                break;
        }
    }

    private void setVolunteerMessage(int msg_text, int msg_btn) {
        Button btn = findViewById(R.id.vol_login_button);
        TextView text = findViewById(R.id.volunteer_message);
        if (btn != null && text != null) {
            btn.setText(msg_btn);
            text.setText(msg_text);
        } else {
            Toast.makeText(this, msg_text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void RequestResultHandler(int what, ArrayList<String> data) {
        switch (what) {
            case 0x401:    //志愿者服务网登录成功
//						Toast.makeText(Volunteer.this, str_msg, Toast.LENGTH_SHORT).show();
                isLogin = true;
                savePasswordToLocal();
                get(getString(R.string.volunteer_home), "VOL", 0x402, 15, "utf-8", false);
                break;
            case 0x402://home of volunteer home Page;
                dismissProgressDialog();
                ((RelativeLayout) findViewById(R.id.volunteer_container)).removeAllViews();

                VolunteerHomeFragment fragment_volunteer_home = VolunteerHomeFragment.newInstance(data);
                getFragmentManager().beginTransaction().replace(R.id.volunteer_container, fragment_volunteer_home).commit();
                break;
            case 0x403:
                dismissProgressDialog();
                Bundle vol_list = new Bundle();
                vol_list.putStringArrayList("list", data);
                Intent list = new Intent(Volunteer.this, MyVolunteerList.class);
                list.putExtras(vol_list);
                startActivity(list);
            default:
        }
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
    }
}
