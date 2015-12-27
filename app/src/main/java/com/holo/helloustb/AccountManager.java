package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.holo.sdcard.SdCardPro;

public class AccountManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoginClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.logout_ele:
                new MaterialDialog.Builder(this)
                        .title(R.string.logout_ele)
                        .content(getString(R.string.ensure_logout_ele))
                        .positiveText(R.string.alert_sure)
                        .negativeText(R.string.alert_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_ele_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_ele_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_e:
                new MaterialDialog.Builder(this)
                        .title(R.string.logout_e)
                        .content(getString(R.string.ensure_logout_e))
                        .positiveText(R.string.alert_sure)
                        .negativeText(R.string.alert_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_e_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_e_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_network:
                new MaterialDialog.Builder(this)
                        .title(R.string.logout_network)
                        .content(getString(R.string.ensure_logout_net))
                        .positiveText(R.string.alert_sure)
                        .negativeText(R.string.alert_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_net_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_net_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_volunteer:
                new MaterialDialog.Builder(this)
                        .title(R.string.logout_volunteer)
                        .content(getString(R.string.ensure_logout_volunteer))
                        .positiveText(R.string.alert_sure)
                        .negativeText(R.string.alert_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_vol_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_vol_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
        }
    }
}
