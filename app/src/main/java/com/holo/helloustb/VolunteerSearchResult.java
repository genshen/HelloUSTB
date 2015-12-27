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
import com.holo.view.ProgressWheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerSearchResult extends AppCompatActivity {
    ProgressWheel progress_wheel;
    TextView search_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        search_message = (TextView) findViewById(R.id.search_message);

        Intent intent = getIntent();
        int search_method = intent.getIntExtra("search_method", 1);
        String key_word = intent.getStringExtra("key_word");

        if (((MyApplication) getApplication()).CheckNetwork()) {
            GetPostHandler.handlerPost(handler, getString(R.string.volunteer_search), "VOL", 0x100, 10,
                    "gb2312", getPostData(search_method, key_word));
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            search_message.setText(getString(R.string.NoNetwork));
            search_message.setError(getString(R.string.NoNetwork));
            progress_wheel.setVisibility(View.INVISIBLE);
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
            DataInfo data_info = (DataInfo) msg.obj;
            progress_wheel.setVisibility(View.INVISIBLE);

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

    private void setSearchData(final ArrayList<String> search_result) {
        ListView search_result_list = (ListView) findViewById(R.id.search_result_list);

        List<Map<String, Object>> listitems = new ArrayList<>();
        for (int i = 0; i < search_result.size() / 8; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("search_activity_name", search_result.get(8 * i));
            listitem.put("search_activity_id", search_result.get(8 * i + 1));    //id
            listitem.put("search_activity_type", search_result.get(8 * i + 2));
            listitem.put("search_activity_place", search_result.get(8 * i + 4));
            listitem.put("search_activity_state", "火热报名中");
            listitem.put("search_activity_join", search_result.get(8 * i + 5) + "人参与");
            listitem.put("search_activity_time", search_result.get(8 * i + 3));
            listitems.add(listitem);
        }

        SimpleAdapter home_adapter = new SimpleAdapter(this, listitems, R.layout.listview_volunteer_search_result,
                new String[]{"search_activity_name", "search_activity_id", "search_activity_type",
                        "search_activity_place", "search_activity_state", "search_activity_join",
                        "search_activity_time"},
                new int[]{R.id.search_result_activity_name, R.id.search_result_activity_id, R.id.search_result_activity_type,
                        R.id.search_result_activity_place, R.id.search_result_activity_state, R.id.search_result_activity_join,
                        R.id.search_result_activity_timer});
        search_result_list.setAdapter(home_adapter);

        search_result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Intent search_result_activity = new Intent(VolunteerSearchResult.this, VolunteerDetail.class);

                search_result_activity.putExtra("flag", 1);
                search_result_activity.putExtra("id", search_result.get(8 * position + 1).split("：")[1]);
                search_result_activity.putExtra("title", search_result.get(8 * position));
                search_result_activity.putExtra("join", search_result.get(8 * position + 5));
                search_result_activity.putExtra("interest", search_result.get(8 * position + 6));
                startActivity(search_result_activity);
            }
        });

    }

    private Map<String, String> getPostData(int search_method, String key_word) {
        // TODO Auto-generated method stub
        String name = "";
        String id = "";
        String place = "";
//        try {
        switch (search_method) {
            case 0:
                id = key_word;  //URLEncoder.encode(key_word, "gbk");
                break;
            case 1:
                name = key_word;  //URLEncoder.encode(key_word, "gbk");
                break;
            case 2:
                place = key_word;  //URLEncoder.encode(key_word, "gbk");// key_word;
                break;
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Map<String, String> post_data = new HashMap<>();
        post_data.put("id", id);
        post_data.put("name", name);
        post_data.put("place", place);
        post_data.put("abilityRequire", "");
        post_data.put("keywords", "");
        post_data.put("inoutSchool", "-1");
        post_data.put("activityType2", "-1");
        post_data.put("organization", "-1");
        post_data.put("Submit", "");

//		return getString(R.string.volunteer_search)+"&id="
//				+ id +"&name=" + name + "&place=" + place
//				+"&abilityRequire=&keywords=&inoutSchool=-1&activityType2=-1&organization=-1&Submit=";
//		http://sztz.ustb.edu.cn/zyz/activityshow.do?method=search
//		name=e&id=&place=	&abilityRequire=&keywords=&inoutSchool=-1
//		&activityType2=-1&organization=-1&Submit=
        return post_data;
    }
}
