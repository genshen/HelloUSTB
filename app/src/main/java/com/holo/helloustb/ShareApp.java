package com.holo.helloustb;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ShareApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_share_app);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    public void clickHander(View v)
    {
        int id = v.getId();
        switch(id)
        {
            case R.id.send_to_friend:
                shareMe();
                break;
            case R.id.er_wei_ma:
                ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(getString(R.string.download_link)); // ����
                Toast.makeText(this, R.string.haveCopied, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void shareMe() {
        // TODO Auto-generated method stub
        String appDir;
        try {
            appDir = getPackageManager().getApplicationInfo(this.getPackageName(), 0).sourceDir;
            appDir = "file://" + appDir;
            Uri uri = Uri.parse(appDir);
            // 发送
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "发送"));
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, R.string.no_app_exist, Toast.LENGTH_LONG).show();
        }
    }
}
