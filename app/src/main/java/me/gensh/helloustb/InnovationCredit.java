package me.gensh.helloustb;

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

import me.gensh.network.DataInfo;
import me.gensh.network.GetPostHandler;
import me.gensh.view.RiseNumberTextView;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnovationCredit extends AppCompatActivity {
    GoogleProgressBar progress_bar;
    float credit_sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_innovation_credit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progress_bar = (GoogleProgressBar) findViewById(R.id.progress_bar);
        if (((MyApplication) getApplication()).CheckNetwork()) {
//           showProgressDialog();
            progress_bar.setVisibility(View.VISIBLE);
            GetPostHandler.handlerGet(handler, getString(R.string.ele_innovation_credit), "ELE", 0x001, 11, "UTF-8");
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
//        ArrayList <String> innovation_credit = getIntent().getStringArrayListExtra("innovation_credit");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress_bar.setVisibility(View.INVISIBLE);

            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
                Toast.makeText(InnovationCredit.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            if (msg.what == 0x001 && str_msg != null) {
                setListView(str_msg);
                setCreditSum();
            }
        }
    };

    private void setCreditSum() {
        RiseNumberTextView riseNumberTextView = (RiseNumberTextView) findViewById(R.id.credit_sum);
        riseNumberTextView.setRiseInterval(0, credit_sum)
                .setDuration(1500)
                .runInt(false)
                .setDecimal(1)
                .start();
    }

    private void setListView(ArrayList<String> data) {
        List<Map<String, Object>> listitems = new ArrayList<>();
        int size = data.size();
        for (int i = 0; i < size / 4; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("type", data.get(i * 4));
            m.put("title", data.get(i * 4 + 1));
            m.put("credit", data.get(i * 4 + 2));
            m.put("time", data.get(i * 4 + 3));
            listitems.add(m);
            credit_sum += Float.parseFloat(data.get(i * 4 + 2));
        }

        ListView innovation_credit_list = (ListView) findViewById(R.id.innovation_credit_list);
        SimpleAdapter home_adapter = new SimpleAdapter(this, listitems, R.layout.listview_innovation_credit,
                new String[]{"type", "title", "credit", "time"},
                new int[]{R.id.innovation_credit_type, R.id.innovation_credit_title,
                        R.id.innovation_credit_credit, R.id.innovation_credit_time});
        innovation_credit_list.setAdapter(home_adapter);
    }
}
