package com.holo.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.holo.base.BasicDate;
import com.holo.base.StrPro;
import com.holo.dataBase.CourseDbHelper;
import com.holo.dataBase.QueryData;
import com.holo.helloustb.MainActivity;
import com.holo.helloustb.R;
import com.holo.helloustb.TimetableDetail;

import java.util.HashMap;
import java.util.List;

public class TodayCourseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View sch_timetable = inflater.inflate(R.layout.fragment_today_course, container, false);
        showCourse(sch_timetable);
        return sch_timetable;
    }

    private void showCourse(View sch_timetable) {
        CourseDbHelper course = new CourseDbHelper(getActivity(), 1);
        if (!course.isTableEmpty()) {
            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
            long week_start_days = pre.getLong(GeneralSettingFragment.WEEK_START, 0);
            QueryData qd = new QueryData(getActivity());
            final List<HashMap<String, Object>> mapList = qd.getTodayCourse(BasicDate.getWeekNum(week_start_days));
            MainActivity.shareCourse = StrPro.getCourseShareStr(mapList);

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), mapList, R.layout.listview_today_course,
                    new String[]{"lesson_no", "times", "course_name", "place", "teachers"},
                    new int[]{R.id.Course_No, R.id.Course_Time, R.id.Course_Name, R.id.Course_Room, R.id.Course_Teacher});

            ListView mylist = (ListView) sch_timetable.findViewById(R.id.CourseListView);
            mylist.setAdapter(adapter);

            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= mapList.size()) return;
                    HashMap<String, Object> map = mapList.get(position);
                    int _id = (int)map.get("_id");

                    Intent intent = new Intent(getActivity(), TimetableDetail.class);
                    intent.putExtra("_id", _id);
                    startActivity(intent);
                }
            });
            qd.close();
        }

        course.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    private void CursorToMap(Cursor cursor) {
//
//    }
//    getCourseImage(){
//
//    }
}
