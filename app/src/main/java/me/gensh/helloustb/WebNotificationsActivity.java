package me.gensh.helloustb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.NetWorkActivity;

public class WebNotificationsActivity extends NetWorkActivity implements HttpRequestTask.OnTaskFinished {
    public static final String EXTRA_WEB_NOTIFICATIONS = "EXTRA_WEB_NOTIFICATIONS";
    List<Map<String, Object>> listItems = new ArrayList<>();
    SimpleAdapter webNotificationAdapter;
    @BindView(R.id.web_notifications_list_view)
    ListView webNotificationList;
    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;

    int[] randomColor = {R.color.green, R.color.yellow, R.color.side_nav_bar,
            R.color.red_light, R.color.purple, R.color.bright_yellow};
    final int randomLength = 6;
    int random = (int) (Math.random() * 10) % randomLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //set ListView
        webNotificationAdapter = new SimpleAdapter(this, listItems, R.layout.listview_web_notifications,
                new String[]{"title", "date", "left_color", "right_color"},
                new int[]{R.id.web_notification_title, R.id.web_notification_date, R.id.web_notification_card_left, R.id.web_notification_card_right});
        webNotificationList.setAdapter(webNotificationAdapter);

        webNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Browser.openBrowserWithUrl(WebNotificationsActivity.this, "http://teach.ustb.edu.cn/" + listItems.get(position).get("url").toString());
            }
        });

        //set listView data
        final ArrayList<String> notifications = getIntent().getStringArrayListExtra(EXTRA_WEB_NOTIFICATIONS);
        if (notifications != null) {
            setNotificationsList(notifications);
        } else {
            //set http request
            attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.teach), "TEACH", 0x101, 1, "gb2312", null, true);
        }
    }

    private void setNotificationsList(@NonNull ArrayList<String> notifications) {
        //set necessary data.
        int size = notifications.size();
        for (int i = 0; i < size / 3; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("url", notifications.get(3 * i));  //data not for listView
            listItem.put("title", notifications.get(3 * i + 1));
            listItem.put("date", notifications.get(3 * i + 2));
            listItem.put("left_color", randomColor[random]);
            random = (random + 1) % randomLength;
            listItem.put("right_color", randomColor[random]);
            listItems.add(listItem);
        }
        webNotificationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        dismissProgressDialog();
        if (what == 0x101) {
            if (data != null) {
                setNotificationsList(data);
            }
        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        Toast.makeText(WebNotificationsActivity.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        Toast.makeText(WebNotificationsActivity.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
    }

}
