package me.gensh.helloustb;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import me.gensh.fragments.CampusNetworkFragment;
import me.gensh.fragments.ErrorFragment;
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
                Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x101);  //todo something goes wrong after canceling Login,showing a blank page.
            }
        });

        Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x101);
        setErrorHandler(new ErrorHandler() {
            @Override
            public void onPasswordError() {
                dismissProgressDialog();
                errorContainer.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.error_net_sign_in_password);
                Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTimeoutError() {
                dismissProgressDialog();
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
            case 0x101:  //login to 202.204.48.66 success feedback;  post
                savePasswordToLocal();
                Toast.makeText(getBaseContext(), R.string.net_sign_in_success, Toast.LENGTH_LONG).show();
                get(getString(R.string.sch_net), "NET", 0x102, 8, "GB2312", false);
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
