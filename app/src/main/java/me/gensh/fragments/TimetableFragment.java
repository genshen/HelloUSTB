package me.gensh.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import me.gensh.database.DBTimetable;
import me.gensh.database.QueryData;
import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.helloustb.TimetableDetail;
import me.gensh.utils.BasicDate;
import me.gensh.utils.Const;
import me.gensh.utils.TimeTableList;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gensh on 2015/11/22.
 */
public class TimetableFragment extends Fragment {
    final int databaseVersion = 1;
    boolean hasImported = false;
    int position, week_num;
    TimeTableList ttl;
    TimeTableAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        long week_start_days = pre.getLong(Const.Settings.KEY_WEEK_START, 0);
        week_num = BasicDate.getWeekNum(week_start_days);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        position = FragmentPagerItem.getPosition(getArguments());
//        CourseDbHelper courseDb = new CourseDbHelper(getActivity(), databaseVersion);
        if (QueryData.haveCoursesImported(((MyApplication) getContext().getApplicationContext()).getDaoSession())) {
            hasImported = true;
            adapter = new TimeTableAdapter(ttl, getActivity(), position);
            View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
            ((ListView) rootView.findViewById(R.id.timetableListView)).setAdapter(adapter);
            return rootView;
        }
//            Toast.makeText(getActivity(), R.string.no_course_imported, Toast.LENGTH_SHORT).show();
//            Snackbar.make(getActivity().findViewById(R.id.timetableListView),  R.string.no_course_imported,
//                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasImported) {
            // todo in a new thread?
            List<DBTimetable> someDayCoursesList = QueryData.getSomedayCourse(((MyApplication) getContext().getApplicationContext()).getDaoSession(), position);
            adapter.ttl = new TimeTableList(someDayCoursesList);
            adapter.notifyDataSetChanged();
        }
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
            View root_view = LayoutInflater.from(mContext).inflate(R.layout.listview_timetable, null);
            TextView lesson_id = root_view.findViewById(R.id.timetable_lesson_id);
            LinearLayout lesson_summary = root_view.findViewById(R.id.timetable_lesson_summary);
            lesson_id.setText(getContext().getString(R.string.lesson_id, position + 1));

            if (ttl == null) return root_view;

            ArrayList<Map<String, Object>> c = ttl.getCourseListByLessonNo(position);
            int size = c.size();
            for (int i = 0; i < size; i++) {
                TextView tv = new TextView(mContext);
                Map<String, Object> lesson = c.get(i);
                final long _id = (long) lesson.get(DBTimetable.TimetableInfo._ID);
                String content = lesson.get(DBTimetable.TimetableInfo.COURSE_NAME) + "\n" +
                        lesson.get(DBTimetable.TimetableInfo.TEACHERS) + "\n" +
                        lesson.get(DBTimetable.TimetableInfo.PLACE) + "\n" +
                        "第" + lesson.get(DBTimetable.TimetableInfo.WEEKS);

                tv.setText(content);
                tv.setPadding(0, 5, 0, 5);
                if ((((int) lesson.get(DBTimetable.TimetableInfo.WEEK_ID)) >> week_num & 1) == 1) {
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                }
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TimetableDetail.class);
                        intent.putExtra("_id", _id);
                        startActivity(intent);
                    }
                });
                lesson_summary.addView(tv);
            }    //end for

            return root_view;
        }
    }
}