package me.gensh.helloustb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.CourseDbHelper;
import me.gensh.database.StoreData;
import me.gensh.fragment.TimetableFragment;
import me.gensh.utils.BasicDate;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

/**
 * Created by gensh on 2016.
 */
public class Timetable extends LoginNetworkActivity {
    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;

    int tabs[] = {
            R.string.timetable_monday,
            R.string.timetable_tuesday,
            R.string.timetable_wednesday,
            R.string.timetable_thursday,
            R.string.timetable_friday,
            R.string.timetable_saturday,
            R.string.timetable_sunday,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = findViewById(R.id.timetable_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);

        ViewGroup tab = findViewById(R.id.timetable_tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.timetale_tab_header, tab, false));

        ViewPager viewPager = findViewById(R.id.timetable_viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        for (int titleResId : tabs) {
            pages.add(FragmentPagerItem.of(getString(titleResId), TimetableFragment.class));
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        setErrorHandler(new ErrorHandler() {
            @Override
            public void onPasswordError() {
                dismissProgressDialog();
                Toast.makeText(Timetable.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTimeoutError() {
                dismissProgressDialog();
                Toast.makeText(Timetable.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_import_table)
    public void floatingActionMenuClickListener(View menu) {
        int id = menu.getId();
        if (id == R.id.fab_import_table) {
            CourseDbHelper course = new CourseDbHelper(this, 1);
            if (course.haveCoursesImported()) { //maybe
                Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x101);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.importCourse)
                        .setMessage(R.string.haveImported)
                        .setNegativeButton(R.string.cancelImport, null)
                        .setPositiveButton(R.string.reImport, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StoreData sd = new StoreData(Timetable.this);
                                sd.clearTable();
                                sd.close();
                                Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x101);
                            }
                        })
                        .show();
            }
            course.close();
        } else {

        }
    }

    @Override
    public void RequestResultHandler(int what, ArrayList<String> data) {
        if (what == 0x101) { // from: verify elearning.ustb.edu.cn  password; post
            //which means:if username and password are set,and the 'Login' button is clicked.And if  login is succeed.the code below will execute
            savePasswordToLocal();
            LoginDialog time_table = new LoginDialog(LoginDialog.Timetable);
            time_table.setAccount(username, BasicDate.getTimetableYear());
            post(getString(R.string.school_timetable_addresss), "ELE", 0x102, 5, "UTF-8", time_table.post_params, false);
        } else if (what == 0x102) { //from: post getTimetable
            (new ImportCourseDataTask(this, data)).execute();
        }
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
    }

    private class ImportCourseDataTask extends AsyncTask<Void, Integer, Integer> {
        final static int IMPORT_TIMETABLE_TASK_RESULT_SUCCESSED = 1, IMPORT_TIMETABLE_TASK_RESULT_FAILED = 2;
        Context context;
        ArrayList<String> data;

        ImportCourseDataTask(Context context, ArrayList<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (data.size() == 1) {
                StoreData store_data = new StoreData(Timetable.this);
                boolean success = store_data.SaveToDb(data.get(0)); //todo if you han an empty timetable,it will cause an failure.
                store_data.close();
                if (success) {
                    return IMPORT_TIMETABLE_TASK_RESULT_SUCCESSED;
                } else {
                    return IMPORT_TIMETABLE_TASK_RESULT_FAILED;
                }
            } else {
                return IMPORT_TIMETABLE_TASK_RESULT_FAILED;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            dismissProgressDialog();
            if (result == IMPORT_TIMETABLE_TASK_RESULT_SUCCESSED) {

                Toast.makeText(context, R.string.importsuccess, Toast.LENGTH_SHORT).show();
                //todo

            } else if (result == IMPORT_TIMETABLE_TASK_RESULT_FAILED) {
                Toast.makeText(context, R.string.importfail, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
