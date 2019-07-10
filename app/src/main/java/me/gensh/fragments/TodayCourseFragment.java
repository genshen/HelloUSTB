package me.gensh.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.QueryData;
import me.gensh.helloustb.MyApplication;
import me.gensh.helloustb.R;
import me.gensh.helloustb.Timetable;
import me.gensh.helloustb.TimetableDetail;
import me.gensh.utils.BasicDate;
import me.gensh.utils.Const;
import me.gensh.views.ViewUtils;

/**
 * Created by gensh on 2015/11/13.
 * uodated by gensh on 2017/9/2.
 */
public class TodayCourseFragment extends Fragment {

    @BindView(R.id.today_course_card_hint)
    AppCompatTextView todayCourseCardHint;
    @BindView(R.id.today_course_import_or_see_all)
    Button todayCourseImportOrSeeAll;
    @BindView(R.id.today_course_list)
    ListView todayCourseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View sch_timetable = inflater.inflate(R.layout.fragment_today_course, container, false);
        ButterKnife.bind(this, sch_timetable);
        return sch_timetable;
    }

    @Override
    public void onResume() {
        super.onResume();
        showCourse();  //if we have imported course,and then I deleted the database(or imported empty timetable),but, then the today course have no updated.todo: bug
    }

    @OnClick(R.id.today_course_import_or_see_all)
    public void seeAll() {
        Intent through_out = new Intent(getActivity(), Timetable.class);
        startActivity(through_out);
    }

    private void showCourse() {
        if (QueryData.haveCoursesImported(((MyApplication) getActivity().getApplicationContext()).getDaoSession())) {
            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
            long week_start_days = pre.getLong(Const.Settings.KEY_WEEK_START, 0);
            final List<HashMap<String, Object>> mapList = QueryData.getTodayCourse(((MyApplication) getActivity().getApplicationContext()).getDaoSession(),
                    BasicDate.getWeekNum(week_start_days));
            if (mapList.size() == 0) { //the default text for todayCourseCardHint is R.string.today_course_card_hint_not_imported
                todayCourseCardHint.setVisibility(View.VISIBLE); //for changing weeks and then resumed.
                todayCourseCardHint.setText(R.string.today_course_card_hint_no_courses);
            } else {
                todayCourseCardHint.setVisibility(View.GONE);
            }
            todayCourseImportOrSeeAll.setText(R.string.today_course_card_see_all);

//          MainActivity.shareCourse = StringUtils.getCourseShareStr(mapList); //todo share courses.

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), mapList, R.layout.listview_today_course,
                    new String[]{"lesson_no", "times", "course_name", "place", "teachers"},
                    new int[]{R.id.Course_No, R.id.Course_Time, R.id.Course_Name, R.id.Course_Room, R.id.Course_Teacher});
            todayCourseList.setAdapter(adapter);

            todayCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= mapList.size()) return;
                    HashMap<String, Object> map = mapList.get(position);
                    long _id = (long) map.get("_id");

                    Intent intent = new Intent(getActivity(), TimetableDetail.class);
                    intent.putExtra("_id", _id);
                    startActivity(intent);
                }
            });
            ViewUtils.setListViewHeightBasedOnChildren(todayCourseList);  //used in NestedScrollView for listView
        }
    }

}
