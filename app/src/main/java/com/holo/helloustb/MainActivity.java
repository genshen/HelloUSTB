package com.holo.helloustb;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.holo.utils.LoginDialog;
import com.holo.utils.BasicDate;
import com.holo.utils.LoginNetworkActivity;
import com.holo.utils.StrUtils;
import com.holo.database.CourseDbHelper;
import com.holo.database.StoreData;
import com.holo.fragment.CampusNetworkFragment;
import com.holo.fragment.ErrorFragment;
import com.holo.fragment.HomeFragment;
import com.holo.fragment.RecordFragment;
import com.holo.fragment.TodayCourseFragment;
import com.holo.network.GetPostHandler;
import com.holo.network.VersionChecker;
import com.holo.service.DownloadApk;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.Calendar;

import static com.holo.network.VersionChecker.Update;

public class MainActivity extends LoginNetworkActivity {
    //        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private View navigation_drawer;
    private Toolbar toolbar;
    private VersionChecker checker;

    public int state;
    private long lastDown = 0;
    public static String shareCourse = "";

    static String username, password, passFileName;    //发送post请求之前传给变量,如果密码正确,则保存至本地
    Boolean canWrite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigation_drawer = findViewById(R.id.navigation_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        (findViewById(R.id.account)).setOnClickListener(new ClickHand());
        (findViewById(R.id.home)).setOnClickListener(new ClickHand());
        (findViewById(R.id.record_query)).setOnClickListener(new ClickHand());
//		( findViewById(R.id.elective_system)).setOnClickListener(new ClickHand());
        (findViewById(R.id.today_course)).setOnClickListener(new ClickHand());
        (findViewById(R.id.volunteer)).setOnClickListener(new ClickHand());
        (findViewById(R.id.library)).setOnClickListener(new ClickHand());
        (findViewById(R.id.campus_network)).setOnClickListener(new ClickHand());
        (findViewById(R.id.menu_setting)).setOnClickListener(new ClickHand());
        (findViewById(R.id.menu_playground)).setOnClickListener(new ClickHand());
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//      ((LinearLayout) findViewById(R.id.main_container)).removeAllViews();
        state = getIntent().getIntExtra("open_id", 1);    //如果不是通知栏打开的，则为1，否则是7;

        toolbar.setTitle(R.string.left_side_home);
        get(getString(R.string.teach), "TEACH", 0x101, 1, "gb2312", true);
        checker = new VersionChecker(getString(R.string.UpdateAddress), this);
        checker.setOnUpdate(new Update() {
            @Override
            public void onUpdate(boolean latestVersion) {
                if (latestVersion) {
                    updateUI.sendEmptyMessage(0x003);
                }
            }
        });
        checker.check(((MyApplication) getApplication()).CheckNetwork());

        setErrorHandler(new ErrorHandler() {
            @Override
            public void onPasswordError() {
                cancelProcessDialog();
                Toast.makeText(MainActivity.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onTimeoutError() {
                cancelProcessDialog();
                Toast.makeText(MainActivity.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long nowDown = System.currentTimeMillis();
            if (nowDown - lastDown > 2000) {
                Toast.makeText(MainActivity.this, R.string.NotExit, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            lastDown = nowDown;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent setting = new Intent(MainActivity.this, Settings.class);
            startActivity(setting);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
//        ((MyApplication) getApplication()).setUpShortCut();
        super.onStart();
    }

    @Override
    public void onResume() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int drawable;
        if (hour < 6) {
            drawable = R.drawable.nav_night;
        } else if (hour < 11) {
            drawable = R.drawable.nav_morning;
        } else if (hour < 18) {
            drawable = R.drawable.nav_afternoon;
        } else {
            drawable = R.drawable.nav_night;
        }
        findViewById(R.id.account).setBackgroundResource(drawable);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GetPostHandler.setTagEmpty();
        checker.setOnUpdate(null);
    }

    private void drawerToggle() {
        if (mDrawerLayout.isDrawerOpen(navigation_drawer)) {
            mDrawerLayout.closeDrawer(navigation_drawer);
            toolbar.setTitle(StrUtils.getActionBarTitle(state));
        } else {
            mDrawerLayout.openDrawer(navigation_drawer);
            toolbar.setTitle(R.string.home_title);
        }
    }

    class ClickHand implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.account:
                    Intent my_center = new Intent(MainActivity.this, Account.class);
                    startActivity(my_center);
                    break;
                case R.id.home:
                    state = 1;
                    drawerToggle();
                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
                    get("http://teach.ustb.edu.cn/", "TEACH", 0x101, 1, "gb2312", true);
                    break;
                case R.id.record_query:
                    state = 2;
                    drawerToggle();
                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x102);
                    break;
                case R.id.today_course:
                    state = 4;
                    drawerToggle();
                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
                    TodayCourseFragment fragment = new TodayCourseFragment();
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    break;
                case R.id.volunteer:
                    Intent volunteer = new Intent(MainActivity.this, Volunteer.class);
                    startActivity(volunteer);
                    break;
                case R.id.library:
                    Intent library = new Intent(MainActivity.this, Library.class);
                    startActivity(library);
                    break;
                case R.id.campus_network:
                    state = 7;
                    drawerToggle();
                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
                    Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x107);
                    break;
                case R.id.menu_setting:
                    Intent setting = new Intent(MainActivity.this, Settings.class);
                    startActivity(setting);
                    break;
                case R.id.menu_playground:
//                    Intent playground=new Intent(MainActivity.this, TestActivity.class);
//                    startActivity(playground);
                    break;
            }
        }
    }

    public void FabClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.innovation_credit:
                Intent innovation = new Intent(MainActivity.this, InnovationCredit.class);
                startActivity(innovation);
                break;
            case R.id.exam_query:
                Intent exam_query = new Intent(MainActivity.this, ExamQuery.class);
                startActivity(exam_query);
                break;
            case R.id.import_me:
                CourseDbHelper course = new CourseDbHelper(MainActivity.this, 1);
                if (course.isTableEmpty()) {
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x104);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.importCourse)
                            .setMessage(R.string.haveImported)
                            .setNegativeButton(R.string.cancelImport, null)
                            .setPositiveButton(R.string.reImport, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    StoreData sd = new StoreData(MainActivity.this);
                                    sd.clearTable();
                                    sd.close();
                                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x104);
                                }
                            })
                            .show();
                }
                course.close();
                break;
            case R.id.through_out:
                Intent through_out = new Intent(MainActivity.this, Timetable.class);
                startActivity(through_out);
                break;
            case R.id.share_table:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareCourse);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
    }

    @Override
    public void RequestResultHandler(int what, ArrayList<String> str_msg) {
        //switch to every part to update UI
        switch (what) {
            case 0x101://本科教学网首页 	get
                cancelProcessDialog();
                if (str_msg != null) {
                    Bundle argument = new Bundle();
                    argument.putStringArrayList("home", str_msg);
                    HomeFragment fragment = new HomeFragment();
                    fragment.setArguments(argument);
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                }
                break;
            case 0x102:    //login elearning.ustb.edu.cn，post
                savePass();
                Toast.makeText(this, R.string.edu_login_success, Toast.LENGTH_SHORT).show();
                get(getString(R.string.ele_score), "ELE", 0x103, 3, "UTF-8", false);
                break;
            case 0x103:    //get all score ,get
                cancelProcessDialog();
                if (str_msg.size() % 8 == 2) {
                    RecordFragment fragment = RecordFragment.newInstance(str_msg);
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                } else {
                    Toast.makeText(this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
                break;
            case 0x104:    //verify elearning.ustb.edu.cn  password; post
                savePass();
                LoginDialog time_table = new LoginDialog(LoginDialog.Timetable);
                time_table.setAccount(username, BasicDate.getTimetableYear());
                post(getString(R.string.school_timetable_addresss), "ELE",
                        0x105, 5, "UTF-8", time_table.post_params, false);
                break;
            case 0x105:    //post get timetable
                ImportCourseData(str_msg);
                break;
//                case 0x106: // get ipv6 address for login
//                    if (str_msg.size() == 1) {
//                        Login(new LoginDialog(LoginDialog.LoginNet, str_msg.get(0)), false, "NET", 0x107);
//                    } else {
//                        Toast.makeText(MainActivity.this, R.string.error_obtain_ip, Toast.LENGTH_LONG).show();
//                    }
//                    break;
            case 0x107://login to 202.204.48.66 success feedback;  post
                savePass();
                Toast.makeText(MainActivity.this, R.string.net_sign_in_success, Toast.LENGTH_SHORT).show();
                get(getString(R.string.sch_net), "NET", 0x108, 8, "GB2312", false);
                break;
            case 0x108:
                cancelProcessDialog();
                Bundle argument = new Bundle();
                argument.putStringArrayList("Campus_network_information", str_msg);
                CampusNetworkFragment fragment = new CampusNetworkFragment();
                fragment.setArguments(argument);
                getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                break;
        }
    }

    Handler updateUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    cancelProcessDialog();
                    Toast.makeText(MainActivity.this, R.string.importsuccess, Toast.LENGTH_SHORT).show();
                    TodayCourseFragment fragment = new TodayCourseFragment(); //show new timetable after imported
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    break;
                case 0x002:
                    cancelProcessDialog();
                    Toast.makeText(MainActivity.this, R.string.importfail, Toast.LENGTH_SHORT).show();
                    break;
                case 0x003: // show new version dialog
                    showUpdate(checker.versionCode, checker.version_name_new, checker.package_size, checker.version_describe);
                    break;
            }
        }
    };

    private void ImportCourseData(final ArrayList<String> course_data) {
        if (course_data.size() == 1) {
            new Thread() {
                @Override
                public void run() {
                    StoreData store_data = new StoreData(MainActivity.this);
                    boolean success = store_data.SaveToDb(course_data.get(0));
                    store_data.close();
                    if (success) {
                        updateUI.sendEmptyMessage(0x001);
                    } else {
                        updateUI.sendEmptyMessage(0x002);
                    }
                }
            }.start();
        } else {
            Toast.makeText(MainActivity.this, R.string.importfail, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdate(final int versionCode, final String versionName, final long size, final String describe) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.versionUpdate)
                .setMessage(getString(R.string.updateRequest, versionName, describe))
                .setNegativeButton(R.string.sayLater, null)
                .setPositiveButton(R.string.updateNow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DownloadApk.class);
                        intent.putExtra("latestVersion", versionName);
                        intent.putExtra("size", size);
                        startService(intent);
                    }
                })
                .show();
    }


    public void onNetworkDisabled() {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_container);
        if (rl.getChildCount() == 0) {
            ErrorFragment fragment = ErrorFragment.newInstance("ERROR_NO_Connection");
            getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }


    GoogleProgressBar main_google_progress_bar;

    public void setProcessDialog() {
        if (main_google_progress_bar == null) {
            main_google_progress_bar = (GoogleProgressBar) findViewById(R.id.main_google_progress_bar);
        }
        main_google_progress_bar.setVisibility(View.VISIBLE);
    }

    private void cancelProcessDialog() {
        if (main_google_progress_bar == null) {
            main_google_progress_bar = (GoogleProgressBar) findViewById(R.id.main_google_progress_bar);
        }
        main_google_progress_bar.setVisibility(View.INVISIBLE);
    }
}
