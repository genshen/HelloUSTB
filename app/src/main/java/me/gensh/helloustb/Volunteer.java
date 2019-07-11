package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;

import me.gensh.fragments.VolunteerHomeFragment;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;

public class Volunteer extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished {
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
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progress_bar);

        Login(LoginDialog.newInstanceForVolunteer(), Tags.VOLUNTEER, 0x401);
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
                attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.my_volunteer_list), Tags.VOLUNTEER,
                        0x403, Tags.GET.ID_VOLUNTEER_ACTIVITIES_LIST, HttpClients.CHARSET_BTF8, null, true);
                break;
            case R.id.vol_login_button:
                Login(LoginDialog.newInstanceForVolunteer(), Tags.VOLUNTEER, 0x401);
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
    public void onOk(int what, @NonNull ArrayList<String> data) {
        switch (what) {
            case 0x401:    //志愿者服务网登录成功
//						Toast.makeText(Volunteer.this, str_msg, Toast.LENGTH_SHORT).show();
                isLogin = true;
                savePassword();
                attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.volunteer_home), Tags.VOLUNTEER,
                        0x402, Tags.GET.ID_VOLUNTEER_USER_INFORMATION, HttpClients.CHARSET_BTF8, null, false);
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
    public void onPasswordError() {
        dismissProgressDialog();
        setVolunteerMessage(R.string.error_password, R.string.login_vol_button_again);
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        setVolunteerMessage(R.string.connection_timeout, R.string.login_vol_button_again);
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
        Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
    }

}
