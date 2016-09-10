package com.holo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.holo.utils.BasicDate;
import com.holo.utils.SerializableList;
import com.holo.helloustb.R;
import com.holo.helloustb.RecordQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {
    public ArrayList<BasicDate.RecordTerm> termList = new ArrayList<>();

    public static RecordFragment newInstance(ArrayList<String> scored) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("record", scored);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View score_view = inflater.inflate(R.layout.fragment_record, container, false);

        Bundle args = getArguments();
        final ArrayList<String> record = args.getStringArrayList("record");
        final String GPA = record == null ? "0.00" : record.get(0);
        final List<List<Map<String, Object>>> scored_split = BasicDate.getScoredSplit(record, termList);
//        int year = BasicDate.getStartYear(record);
//        int term_length = scored_split == null ? 2013 : year + scored_split.size();

        List<Map<String, Object>> listitems = new ArrayList<>();
        int term_length = termList.size();
        for (int i = 0; i < term_length; i++)
            listitems.add(getScoreText(termList.get(i)));

        SimpleAdapter home_adapter = new SimpleAdapter(getActivity(), listitems, R.layout.listview_score,
                new String[]{"score_text_year", "score_text_term"},
                new int[]{R.id.score_query_year, R.id.score_query_term});

        ListView score_list = (ListView) score_view.findViewById(R.id.score_list);
        score_list.setAdapter(home_adapter);

        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SerializableList scoreList = new SerializableList();
                scoreList.setList(scored_split == null ? null : scored_split.get(position));
                Bundle data = new Bundle();
                data.putSerializable("score_data", scoreList);
                data.putString("GPA", GPA);
                Intent record_query = new Intent(getActivity(), RecordQuery.class);
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
