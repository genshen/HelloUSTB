package me.gensh.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
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
import butterknife.OnClick;
import me.gensh.helloustb.Browser;
import me.gensh.helloustb.R;
import me.gensh.helloustb.WebNotificationsActivity;
import me.gensh.utils.NetWorkFragment;

/**
 * * Created by gensh on 2017/9/1.
 */
public class HomeFragment extends NetWorkFragment {
    //    private OnHomeFragmentInteractionListener mListener;
    SimpleAdapter listAdapter;
    List<Map<String, String>> notificationCardListData = new ArrayList<>();
    final static String KEY_URL = "url", KEY_TITLE = "title", KEY_DATE = "date";
    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;
    @BindView(R.id.notification_card_list)
    ListView notificationCardListView;
    @BindView(R.id.notification_card_hint)
    AppCompatTextView notificationCardHint;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     **/
    public static HomeFragment newInstance() {
        //        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { //todo
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        listAdapter = new SimpleAdapter(getActivity(), notificationCardListData, R.layout.listview_notisification,
                new String[]{KEY_TITLE, KEY_DATE}, new int[]{R.id.listview_notification_title, R.id.listview_notification_date});
        notificationCardListView.setAdapter(listAdapter);
        notificationCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Browser.openBrowserWithUrl(getActivity(), "http://teach.ustb.edu.cn/" + notificationCardListData.get(position).get(KEY_URL));
            }
        });

        //network
        get(getString(R.string.teach), "TEACH", 0x101, 1, "gb2312", true); //todo
        setErrorHandler(new ErrorHandler() {
            @Override
            public void onPasswordError() {
                dismissProgressDialog();
                Toast.makeText(getActivity(), R.string.errorPassword, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTimeoutError() {
                dismissProgressDialog();
                Toast.makeText(getActivity(), R.string.connectionTimeout, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @OnClick(R.id.notification_card_btn_see_more)
    public void seeMoreNotification(Button button) {
        if (originResponseData.size() != 0) {
            //// TODO: 2017/9/14
            Intent intent = new Intent(getActivity(), WebNotificationsActivity.class);
            intent.putExtra(WebNotificationsActivity.EXTRA_WEB_NOTIFICATIONS, originResponseData);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                        notificationCardListView, getString(R.string.transition_element_name_for_web_notifications)).toBundle());
            } else {
                startActivity(intent);
            }
        }
    }

    /**
     * deliver origin http response data to {@link me.gensh.helloustb.WebNotificationsActivity}
     */
    ArrayList<String> originResponseData = new ArrayList<>();

    @Override
    public void RequestResultHandler(int what, ArrayList<String> data) {
        dismissProgressDialog();
        if (what == 0x101) {
            if (data != null) {
                originResponseData = data;
                int size = data.size();
                for (int i = 0; i < size / 3 && i < 3; i++) { //3 message at most!
                    Map<String, String> listitem = new HashMap<>();
                    listitem.put(KEY_URL, data.get(3 * i));
                    listitem.put(KEY_TITLE, data.get(3 * i + 1));
                    listitem.put(KEY_DATE, data.get(3 * i + 2));
                    notificationCardListData.add(listitem);
                }
                if (notificationCardListData.size() > 0) { //has data
                    notificationCardHint.setVisibility(View.GONE);
                }
                listAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(notificationCardListView);
            }
        }
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
        Toast.makeText(getActivity(), R.string.NoNetwork, Toast.LENGTH_LONG).show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
