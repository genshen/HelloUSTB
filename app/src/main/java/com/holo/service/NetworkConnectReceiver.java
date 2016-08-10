package com.holo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.holo.utils.Const;

public class NetworkConnectReceiver extends BroadcastReceiver {
    static long lastReceiveTime = 0;

    public NetworkConnectReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                //check SharedPreferences
                SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
                boolean autoSignIn = pre.getBoolean(Const.Settings.KEY_NET_AUTO_SIGN_IN_ENABLE, true);
                if (!autoSignIn) {
                    return;
                }

                //check time
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastReceiveTime < 2000) {
                    lastReceiveTime = currentTime;
                    return;
                }
                lastReceiveTime = currentTime;

                Log.v("net", "wifi connected");
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                CampusNetworkTest.startCampusNetworkService(context, wifiInfo.getSSID());
            }
        }
    }
}