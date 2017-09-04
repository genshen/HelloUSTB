package me.gensh.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import me.gensh.helloustb.Browser;
import me.gensh.helloustb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gensh on 2015/11/13.
 */
public class WebNotificationFragment extends Fragment {
    int[] randomColor = {R.color.green, R.color.yellow, R.color.side_nav_bar,
            R.color.red_light, R.color.purple, R.color.bright_yellow};
    final int randomLength = 6;
    int random = (int) (Math.random() * 10) % randomLength;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        final ArrayList<String> webNotificationInfo = args.getStringArrayList("home");

        View notice = inflater.inflate(R.layout.fragment_web_notification, container, false);

        List<Map<String, Object>> listitems = new ArrayList<>();
        int size = webNotificationInfo.size();
        for (int i = 0; i < size / 3; i++) {
            listitems.add(addData(webNotificationInfo.get(3 * i + 1), webNotificationInfo.get(3 * i + 2)));
        }

        SimpleAdapter webNotificationAdapter = new SimpleAdapter(getActivity(), listitems, R.layout.listview_web_notification,
                new String[]{"title", "date", "left_color", "right_color"},
                new int[]{R.id.web_notification_title, R.id.web_notification_date, R.id.web_notification_card_left, R.id.web_notification_card_right});
        ListView webNotificationList = notice.findViewById(R.id.web_notification_select);
        webNotificationList.setAdapter(webNotificationAdapter);

        webNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Browser.openBrowserWithUrl(getActivity(), "http://teach.ustb.edu.cn/" + webNotificationInfo.get(3 * position));
            }
        });
        return notice;
    }

    private Map<String, Object> addData(String title, String date) {
        Map<String, Object> listitem = new HashMap<>();
        listitem.put("title", title);
        listitem.put("date", date);
        listitem.put("left_color", randomColor[random]);
        random = (random + 1) % randomLength;
        listitem.put("right_color", randomColor[random]);

        return listitem;
    }
}
