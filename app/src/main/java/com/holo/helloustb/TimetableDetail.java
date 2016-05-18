package com.holo.helloustb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.holo.database.DeleteData;
import com.holo.database.QueryData;

import java.util.HashMap;

public class TimetableDetail extends AppCompatActivity {
    int _id, i = 0;
    HashMap<String, Object> course_detail;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        _id = intent.getIntExtra("_id", 1);
    }

    @Override
    protected void onResume() {
        if (course_detail != null)
            course_detail.clear();
        QueryData qd = new QueryData(this);
        course_detail = qd.getCourseById(_id);
        qd.close();
        course_detail.put("position", "周" + ((int) course_detail.get("week_day") + 1) +
                " 第" + ((int) course_detail.get("lesson_no") + 1) + "节");
        actionBar.setTitle(getString(R.string.timetable_detail_title, course_detail.get("course_name").toString()));
        setValue(course_detail);
        super.onResume();
    }

    private void setValue(HashMap<String, Object> course_detail) {
        String[] key = {"course_name", "teachers", "place", "times", "weeks", "position", "student_num",
                "learn_time", "credit", "course_type", "time_place"};

        //learn_time credit course_id course_type time_place
        int[] id = {R.id.detail_course_name_value, R.id.detail_course_teacher_value,
                R.id.detail_course_place_value, R.id.detail_course_time_value,
                R.id.detail_course_week_value, R.id.detail_course_position_value,
                R.id.detail_course_student_num_value,
                R.id.detail_course_learn_time_value, R.id.detail_course_credit_value,
                R.id.detail_course_type_value, R.id.detail_course_time_place_value};
        for (int i = 0; i < id.length; i++) {
            TextView tv = (TextView) findViewById(id[i]);
            tv.setText(course_detail.get(key[i]).toString());
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
                Bundle bundle = new Bundle();
                bundle.putSerializable(TimetableEdit.DATA_FLAG, course_detail);
                bundle.putBoolean(TimetableEdit.ADD_NEW_FLAG, false);
                bundle.putInt(TimetableEdit.ID_FLAG, _id);
                Intent intent = new Intent(this, TimetableEdit.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_delete_course_ack_title)
                        .setMessage(getString(R.string.dialog_delete_course_ack_message,
                                course_detail.get("course_name").toString()))
                        .setNegativeButton(R.string.alert_cancel, null)
                        .setPositiveButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteData deleteData = new DeleteData(TimetableDetail.this);
                                deleteData.deleteCourseById(_id);
                                deleteData.close();
                                finish();
                            }
                        })
                        .show();
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
