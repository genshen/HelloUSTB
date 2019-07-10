package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Calendar;

import me.gensh.utils.VersionChecker;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        AppCompatTextView bottomText = findViewById(R.id.bottom_text);
        bottomText.setText(getString(R.string.bottom_text_format, Calendar.getInstance().get(Calendar.YEAR)));
//        View mContentView = findViewById(R.id.fullscreen_content);
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        VersionChecker mVersionChecker = new VersionChecker(this);
        mVersionChecker.check();
        mVersionChecker.waitFor(handler);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VersionChecker.VERSION_CHECKER_TO_MAIN_ACTIVITY://不是新程序，但是一天的第一次打开
                    Intent newIntent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(newIntent);
                    finish();
                    break;
                case VersionChecker.VERSION_CHECKER_TO_GUIDE_ACTIVITY://新程序
                    Intent guideActivity = new Intent(StartActivity.this, GuideActivity.class);
                    startActivity(guideActivity);
                    finish();
                    break;
            }
        }
    };

}
