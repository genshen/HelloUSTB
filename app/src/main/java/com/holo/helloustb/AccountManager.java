package com.holo.helloustb;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
                new AlertDialog.Builder(this)
                        .setTitle(R.string.logout_ele)
                        .setMessage(R.string.ensure_logout_ele)
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_ele_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_ele_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_e:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.logout_e)
                        .setMessage(R.string.ensure_logout_e)
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_e_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_e_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_network:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.logout_network)
                        .setMessage(R.string.ensure_logout_net)
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (SdCardPro.delFile("/Myustb/Pass_store/sch_net_pass.ustb")) {
                                    Toast.makeText(AccountManager.this, R.string.logout_net_success, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
                break;
            case R.id.logout_volunteer:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.logout_volunteer)
                        .setMessage(R.string.logout_volunteer)
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
