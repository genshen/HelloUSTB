package com.holo.helloustb;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.holo.account.LoginDialog;
import com.holo.base.BasicDate;
import com.holo.base.StrPro;
import com.holo.dataBase.CourseDbHelper;
import com.holo.dataBase.StoreData;
import com.holo.fragment.CampusNetworkFragment;
import com.holo.fragment.ErrorFragment;
import com.holo.fragment.HomeFragment;
import com.holo.fragment.RecordFragment;
import com.holo.fragment.TodayCourseFrament;
import com.holo.network.DataInfo;
import com.holo.network.GetPostHandler;
import com.holo.sdcard.SdCardPro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private View navigation_drawer;
    private Toolbar toolbar;

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
        (findViewById(R.id.TodayCourse)).setOnClickListener(new ClickHand());
        (findViewById(R.id.volunteer)).setOnClickListener(new ClickHand());
        (findViewById(R.id.library)).setOnClickListener(new ClickHand());
        (findViewById(R.id.campus_network)).setOnClickListener(new ClickHand());
        (findViewById(R.id.menu_setting)).setOnClickListener(new ClickHand());
        (findViewById(R.id.menu_playground)).setOnClickListener(new ClickHand());
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//      ((LinearLayout) findViewById(R.id.main_container)).removeAllViews();
        int open_id = getIntent().getIntExtra("open_id", 1);
        state = open_id;    //如果不是通知栏打开的，则为1，否则是7；
        if (state == 7) {
//			WifiCheck.mNotifyManager.cancel(WifiCheck.mNotificationId);
            toolbar.setTitle(R.string.left_side_campus_network);
            Login(new LoginDialog(LoginDialog.LoginNet), "Net", 0x107);
        } else {
            toolbar.setTitle(R.string.left_side_home);
            get(getString(R.string.UpdateAddress), "MYWEB", 0x110, 10, "utf-8", true);
        }
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
        if( id == R.id.action_settings){
            Intent setting = new Intent(MainActivity.this, Settings.class);
            startActivity(setting);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
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

    private void drawerToggle() {
        if (mDrawerLayout.isDrawerOpen(navigation_drawer)) {
            mDrawerLayout.closeDrawer(navigation_drawer);
            toolbar.setTitle(StrPro.getActionBarTitle(state));
        } else {
            mDrawerLayout.openDrawer(navigation_drawer);
            toolbar.setTitle(R.string.home_title);
        }
    }

    class ClickHand implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // TODO 自动生成的方法存根
            int id = view.getId();
            switch (id) {
                case R.id.account:
                    Intent mycenter = new Intent(MainActivity.this, Account.class);
                    startActivity(mycenter);
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
                case R.id.TodayCourse:
                    state = 4;
                    drawerToggle();
                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
                    TodayCourseFrament fragment = new TodayCourseFrament();
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
//                    Intent playground=new Intent(MainActivity.this,FullscreenActivity.class);
//                    startActivity(playground);
                    break;
            }
        }
    }

    public void Myclick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.innovation_credit:
                Intent innovation=new Intent(MainActivity.this,InnovationCredit.class);
                startActivity(innovation);
                break;
            case R.id.exam_query:
                Intent exam_query=new Intent(MainActivity.this,ExamQuery.class);
                startActivity(exam_query);
                break;
            case R.id.import_me:
                CourseDbHelper course = new CourseDbHelper(MainActivity.this, 1);
                if (course.isTableEmpty()) {
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x104);
                } else {
                    new MaterialDialog.Builder(this)
                            .title(R.string.importCourse)
                            .content(R.string.haveImported)
                            .positiveText(R.string.reImport)
                            .negativeText(R.string.cancelImport)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
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

    private void Login(final LoginDialog ld, final String tag, final int feedback) {
        passFileName = ld.passFileName;
//        final int feedback = 0x100 + feedback;
        if (!SdCardPro.fileIsExists(ld.passFileName)) {
            canWrite = true;
            final View enter = getLayoutInflater().inflate(R.layout.dialog_enter, null);
            new MaterialDialog.Builder(this)
                    .title(ld.dialog_title)
                    .customView(enter, false)
                    .positiveText(R.string.alert_login)
                    .negativeText(R.string.alert_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                            username = ((TextView) enter.findViewById(R.id.account)).getText().toString();
                            password = ((TextView) enter.findViewById(R.id.pass)).getText().toString();
//                            TextInputLayout text_input_layout_account = (TextInputLayout) enter.findViewById(R.id.text_input_layout_account);
//                            TextInputLayout text_input_layout_pass = (TextInputLayout) enter.findViewById(R.id.text_input_layout_pass);
//                            text_input_layout_account.setError(getString(R.string.errorEmptyUsername));
                            if (!username.isEmpty() && !password.isEmpty()) {
                                ld.setAccount(username, password);
                                post(MainActivity.this.getString(ld.post_address), tag, feedback, ld.verify_id, "GB2312", ld.post_params, true);
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        } else {
            canWrite = false;
            String myaccount[] = StrPro.ReadWithEncryption(passFileName).split("@");
            username = myaccount[0];
            password = myaccount[1];
            ld.setAccount(username, password);
            post(MainActivity.this.getString(ld.post_address), tag, feedback,  ld.verify_id, "GB2312", ld.post_params, true);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataInfo data_info = (DataInfo) msg.obj;

            //error password and timeout process
            if (data_info.code == DataInfo.ERROR_PASSWORD) {
                cancelProcessDialog();
                Toast.makeText(MainActivity.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
                return;
            } else if (data_info.code == DataInfo.TimeOut) {
                cancelProcessDialog();
                Toast.makeText(MainActivity.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
                return;
            }

            //switch to every part to update UI
            ArrayList<String> str_msg = data_info.data;
            switch (msg.what) {
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
                    Toast.makeText(MainActivity.this, R.string.edu_login_success, Toast.LENGTH_SHORT).show();
                    get(getString(R.string.ele_score),"ELE", 0x103, 3, "UTF-8", false);
                    break;
                    //因为网站问题，暂时无法获取个人信息
                    // 是否需要得到 得到个人信息？
//				get(getString(R.string.information_url),0x103,14,"GB2312");
                case 0x103:	//get all score ,get
                    cancelProcessDialog();
                    if(str_msg.size() % 8 == 2) {
                        RecordFragment fragment =  RecordFragment.newInstance(str_msg);
                        getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.request_error, Toast.LENGTH_LONG).show();
                    }
                    break;
                case 0x104:    //verify elearning.ustb.edu.cn  password; post
                    savePass();
                    LoginDialog time_table = new LoginDialog(LoginDialog.Timetable);
                    time_table.setAccount(username, BasicDate.getTimetableYear());
                    MainActivity.this.post(getString(R.string.school_timetable_addresss), "ELE",
                            0x105, 5, "utf-8", time_table.post_params, false);
                    break;
                case 0x105:    //post get timetable
                    ImportCourseData(str_msg);
                    break;
                case 0x106:    //自服务登录   post	(不需要保存密码，在登录校园网已经保存了)
                    get(getString(R.string.zifuwu_userinfo), "ZFW", 0x007, 7, "GB2312", false);
                    break;
                case 0x107://login to 202.204.48.66 success feedback;  post
                    savePass();
                    Toast.makeText(MainActivity.this, "校园网登录成功", Toast.LENGTH_SHORT).show();
                    LoginDialog zfw_login = new LoginDialog(LoginDialog.LoginZFW);
                    zfw_login.setAccount(username, password);
                    MainActivity.this.post(getString(R.string.zifuwu_login), "ZFW", 0x106, 71, "GB2312", zfw_login.post_params, false);
                    break;
                case 0x110://版本更新
//							String spiltArr[] = str_msg.split("@");
                    if (str_msg!=null && str_msg.size() == 4) {//网络连接有效...
                        showUpdate(str_msg);
                    }
                    get(getString(R.string.teach), "TEACH", 0x101, 1, "gb2312", false);
                    break;
                case 0x007://show selfhelp infomaton get
                    cancelProcessDialog();
                    Bundle argument = new Bundle();
                    argument.putStringArrayList("Campus_network_information", str_msg);
                    CampusNetworkFragment fragment = new CampusNetworkFragment();
                    fragment.setArguments(argument);
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    break;
            }
        }
    };

    Handler updataUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    cancelProcessDialog();
                    Toast.makeText(MainActivity.this, R.string.importsuccess, Toast.LENGTH_SHORT).show();
                    TodayCourseFrament fragment = new TodayCourseFrament(); //show new timetable after imported
                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    break;
                case 0x002:
                    cancelProcessDialog();
                    Toast.makeText(MainActivity.this, R.string.importfail, Toast.LENGTH_SHORT).show();
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
                        updataUI.sendEmptyMessage(0x001);
                    } else {
                        updataUI.sendEmptyMessage(0x002);
                    }
                }
            }.start();
        } else {
            Toast.makeText(MainActivity.this, R.string.importfail, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdate(final ArrayList<String> Version) {
        int versionCode = 0;
        try {
            PackageInfo info = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (versionCode < Integer.parseInt(Version.get(0))) {    //当前版本小于服务器版本
            new MaterialDialog.Builder(this)
                    .title(R.string.versionUpdate)
                    .content(getString(R.string.updateRequest) + "v" + Version.get(1) + "\n" + Version.get(2))
                    .positiveText(R.string.updateNow)
                    .negativeText(R.string.sayLater)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction witch) {
                            Toast.makeText(MainActivity.this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, DownloadApk.class);
                            intent.putExtra("latestVersion", Version.get(1));
                            intent.putExtra("size", Long.parseLong(Version.get(3)));
                            startService(intent);
                        }
                    })
                    .show();
        }
    }

    private void get(String url, String tag, int feedback, int id, String code, boolean setdialog) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (setdialog) {
                setProcessDialog();
            }
            GetPostHandler.handlerGet(handler, url, tag, feedback, id, code);
        } else {
            setNetworkUnable();
        }
    }

    private void post(String url, String tag, int feedback, int id, String code,
                      Map<String, String> post_params, boolean setdialog) {
        if (((MyApplication) getApplication()).CheckNetwork()) {
            if (setdialog) setProcessDialog();
            GetPostHandler.handlerPost(handler, url, tag, feedback, id, code, post_params);
        } else {
            setNetworkUnable();
        }
    }

    private void setNetworkUnable() {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_container);
        if (rl.getChildCount() == 0) {
            ErrorFragment fragment = ErrorFragment.newInstance("ERROR_NO_Connection");
            getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        } else {
            Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

    private void savePass() { // TODO 自动生成的方法存根
        if (canWrite) {
            SdCardPro.checkDirExit();
            StrPro.WriteWithEncryption(username + "@" + password, passFileName);
        }
    }

    private void setProcessDialog() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final View progress_wheel = getLayoutInflater().inflate(R.layout.progress_wheel, null);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_container);
        rl.addView(progress_wheel, p);
    }

    private void cancelProcessDialog() {
        View progress_wheel = findViewById(R.id.progress_wheel);
        if (progress_wheel != null) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_container);
            rl.removeAllViews();
        }
    }
}
