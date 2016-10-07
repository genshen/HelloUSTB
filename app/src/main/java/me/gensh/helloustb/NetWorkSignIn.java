package me.gensh.helloustb;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import me.gensh.fragment.CampusNetworkFragment;
import me.gensh.fragment.ErrorFragment;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;

public class NetWorkSignIn extends LoginNetworkActivity {
    GoogleProgressBar progressBar;
    AppCompatImageView reload;
    AppCompatTextView errorMessage;
    RelativeLayout errorContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        reload = (AppCompatImageView) findViewById(R.id.reload);
        errorMessage = (AppCompatTextView) findViewById(R.id.error_message);
        errorContainer = (RelativeLayout) findViewById(R.id.error_container);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                errorContainer.setVisibility(View.INVISIBLE);
                Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x101);
            }
        });

        Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x101);
        setErrorHandler(new ErrorHandler() {
            @Override
            public void onPasswordError() {
                progressBar.setVisibility(View.INVISIBLE);
                errorContainer.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.error_net_sign_in_password);
                Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTimeoutError() {
                progressBar.setVisibility(View.INVISIBLE);
                errorContainer.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.error_net_sign_in_connection_timeout);
                Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
            }
        });
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
    public void RequestResultHandler(int what, ArrayList<String> data) {
        switch (what) {
            case 0x101:
                savePass();
                Toast.makeText(getBaseContext(), R.string.net_sign_in_success, Toast.LENGTH_LONG).show();
                get(getString(R.string.sch_net), "NET", 0x102, 8, "GB2312", false);
                break;
            case 0x102:
                progressBar.setVisibility(View.GONE);
                errorContainer.setVisibility(View.GONE);
                Bundle argument = new Bundle();
                argument.putStringArrayList("Campus_network_information", data);
                CampusNetworkFragment fragment = new CampusNetworkFragment();
                fragment.setArguments(argument);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
        }
    }

    @Override
    public void setProcessDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkDisabled() {
        ErrorFragment fragment = ErrorFragment.newInstance("ERROR_NO_Connection");
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
