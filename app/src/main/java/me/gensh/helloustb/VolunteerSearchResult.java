package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.NetWorkActivity;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerSearchResult extends NetWorkActivity implements HttpRequestTask.OnTaskFinished {
    GoogleProgressBar progress_bar;
    MaterialRefreshLayout materialRefreshLayout;
    TextView search_message;
    HttpRequestTask httpRequestTask;

    boolean has_init = false, is_loading = false;
    int page_count = 1, search_method;
    String key_word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_search_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_bar = findViewById(R.id.progress_bar);
        search_message = (TextView) findViewById(R.id.search_message);
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.volunteer_refresh);

        Intent intent = getIntent();
        search_method = intent.getIntExtra("search_method", 1);
        key_word = intent.getStringExtra("key_word");
        initListView();
        search(page_count, true, true);

        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if (is_loading) {
                    materialRefreshLayout.finishRefresh();
                    return;
                }
                has_init = false;
                page_count = 1;
                search(page_count, false, true);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (!has_init || is_loading) {
                    materialRefreshLayout.finishRefreshLoadMore();
                    return;
                }
                search(page_count, false, false);
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


    final static int LENGTH_PER_PAGE = 10;

    private boolean checkHasMore(ArrayList<String> str_msg) {
        int old_index = (listitems.size() - 1) / LENGTH_PER_PAGE * LENGTH_PER_PAGE;
        if (str_msg.size() <= 2 || listitems.size() <= old_index) {
            return false;
        }
        String old_id = listitems.get(old_index).get("search_activity_id").toString();
        return !old_id.endsWith(str_msg.get(1));
    }

    private void search(int page_count, boolean first, boolean init) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            int what = 0x102;
            if (first) {   // first load
                what = 0x100;
                showProgressDialog();
            } else if (init) {  // refresh
                what = 0x101;
            }
            is_loading = true; //@important: cancel task if it is not null,to make sure there is one task running in a activity or fragment.
            httpRequestTask = new HttpRequestTask(this, HttpClients.HTTP_GET,
                    getSearchUrl(search_method, page_count, key_word), Tags.VOLUNTEER, what, Tags.GET.ID_VOLUNTEER_ACTIVITIES_SEARCH, HttpClients.CHARSET_BTF8, null);
            httpRequestTask.setOnTaskFinished(this);
            httpRequestTask.execute();
        } else {
            onNetworkDisabled();
        }
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        is_loading = false;
        switch (what) {
            case 0x100: // first load
                dismissProgressDialog();
                if (data.size() == 0) {
                    search_message.setText(getString(R.string.no_search_result));
                    return;
                }
                has_init = true;
                page_count += 1;
                setData(data, true);
                break;
            case 0x101:  // refresh
                materialRefreshLayout.finishRefresh();
                if (data.size() == 0) {
                    Toast.makeText(VolunteerSearchResult.this, R.string.no_search_result, Toast.LENGTH_SHORT).show();
                    return;
                }
                has_init = true;
                page_count += 1;
                setData(data, true);
                break;
            case 0x102:  // load more
                materialRefreshLayout.finishRefreshLoadMore();
                if (data.size() == 0 || !checkHasMore(data)) {
                    Toast.makeText(VolunteerSearchResult.this, R.string.no_search_result_more, Toast.LENGTH_SHORT).show();
                    return;
                }
                page_count += 1;
                setData(data, false);
                break;
        }
    }

    @Override
    public void onPasswordError() {  // no password.

    }

    @Override
    public void onTimeoutError() {
        is_loading = false;
        dismissProgressDialog();
        onNetworkDisabled();
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
        Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        search_message.setText(getString(R.string.no_network));
        search_message.setError(getString(R.string.no_network));
    }

    private String getSearchUrl(int search_method, int pageNum, String keyword) {
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

    List<Map<String, Object>> listitems = new ArrayList<>();
    SimpleAdapter search_result_adapter;

    private void initListView() {
        ListView search_result_list = (ListView) findViewById(R.id.search_result_list);
        search_result_adapter = new SimpleAdapter(this, listitems, R.layout.listview_volunteer_search_result,
                new String[]{"search_activity_name", "search_activity_id", "search_activity_type",
                        "search_activity_place", "search_activity_hours", "search_activity_state",
                        "search_activity_time", "search_activity_org"},
                new int[]{R.id.search_result_activity_name, R.id.search_result_activity_id, R.id.search_result_activity_type,
                        R.id.search_result_activity_place, R.id.search_result_activity_hours, R.id.search_result_activity_state,
                        R.id.search_result_activity_time, R.id.search_result_activity_org});
        search_result_list.setAdapter(search_result_adapter);

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
                search_result_activity.putExtra("detail", map);
                search_result_activity.putExtra("id", listitems.get(position).get("id_for_detail").toString());
                startActivity(search_result_activity);
            }
        });
    }

    private void setData(final ArrayList<String> search_result, boolean clean) {
        if (clean) {
            listitems.clear();
        }
        int length = search_result.size() / 8;
        for (int i = 0; i < length; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("search_activity_name", search_result.get(8 * i));
            listitem.put("search_activity_id", "活动编号:" + search_result.get(8 * i + 1));
            listitem.put("search_activity_type", "活动类型:" + search_result.get(8 * i + 2));
            listitem.put("search_activity_place", "活动地点:" + search_result.get(8 * i + 3));
            listitem.put("search_activity_hours", "活动工时:" + search_result.get(8 * i + 4));
            listitem.put("search_activity_state", "活动状态:" + search_result.get(8 * i + 5));
            listitem.put("search_activity_time", "活动时间:" + search_result.get(8 * i + 6));
            listitem.put("search_activity_org", "发起人:" + search_result.get(8 * i + 7));
            listitem.put("id_for_detail", search_result.get(8 * i + 1));
            listitems.add(listitem);
        }
        search_result_adapter.notifyDataSetChanged();
    }

}
