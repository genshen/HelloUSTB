package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyVolunteerList extends AppCompatActivity {
    List<HashMap<String, Object>> List = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_volunteer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        final ArrayList<String> vol_list = intent.getStringArrayListExtra("list");

        int length = vol_list.size() / 10;
        for (int i = 0; i < length; i++) {
            HashMap<String, Object> listitem = new HashMap<>();
            listitem.put("name", vol_list.get(10 * i));
            listitem.put("timer", vol_list.get(10 * i + 1));
            listitem.put("type", vol_list.get(10 * i + 3));
            listitem.put("place", vol_list.get(10 * i + 4));
            listitem.put("work_hour", vol_list.get(10 * i + 5));
            List.add(listitem);
        }
        ListView li = (ListView) findViewById(R.id.my_volunteer_list);
        SimpleAdapter Adapter = new SimpleAdapter(this, List,
                R.layout.listview_volunteer_list,
                new String[]{"name", "type", "timer", "place", "work_hour"},
                new int[]{R.id.name, R.id.type, R.id.timer, R.id.place, R.id.work_hour});
        li.setAdapter(Adapter);

        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent search_result_activity = new Intent(MyVolunteerList.this, VolunteerDetail.class);
                search_result_activity.putExtra("detail", List.get(position));
                search_result_activity.putExtra("id", vol_list.get(10 * position + 2));
                startActivity(search_result_activity);
            }
        });
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
}
