package com.holo.helloustb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void	click_Hander(View view){
        int id=view.getId();
        switch(id){
            case R.id.general:
                Intent general=new Intent(this,GeneralSetting.class);
                startActivity(general);
                break;
            case R.id.file_manager:
                Intent file=new Intent(this,FileManager.class);
                startActivity(file);
                break;
            case R.id.about:
                Bundle data=new Bundle();
                data.putSerializable("url",getString(R.string.myHomePage));
                Intent browser=new Intent(this,Browser.class);
                browser.putExtras(data);
                startActivity(browser);
                break;
            case R.id.share_app:
                Intent share=new Intent(this,ShareApp.class);
                startActivity(share);
                break;
            case R.id.setting_about:
                Intent about=new Intent(this,About.class);
                startActivity(about);
                break;
        }
    }
}
