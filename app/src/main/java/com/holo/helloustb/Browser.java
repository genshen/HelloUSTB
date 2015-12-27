package com.holo.helloustb;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.holo.sdcard.SdCardPro;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Browser extends AppCompatActivity {
    View container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_PROGRESS);//这句话只能写在这里，写在下面会出错....郁闷，，，
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.borwser_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        container = findViewById(R.id.browser_container);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = getIntent();
        String MyUrl = (String) intent.getSerializableExtra("url");
        web = (WebView) findViewById(R.id.web);
        web.setWebChromeClient(new chromeClient());
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings websetting = web.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setSupportZoom(true);//设置放大缩小
        websetting.setBuiltInZoomControls(true);//设置放大缩小

        web.loadUrl(MyUrl);
        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                final String downloadURL = url;
                String[] FileSplit = url.split("/");
                final String FileName = FileSplit[FileSplit.length - 1];
                Snackbar.make(container, "正在下载文件" + FileName, Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Toast.makeText(Browser.this, , Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(downloadURL);//下载地址
                            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                            urlConn.setConnectTimeout(3000);
                            InputStream inputStream = urlConn.getInputStream();
                            SdCardPro.checkDirExit();
                            File resultFile = SdCardPro.writeToSDfromInput("/MyUstb/DownloadFile", "/" + FileName, inputStream);
                            if (resultFile == null) {
                                System.out.print("error");
                            } else {
                                Message msg = new Message();
                                msg.what = 0x100;
                                msg.obj = FileName;
                                handler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            System.out.print("error");
                        }
                    }
                }.start();
            }
        });

    }


    WebView web;
    static final int NOTIFICATION_ID = 0x123;
    NotificationManager nm;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str_msg = msg.obj.toString();
            switch (msg.what) {
                case 0x100://下载
//					Toast.makeText(Browser.this,"文件"+str_msg+"下载完成", Toast.LENGTH_SHORT).show();
                    showNoptify(str_msg);
                    break;
            }
        }
    };


    // 为发送通知的按钮的点击事件定义事件处理方法
    public void showNoptify(String FileName) {

        Intent main_intent = new Intent(this, FileManager.class);
//		 	main_intent.putExtra("open_id",1);
        PendingIntent pi = PendingIntent.getActivity(this, 0, main_intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("点击打开文件")
                .setTicker("下载成功")
                .setContentText("文件" + FileName + "下载成功")
                .setContentIntent(pi)
                .setAutoCancel(true);

        nm.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (web.canGoBack()) {
                web.goBack();
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.collect:
                Snackbar.make(container, R.string.noOperation, Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Toast.makeText(this, R.string.noOperation, Toast.LENGTH_SHORT).show();
                break;
            case R.id.openWithOtherBrowser:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_uri_browsers = Uri.parse(web.getUrl());
                intent.setData(content_uri_browsers);
                startActivity(intent);
                break;
            case R.id.copyUrl:
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(web.getUrl()); // 复制
                Snackbar.make(container, R.string.haveCopied, Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Toast.makeText(this, R.string.haveCopied, Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String shareContent = "【来自" + this.getString(R.string.app_name) + "的网页分享】" + web.getTitle() + "\n" + web.getUrl();
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
//		if (id == R.id.collect) {
//			return true;
//		}
        return super.onOptionsItemSelected(item);
    }

    //chromeClient类
    public class chromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //动态在标题栏显示进度条
            Browser.this.setProgress(newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            //设置当前activity的标题栏
            Browser.this.setTitle(title);
            super.onReceivedTitle(view, title);
        }
    }
}
