package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;

import java.util.ArrayList;

public class VolunteerDetail extends AppCompatActivity {
    String title, join, interest, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String detail_id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        join = intent.getStringExtra("join");
        interest = intent.getStringExtra("interest");
        state = intent.getIntExtra("flag", 0) == 0 ? "活动状态:活动已审批" : "活动状态:火热报名中";

        //get function in oncreate
        if (((MyApplication) getApplication()).CheckNetwork()) {
            GetPostHandler.handlerGet(handler, getString(R.string.volunteer_view) + "&id=" + detail_id,
                    "VOL", 0x100, 18, "GB2312");
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
//            case R.id.action_settings:
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
//                progress_wheel.setVisibility(View.INVISIBLE);
                Toast.makeText(VolunteerDetail.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<String> str_msg = data_info.data;

            switch (msg.what) {
                case 0x100:
                    int length = str_msg.size();
                    String data[];

                    int[] id = {R.id.detail_title, R.id.detail_join, R.id.detail_interest, R.id.detail_state,
                            R.id.detail_id, R.id.detail_type, R.id.detail_time, R.id.detail_place,
                            R.id.detail_hour, R.id.detail_organizer_unit,
                            R.id.detail_recruit_method, R.id.detail_organizer_num, R.id.detail_organizer_contact,
                            R.id.detail_duty, R.id.detail_ability, R.id.detail_recruit_condition,
                            R.id.detail_introduce, R.id.detail_plan};
//						0-id;1-type	;2-time;3-place;4-hour;5-organizer_unit; 6-recruit_method;
//						7-organizer_num; 8 -organizer_contact;
//						9-duty;10:ablity;11-condition;12-intruduce;13-plan
                    if (length == 13) // there is no tel of the organizer
                    {
                        data = new String[]{title, join, interest, state,
                                str_msg.get(0), str_msg.get(1), str_msg.get(2), str_msg.get(3),
                                str_msg.get(4), str_msg.get(5),
                                str_msg.get(6), str_msg.get(7), "组织者联系方式: 无",
                                str_msg.get(8), str_msg.get(9), str_msg.get(10),
                                "活动介绍:" + str_msg.get(11), "活动计划:" + str_msg.get(12)};
                    } else if (length == 14) {
                        data = new String[]{title, join, interest, state,
                                str_msg.get(0), str_msg.get(1), str_msg.get(2), str_msg.get(3),
                                str_msg.get(4), str_msg.get(5),
                                str_msg.get(6), str_msg.get(7), "组织者联系方式:" + str_msg.get(8),
                                str_msg.get(9), str_msg.get(10), str_msg.get(11),
                                "活动介绍:" + str_msg.get(12), "活动计划:" + str_msg.get(13)};
                    } else {
                        // set empty messages
                        return;
                    }

                    for (int i = 0; i < 18; i++) {
                        TextView tv = (TextView) findViewById(id[i]);
                        tv.setText(data[i]);
                    }
                    break;
            }
        }
//			感兴趣：
//			callCount=1
//					c0-scriptName=GETATTIME
//					c0-methodName=InterestActivity
//					c0-id=3960_1437553689786
//					c0-param0=string:10527
//					c0-param1=boolean:false
//					c0-param2=boolean:false
//					xml=true

//			callCount=1
//					c0-scriptName=GETATTIME
//					c0-methodName=JoinActivity
//					c0-id=5977_1437554251339
//					c0-param0=string:10526
//					c0-param1=boolean:false
//					c0-param2=boolean:false
//					xml=true
//			http://sztz.ustb.edu.cn/zyz/dwr/exec/GETATTIME.JoinActivity.dwr

    };
}
