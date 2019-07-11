package me.gensh.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.helloustb.BookDetail;
import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.Const;
import me.gensh.utils.NetWorkFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class LibrarySearchFragment extends NetWorkFragment implements HttpRequestTask.OnTaskFinished {
    private String search_key = "";
    private int page = 1;
    private boolean isInitialized = false, isLoading = false;
    private SimpleAdapter search_adapter;
    @BindView(R.id.lib_search_list)
    ListView searchListView;
    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;
    @BindView(R.id.lib_refresh)
    MaterialRefreshLayout materialRefreshLayout;

    SharedPreferences sharedPreferences;

    public LibrarySearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_library, container, false);
        ButterKnife.bind(this, rootView);

        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if (search_key.isEmpty() || isLoading) {
                    materialRefreshLayout.finishRefresh();
                    return;
                }
                page = 1;
                isInitialized = false;
                get(getSearchURL(search_key, page), Tags.LIB, 0x101, Tags.GET.ID_LIB_SEARCH, HttpClients.CHARSET_BTF8);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (!isInitialized || isLoading) {
                    materialRefreshLayout.finishRefreshLoadMore();
                    return;
                }
                get(getSearchURL(search_key, page), Tags.LIB, 0x102, Tags.GET.ID_LIB_SEARCH, HttpClients.CHARSET_BTF8);
            }
        });

        setListViewInit();
        Button search_bt = rootView.findViewById(R.id.search_start);
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading) {
                    isInitialized = false;
                    search();
                }
            }
        });
        return rootView;
    }

    private List<Map<String, Object>> search_list = new ArrayList<>();

    private void setListViewInit() {
        search_adapter = new SimpleAdapter(getActivity(), search_list, R.layout.listview_lib_search_result,
                new String[]{"lib_type", "lib_name", "lib_num", "lib_copy", "lib_borrow",
                        "lib_author", "lib_press_name", "lib_publish_time"},
                new int[]{R.id.lib_search_type, R.id.lib_search_name, R.id.lib_search_num,
                        R.id.lib_search_copy, R.id.lib_search_borrow, R.id.lib_search_author,
                        R.id.lib_search_press_name, R.id.lib_search_publish_time});
        searchListView.setAdapter(search_adapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), BookDetail.class);
                detail.putExtra("link", search_list.get(position).get("lib_link").toString());
                //position -1?
                startActivity(detail);
            }
        });
    }

    private void search() {
        String search_key = ((EditText) getActivity().findViewById(R.id.lib_search_text)).getText().toString();
        if (!search_key.isEmpty()) {
            this.search_key = search_key;
            page = 1;   //页数还原;
            get(getSearchURL(search_key, page), Tags.LIB, 0x100, Tags.GET.ID_LIB_SEARCH, HttpClients.CHARSET_BTF8);
        }
    }

    private void setSearchResult(ArrayList<String> search_result, boolean clear) {
        if (clear) {
            search_list.clear();
        }
        int length = search_result.size() / 9;
        for (int i = 0; i < length; i++) {
            Map<String, Object> listitem = new HashMap<>();
            listitem.put("lib_type", search_result.get(9 * i));
            listitem.put("lib_link", search_result.get(9 * i + 1));
            listitem.put("lib_name", search_result.get(9 * i + 2));
            listitem.put("lib_num", search_result.get(9 * i + 3));
            listitem.put("lib_copy", search_result.get(9 * i + 4));
            listitem.put("lib_borrow", search_result.get(9 * i + 5));
            listitem.put("lib_author", search_result.get(9 * i + 6));
            listitem.put("lib_press_name", search_result.get(9 * i + 7));
            listitem.put("lib_publish_time", search_result.get(9 * i + 8));
            search_list.add(listitem);
        }
        search_adapter.notifyDataSetChanged();
    }

    protected boolean checkIndex(ArrayList<String> list) {
        int index = 2;
        if (list.size() > index) {
            char[] name = list.get(index).toCharArray();
            int id = 0, i = 0;
            while (name[i] >= 48 && name[i] <= 57) {
                id = id * 10 + name[i] - 48;
                i++;
            }
            if (id == (search_list.size() + 1)) {
                return false;
            }
        }
        return true;
    }

    private String getSearchURL(String search_key, int page) {
        String search_url = "";
        try {
            search_url = URLEncoder.encode(search_key, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (sharedPreferences.getBoolean(Const.SharedPreferences.KEY_USE_ALTERNATE, false)) {
            return getString(R.string.lib_search_address_alternate)
                    + "?strSearchType=title&strText=" + search_url + "&page=" + page;
        } else {
            return getString(R.string.lib_search_address)
                    + "?strSearchType=title&strText=" + search_url + "&page=" + page;
        }

    }

    HttpRequestTask httpRequestTask;

    private void get(String url, String tag, int feedback, int id, String code) {
        if (((MyApplication) getActivity().getApplication()).CheckNetwork()) {
            if (feedback == 0x100) { // clicked search button
                showProgressDialog();
            }
            isLoading = true;
            if (httpRequestTask != null) {   //cancel task if it is not null,to make sure there is one task running in a activity or fragment.
                httpRequestTask.cancel(true);
            }
            httpRequestTask = new HttpRequestTask(getActivity(), HttpClients.HTTP_GET, url, tag, feedback, id, code, null);
            httpRequestTask.setOnTaskFinished(this);
            httpRequestTask.execute();
        } else {
            onNetworkDisabled();
        }
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        isLoading = false;
        switch (what) {
            case 0x100: //response for click search
                dismissProgressDialog();
                if (data.size() == 0) {
                    Toast.makeText(getActivity(), R.string.no_search_result, Toast.LENGTH_SHORT).show();
                    return;
                }
                page++;
                isInitialized = true;
                setSearchResult(data, true);
                break;
            case 0x101: // response for refresh
                materialRefreshLayout.finishRefresh();
                if (data.size() == 0) {    //如果没找到书籍
                    Toast.makeText(getActivity(), R.string.no_book_found, Toast.LENGTH_LONG).show();
                    return;
                }
                page++;
                isInitialized = true;
                setSearchResult(data, true);
                break;
            case 0x102: //response for load more
                materialRefreshLayout.finishRefreshLoadMore();
                if (checkIndex(data)) {    //看是否到达索引结尾
                    Toast.makeText(getActivity(), R.string.no_book_found_more, Toast.LENGTH_LONG).show();
                    break;
                }
                page++;
                setSearchResult(data, false);
                break;
        }
    }

    @Override
    public void onPasswordError() {  //no password.

    }

    @Override
    public void onTimeoutError() {
        isLoading = false;
        dismissProgressDialog();
        Toast.makeText(getActivity(), R.string.connection_timeout, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
    }
}
