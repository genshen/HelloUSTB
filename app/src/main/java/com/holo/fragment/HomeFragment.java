package com.holo.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.holo.helloustb.Browser;
import com.holo.helloustb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by 根深 on 2015/11/13.
 */
public class HomeFragment extends Fragment {
    int[] randomColor = {R.color.green, R.color.yellow, R.color.side_nav_bar,
            R.color.red_light, R.color.purple, R.color.bright_yellow};
    final int  randomLength = 6;
    int random = (int) (Math.random() * 10) % randomLength;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        final ArrayList<String> home_info = args.getStringArrayList("home");

        View notice = inflater.inflate(R.layout.fragment_home, container, false);

        List<Map<String, Object>> listitems = new ArrayList<>();
        int size = home_info.size();
        for (int i = 0; i < size / 3; i++) {
            listitems.add(addData(home_info.get(3 * i + 1), home_info.get(3 * i + 2)));
        }

        SimpleAdapter home_adapter = new SimpleAdapter(getActivity(), listitems, R.layout.listview_home,
                new String[]{"home_title", "home_date", "left_color", "right_color"},
                new int[]{R.id.home_title, R.id.home_date, R.id.home_card_left, R.id.home_card_right});
        ListView home_list = (ListView) notice.findViewById(R.id.home_select);
        home_list.setAdapter(home_adapter);

        home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle data = new Bundle();
                data.putSerializable("url", "http://teach.ustb.edu.cn/" + home_info.get(3 * position));
                Intent browser = new Intent(getActivity(), Browser.class);
                browser.putExtras(data);
                startActivity(browser);
            }
        });
        return notice;
    }

    private Map<String, Object> addData(String title, String date) {
        Map<String, Object> listitem = new HashMap<>();
        listitem.put("home_title", title);
        listitem.put("home_date", date);
        listitem.put("left_color", randomColor[random]);
        random = (random + 1) % randomLength;
        listitem.put("right_color", randomColor[random]);

        return listitem;
    }
}
