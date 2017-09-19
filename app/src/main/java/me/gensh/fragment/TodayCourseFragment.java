package me.gensh.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.CourseDbHelper;
import me.gensh.database.QueryData;
import me.gensh.helloustb.R;
import me.gensh.helloustb.Timetable;
import me.gensh.helloustb.TimetableDetail;
import me.gensh.utils.BasicDate;
import me.gensh.utils.Const;

import java.util.HashMap;
import java.util.List;

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
        showCourse();  //if we have imported course,and then I deleted the database(or imported empty timetable),but, then the todat have no updated.todo: bug
    }

    @OnClick(R.id.today_course_import_or_see_all)
    public void seeAll(View view) {
        Intent through_out = new Intent(getActivity(), Timetable.class);
        startActivity(through_out);
    }

    private void showCourse() {
        CourseDbHelper course = new CourseDbHelper(getActivity(), 1);
        if (!course.haveCoursesImported()) {
            SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(getActivity());
            long week_start_days = pre.getLong(Const.Settings.KEY_WEEK_START, 0);
            QueryData qd = new QueryData(getActivity());
            final List<HashMap<String, Object>> mapList = qd.getTodayCourse(BasicDate.getWeekNum(week_start_days));
            if (mapList.size() == 0) { //the default text for todayCourseCardHint is R.string.today_course_card_hint_not_imported
                todayCourseCardHint.setText(R.string.today_course_card_hint_no_courses);
            } else {
                todayCourseCardHint.setVisibility(View.GONE);
            }
            todayCourseImportOrSeeAll.setText(R.string.today_course_card_see_all);

//          MainActivity.shareCourse = StrUtils.getCourseShareStr(mapList); //todo share courses.

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), mapList, R.layout.listview_today_course,
                    new String[]{"lesson_no", "times", "course_name", "place", "teachers"},
                    new int[]{R.id.Course_No, R.id.Course_Time, R.id.Course_Name, R.id.Course_Room, R.id.Course_Teacher});
            todayCourseList.setAdapter(adapter);

            todayCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= mapList.size()) return;
                    HashMap<String, Object> map = mapList.get(position);
                    int _id = (int) map.get("_id");

                    Intent intent = new Intent(getActivity(), TimetableDetail.class);
                    intent.putExtra("_id", _id);
                    startActivity(intent);
                }
            });
            qd.close();
        }
        course.close();
    }

}
