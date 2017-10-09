package me.gensh.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.Const;
import me.gensh.utils.NetWorkActivity;

public class BookDetail extends NetWorkActivity implements HttpRequestTask.OnTaskFinished {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = findViewById(R.id.library_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");

        attemptHttpRequest(HttpClients.HTTP_GET, getLibMainString() + link, Tags.LIB, 0x100, Tags.GET.ID_LIB_BOOK_DETAIL, HttpClients.CHARSET_BTF8, null, false);
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

    //todo data check
    protected void setView(ArrayList<String> data) {
        int i = 0;
        final String isbn = data.get(data.size() - 1);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout book_info = findViewById(R.id.book_info);
        LinearLayout book_where = findViewById(R.id.book_where);

        while (i + 1 < data.size() && data.get(i + 1) != null) {
            LinearLayout ly = (LinearLayout) inflater.inflate(R.layout.inflater_library_book_info, null);
            TextView item_name = ly.findViewById(R.id.book_info_item_name);
            TextView item_detail = ly.findViewById(R.id.book_info_item_detail);
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
            TextView book_where_suoshu = ly.findViewById(R.id.book_where_suoshu);
            TextView book_where_tiaoma = ly.findViewById(R.id.book_where_tiaoma);
            TextView book_where_year = ly.findViewById(R.id.book_where_year);
            TextView book_where_place = ly.findViewById(R.id.book_where_place);
            TextView book_where_state = ly.findViewById(R.id.book_where_state);

            book_where_suoshu.setText(data.get(i));
            book_where_tiaoma.setText(data.get(i + 1));
            book_where_year.setText(data.get(i + 2));
            book_where_place.setText(data.get(i + 3));
            book_where_state.setText(data.get(i + 4));

            book_where.addView(ly);
            i += 5;
        }
        attemptHttpRequest(HttpClients.HTTP_GET, getLibMainString() + isbn, Tags.LIB, 0x101, Tags.GET.ID_LIB_BOOK_DOUBAN, HttpClients.CHARSET_BTF8, null, false);
    }

    private String getLibMainString() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Const.SharedPreferences.KEY_USE_ALTERNATE, false)) {
            return getString(R.string.lib_main_alternate);
        } else {
            return getString(R.string.lib_main);
        }
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        switch (what) {
            case 0x100:
                setView(data);
                break;
            case 0x101:
                final String url = data.get(0);
                TextView douban = findViewById(R.id.to_douban);
                douban.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (url.isEmpty()) {
                            Toast.makeText(BookDetail.this, R.string.no_book_in_douban, Toast.LENGTH_LONG).show();
                        } else {
                            Browser.openBrowserWithUrl(BookDetail.this, url);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onPasswordError() {
        Toast.makeText(getBaseContext(), R.string.errorPassword, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        Toast.makeText(getBaseContext(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
    }

}
