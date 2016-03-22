package com.holo.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holo.base.TimeTableList;
import com.holo.dataBase.CourseDbHelper;
import com.holo.dataBase.QueryData;
import com.holo.helloustb.R;
import com.holo.helloustb.TimetableDetail;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 根深 on 2015/11/22.
 */
public class TimetableFragment extends Fragment {
    final int databaseVersion = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int position = FragmentPagerItem.getPosition(getArguments());

        CourseDbHelper courseDb = new CourseDbHelper(getActivity(), databaseVersion);
        if (!courseDb.isTableEmpty()) {
            QueryData qd = new QueryData(getActivity());

            Cursor cursor = qd.getSomedayCourse(position);
            TimeTableList ttl = new TimeTableList(cursor);
            cursor.close();

            TimeTableAdapter adapter = new TimeTableAdapter(ttl, getActivity(), position);
            View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
            ((ListView) rootView.findViewById(R.id.timetableListView)).setAdapter(adapter);
            return rootView;
        } else {
            Toast.makeText(getActivity(), R.string.no_course_imported, Toast.LENGTH_SHORT).show();
//            Snackbar.make(getActivity().findViewById(R.id.timetableListView),  R.string.no_course_imported,
//                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private class TimeTableAdapter extends BaseAdapter {

        private TimeTableList ttl;
        private Context mContext;
        private int week;

        public TimeTableAdapter(TimeTableList ttl, Context context, int week) {
            this.ttl = ttl;
            this.mContext = context;
            this.week = week;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 由于只有6个，所以可以不考虑view的回收
            final int pos = position;
            View rootview = LayoutInflater.from(mContext).inflate(R.layout.listview_timetable, null);
            TextView lesson_id = (TextView) rootview.findViewById(R.id.timetable_lesson_id);
            LinearLayout lesson_summary = (LinearLayout) rootview.findViewById(R.id.timetable_lesson_summary);
            lesson_id.setText(getContext().getString(R.string.lesson_id, position + 1));

            ArrayList<Map<String, Object>> c = ttl.getCourseList(position);
            int size = c.size();
            for (int i = 0; i < size; i++) {
                TextView tv = new TextView(mContext);
                Map<String, Object> lesson = c.get(i);
                final String course_id = lesson.get("course_id").toString();
                String content = lesson.get("course_name") + "\n" +
                        lesson.get("teachers") + "\n" +
                        lesson.get("place") + "\n" +
                        "第" + lesson.get("weeks");

                tv.setText(content);
                tv.setPadding(0, 5, 0, 5);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TimetableDetail.class);
                        intent.putExtra("week", week);
                        intent.putExtra("course_id", course_id);
                        intent.putExtra("position", pos);
                        startActivity(intent);
                    }
                });
                lesson_summary.addView(tv);
            }    //end for

            return rootview;
        }
    }
}