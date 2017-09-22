package me.gensh.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.gensh.network.HttpRequestTask;
import me.gensh.sdcard.SdCardPro;
import me.gensh.utils.NetWorkActivity;
import me.gensh.utils.StrUtils;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class VolunteerDetail extends NetWorkActivity implements HttpRequestTask.OnTaskFinished {
    final private static String STATUS_CODE = "statusCode";
    final private static String MESSAGE = "message";
    final private static String INTEREST_ID = "interestId";
    final private static String VOLUNTEER_TAG = "VOL";
    final private static String VOLUNTEER_HTTP_CHARSET = "utf-8";
    int cancelId = 0;
    String detail_id, activityTitle;
    boolean detailLoaded = false, hasCollected = false;
    GoogleProgressBar progress_bar;
    LinearLayout detailContainer;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        detailContainer = (LinearLayout) findViewById(R.id.detail_container);
        progress_bar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        Intent intent = getIntent();
        detail_id = intent.getStringExtra("id");
        setBaseInformation((HashMap) getIntent().getSerializableExtra("detail"));

        //get function in oncreate
        attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.volunteer_detail_view, detail_id),
                VOLUNTEER_TAG, 0x100, 18, VOLUNTEER_HTTP_CHARSET, null, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_volunteer_detail, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_join:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_join_activity_title)
                        .setMessage(getString(R.string.dialog_join_activity_ack, activityTitle))
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET,
                                        getString(R.string.volunteer_activity_join, detail_id), VOLUNTEER_TAG, 0x101, 10, VOLUNTEER_HTTP_CHARSET, null, true);
                            }
                        })
                        .show();
                break;
            case R.id.action_exit:
                final String stuNum = getVolunteerStuNumber();
                if (stuNum != null) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_exit_activity_title)
                            .setMessage(getString(R.string.dialog_exit_activity_ack, activityTitle))
                            .setNegativeButton(R.string.alert_cancel, null)
                            .setPositiveButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET,
                                            getString(R.string.volunteer_activity_exit, detail_id, stuNum), VOLUNTEER_TAG, 0x102, 10, VOLUNTEER_HTTP_CHARSET, null, true);
                                }
                            })
                            .show();
                } else {
                    showSnackbar(R.string.error_volunteer_no_login);
                }
                break;
            case R.id.action_collect:
                if (detailLoaded) {
                    if (hasCollected) {
                        attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.volunteer_activity_collect_cancel, cancelId),
                                VOLUNTEER_TAG, 0x104, 10, VOLUNTEER_HTTP_CHARSET, null, true);
                    } else {
                        attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.volunteer_activity_collect, detail_id),
                                VOLUNTEER_TAG, 0x103, 10, VOLUNTEER_HTTP_CHARSET, null, true);
                    }
                } else {
                    showSnackbar(R.string.error_obtain_detail_information);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCollectAble(boolean hasCollected) {
        this.hasCollected = hasCollected;
        MenuItem item = menu.findItem(R.id.action_collect);
        if (hasCollected) { // be about to cancel collection
            item.setTitle(R.string.setting_volunteer_collect_cancel);
            item.setIcon(R.drawable.ic_favorite_white);
        } else {
            item.setTitle(R.string.setting_volunteer_collect);
            item.setIcon(R.drawable.ic_favorite_border_white);
        }
    }

    private void showMenuItem(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
        item.setEnabled(true);
    }

    private void hideMenuItem(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
        item.setEnabled(false);
    }

    final static int JOINED = 2;
    final static int TO_JOIN = 1;

    private void updateMenu(String date, String joinFlag) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            int flag = Integer.parseInt(joinFlag);
            setCollectAble(((flag & 4) != 0)); //if you haven collected this activity,the 3th bit is 1
            cancelId = flag >> 3;
            if (format.parse(date).getTime() >= System.currentTimeMillis()) {
                if (flag % 4 == JOINED) { // have joined
                    showMenuItem(R.id.action_exit);
                } else if (flag % 4 == TO_JOIN) {  // will join
                    showMenuItem(R.id.action_join);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBaseInformation(HashMap map) {
        activityTitle = map.get("name").toString();
        ((TextView) findViewById(R.id.detail_title)).setText(activityTitle);
//        ((TextView) findViewById(R.id.detail_id)).setText(detail_id);
//        ((TextView) findViewById(R.id.detail_hour)).setText(map.get("work_hour").toString());
//        ((TextView) findViewById(R.id.detail_time)).setText(map.get("timer").toString());
//        ((TextView) findViewById(R.id.detail_place)).setText(map.get("place").toString());
//        ((TextView) findViewById(R.id.detail_type)).setText(map.get("type").toString());
    }

    // Todo not useful for Android 6.0
    final static String VolPassFileName = "/MyUstb/Pass_store/sch_vol_pass.ustb";
    String stuNum;

    private String getVolunteerStuNumber() {
        if (stuNum != null) {
            return stuNum;
        }
        if (SdCardPro.fileIsExists(VolPassFileName)) {
            String account[] = StrUtils.ReadWithEncryption(VolPassFileName).split("@");
            if (account.length == 2) {
                stuNum = account[0];
                return stuNum;
            }
        }
        return null;
    }

    private void showSnackbar(int message) {
        Snackbar.make(detailContainer, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(detailContainer, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        switch (what) {
            case 0x100:
                progress_bar.setVisibility(View.INVISIBLE);
                int length = data.size();
                String volData[];

                int[] id = {R.id.detail_join, R.id.detail_interest, R.id.detail_id,
                        R.id.detail_type, R.id.detail_place, R.id.detail_hour,
                        R.id.detail_recruit_method, R.id.detail_state,
                        R.id.detail_organizer_unit, R.id.detail_organizer_person, R.id.detail_deadline,
                        R.id.detail_duty, R.id.detail_ability, R.id.detail_recruit_condition,
                        R.id.detail_introduce, R.id.detail_plan, R.id.detail_organizer_contact};
                if (length == 16) { // there is no tel of the organizer
                    detailLoaded = true;
                    volData = new String[]{"", "", "活动编号:" + data.get(0),
                            "活动类型:" + data.get(1), "活动地点:" + data.get(2), "活动工时:" + data.get(3),
                            "招募方式:" + data.get(4), "活动状态:" + data.get(5),
                            "活动组织方:" + data.get(6), "发起人:" + data.get(7), "报名截止时间 :" + data.get(8),
                            "志愿者职责:" + data.get(9), "志愿者能力要求:" + data.get(10), "招募选拔条件:" + data.get(11),
                            "活动介绍:" + data.get(12), "活动计划:" + data.get(13), "组织者联系方式:无"};
                } else if (length == 17) {
                    detailLoaded = true;
                    volData = new String[]{"", "", "活动编号:" + data.get(0),
                            "活动类型:" + data.get(1), "活动地点:" + data.get(2), "活动工时:" + data.get(3),
                            "招募方式:" + data.get(4), "活动状态:" + data.get(5),
                            "活动组织方:" + data.get(6), "发起人:" + data.get(7), "报名截止时间 :" + data.get(8),
                            "志愿者职责:" + data.get(9), "志愿者能力要求:" + data.get(10), "招募选拔条件:" + data.get(11),
                            "活动介绍:" + data.get(12), "活动计划:" + data.get(13), "组织者联系方式:" + data.get(14)};
                } else {
                    // set empty messages
                    return;
                }

                for (int i = 0; i < 17; i++) {
                    TextView tv = (TextView) findViewById(id[i]);
                    tv.setText(volData[i]);
                }
                if (detailLoaded) {
                    updateMenu(data.get(8), data.get(length - 1));
                }
                break;
            case 0x101:
                progress_bar.setVisibility(View.INVISIBLE);
                if (data.size() == 1) {
                    try {
                        JSONObject json = new JSONObject(data.get(0));
                        int code = json.getInt(STATUS_CODE);
                        if (code == 200) {
                            showSnackbar(R.string.volunteer_join_success);
                            showMenuItem(R.id.action_exit);
                            hideMenuItem(R.id.action_join);
                        } else {
                            showSnackbar(getString(R.string.error_volunteer_join_result, json.getString(MESSAGE), code));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackbar(R.string.error_volunteer_join_request);
                }
                break;
            case 0x102:
                progress_bar.setVisibility(View.INVISIBLE);
                if (data.size() == 1) {
                    try {
                        JSONObject json = new JSONObject(data.get(0));
                        int code = json.getInt(STATUS_CODE);
                        if (code == 200) {
                            showSnackbar(R.string.volunteer_exit_success);
                            showMenuItem(R.id.action_join);
                            hideMenuItem(R.id.action_exit);
                        } else {
                            showSnackbar(getString(R.string.error_volunteer_exit_result, json.getString(MESSAGE), code));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackbar(R.string.error_volunteer_exit_request);
                }
                break;
            case 0x103: // collection activity
                progress_bar.setVisibility(View.INVISIBLE);
                if (data.size() == 1) {
                    try {
                        JSONObject json = new JSONObject(data.get(0));
                        int code = json.getInt(STATUS_CODE);
                        if (code == 200) {
                            showSnackbar(R.string.volunteer_collect_success);
                            setCollectAble(true);
                            cancelId = json.getInt(INTEREST_ID);
                        } else {
                            showSnackbar(getString(R.string.error_volunteer_collect_result, json.getString(MESSAGE), code));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackbar(R.string.error_volunteer_collect_request);
                }
                break;
            case 0x104:  // cancel activity collection
                progress_bar.setVisibility(View.INVISIBLE);
                if (data.size() == 1) {
                    try {
                        JSONObject json = new JSONObject(data.get(0));
                        int code = json.getInt(STATUS_CODE);
                        if (code == 200) {
                            showSnackbar(R.string.volunteer_collect_cancel_success);
                            setCollectAble(false);
                        } else {
                            showSnackbar(getString(R.string.error_volunteer_collect_cancel_result, json.getString(MESSAGE), code));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showSnackbar(R.string.error_volunteer_collect_cancel_request);
                }
                break;
        }
    }

    @Override
    public void onPasswordError() {  //no password

    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        showSnackbar(R.string.connectionTimeout);
    }

    @Override
    public void showProgressDialog() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progress_bar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkDisabled() {
        showSnackbar(R.string.NoNetwork);
    }

}
