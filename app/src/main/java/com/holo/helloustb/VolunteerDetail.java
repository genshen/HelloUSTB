package com.holo.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VolunteerDetail extends AppCompatActivity {
    final static String STATUS_CODE = "statusCode";
    final static String MESSAGE = "message";
    String detail_id, activityTitle;
    boolean detailLoaded = false;
    GoogleProgressBar progress_bar;
    LinearLayout detailContainer;

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
        get(getString(R.string.volunteer_detail_view, detail_id), 0x100, 18);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_volunteer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_join:
                if (detailLoaded) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_delete_course_ack_title)
                            .setMessage(getString(R.string.dialog_join_activity_ack, activityTitle))
                            .setNegativeButton(R.string.alert_cancel, null)
                            .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    get(getString(R.string.volunteer_activity_join, detail_id), 0x101, 10);
                                }
                            })
                            .show();

                } else {
                    showSnackbar(R.string.error_obtain_detail_information);
                }
                break;
//            case R.id.action_collect:
//                TshowSnackbar("无效操作,收藏功能暂未实现",)
//                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
                progress_bar.setVisibility(View.INVISIBLE);
                showSnackbar(R.string.connectionTimeout);
                return;
            }
            ArrayList<String> str_msg = data_info.data;

            switch (msg.what) {
                case 0x100:
                    progress_bar.setVisibility(View.INVISIBLE);
                    int length = str_msg.size();
                    String data[];

                    int[] id = {R.id.detail_join, R.id.detail_interest, R.id.detail_id,
                            R.id.detail_type, R.id.detail_place, R.id.detail_hour,
                            R.id.detail_recruit_method, R.id.detail_state,
                            R.id.detail_organizer_unit, R.id.detail_organizer_person, R.id.detail_deadline,
                            R.id.detail_duty, R.id.detail_ability, R.id.detail_recruit_condition,
                            R.id.detail_introduce, R.id.detail_plan, R.id.detail_organizer_contact};
                    if (length == 15) { // there is no tel of the organizer
                        detailLoaded = true;
                        data = new String[]{"", "", "活动编号:" + str_msg.get(0),
                                "活动类型:" + str_msg.get(1), "活动地点:" + str_msg.get(2), "活动工时:" + str_msg.get(3),
                                "招募方式:" + str_msg.get(4), "活动状态:" + str_msg.get(5),
                                "活动组织方:" + str_msg.get(6), "发起人:" + str_msg.get(7), "报名截止时间 :" + str_msg.get(8),
                                "志愿者职责:" + str_msg.get(9), "志愿者能力要求:" + str_msg.get(10), "招募选拔条件:" + str_msg.get(11),
                                "活动介绍:" + str_msg.get(12), "活动计划:" + str_msg.get(13), "组织者联系方式:无"};
                    } else if (length == 16) {
                        detailLoaded = true;
                        data = new String[]{"", "", "活动编号:" + str_msg.get(0),
                                "活动类型:" + str_msg.get(1), "活动地点:" + str_msg.get(2), "活动工时:" + str_msg.get(3),
                                "招募方式:" + str_msg.get(4), "活动状态:" + str_msg.get(5),
                                "活动组织方:" + str_msg.get(6), "发起人:" + str_msg.get(7), "报名截止时间 :" + str_msg.get(8),
                                "志愿者职责:" + str_msg.get(9), "志愿者能力要求:" + str_msg.get(10), "招募选拔条件:" + str_msg.get(11),
                                "活动介绍:" + str_msg.get(12), "活动计划:" + str_msg.get(13), "组织者联系方式:" + str_msg.get(14)};
                    } else {
                        // set empty messages
                        return;
                    }

                    for (int i = 0; i < 17; i++) {
                        TextView tv = (TextView) findViewById(id[i]);
                        tv.setText(data[i]);
                    }
                    break;
                case 0x101:
                    progress_bar.setVisibility(View.INVISIBLE);
                    if (str_msg.size() == 1) {
                        try {
                            JSONObject json = new JSONObject(str_msg.get(0));
                            int code = json.getInt(STATUS_CODE);
                            if (code == 200) {
                                showSnackbar(R.string.volunteer_join_success);
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
            }
        }
    };

    private void setBaseInformation(HashMap map) {
        activityTitle = map.get("name").toString();
        ((TextView) findViewById(R.id.detail_title)).setText(activityTitle);
//        ((TextView) findViewById(R.id.detail_id)).setText(detail_id);
//        ((TextView) findViewById(R.id.detail_hour)).setText(map.get("work_hour").toString());
//        ((TextView) findViewById(R.id.detail_time)).setText(map.get("timer").toString());
//        ((TextView) findViewById(R.id.detail_place)).setText(map.get("place").toString());
//        ((TextView) findViewById(R.id.detail_type)).setText(map.get("type").toString());
    }

    private void get(String url, int feedback, int id) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            progress_bar.setVisibility(View.VISIBLE);
            GetPostHandler.handlerGet(handler, url, "VOL", feedback, id, "utf-8");
        } else {
            showSnackbar(R.string.NoNetwork);
        }
    }

    private void showSnackbar(int message) {
        Snackbar.make(detailContainer, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(detailContainer, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

}
