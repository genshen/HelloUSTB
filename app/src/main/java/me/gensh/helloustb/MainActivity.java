package me.gensh.helloustb;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.Calendar;

import me.gensh.fragments.DashboardFragment;
import me.gensh.fragments.HomeFragment;
import me.gensh.fragments.SettingsFragment;
import me.gensh.io.IOUtils;
import me.gensh.natives.MailAccount;
import me.gensh.network.VersionCheckerTask;
import me.gensh.service.ApkDownloadIntentService;
import me.gensh.utils.EasyFeedback;
import me.gensh.views.CircularImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by gensh on 2017/09/01
 */
@RuntimePermissions
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, VersionCheckerTask.OnNewVersionListener {
    VersionCheckerTask checker;
    Bitmap avatarBitmap;
    //    SearchView mSearchView;
//    AppBarLayout appBarLayout;
    DrawerLayout drawer;
    View navigationHeader;
    Fragment homeFragment, dashboardFragment, settingFragment;
    private long lastDown = 0;
    final static String HOME_FRAGMENT_TAG = "HomeFragment", DASHBOARD_FRAGMENT_TAG = "DashboardBlankFragment",
            SETTINGS_FRAGMENT = "SettingsFragment";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getFragmentManager().beginTransaction().show(homeFragment).hide(dashboardFragment).hide(settingFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    getFragmentManager().beginTransaction().hide(homeFragment).show(dashboardFragment).hide(settingFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    getFragmentManager().beginTransaction().hide(homeFragment).hide(dashboardFragment).show(settingFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        appBarLayout = findViewById(R.id.app_bar_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        StatusBarUtil.setTransparent(this); //actually,it can be removed,but it will cause a bug for drawer item selection;todo bugs,so removed.

        // drawLayout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = navigationView.getHeaderView(0);

        // bottom navigation
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (savedInstanceState != null) {
            homeFragment = getFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG); //it may be null
            dashboardFragment = getFragmentManager().findFragmentByTag(DASHBOARD_FRAGMENT_TAG);
            settingFragment = getFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT);
        }

        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance();
            ft.add(R.id.fragment_container, homeFragment, HOME_FRAGMENT_TAG);
        }
        if (dashboardFragment == null) {
            dashboardFragment = DashboardFragment.newInstance();
            ft.add(R.id.fragment_container, dashboardFragment, DASHBOARD_FRAGMENT_TAG);
        }
        if (settingFragment == null) {
            settingFragment = SettingsFragment.newInstance();
            ft.add(R.id.fragment_container, settingFragment, SETTINGS_FRAGMENT);
        }
        ft.show(homeFragment).hide(dashboardFragment).hide(settingFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // searchView
        /* //todo have bugs
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
*/
        //event
        checker = new VersionCheckerTask(getString(R.string.UpdateAddress), this, true);
        checker.execute();
        navigationHeader.findViewById(R.id.avatar_background).setOnClickListener(new ClickHand());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                //todo have bugs
//                StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.white));//todo
//                appBarLayout.setVisibility(View.INVISIBLE);
//                mSearchView.setVisibility(View.VISIBLE);
//                mSearchView.open(false); // enable or disable animation
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        setAvatar();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checker.cancel(true);
        checker = null;
        if (avatarBitmap != null) {
            avatarBitmap.recycle();
            avatarBitmap = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {  //check drawer
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

    private void setAvatar() {
        File avatarFile = new File(getFilesDir(), IOUtils.HELLO_USTB_AVATAR_FILENAME);
        if (avatarFile.exists()) {
            if (avatarBitmap != null) {
                avatarBitmap.recycle();
            }
            System.out.println(avatarFile.length());
            avatarBitmap = BitmapFactory.decodeFile(avatarFile.getPath());
            CircularImageView avatarView = navigationHeader.findViewById(R.id.avatar);
            avatarView.setImageBitmap(avatarBitmap);
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
                new EasyFeedback.Builder(this)
                        .withEmail(MailAccount.getMailAccount())
                        .build()
                        .start();
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

    //methods for updating
    @Override
    public void onAttemptToDownload(long packageSize, int newVersionCode, String newVersionName) {
        MainActivityPermissionsDispatcher.startDownloadWithPermissionCheck(this, packageSize, newVersionCode, newVersionName);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startDownload(long packageSize, int newVersionCode, String newVersionName) {
        Toast.makeText(this, R.string.update_downloading, Toast.LENGTH_SHORT).show();
        ApkDownloadIntentService.startActionDownloadApk(this, packageSize, newVersionCode, newVersionName);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForApkDownload(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_download_apk_rationale)
                .setPositiveButton(R.string.permission_button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForApkDownload() {
        Toast.makeText(this, R.string.permission_download_apk_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
            }
        }
    }

}
