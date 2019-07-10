package me.gensh.helloustb;

import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.database.DBTimetable;
import me.gensh.database.UpdateData;
import me.gensh.views.MultiSelectView;
import me.gensh.views.SelectWheel;

public class TimetableEdit extends AppCompatActivity {
    // UI references.
    final public static String ADD_NEW_FLAG = "add_new";
    final public static String DATA_FLAG = "data";
    final public static String ID_FLAG = "_id";

    @BindView(R.id.edit_course_name)
    public EditText courseNameEdit;
    @BindView(R.id.edit_course_teacher)
    public EditText courseTeacherEdit;
    @BindView(R.id.edit_course_place)
    public EditText coursePlaceEdit;
    @BindView(R.id.edit_course_week_id_summary)
    public AppCompatTextView courseWeekIdEdit;
    @BindView(R.id.edit_course_lesson_no_summary)
    public AppCompatTextView courseLessonNoEdit;

    DBTimetable courseDetail;
    String orgTimePlace;   // todo when add lesson!
    long _id;
    boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        isNew = bundle.getBoolean(ADD_NEW_FLAG);
        if (!isNew) {
            //make sure courseDetail from detail Activity is not nul!.
            courseDetail = (DBTimetable) bundle.getSerializable(DATA_FLAG);
            _id = bundle.getLong(ID_FLAG);
            setViewData();
        }

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });
    }

    private void setViewData() {
        courseNameEdit.setText(courseDetail.getCourseName());
        courseTeacherEdit.setText(courseDetail.getTeachers());
        coursePlaceEdit.setText(courseDetail.getPlace());

        courseWeekIdEdit.setText(courseDetail.getWeeks()); // set week_id instead
        courseLessonNoEdit.setText(getLessonValue());

        orgTimePlace = getAllTimePlace();// todo when add lesson!
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_timetable_detail, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void editHandle(View view) {
        switch (view.getId()) {
            case R.id.edit_course_week_id_container:
                LayoutInflater factory_week_id = LayoutInflater.from(this);
                final View dialogMultiSelect = factory_week_id.inflate(R.layout.dialog_edit_week_id_selecort, null);
                final MultiSelectView multiSelectView = dialogMultiSelect.findViewById(R.id.week_id_selector);
                multiSelectView.setSelect(courseDetail.getWeekId());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.edit_alert_week_id_title)
                        .setView(dialogMultiSelect)
                        .setNeutralButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int value = (int) multiSelectView.getSelected();
                                String weeks = getWeeksValue(value);
                                courseDetail.setWeekId(value);
                                courseDetail.setWeeks(weeks);

                                courseWeekIdEdit.setText(weeks);
                            }
                        })
                        .show();
                break;
            case R.id.edit_course_lesson_no_container:
                LayoutInflater factory = LayoutInflater.from(this);
                final View dialogWheelView = factory.inflate(R.layout.dialog_edit_course_selector, null);
                final SelectWheel wheel_view_week_days = dialogWheelView.findViewById(R.id.wheel_view_week_days);
                final SelectWheel wheel_view_lesson_no = dialogWheelView.findViewById(R.id.wheel_view_lesson_no);
                wheel_view_week_days.selectIndex(courseDetail.getWeekDay());
                wheel_view_lesson_no.selectIndex(courseDetail.getLessonNo());

                new AlertDialog.Builder(this)
                        .setTitle(R.string.edit_alert_lesson_no_title)
                        .setView(dialogWheelView)
                        .setNeutralButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                courseDetail.setWeekDay(wheel_view_week_days.getSelectedPosition());
                                courseDetail.setLessonNo(wheel_view_lesson_no.getSelectedPosition());
                                courseLessonNoEdit.setText(getLessonValue());
                            }
                        })
                        .show();
                break;
        }
    }

    private void attemptSubmit() {
        String name = courseNameEdit.getText().toString();
        String teachers = courseTeacherEdit.getText().toString();
        String place = coursePlaceEdit.getText().toString();
        String weeks = courseWeekIdEdit.getText().toString();
        int week_id = courseDetail.getWeekId();
        if (name.isEmpty()) {
            Snackbar.make(courseNameEdit, R.string.error_empty_course_name, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (teachers.isEmpty()) {
            Snackbar.make(courseNameEdit, R.string.error_empty_course_teacher, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (place.isEmpty()) {
            Snackbar.make(courseNameEdit, R.string.error_empty_course_place, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (week_id == 0) {
            Snackbar.make(courseNameEdit, R.string.error_week_id_needed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            // todo change AllTimePlace
            UpdateData.submitCourseInfoEdit(((MyApplication) getApplication()).getDaoSession(),
                    _id, name, teachers, place, weeks, week_id, courseDetail.getWeekDay(), courseDetail.getLessonNo());

            finish();
        }
    }

    private String getLessonValue() {
        return "周" + (courseDetail.getWeekDay() + 1) + " 第" + (courseDetail.getLessonNo() + 1) + "节";
    }

    final static int INT_LENGTH = 32;

    private static String getWeeksValue(int value) {
        StringBuilder builder = new StringBuilder();
        boolean startFlag = false, isFirst = true;
        int tempStart = 0;
        for (int i = 0; i < INT_LENGTH; i++) {
            if ((value >> i & 1) == 1) {
                if (!startFlag) {
                    if (!isFirst) {
                        builder.append(',');
                    }
                    startFlag = true;
                    isFirst = false;
                    tempStart = i;
                }

            } else {
                if (startFlag) {
                    startFlag = false;
                    if (tempStart + 1 == i) {
                        builder.append(i);
                    } else {
                        builder.append(tempStart + 1);
                        builder.append('-');
                        builder.append(i);
                    }
                }
            }
        }
        builder.append('周');
        return builder.toString();
    }

    private String setAllTimePlace(String orgTimePlace) {
        // todo when add lesson!
        return orgTimePlace.replaceFirst(this.orgTimePlace, getAllTimePlace());
    }

    // todo when add lesson!
    private String getAllTimePlace() {
        return "周" + (courseDetail.getWeekDay() + 1) + ",第" +
                (courseDetail.getLessonNo() + 1) + "节," +
                courseDetail.getWeeks() + " " +
                courseDetail.getPlace();
    }

}

