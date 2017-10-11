package me.gensh.helloustb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.DBAccounts;
import me.gensh.database.DeleteData;
import me.gensh.database.QueryData;
import me.gensh.database.StoreData;
import me.gensh.fragments.TimetableFragment;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.BasicDate;
import me.gensh.utils.Const;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;

/**
 * Created by gensh on 2016.
 * //todo timetable can use this https://github.com/zhouchaoyuan/excelPanel
 */
public class Timetable extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished {
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
        getSupportActionBar().setSubtitle(getString(R.string.timetable_week_num, getWeekNum() + 1));
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

        if (!QueryData.haveCoursesImported(((MyApplication) getApplication()).getDaoSession())) {
            Snackbar.make(findViewById(R.id.fab_import_table), R.string.course_imported_guide, Snackbar.LENGTH_LONG).show();
        }
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
            if (QueryData.haveCoursesImported(((MyApplication) getApplication()).getDaoSession())) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.importCourse)
                        .setMessage(R.string.haveImported)
                        .setNegativeButton(R.string.cancelImport, null)
                        .setPositiveButton(R.string.reImport, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteData.clearAllTimetable(((MyApplication) Timetable.this.getApplication()).getDaoSession());
                                Login(LoginDialog.newInstanceForELearning(), Tags.E_LEARNING, 0x101);
                            }
                        })
                        .show();
            } else {
                Login(LoginDialog.newInstanceForELearning(), Tags.E_LEARNING, 0x101);
            }
        }
    }

    private int getWeekNum() {
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        long week_start_days = pre.getLong(Const.Settings.KEY_WEEK_START, 0);
        return BasicDate.getWeekNum(week_start_days);
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        if (what == 0x101) { // from: verify elearning.ustb.edu.cn  password; post
            //which means:if username and password are set and the 'Login' button is clicked.Then if login is succeed.the code below will execute
            savePassword();
            DBAccounts account = QueryData.queryAccountByType(((MyApplication) getApplication()).getDaoSession(), LoginDialog.UserType.ELE);  //todo save username while login
            if (account != null) {  //query username from DB
//			listXnxq=&uid=41355059
                Map<String, String> params = new ArrayMap<>();
                params.put("uid", account.getUsername());
                params.put("listXnxq", BasicDate.getTimetableYear());
                attemptHttpRequest(HttpClients.HTTP_POST, getString(R.string.school_timetable_addresss),
                        Tags.E_LEARNING, 0x102, Tags.POST.ID_E_LEARNING_GET_TIMETABLE, HttpClients.CHARSET_BTF8, params, false);
            } else {
                onOk(0x404, new ArrayList<String>());  //error:no username found
            }
//            time_table.setAccount(username, BasicDate.getTimetableYear());

        } else if (what == 0x102) { //from: post getTimetable
            (new ImportCourseDataTask(this, data)).execute();
        } else if (what == 0x404) {

        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        Toast.makeText(Timetable.this, R.string.error_password, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        Toast.makeText(Timetable.this, R.string.connection_timeout, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
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
                boolean success = StoreData.SaveTimetableToDb(((MyApplication) context.getApplicationContext()).getDaoSession(), data.get(0));
                //todo if you han an empty timetable,it will cause an failure.
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
                //todo update fragment course list.

            } else if (result == IMPORT_TIMETABLE_TASK_RESULT_FAILED) {
                Toast.makeText(context, R.string.importfail, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}
