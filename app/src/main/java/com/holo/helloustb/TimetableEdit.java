package com.holo.helloustb;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.holo.dataBase.CourseDbHelper;
import com.holo.dataBase.UpdateData;
import com.holo.view.MultiSelectView;
import com.holo.view.SelectWheel;

import java.util.HashMap;

public class TimetableEdit extends AppCompatActivity {
    // UI references.
    public static String ADD_NEW_FLAG = "add_new";
    public static String DATA_FLAG = "data";
    public static String ID_FLAG = "_id";

    private EditText courseNameEdit, courseTeacherEdit, coursePlaceEdit;
    AppCompatTextView courseWeekIdEdit, courseLessonNoEdit;

    HashMap<String, Object> course_detail;
    String orgTimePlace;   // todo when add lesson!
    int _id;
    boolean is_new = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initViews();
        Bundle bundle = getIntent().getExtras();
        is_new = bundle.getBoolean(ADD_NEW_FLAG);
        if (!is_new) {
            course_detail = (HashMap<String, Object>) bundle.getSerializable(DATA_FLAG);
            _id = bundle.getInt(ID_FLAG);
            setViewData();
        }

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });
    }

    private void setViewData() {
        courseNameEdit.setText(course_detail.get(CourseDbHelper.CourseInfoTable.COURSE_NAME).toString());
        courseTeacherEdit.setText(course_detail.get(CourseDbHelper.CourseInfoTable.TEACHERS).toString());
        coursePlaceEdit.setText(course_detail.get(CourseDbHelper.CourseInfoTable.PLACE).toString());

        courseWeekIdEdit.setText(course_detail.get(CourseDbHelper.CourseInfoTable.WEEKS).toString()); // set week_id instead
        courseLessonNoEdit.setText(getLessonValue());

        orgTimePlace = getAllTimePlace();// todo when add lesson!
    }

    private void initViews() {
        courseNameEdit = (EditText) findViewById(R.id.edit_course_name);
        courseTeacherEdit = (EditText) findViewById(R.id.edit_course_teacher);
        coursePlaceEdit = (EditText) findViewById(R.id.edit_course_place);

        courseWeekIdEdit = (AppCompatTextView) findViewById(R.id.edit_course_week_id_summary);
        courseLessonNoEdit = (AppCompatTextView) findViewById(R.id.edit_course_lesson_no_summary);
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
                final MultiSelectView multiSelectView = (MultiSelectView) dialogMultiSelect.findViewById(R.id.week_id_selector);
                multiSelectView.setSelect((int) course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_ID));
                new AlertDialog.Builder(this)
                        .setTitle(R.string.edit_alert_week_id_title)
                        .setView(dialogMultiSelect)
                        .setNeutralButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int value = (int) multiSelectView.getSelected();
                                String weeks = getWeeksValue(value);
                                course_detail.put(CourseDbHelper.CourseInfoTable.WEEK_ID, value);
                                course_detail.put(CourseDbHelper.CourseInfoTable.WEEKS, weeks);

                                courseWeekIdEdit.setText(weeks);
                            }
                        })
                        .show();
                break;
            case R.id.edit_course_lesson_no_container:
                LayoutInflater factory = LayoutInflater.from(this);
                final View dialogWheelView = factory.inflate(R.layout.dialog_edit_course_selector, null);
                final SelectWheel wheel_view_week_days = (SelectWheel) dialogWheelView.findViewById(R.id.wheel_view_week_days);
                final SelectWheel wheel_view_lesson_no = (SelectWheel) dialogWheelView.findViewById(R.id.wheel_view_lesson_no);
                wheel_view_week_days.selectIndex((int) course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_DAY));
                wheel_view_lesson_no.selectIndex((int) course_detail.get(CourseDbHelper.CourseInfoTable.LESSON_NO));

                new AlertDialog.Builder(this)
                        .setTitle(R.string.edit_alert_lesson_no_title)
                        .setView(dialogWheelView)
                        .setNeutralButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                course_detail.put(CourseDbHelper.CourseInfoTable.WEEK_DAY, wheel_view_week_days.getSelectedPosition());
                                course_detail.put(CourseDbHelper.CourseInfoTable.LESSON_NO, wheel_view_lesson_no.getSelectedPosition());
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
        int week_id = (int) course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_ID);
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
            UpdateData UDB = new UpdateData(this);
            UDB.submitCourseInfoEdit(_id, name, teachers, place, weeks,week_id,
                    (int)course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_DAY),
                    (int) course_detail.get(CourseDbHelper.CourseInfoTable.LESSON_NO));
            UDB.close();
            finish();
        }
    }

    private String getLessonValue() {
        return "周" + ((int) course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_DAY) + 1) + " 第" +
                ((int) course_detail.get(CourseDbHelper.CourseInfoTable.LESSON_NO) + 1) + "节";
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
        return "周" + ((int) course_detail.get(CourseDbHelper.CourseInfoTable.WEEK_DAY) + 1) + ",第" +
                ((int) course_detail.get(CourseDbHelper.CourseInfoTable.LESSON_NO) + 1) + "节," +
                course_detail.get(CourseDbHelper.CourseInfoTable.WEEKS).toString() + " " +
                course_detail.get(CourseDbHelper.CourseInfoTable.PLACE).toString();
    }

}

