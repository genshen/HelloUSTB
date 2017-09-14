package me.gensh.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gensh.helloustb.R;

/**
 * A placeholder fragment containing a simple view.
 * created by gensh on 2017/09/10
 */
public class ELearningExamQueryFragment extends Fragment {

    private static final String ARG_PARAM_EXAM_QUERY = "ARG_PARAM_EXAM_QUERY";
    private List<Map<String, Object>> listItems = new ArrayList<>();

    public ELearningExamQueryFragment() {
    }

    /**
     * @param exam data from http response in its parent activity
     */
    public static ELearningExamQueryFragment newInstance(ArrayList<String> exam) {
        ELearningExamQueryFragment fragment = new ELearningExamQueryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM_EXAM_QUERY, exam);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final ArrayList<String> examPlaceQueryResults = getArguments().getStringArrayList(ARG_PARAM_EXAM_QUERY);
            if (examPlaceQueryResults != null) {
                int size = examPlaceQueryResults.size();
                //添加考试周的考试
                for (int i = 0; i < size / 5; i++) {
                    if (!examPlaceQueryResults.get(i * 5 + 3).isEmpty()) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("name", examPlaceQueryResults.get(i * 5 + 1));
                        m.put("time", examPlaceQueryResults.get(i * 5 + 2));
                        m.put("place", examPlaceQueryResults.get(i * 5 + 3));
                        m.put("remark", examPlaceQueryResults.get(i * 5 + 4));
                        listItems.add(m);
                    }
                }
            }

//        //添加非考试周的考试
//        for (int i = 0; i < size / 5; i++) {
//            if(listdata.get(i * 5 + 3).isEmpty()) {
//                Map<String, Object> m = new HashMap<>();
//                m.put("name", listdata.get(i * 5 + 1));
//                m.put("time", listdata.get(i * 5 + 2));
//                m.put("place", listdata.get(i * 5 + 3));
//                m.put("remark", listdata.get(i * 5 + 4));
//                listItems.add(m);
//            }
//        }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elearning_exam_query, container, false);
        ListView examPlaceList = view.findViewById(R.id.exam_query_listview);
        SimpleAdapter examAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.listview_exam_query,
                new String[]{"name", "time", "place"},
                new int[]{R.id.exam_query_name, R.id.exam_query_time, R.id.exam_query_place});
        examPlaceList.setAdapter(examAdapter);

        if (listItems == null || listItems.size() == 0) {
            AppCompatTextView examQueryResultHint = view.findViewById(R.id.exam_query_result_hint);
            examQueryResultHint.setVisibility(View.VISIBLE);
        }
        return view;
    }

    //static methods below.
    public static Map<String, String> loadExamPlaceQueryRequestParams(String username) {
        Map<String, String> m = new HashMap<>();
        m.put("uid", username);
        m.put("winName", "examListPanel");
        m.put("listXnxq", getExamPlaceQueryRequestParamsYear());
        return m;
    }

    private static String getExamPlaceQueryRequestParamsYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month <= 7 && month > 3) {
            return (year - 1) + "-" + year + "-2";
        } else if (month > 7) {
            return (year) + "-" + (year + 1) + "-1";
        } else {
            return (year - 1) + "-" + year + "-1";
        }
    }
}
