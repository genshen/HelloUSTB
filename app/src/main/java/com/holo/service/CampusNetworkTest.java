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

import com.holo.base.StrPro;
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
    final String SILENT_MODE_KEY = "SILENT_MODE";
    final int NETWORK_SING_IN_NOTIFY_ID = 10;

    public CampusNetworkTest() {
        super("CampusNetworkTest");
    }

    public static void startCampusNetworkService(Context context, String ssid) {
        Intent intent = new Intent(context, CampusNetworkTest.class);
        intent.putExtra("ssid", ssid);
        context.startService(intent);
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
                boolean isSilentMode = pre.getBoolean(SILENT_MODE_KEY, false);
                if (isSilentMode && SdCardPro.fileIsExists("/MyUstb/Pass_store/sch_net_pass.ustb")) {
                    if (((MyApplication) getApplication()).CheckNetwork()) {
                        String myaccount[] = StrPro.ReadWithEncryption("/MyUstb/Pass_store/sch_net_pass.ustb").split("@");
                        Map<String, String> post_params = new LinkedHashMap<>();
                        post_params.put("v6ip", "");
                        post_params.put("0MKKey", "123456789");
                        post_params.put("DDDDD", myaccount[0]);
                        post_params.put("upass", myaccount[1]);
                        GetPostHandler.handlerPost(handler, getString(R.string.sch_net), "NET", 0x101, 7, "GB2312", post_params);
                    }
                } else {
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;

            //error password and timeout process
            if (data_info.code == DataInfo.ERROR_PASSWORD) { //todo
                Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
                return;
            } else if (data_info.code == DataInfo.TimeOut) {
                Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            switch (msg.what) {
                case 0x101:
                    if (str_msg != null) {  //todo
                        Toast.makeText(getBaseContext(), R.string.net_sign_in_success, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

}
