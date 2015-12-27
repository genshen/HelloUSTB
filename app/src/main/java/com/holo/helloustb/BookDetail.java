package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;

import java.util.ArrayList;

public class BookDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.library_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        get(getString(R.string.lib_main) + link, "LIB",0x100, 20, "utf-8");
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

    Handler handler = new Handler()
    {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg)
        {
            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
//                cancelProcessDialog();
                Toast.makeText(BookDetail.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            switch(msg.what)
            {
                case 0x100:
                    setView(str_msg);
                    break;
                case 0x101:
                    final String url = str_msg.get(0);
                    TextView douban = (TextView) findViewById(R.id.to_douban);
                    douban.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if(url.isEmpty())
                            {
                                Toast.makeText(BookDetail.this,R.string.no_book_in_douban, Toast.LENGTH_LONG).show();
                            }else{
                                Intent douban_intent =  new Intent(BookDetail.this,Browser.class);
                                douban_intent.putExtra("url", url);
                                startActivity(douban_intent);
                            }
                        }
                    });
                    break;
            }
        }
    };

    private void get(String url, String tag, int feedback, int id, String code) {
        if (((MyApplication) this.getApplication()).CheckNetwork()) {
            GetPostHandler.handlerGet(handler,url, tag, feedback, id, code);
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

    protected void setView(ArrayList<String> data) {
        // TODO Auto-generated method stub
        int i = 0;
        final String isbn = data.get(data.size() - 1);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout book_info = (LinearLayout) findViewById(R.id.book_info);
        LinearLayout book_where = (LinearLayout) findViewById(R.id.book_where);

        while (i + 1 < data.size() && data.get(i + 1) != null) {
            LinearLayout ly = (LinearLayout) inflater.inflate(R.layout.inflater_library_book_info, null);
            TextView item_name = (TextView) ly.findViewById(R.id.book_info_item_name);
            TextView item_detail = (TextView) ly.findViewById(R.id.book_info_item_detail);
            item_name.setText(data.get(i));
            item_detail.setText(data.get(i + 1));

//			 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//	                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			 ly.setLayoutParams(lp);
            book_info.addView(ly);
            i += 2;
        }

        i++;

        while (i + 5 < data.size()) {
            LinearLayout ly = (LinearLayout) inflater.inflate(R.layout.inflater_library_book_where, null);
            TextView book_where_suoshu = (TextView) ly.findViewById(R.id.book_where_suoshu);
            TextView book_where_tiaoma = (TextView) ly.findViewById(R.id.book_where_tiaoma);
            TextView book_where_year = (TextView) ly.findViewById(R.id.book_where_year);
            TextView book_where_place = (TextView) ly.findViewById(R.id.book_where_place);
            TextView book_where_state = (TextView) ly.findViewById(R.id.book_where_state);

            book_where_suoshu.setText(data.get(i));
            book_where_tiaoma.setText(data.get(i + 1));
            book_where_year.setText(data.get(i + 2));
            book_where_place.setText(data.get(i + 3));
            book_where_state.setText(data.get(i + 4));

            book_where.addView(ly);
            i += 5;
        }
        get(getString(R.string.lib_main) + isbn, "LIB", 0x101, 21, "utf-8");
    }
}