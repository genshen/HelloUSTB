package com.holo.helloustb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.holo.sdcard.SdCardPro;
import com.holo.sdcard.Update;

public class StartActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x220://不是新程序，但是一天的第一次打开
                    Intent newactivity = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(newactivity);
                    finish();
                    break;
                case 0x221://新程序
                    Intent guidActivity = new Intent(StartActivity.this, GuideActivity.class);
                    startActivity(guidActivity);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        View mContentView = findViewById(R.id.fullscreen_content);
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

//my code
        SdCardPro.checkDirExit();
        //新版本向下兼容
        int versionCode = 0;
        String versionName = null;
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final boolean versionSame = Update.Renew(versionCode, versionName);
        final boolean todayFirst = Update.compareDate();

        new Thread(new Runnable() {
            public void run() {
                try {
                    if (!versionSame || !todayFirst) {    //如果版本不相同或者今天第一次打开
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }

                if (versionSame) {
                    handler.sendEmptyMessage(0x220);
                } else {
                    handler.sendEmptyMessage(0x221);
                }
            }

        }).start();
    }


}
