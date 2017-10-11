package me.gensh.helloustb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;

import me.gensh.fragments.CampusNetworkFragment;
import me.gensh.fragments.ErrorFragment;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;

public class NetWorkSignIn extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished {
    GoogleProgressBar progressBar;
    AppCompatImageView reload;
    AppCompatTextView errorMessage;
    RelativeLayout errorContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_sign_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        reload = findViewById(R.id.reload);
        errorMessage = findViewById(R.id.error_message);
        errorContainer = findViewById(R.id.error_container);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorContainer.setVisibility(View.INVISIBLE);
                Login(LoginDialog.newInstanceForNetwork(), Tags.NETWORK, 0x101);
                //todo redesign reload button
                //todo something goes wrong after canceling Login,showing a blank page.
            }
        });

        Login(LoginDialog.newInstanceForNetwork(), Tags.NETWORK, 0x101);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        switch (what) {
            case 0x101:  //login to 202.204.48.66 success feedback;  post
                savePassword();
                Toast.makeText(getBaseContext(), R.string.net_sign_in_success, Toast.LENGTH_LONG).show();
                attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.sch_net), Tags.NETWORK, 0x102, Tags.GET.ID_NETWORK_INFO, HttpClients.CHARSET_GB2312, null, false);
                break;
            case 0x102:
                dismissProgressDialog();
                errorContainer.setVisibility(View.GONE);
                Bundle argument = new Bundle();
                argument.putStringArrayList("Campus_network_information", data);
                CampusNetworkFragment fragment = CampusNetworkFragment.newInstance(data);
                fragment.setArguments(argument);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        errorContainer.setVisibility(View.VISIBLE);
        errorMessage.setText(R.string.error_net_sign_in_password);
        Toast.makeText(getBaseContext(), R.string.error_password, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        errorContainer.setVisibility(View.VISIBLE);
        errorMessage.setText(R.string.error_net_sign_in_connection_timeout);
        Toast.makeText(getBaseContext(), R.string.connection_timeout, Toast.LENGTH_LONG).show();
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
        ErrorFragment fragment = ErrorFragment.newInstance("ERROR_NO_Connection");
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
