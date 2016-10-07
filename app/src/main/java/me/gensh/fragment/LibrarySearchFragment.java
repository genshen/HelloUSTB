package me.gensh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import me.gensh.helloustb.BookDetail;
import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.network.DataInfo;
import me.gensh.network.GetPostHandler;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class LibrarySearchFragment extends Fragment {
    private String search_key = "";
    private int page = 1;
    private boolean is_init = false, is_loading = false;
    private SimpleAdapter search_adapter;
    private ListView search_listview;
    private GoogleProgressBar progress_bar;
    MaterialRefreshLayout materialRefreshLayout;

    public LibrarySearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_search_library, container, false);

        materialRefreshLayout = (MaterialRefreshLayout) root_view.findViewById(R.id.lib_refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if (search_key.isEmpty() || is_loading) {
                    materialRefreshLayout.finishRefresh();
                    return;
                }
                page = 1;
                is_init = false;
                get(getSearchURL(search_key, page), "LIB", 0x101, 19, "utf-8");
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (!is_init || is_loading) {
                    materialRefreshLayout.finishRefreshLoadMore();
                    return;
                }
                get(getSearchURL(search_key, page), "LIB", 0x102, 19, "utf-8");
            }
        });

        progress_bar = (GoogleProgressBar) root_view.findViewById(R.id.progress_bar);
        search_listview = (ListView) root_view.findViewById(R.id.lib_search_list);
        setListViewInit();
        Button search_bt = (Button) root_view.findViewById(R.id.search_start);
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_loading){
                    is_init = false;
                    search();
                }

            }
        });
        return root_view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            is_loading = false;
            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
                progress_bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;
            switch (msg.what) {
                case 0x100: //response for click search
                    progress_bar.setVisibility(View.INVISIBLE);
                    if (str_msg.size() == 0) {
                        Toast.makeText(getActivity(), R.string.no_search_result, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    page++;
                    is_init = true;
                    setSearchResult(str_msg, true);
                    break;
                case 0x101: // response for refresh
                    materialRefreshLayout.finishRefresh();
                    if (str_msg.size() == 0) {    //如果没找到书籍
                        Toast.makeText(getActivity(), R.string.no_book_found, Toast.LENGTH_LONG).show();
                        return;
                    }
                    page++;
                    is_init = true;
                    setSearchResult(str_msg, true);
                    break;
                case 0x102: //response for load more
                    materialRefreshLayout.finishRefreshLoadMore();
                    if (checkIndex(str_msg)) {    //看是否到达索引结尾
                        Toast.makeText(getActivity(), R.string.no_book_found_more, Toast.LENGTH_LONG).show();
                        break;
                    }
                    page++;
                    setSearchResult(str_msg, false);
                    break;

            }
        }
    };

    private List<Map<String, Object>> search_list = new ArrayList<>();
    private void setListViewInit() {
        search_adapter = new SimpleAdapter(getActivity(), search_list, R.layout.listview_lib_search_result,
                new String[]{"lib_type", "lib_name", "lib_num", "lib_copy", "lib_borrow",
                        "lib_author", "lib_press_name", "lib_publish_time"},
                new int[]{R.id.lib_search_type, R.id.lib_search_name, R.id.lib_search_num,
                        R.id.lib_search_copy, R.id.lib_search_borrow, R.id.lib_search_author,
                        R.id.lib_search_press_name, R.id.lib_search_publish_time});
        search_listview.setAdapter(search_adapter);
        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            get(getSearchURL(search_key, page), "LIB", 0x100, 19, "utf-8");
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
        return getString(R.string.lib_search_address)
                + "?strSearchType=title&strText=" + search_url + "&page=" + page;
    }

    private void get(String url, String tag, int feedback, int id, String code) {
        if (((MyApplication) getActivity().getApplication()).CheckNetwork()) {
            if (feedback == 0x100) { // clicked search button
                progress_bar.setVisibility(View.VISIBLE);
            }
            is_loading = true;
            GetPostHandler.handlerGet(handler, url, tag, feedback, id, code);
        } else {
            Toast.makeText(getActivity(), R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

}
