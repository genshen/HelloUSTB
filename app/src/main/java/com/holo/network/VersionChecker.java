package com.holo.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 根深 on 2016/3/11.
 */
public class VersionChecker {
    Update update;
    String updateUrl;
    public  int versionCode = 0,version_code_new = 0;
    public long package_size = 0;
    public String version_name_new,version_describe;

    public interface Update {
        void onUpdate(boolean latestVersion);
    }

    public VersionChecker(String updateUrl, Context context) {
        this.updateUrl = updateUrl;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void check(boolean net_ok) {
        if(!net_ok)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(updateUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    getNewVersionInfo(br);
                    br.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONObject getNewVersionInfo(BufferedReader br_html) {
        try {
            String line;
            if ((line = br_html.readLine()) != null) {
                JSONObject version = new JSONObject(line);
                version_code_new = Integer.parseInt(version.getString("version_code"));
                if (update != null) {
                    version_name_new = version.getString("version_name");
                    package_size = Long.parseLong(version.getString("size"));
                    version_describe = version.getString("describe");
                    update.onUpdate(versionCode < version_code_new);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOnUpdate(Update callback) {
        update = callback;
    }
}
