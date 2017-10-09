package me.gensh.helloustb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import me.gensh.database.DBTimetable;
import me.gensh.database.DeleteData;
import me.gensh.database.QueryData;

public class TimetableDetail extends AppCompatActivity {
    long _id;
    int i = 0;
    DBTimetable courseDetail;

    Toolbar toolbar;
    @BindViews({R.id.detail_course_name_value, R.id.detail_course_teacher_value,
            R.id.detail_course_place_value, R.id.detail_course_time_value,
            R.id.detail_course_week_value, R.id.detail_course_position_value,
            R.id.detail_course_student_num_value, R.id.detail_course_learn_time_value,
            R.id.detail_course_credit_value, R.id.detail_course_type_value,
            R.id.detail_course_time_place_value})
    List<TextView> listTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        _id = intent.getLongExtra("_id", 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        courseDetail = QueryData.getCourseById(((MyApplication) TimetableDetail.this.getApplicationContext()).getDaoSession(), _id);
        if (courseDetail != null) {
            String positionText = "周" + (courseDetail.getWeekDay() + 1) + " 第" + (courseDetail.getLessonNo() + 1) + "节"; //todo text
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout))
                    .setTitle(getString(R.string.timetable_detail_title, courseDetail.getCourseName()));
            //do not use toolbar or getSupportActionBar to set title.

            //textView list as: "course_name", "teachers", "place", "times", "weeks", "position", "student_num","learn_time", "credit", "course_type", "time_place"
            listTextViews.get(0).setText(courseDetail.getCourseName());
            listTextViews.get(1).setText(courseDetail.getTeachers());
            listTextViews.get(2).setText(courseDetail.getPlace());
            listTextViews.get(3).setText(courseDetail.getTime());
            listTextViews.get(4).setText(courseDetail.getWeeks());
            listTextViews.get(5).setText(positionText);
            listTextViews.get(6).setText(getString(R.string.timetable_detail_integer_format, courseDetail.getStudentNum()));
            listTextViews.get(7).setText(getString(R.string.timetable_detail_integer_format, courseDetail.getLearnTime()));
            listTextViews.get(8).setText(getString(R.string.timetable_detail_integer_format, courseDetail.getCredit()));
            listTextViews.get(9).setText(courseDetail.getCourseType());
            listTextViews.get(10).setText(courseDetail.getTimeAndPlace());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable_detail, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                if (courseDetail != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(TimetableEdit.DATA_FLAG, courseDetail);
                    bundle.putBoolean(TimetableEdit.ADD_NEW_FLAG, false);
                    bundle.putLong(TimetableEdit.ID_FLAG, _id);
                    Intent intent = new Intent(this, TimetableEdit.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.action_delete:
                if (courseDetail != null) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_delete_course_ack_title)
                            .setMessage(getString(R.string.dialog_delete_course_ack_message,
                                    courseDetail.getCourseName()))
                            .setNegativeButton(R.string.alert_cancel, null)
                            .setPositiveButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteData.deleteCourseById(((MyApplication) TimetableDetail.this.getApplicationContext()).getDaoSession(), _id);
                                    finish();
                                }
                            })
                            .show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
