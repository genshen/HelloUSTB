package com.holo.helloustb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.holo.dataBase.QueryData;

import java.util.HashMap;

public class TimetableDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        int week_no = intent.getIntExtra("week", 0);    //周几 0-6
        int position = intent.getIntExtra("position", 0);    //第几节课 0-5
        String course_id = intent.getStringExtra("course_id");    //课程编号

        QueryData qd = new QueryData(this);
        HashMap<String, Object> course_detail = qd.getCourseById(course_id, week_no, position);
        course_detail.put("position", "周" + week_no + " 第" + position + "节");
        getSupportActionBar().setTitle(getString(R.string.timetable_detail_title, course_detail.get("course_name").toString()));
        setValue(course_detail);
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


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
