package me.gensh.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.helloustb.ELearningCategory;
import me.gensh.helloustb.Library;
import me.gensh.helloustb.NetWorkSignIn;
import me.gensh.helloustb.R;
import me.gensh.helloustb.Timetable;
import me.gensh.helloustb.Volunteer;
import me.gensh.helloustb.WebNotificationsActivity;

/**
 * created by gensh on 2027/08/28
 */
public class DashboardFragment extends Fragment {
    @BindView(R.id.grid_view_dashboard)
    GridView gridViewDashboard;

    final static String GRID_VIEW_ITEM_ICON = "icon", GRID_VIEW_ITEM_TITLE = "title";
    final static int GRID_VIEW_ITEM_TAG_SCORE = 0x800, GRID_VIEW_ITEM_TAG_INNOVATION_CREDIT = 0x801, GRID_VIEW_ITEM_TAG_NOTIFICATION = 0x802,
            GRID_VIEW_ITEM_TAG_TIMETABLE = 0x803, GRID_VIEW_ITEM_TAG_NETWORK = 0x804, GRID_VIEW_ITEM_TAG_CAMPUS_CARD = 0x805,
            GRID_VIEW_ITEM_TAG_LIBRARY = 0x806, GRID_VIEW_ITEM_TAG_VOLUNTEER = 0x807, GRID_VIEW_ITEM_TAG_EXAM_QUERY = 0x808;
    final static int GRID_VIEW_ITEM_TAGS[] = {GRID_VIEW_ITEM_TAG_SCORE, GRID_VIEW_ITEM_TAG_INNOVATION_CREDIT, GRID_VIEW_ITEM_TAG_NOTIFICATION,
            GRID_VIEW_ITEM_TAG_TIMETABLE, GRID_VIEW_ITEM_TAG_NETWORK, GRID_VIEW_ITEM_TAG_CAMPUS_CARD,
            GRID_VIEW_ITEM_TAG_LIBRARY, GRID_VIEW_ITEM_TAG_VOLUNTEER, GRID_VIEW_ITEM_TAG_EXAM_QUERY};
    final static int[] resIcons = {R.drawable.ic_dashboard_item_score_query, R.drawable.ic_dashboard_item_innovation_credit,
            R.drawable.ic_dashboard_item_notice, R.drawable.ic_dashboard_item_timetable, R.drawable.ic_dashboard_item_internet,
            R.drawable.ic_dashboard_item_campus_card, R.drawable.ic_dashboard_item_libaray,
            R.drawable.ic_dashboard_item_volunteer, R.drawable.ic_dashboard_item_exam_query,};
    final static String[] resTitles = {"成绩查询", "创新学分", "本科教学网通知", "课程表", "校园网",
            "校园卡消费", "图书馆", "志愿服务", "考试查询"};
    ArrayList<Map<String, Object>> dataList = new ArrayList<>();

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DashboardFragment.
     */
    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);

        DashboardSimpleAdapter adapter = new DashboardSimpleAdapter(getActivity(), getData(), R.layout.grid_view_dashboard,
                new String[]{GRID_VIEW_ITEM_ICON, GRID_VIEW_ITEM_TITLE}, new int[]{R.id.grid_view_dashboard_icon, R.id.grid_view_dashboard_title});
        gridViewDashboard.setAdapter(adapter);
        gridViewDashboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int tag = (int) view.getTag();
                switch (tag) {
                    case GRID_VIEW_ITEM_TAG_SCORE:
                        Intent score = new Intent(getActivity(), ELearningCategory.class);
                        score.putExtra(ELearningCategory.E_LEARNING_EXTRA_TYPE, ELearningCategory.INTENT_TYPE_SCORE_QUERY);
                        startActivity(score);
                        break;
                    case GRID_VIEW_ITEM_TAG_INNOVATION_CREDIT:
                        Intent innovationCredit = new Intent(getActivity(), ELearningCategory.class);
                        innovationCredit.putExtra(ELearningCategory.E_LEARNING_EXTRA_TYPE, ELearningCategory.INTENT_TYPE_INNOVATION_CREDIT);
                        startActivity(innovationCredit);
                        break;
                    case GRID_VIEW_ITEM_TAG_NOTIFICATION:
                        Intent notification = new Intent(getActivity(), WebNotificationsActivity.class);
                        startActivity(notification);
                        break;
                    case GRID_VIEW_ITEM_TAG_TIMETABLE:
                        Intent timetable = new Intent(getActivity(), Timetable.class);
                        startActivity(timetable);
                        break;
                    case GRID_VIEW_ITEM_TAG_NETWORK:
                        Intent network = new Intent(getActivity(), NetWorkSignIn.class);
                        startActivity(network);
                        break;
                    case GRID_VIEW_ITEM_TAG_CAMPUS_CARD:
                        Snackbar.make(gridViewDashboard, R.string.developing, Snackbar.LENGTH_INDEFINITE).show(); //// TODO: 2017/9/14
                        break;
                    case GRID_VIEW_ITEM_TAG_LIBRARY:
                        Intent library = new Intent(getActivity(), Library.class);
                        startActivity(library);
                        break;
                    case GRID_VIEW_ITEM_TAG_VOLUNTEER:
                        Intent volunteer = new Intent(getActivity(), Volunteer.class);
                        startActivity(volunteer);
                        break;
                    case GRID_VIEW_ITEM_TAG_EXAM_QUERY:
                        Intent examQuery = new Intent(getActivity(), ELearningCategory.class);
                        examQuery.putExtra(ELearningCategory.E_LEARNING_EXTRA_TYPE, ELearningCategory.INTENT_TYPE_EXAM_QUERY);
                        startActivity(examQuery);
                        break;
                }
            }
        });
        return view;
    }

    private List<Map<String, Object>> getData() {
        for (int i = 0; i < resIcons.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(GRID_VIEW_ITEM_ICON, resIcons[i]);
            map.put(GRID_VIEW_ITEM_TITLE, resTitles[i]);
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class DashboardSimpleAdapter extends SimpleAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
//            ImageButton btn=(ImageButton) v.findViewById(R.id.icon);
            v.setTag(GRID_VIEW_ITEM_TAGS[position]);
            return v;
        }

        DashboardSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }

}
