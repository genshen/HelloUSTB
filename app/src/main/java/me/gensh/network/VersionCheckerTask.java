package me.gensh.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.service.DownloadApk;

/**
 * Created by gensh on 2016/3/11.
 * updated by gensh on 2017/9/1
 */
public class VersionCheckerTask extends AsyncTask<Void, Integer, Integer> {
    private Context context;
    private boolean showOnlyUpdate;
    private String updateUrl;
    private int currentVersionCode = 0, newVersionCode = 0;
    private long packageSize = 0;
    private String newVersionName, versionDescribe;

    private final static int UPDATE_TAG_HAS_UPDATE = 1;
    private final static int UPDATE_TAG_ALREADY_LATEST = 2;
    private final static int UPDATE_TAG_ERROR_NO_NETWORK = 3;
    private final static int UPDATE_TAG_REQUEST_ERROR = 4;

    public VersionCheckerTask(String updateUrl, Context context, boolean showOnlyUpdate) {
        this.updateUrl = updateUrl;
        this.context = context;
        this.showOnlyUpdate = showOnlyUpdate;
    }

    @Override
    protected void onPreExecute() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        if (!((MyApplication) context.getApplicationContext()).CheckNetwork()) //check network
            return UPDATE_TAG_ERROR_NO_NETWORK;
        try {
            String line;
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if ((line = br.readLine()) != null) {
                JSONObject version = new JSONObject(line);
                newVersionCode = Integer.parseInt(version.getString("version_code"));
                newVersionName = version.getString("version_name");
                packageSize = Long.parseLong(version.getString("size"));
                versionDescribe = version.getString("describe");
                return newVersionCode > currentVersionCode ? UPDATE_TAG_HAS_UPDATE : UPDATE_TAG_ALREADY_LATEST;
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return UPDATE_TAG_REQUEST_ERROR;
    }

    @Override
    protected void onPostExecute(Integer updateTag) {
        if (!isCancelled()) {
            if (updateTag == UPDATE_TAG_HAS_UPDATE) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.versionUpdate)
                        .setMessage(context.getString(R.string.updateRequest, newVersionName, versionDescribe))
                        .setNegativeButton(R.string.sayLater, null)
                        .setPositiveButton(R.string.updateNow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, R.string.update_downloading, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, DownloadApk.class);
                                intent.putExtra("latestVersion", newVersionName);
                                intent.putExtra("size", packageSize);
                                context.startService(intent);
                            }
                        })
                        .show();
            } else {
                if (!showOnlyUpdate) {
                    if (updateTag == UPDATE_TAG_ALREADY_LATEST) {
                        Toast.makeText(context, R.string.haveUpdated, Toast.LENGTH_SHORT).show();
                    } else if (updateTag == UPDATE_TAG_ERROR_NO_NETWORK) {
                        Toast.makeText(context, R.string.NoNetwork, Toast.LENGTH_SHORT).show();
                    } else if (updateTag == UPDATE_TAG_REQUEST_ERROR) {
                        Toast.makeText(context, R.string.request_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
