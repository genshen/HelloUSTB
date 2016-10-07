package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    @Override
    public void onResume() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int drawable;
        if (hour < 6) {
            drawable = R.drawable.nav_night;
        } else if (hour < 11) {
            drawable = R.drawable.nav_morning;
        } else if (hour < 18) {
            drawable = R.drawable.nav_afternoon;
        } else {
            drawable = R.drawable.nav_night;
        }
        ((ImageView) findViewById(R.id.account_background)).setImageResource(drawable);
        super.onResume();
    }

    public void AccountClick(View v) {
        switch (v.getId()) {
            case R.id.account_ac:
                Intent account_intent = new Intent(this, AccountManager.class);
                startActivity(account_intent);
                break;
            case R.id.account_msg:
                break;
            case R.id.account_collection:
                break;

        }
    }
}
