package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerSearchResult extends AppCompatActivity {
    GoogleProgressBar progress_bar;
    TextView search_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_bar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        search_message = (TextView) findViewById(R.id.search_message);

        Intent intent = getIntent();
        int search_method = intent.getIntExtra("search_method", 1);
        String key_word = intent.getStringExtra("key_word");

        if (((MyApplication) getApplication()).CheckNetwork()) {
            GetPostHandler.handlerGet(handler, getSearchUrl(search_method, 1, key_word), "VOL", 0x100, 17, "utf-8");
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            search_message.setText(getString(R.string.NoNetwork));
            search_message.setError(getString(R.string.NoNetwork));
            progress_bar.setVisibility(View.INVISIBLE);
        }
    }

    private String getSearchUrl(int search_method, int pageNum, String keyword) {
// and int stage,int type,int org,int state
        String name = "";
        String id = "";
        String place = "";
        switch (search_method) {
            case 0:
                id = keyword;
                break;
            case 1:
                name = keyword;
                break;
            case 2:
                place = keyword;
                break;
        }
        return getString(R.string.volunteer_search, pageNum, name, place, id, "");
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
            DataInfo data_info = (DataInfo) msg.obj;
            progress_bar.setVisibility(View.INVISIBLE);

            if (data_info.code == DataInfo.TimeOut) {
                Toast.makeText(VolunteerSearchResult.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                search_message.setError(getString(R.string.connectionTimeout));
                search_message.setText(getString(R.string.connectionTimeout));
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            switch (msg.what) {
                case 0x100:
                    if (str_msg.size() == 0) {
                        search_message.setText(getString(R.string.no_search_result));
                        return;
                    }
                    setSearchData(str_msg);
                    break;
            }
        }
    };

    List<Map<String, Object>> listitems = new ArrayList<>();
    private void setSearchData(final ArrayList<String> search_result) {
        ListView search_result_list = (ListView) findViewById(R.id.search_result_list);

        int length = search_result.size() / 8;
        for (int i = 0; i < length; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("search_activity_name", search_result.get(8 * i));
            listitem.put("search_activity_id", "活动编号:"+search_result.get(8 * i + 1));
            listitem.put("search_activity_type","活动类型:"+ search_result.get(8 * i + 2));
            listitem.put("search_activity_place", "活动地点:"+search_result.get(8 * i + 3));
            listitem.put("search_activity_hours", "活动工时:"+search_result.get(8 * i + 4));
            listitem.put("search_activity_state", "活动状态:"+search_result.get(8 * i + 5));
            listitem.put("search_activity_time", "活动时间:"+search_result.get(8 * i + 6));
            listitem.put("search_activity_org", "发起人:"+search_result.get(8 * i + 7));
            listitem.put("id_for_detail", search_result.get(8 * i + 1));
            listitems.add(listitem);
        }

        SimpleAdapter home_adapter = new SimpleAdapter(this, listitems, R.layout.listview_volunteer_search_result,
                new String[]{"search_activity_name", "search_activity_id", "search_activity_type",
                        "search_activity_place", "search_activity_hours", "search_activity_state",
                        "search_activity_time","search_activity_org"},
                new int[]{R.id.search_result_activity_name, R.id.search_result_activity_id, R.id.search_result_activity_type,
                        R.id.search_result_activity_place, R.id.search_result_activity_hours, R.id.search_result_activity_state,
                        R.id.search_result_activity_time,R.id.search_result_activity_org});
        search_result_list.setAdapter(home_adapter);

        search_result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent search_result_activity = new Intent(VolunteerSearchResult.this, VolunteerDetail.class);
                Map<String, Object> data_map = listitems.get(position);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", data_map.get("search_activity_name"));
                map.put("timer", data_map.get("search_activity_time"));
                map.put("type", data_map.get("search_activity_type"));
                map.put("place", data_map.get("search_activity_place"));
                map.put("work_hour", data_map.get("search_activity_hours"));
                search_result_activity.putExtra("detail",map);
                search_result_activity.putExtra("id", listitems.get(position).get("id_for_detail").toString());
                startActivity(search_result_activity);
            }
        });

    }
}
