package com.holo.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.holo.base.Const;
import com.holo.base.StrPro;
import com.holo.helloustb.Browser;
import com.holo.helloustb.MyApplication;
import com.holo.helloustb.NetWorkSignIn;
import com.holo.helloustb.R;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.holo.sdcard.SdCardPro;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CampusNetworkTest extends IntentService {
    final String TEST_URL = "http://www.sohu.com";
    final String TEST_TAG = "test connection";
    final int NETWORK_SING_IN_NOTIFY_ID = 10;
    final int AUTO_SIGN_IN_ERROR_NOTIFY_ID = 12;
    Context context;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder errorNotification;

    public CampusNetworkTest() {
        super("CampusNetworkTest");
    }

    public static void startCampusNetworkService(Context context, String ssid) {
        Intent intent = new Intent(context, CampusNetworkTest.class);
        intent.putExtra("ssid", ssid);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        context = getBaseContext();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String ssid = intent.getStringExtra("ssid");
            handleActionNetworkCheck(ssid);
        }
    }

    private void handleActionNetworkCheck(String ssid) {
        try {
            //HttpURLConnection  3s延迟
            Thread.sleep(300);//todo 150
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            HttpGet request = new HttpGet();
            request.setURI(new URI(TEST_URL));
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            Log.v(TEST_TAG, "CONNECTION TEST returned code:" + status);
            if (status == 302) { //todo and location url =="202.204.48.66"
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
                String mode = pre.getString(Const.Settings.KEY_NET_SIGN_IN_MODE, Const.Settings.NET_SIGN_IN_NORMAL_MODE);
                if (Const.Settings.NET_SIGN_IN_SILENT_MODE.equals(mode) && SdCardPro.fileIsExists("/MyUstb/Pass_store/sch_net_pass.ustb")) {
                    //静默模式(有密码)
                    autoSignInNetwork();
                } else if (Const.Settings.NET_SIGN_IN_BROWSER_MODE.equals(mode)) { //浏览器模式
                    Intent browser = new Intent(getBaseContext(), Browser.class);
                    browser.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    browser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    browser.putExtra("url", getString(R.string.sch_net));
                    startActivity(browser);
                } else {   //普通模式或者静默模式(无密码)
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setContentTitle(getBaseContext().getString(R.string.network_notify_title))
                            .setContentText(getBaseContext().getString(R.string.network_notify_content, ssid))
                            .setTicker(getBaseContext().getString(R.string.network_notify_ticker))
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_launcher);

                    Intent notifyIntent = new Intent(this, NetWorkSignIn.class);
                    notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    notifyIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Creates the PendingIntent
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contentIntent);
                    mNotificationManager.notify(NETWORK_SING_IN_NOTIFY_ID, mBuilder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int errorTryTimes = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0x100){
                int leftTime = (int) msg.obj -1;
                if (leftTime <= 0) {  //todo try again. //notice 正在重试
                    errorNotification.setContentTitle(context.getString(R.string.auto_sign_in_retry_title))
                            .setContentText(context.getString(R.string.auto_sign_in_retry_content));
                    mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
                    autoSignInNetwork();
                } else {
                    errorNotification.setContentText(context.getString(R.string.auto_sign_in_error_content_try_again, leftTime));
                    mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
                    Message message = new Message();
                    message.what = 0x100;
                    message.obj = leftTime;
                    Log.v("v","ssssss");
                    handler.sendMessageDelayed(message, 1000);
                }
                return;
            }

            DataInfo data_info = (DataInfo) msg.obj;
            //error password and timeout process
            if (data_info.code == DataInfo.ERROR_PASSWORD) {
                errorNotification = buildNotification(R.string.auto_sign_in_error_title, context.getString(R.string.auto_sign_in_error_content_password));
                mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
                Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
                return;
            } else if (data_info.code == DataInfo.TimeOut) {
                errorTryTimes++;
                if (errorNotification == null) { //第一次超时
                    errorNotification = buildNotification(R.string.auto_sign_in_error_title,
                            context.getString(R.string.auto_sign_in_error_content_try_again, 3));// todo
                } else {  //后续超时,次数限制
                    if (errorTryTimes > 4) {//todo
                        errorNotification.setContentTitle(context.getString(R.string.auto_sign_in_error_title))
                                .setContentText(context.getString(R.string.auto_sign_in_error_content_try_limit, 4));//todo
                        Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                        mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
                        return;
                    } else {
                        errorNotification.setContentTitle(context.getString(R.string.auto_sign_in_error_title)).setContentText(context.getString(R.string.auto_sign_in_error_content_try_again, 3));//todo
                    }
                }
                mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
                Message message = new Message();
                message.what = 0x100;
                message.obj = 3; //seconds todo
                handler.sendMessageDelayed(message, 1000);
                Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            if (msg.what == 0x101) {
                //todo store data. add notice
                mNotificationManager.cancel(AUTO_SIGN_IN_ERROR_NOTIFY_ID);
                Toast.makeText(getBaseContext(), R.string.auto_sign_in_success_toast, Toast.LENGTH_LONG).show();
            }
        }
    };

    NotificationCompat.Builder buildNotification(int title, String content) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(context.getString(title))
                .setContentText(content)
                .setTicker(context.getString(R.string.auto_sign_in_error_ticker))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);
        mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, mBuilder.build());
        return mBuilder;
    }


    void autoSignInNetwork() {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            String myaccount[] = StrPro.ReadWithEncryption("/MyUstb/Pass_store/sch_net_pass.ustb").split("@");//todo move out
            Map<String, String> post_params = new LinkedHashMap<>();
            post_params.put("v6ip", "");
            post_params.put("0MKKey", "123456789");
            post_params.put("DDDDD", myaccount[0]);
            post_params.put("upass", myaccount[1]);
            GetPostHandler.handlerPost(handler, getString(R.string.sch_net), "NET", 0x101, 7, "GB2312", post_params);
        } else {
            if (errorNotification == null) {
                errorNotification = buildNotification(R.string.auto_sign_in_error_title, context.getString(R.string.auto_sign_in_error_content_hand));//todo
            } else {
                errorNotification.setContentTitle(context.getString(R.string.auto_sign_in_error_title)).setContentText(context.getString(R.string.auto_sign_in_error_content_hand));
                mNotificationManager.notify(AUTO_SIGN_IN_ERROR_NOTIFY_ID, errorNotification.build());
            }
        }

    }

}
