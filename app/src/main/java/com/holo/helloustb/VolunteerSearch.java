package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerSearch extends AppCompatActivity {
    SearchView sv;
    ListView tip_list;
    TextView tip_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tip_list = (ListView) findViewById(R.id.search_tip_list);
        tip_text = (TextView) findViewById(R.id.search_tip_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.volunteer_search, menu);
        sv = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.vol_search_view));
        sv.setQueryHint(getString(R.string.vol_search_hint));

//        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        TextView textView = (TextView) sv.findViewById(id);
//        textView.setTextColor(Color.WHITE);
//        textView.setHintTextColor(Color.WHITE);

        sv.setIconified(false);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    setDropdownList(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    setDropdownList(newText);
                } else {
                    tip_text.setVisibility(View.VISIBLE);
                    tip_list.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
        return true;
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


    protected String[] search_tip_pre = {"按活动编号搜索活动:", "按活动名称搜索活动:", "按活动地点搜索活动:"};

    private void setDropdownList(final String value) {
        tip_text.setVisibility(View.INVISIBLE);
        tip_list.setVisibility(View.VISIBLE);

        final int search_start = isNumeric(value) ? 0 : 1;
        List<Map<String, Object>> listitems = new ArrayList<>();
        for (int i = search_start; i < 3; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("search_tip_pre", search_tip_pre[i]);
            listitem.put("search_tip_value", value);
            listitems.add(listitem);
        }

        SimpleAdapter home_adapter = new SimpleAdapter(this, listitems, R.layout.listview_volunteer_search_tip,
                new String[]{"search_tip_pre", "search_tip_value"},
                new int[]{R.id.search_tip_pre, R.id.search_tip_value});
        tip_list.setAdapter(home_adapter);

        tip_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int search_method = search_start + position;
                Intent search_result = new Intent(VolunteerSearch.this, VolunteerSearchResult.class);
                search_result.putExtra("search_method", search_method);
                search_result.putExtra("key_word", value);
                startActivity(search_result);
            }
        });
    }

    private static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
