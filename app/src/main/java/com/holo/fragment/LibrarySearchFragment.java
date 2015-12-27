package com.holo.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.holo.helloustb.BookDetail;
import com.holo.helloustb.MyApplication;
import com.holo.helloustb.R;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.holo.view.ProgressWheel;

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
    private boolean is_init = false;    //
    private List<Map<String, Object>> search_list;
    private SimpleAdapter search_adapter;
    private ListView search_listview;
    private ProgressWheel progress_wheel;
    MaterialRefreshLayout materialRefreshLayout;


    public LibrarySearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_search_library, container, false);

        materialRefreshLayout = (MaterialRefreshLayout) rootview.findViewById(R.id.lib_refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                if (search_key.isEmpty()) {
                    materialRefreshLayout.finishRefresh();
                    return;
                }
                search(search_key);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (!is_init) {
                    materialRefreshLayout.finishRefreshLoadMore();
                    return;
                }
                get(getSearchURL(search_key, page + 1), "LIB", 0x101, 19, "utf-8");
            }
        });

        progress_wheel = (ProgressWheel)rootview.findViewById(R.id.progress_wheel);
        search_listview = (ListView) rootview.findViewById(R.id.lib_search_list);
        Button search_bt = (Button) rootview.findViewById(R.id.search_start);
        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_wheel.setVisibility(View.VISIBLE);
                search();
            }
        });
        return rootview;
    }


    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;
            if (data_info.code == DataInfo.TimeOut) {
                progress_wheel.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<String> str_msg = data_info.data;

            switch (msg.what) {
                case 0x100: //response for click search
                    setListViewInit(str_msg);
                    progress_wheel.setVisibility(View.INVISIBLE);
//                    if (!Library.frontShow)

                    break;
                case 0x101: //response for load more
//                    if (!Library.frontShow) {
                    if (checkIndex(str_msg))    //看是否到达索引结尾
                    {
                        Toast.makeText(getActivity(), R.string.no_book_found_more, Toast.LENGTH_LONG).show();
                        materialRefreshLayout.finishRefreshLoadMore();
                        break;
                    }
                    setSearchResult(str_msg);
                    search_adapter.notifyDataSetChanged();
                    page++;
//                    }
                    //返回结果是否为空?
                    materialRefreshLayout.finishRefreshLoadMore();
                    break;
                case 0x103: // response for refresh
                    materialRefreshLayout.finishRefresh();
                    setListViewInit(str_msg);
                    break;
            }
        }
    };

    private void setListViewInit(ArrayList<String> str_msg) {
        if (str_msg.size() == 0)    //如果没找到书籍
        {
            Toast.makeText(getActivity(), R.string.no_book_found, Toast.LENGTH_LONG).show();
            return;
        }

        setSearchResult(str_msg);
        search_adapter = new SimpleAdapter(getActivity(), search_list, R.layout.listview_lib_search_result,
                new String[]{"lib_type", "lib_name", "lib_num", "lib_copy", "lib_borrow",
                        "lib_author", "lib_press_name", "lib_publish_time"},
                new int[]{R.id.lib_search_type, R.id.lib_search_name, R.id.lib_search_num,
                        R.id.lib_search_copy, R.id.lib_search_borrow, R.id.lib_search_author,
                        R.id.lib_search_press_name, R.id.lib_search_publish_time});
        search_listview.setAdapter(search_adapter);
        is_init = true;

        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), BookDetail.class);
                detail.putExtra("link", search_list.get(position).get("lib_link").toString());
                //position -1?
                startActivity(detail);
            }
        });
//                    }
    }

    private void setSearchResult(ArrayList<String> search_reault) {
        for (int i = 0; i < search_reault.size() / 9; i++) {
            Map<String, Object> listitem = new HashMap<String, Object>();
            listitem.put("lib_type", search_reault.get(9 * i));
            listitem.put("lib_link", search_reault.get(9 * i + 1));    //���û����
            listitem.put("lib_name", search_reault.get(9 * i + 2));
            listitem.put("lib_num", search_reault.get(9 * i + 3));
            listitem.put("lib_copy", search_reault.get(9 * i + 4));
            listitem.put("lib_borrow", search_reault.get(9 * i + 5));
            listitem.put("lib_author", search_reault.get(9 * i + 6));
            listitem.put("lib_press_name", search_reault.get(9 * i + 7));
            listitem.put("lib_publish_time", search_reault.get(9 * i + 8));
            search_list.add(listitem);
        }
    }

    private void search() {
        String search_key = ((EditText) getActivity().findViewById(R.id.lib_search_text)).getText().toString();
        if (!search_key.isEmpty()) {
            this.search_key = search_key;
            page = 1;    //页数还原;
            search_list = new ArrayList<>();    //列表清空
            get(getSearchURL(search_key, page), "LIB", 0x100, 19, "utf-8");
        }
    }

    private void search(String key) {
        page = 1;    //页数还原;
        search_list = new ArrayList<>();    //列表清空
        get(getSearchURL(key, page), "LIB", 0x103, 19, "utf-8");
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
        return getString(R.string.lib_search_address)
                + "?strSearchType=title&strText="
                + search_key + "&page=" + page;
    }

    private void get(String url, String tag, int feedback, int id, String code) {
        if (((MyApplication) getActivity().getApplication()).CheckNetwork()) {
            GetPostHandler.handlerGet(handler, url, tag, feedback, id, code);
        } else {
            Toast.makeText(getActivity(), R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

}
