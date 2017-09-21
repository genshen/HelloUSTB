package me.gensh.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import me.gensh.helloustb.R;
import me.gensh.helloustb.RecordQueryDetail;
import me.gensh.utils.BasicDate;
import me.gensh.utils.SerializableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ELearningRecordQueryFragment extends Fragment {
    private static final String ARG_PARAM_RECORD = "ARG_PARAM_RECORD";
    private String GPA = "0.00";
    private ArrayList<BasicDate.RecordTerm> termList = new ArrayList<>();  //term list
    private List<Map<String, Object>> listItems;   //term list along with adapter
    private List<List<Map<String, Object>>> scoredSplit;  // <term list < course list <single course map>>>

    public static ELearningRecordQueryFragment newInstance(ArrayList<String> scored) {
        ELearningRecordQueryFragment fragment = new ELearningRecordQueryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM_RECORD, scored);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            final ArrayList<String> record = getArguments().getStringArrayList(ARG_PARAM_RECORD);
            GPA = record == null ? "0.00" : record.get(0);
            scoredSplit = BasicDate.getScoredSplit(record, termList);
//        int year = BasicDate.getStartYear(record);
//        int term_length = scoredSplit == null ? 2013 : year + scoredSplit.size();

            listItems = new ArrayList<>();
            int term_length = termList.size();
            for (int i = 0; i < term_length; i++)
                listItems.add(getScoreText(termList.get(i)));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View score_view = inflater.inflate(R.layout.fragment_elearning_record_query, container, false);

        SimpleAdapter homeAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.listview_elearning_record_query,
                new String[]{"score_text_year", "score_text_term"},
                new int[]{R.id.score_query_year, R.id.score_query_term});

        ListView score_list = score_view.findViewById(R.id.score_list);
        score_list.setAdapter(homeAdapter);

        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SerializableList scoreList = new SerializableList();
                scoreList.setList(scoredSplit == null ? null : scoredSplit.get(position));
                Bundle data = new Bundle();
                data.putSerializable("score_data", scoreList);
                data.putString("GPA", GPA);
                Intent record_query = new Intent(getActivity(), RecordQueryDetail.class);
                record_query.putExtras(data);
                startActivity(record_query);
            }
        });
        return score_view;
    }

    private Map<String, Object> getScoreText(BasicDate.RecordTerm term) {
//        year_start = term / 3 + year_start;
//        term = term % 3 + 1;    //学期;
        Map<String, Object> listitem = new HashMap<>();
        listitem.put("score_text_year", term.year + "-" + (term.year + 1) + "学年");
        listitem.put("score_text_term", "第" + term.termIndex + "学期");
        return listitem;
    }
}
