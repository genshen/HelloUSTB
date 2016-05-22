package com.holo.helloustb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.holo.base.StrPro;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamQuery extends AppCompatActivity {
    GoogleProgressBar progress_bar;
    final String passFileName = "/MyUstb/Pass_store/sch_ele_pass.ustb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_bar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        if (((MyApplication) getApplication()).CheckNetwork()) {
            String myaccount[] = StrPro.ReadWithEncryption(passFileName).split("@");
            GetPostHandler.handlerPost(handler, getString(R.string.ele_exam_time_place_query),
                    "ELE", 0x001, 6, "UTF-8", getPostData(myaccount[0]));
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            progress_bar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress_bar.setVisibility(View.GONE);

            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
                Toast.makeText(ExamQuery.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            if (msg.what == 0x001 && str_msg != null) {
                setListView(str_msg);
            }
        }
    };

    private Map<String, String> getPostData(String username) {
        Map<String, String> m = new HashMap<>();
        m.put("uid", username);
        m.put("winName", "examListPanel");
        m.put("listXnxq", getYear());
        return m;
    }

    public void setListView(ArrayList<String> listdata) {
        List<Map<String, Object>> listitems = new ArrayList<>();
        int size = listdata.size();

        //添加考试周的考试
        for (int i = 0; i < size / 5; i++) {
            if(!listdata.get(i * 5 + 3).isEmpty()) {
                Map<String, Object> m = new HashMap<>();
                m.put("name", listdata.get(i * 5 + 1));
                m.put("time", listdata.get(i * 5 + 2));
                m.put("place", listdata.get(i * 5 + 3));
                m.put("remark", listdata.get(i * 5 + 4));
                listitems.add(m);
            }
        }
//
//        //添加非考试周的考试
//        for (int i = 0; i < size / 5; i++) {
//            if(listdata.get(i * 5 + 3).isEmpty()) {
//                Map<String, Object> m = new HashMap<>();
//                m.put("name", listdata.get(i * 5 + 1));
//                m.put("time", listdata.get(i * 5 + 2));
//                m.put("place", listdata.get(i * 5 + 3));
//                m.put("remark", listdata.get(i * 5 + 4));
//                listitems.add(m);
//            }
//        }

        ListView innovation_credit_list = (ListView) findViewById(R.id.exam_query_listview);
        SimpleAdapter home_adapter = new SimpleAdapter(this, listitems, R.layout.listview_exam_query,
                new String[]{"name", "time", "place"},
                new int[]{R.id.exam_query_name, R.id.exam_query_time, R.id.exam_query_place});
        innovation_credit_list.setAdapter(home_adapter);
    }

    public String getYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        if (month <= 7 && month > 3) {
            return (year - 1) + "-" + year + "-2";
        } else if (month > 7) {
            return (year) + "-" + (year + 1) + "-1";
        } else {
            return (year - 1) + "-" + year + "-1";
        }
    }
}
