package me.gensh.helloustb;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.gensh.fragment.DashboardFragment;
import me.gensh.fragment.HomeFragment;
import me.gensh.fragment.SettingsFragment;
import me.gensh.network.GetPostHandler;
import me.gensh.network.VersionCheckerTask;
import me.gensh.utils.NetWorkActivity;

/**
 * Created by gensh on 2017/09/01
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    VersionCheckerTask checker;
    SearchView mSearchView;
    AppBarLayout appBarLayout;
    DrawerLayout drawer;
    View navigationHeader;
    Fragment homeFragment, dashboardFragment, settingFragment;
    private long lastDown = 0;
    final static String HOME_FRAGMENT_TAG = "WebNotificationFragment", DASHBOARD_BLANK_FRAGMENT_TAG = "DashboardBlankFragment",
            DASHBOARD_EDIT_FRAGMENT_TAG = "DashboardEditFragment", SETTINGS_FRAGMENT = "SettingsFragment";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().show(homeFragment).hide(dashboardFragment).hide(settingFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    getSupportFragmentManager().beginTransaction().hide(homeFragment).show(dashboardFragment).hide(settingFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(dashboardFragment).show(settingFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayout = findViewById(R.id.app_bar_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StatusBarUtil.setTransparent(this); //actually,it can be removed,but it will cause a bug for drawer item selection

        // drawLayout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = navigationView.getHeaderView(0);

        // bottom navigation
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState != null) {
            homeFragment = getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG); //it may be null
            if ((dashboardFragment = getSupportFragmentManager().findFragmentByTag(DASHBOARD_EDIT_FRAGMENT_TAG)) == null) { //todo
                dashboardFragment = getSupportFragmentManager().findFragmentByTag(DASHBOARD_BLANK_FRAGMENT_TAG);
            }
            settingFragment = getSupportFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT);
        }

        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance();
            ft.add(R.id.fragment_container, homeFragment, HOME_FRAGMENT_TAG);
        }
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance("", "");
            ft.add(R.id.fragment_container, dashboardFragment, DASHBOARD_BLANK_FRAGMENT_TAG);
        }

        if (settingFragment == null) {
            settingFragment = SettingsFragment.newInstance("", "");
            ft.add(R.id.fragment_container, settingFragment, SETTINGS_FRAGMENT);
        }
        ft.show(homeFragment).hide(dashboardFragment).hide(settingFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // searchView
        mSearchView = findViewById(R.id.searchView);
        mSearchView.setVersionMargins(SearchView.VersionMargins.TOOLBAR_SMALL);
        mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
            @Override
            public boolean onOpen() {
                return true;
            }

            @Override
            public boolean onClose() {
                StatusBarUtil.setTransparent(MainActivity.this);
                appBarLayout.setVisibility(View.VISIBLE);
                mSearchView.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        List<SearchItem> suggestionsList = new ArrayList<>();
        suggestionsList.add(new SearchItem("search1"));
        suggestionsList.add(new SearchItem("search2"));
        suggestionsList.add(new SearchItem("search3"));
        SearchAdapter searchAdapter = new SearchAdapter(this, suggestionsList);
        searchAdapter.setOnSearchItemClickListener(new SearchAdapter.OnSearchItemClickListener() {
            @Override
            public void onSearchItemClick(View view, int position) {
                TextView textView = view.findViewById(R.id.textView);
                String query = textView.getText().toString();
//            mHistoryDatabase.addItem(new SearchItem(text));
                mSearchView.close(false);
            }
        });
        mSearchView.setAdapter(searchAdapter);

        //event
        checker = new VersionCheckerTask(getString(R.string.UpdateAddress), this, true);
        checker.execute();
        navigationHeader.findViewById(R.id.avatar_background).setOnClickListener(new ClickHand());
    }

    @Override
    protected void onStart() {
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
        navigationHeader.findViewById(R.id.avatar_background).setBackgroundResource(drawable);
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
        checker.cancel(true);
        checker = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.white));//todo
                appBarLayout.setVisibility(View.INVISIBLE);
                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.open(false); // enable or disable animation
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long nowDown = System.currentTimeMillis();
            if (nowDown - lastDown > 2000) {
                Toast.makeText(this, R.string.pressAgainToExit, Toast.LENGTH_SHORT).show();
                lastDown = nowDown;
            } else {
                finish();
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:

                break;
            case R.id.nav_account:
                Intent account = new Intent(this, AccountManager.class);
                startActivity(account);
                break;
            case R.id.nav_download:
                Intent download = new Intent(this, FileManager.class);
                startActivity(download);
                break;
            case R.id.nav_share:
                Intent share = new Intent(this, ShareApp.class);
                startActivity(share);
                break;
            case R.id.nav_feedback:
                Intent feedback = new Intent(this, Feedback.class);
                startActivity(feedback);
                break;
            case R.id.nav_github:
                Intent browser = new Intent();
                browser.setAction("android.intent.action.VIEW");
                Uri content_uri_browsers = Uri.parse(getString(R.string.app_github_url));
                browser.setData(content_uri_browsers);
                startActivity(browser);
                break;
            case R.id.nav_about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class ClickHand implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.avatar_background:
                    Intent my_center = new Intent(MainActivity.this, Account.class);
                    startActivity(my_center);
                    break;
//                case R.id.home:
//                    state = 1;
//                    drawerToggle();
//                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
//                    get("http://teach.ustb.edu.cn/", "TEACH", 0x101, 1, "gb2312", true);
//                    break;
//                case R.id.menu_record_query:
//                    state = 2;
//                    drawerToggle();
//                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
//                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", 0x102);
//                    break;
//                case R.id.today_course:
//                    state = 4;
//                    drawerToggle();
//                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
//                    TodayCourseFragment fragment = new TodayCourseFragment();
//                    getFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
//                    break;
//                case R.id.volunteer:
//                    Intent volunteer = new Intent(MainActivity.this, Volunteer.class);
//                    startActivity(volunteer);
//                    break;
//                case R.id.library:
//                    Intent library = new Intent(MainActivity.this, LibraryActivity.class);
//                    startActivity(library);
//                    break;
//                case R.id.campus_network:
//                    state = 7;
//                    drawerToggle();
//                    ((RelativeLayout) findViewById(R.id.main_container)).removeAllViews();
//                    Login(new LoginDialog(LoginDialog.LoginNet), "NET", 0x107);
//                    break;
//                case R.id.menu_setting:
//                    Intent setting = new Intent(MainActivity.this, Settings.class);
//                    startActivity(setting);
//                    break;
//                case R.id.menu_playground:
//                    Intent playground=new Intent(MainTempActivity.this, TestActivity.class);
//                    startActivity(playground);
//                    break;
            }
        }
    }

}
