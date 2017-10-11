package me.gensh.helloustb;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.DBAccounts;
import me.gensh.database.QueryData;
import me.gensh.fragments.ELearningExamQueryFragment;
import me.gensh.fragments.ELearningRecordQueryFragment;
import me.gensh.fragments.InnovationCreditFragment;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;

/**
 * created by gensh on 2017/09/10
 */
public class ELearningCategory extends LoginNetworkActivity implements InnovationCreditFragment.OnInnovationCreditLoadListener, HttpRequestTask.OnTaskFinished {
    final public static int INTENT_TYPE_SCORE_QUERY = 1, INTENT_TYPE_EXAM_QUERY = 2, INTENT_TYPE_INNOVATION_CREDIT = 3;
    final private static int LOGIN_FEEDBACK_TYPE_SCORE_QUERY = 0x101, LOGIN_FEEDBACK_TYPE_EXAM_QUERY = 0x102, LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT = 0x103;
    final private static int DATA_FETCH_FEEDBACK_TYPE_SCORE_QUERY = 0x201, DATA_FETCH_FEEDBACK_TYPE_EXAM_QUERY = 0x202, DATA_FETCH_FEEDBACK_TYPE_INNOVATION_CREDIT = 0x203;
    final private static int USERNAME_ERROR_CALLBACK = 0x404;
    final public static String E_LEARNING_EXTRA_TYPE = "e_learning";

    boolean loginStatus = false; //to show if the user have signed in the <a>http://elearning.ustb.edu.cn</a>

    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;
    @BindView(R.id.fab_menu_e_learning)
    FloatingActionMenu fab;
    @BindView(R.id.fab_menu_innovation_credit)
    FloatingActionButton fabInnovationCredit;
    @BindView(R.id.fab_menu_exam_query)
    FloatingActionButton fabExamQuery;
    @BindView(R.id.fab_menu_record_query)
    FloatingActionButton fabRecordQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_learning_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        int type = getIntent().getIntExtra(E_LEARNING_EXTRA_TYPE, INTENT_TYPE_SCORE_QUERY);
        goToSignInOrFetchData(type); //todo:make a error fragment;
        getSupportActionBar().setTitle(getTitleByType(type));
        /*@link{https://stackoverflow.com/questions/26486730/in-android-app-toolbar-settitle-method-has-no-effect-application-name-is-shown}*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        if (what == LOGIN_FEEDBACK_TYPE_EXAM_QUERY || what == LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT || what == LOGIN_FEEDBACK_TYPE_SCORE_QUERY) { //if it is login feedback
            loginStatus = true;
            savePassword();
            Toast.makeText(this, R.string.edu_login_success, Toast.LENGTH_SHORT).show();
            switch (what) {
                case LOGIN_FEEDBACK_TYPE_EXAM_QUERY:
                    fetchELearningData(INTENT_TYPE_EXAM_QUERY, false);
                    break;
                case LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT:
                    fetchELearningData(INTENT_TYPE_INNOVATION_CREDIT, false);
                    break;
                case LOGIN_FEEDBACK_TYPE_SCORE_QUERY:
                    fetchELearningData(INTENT_TYPE_SCORE_QUERY, false);
                    break;
            }
        } else {  //get request,etc. data fetch.
            if (what == DATA_FETCH_FEEDBACK_TYPE_EXAM_QUERY) {
                dismissProgressDialog();
                if (data != null) {
                    ELearningExamQueryFragment examQueryFragment = ELearningExamQueryFragment.newInstance(data);
                    getFragmentManager().beginTransaction().replace(R.id.e_learning_container, examQueryFragment).commit();
                } else {
                    Toast.makeText(this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
            } else if (what == DATA_FETCH_FEEDBACK_TYPE_INNOVATION_CREDIT) {
                dismissProgressDialog();
                if (data != null) {
                    InnovationCreditFragment innovationCreditFragment = InnovationCreditFragment.newInstance(data);
                    getFragmentManager().beginTransaction().replace(R.id.e_learning_container, innovationCreditFragment).commit();
                } else {
                    Toast.makeText(this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
            } else if (what == DATA_FETCH_FEEDBACK_TYPE_SCORE_QUERY) {
                dismissProgressDialog();
                if (data.size() % 8 == 2) {
                    ELearningRecordQueryFragment fragment = ELearningRecordQueryFragment.newInstance(data);
                    getFragmentManager().beginTransaction().replace(R.id.e_learning_container, fragment).commit();
                } else {
                    Toast.makeText(this, R.string.request_error, Toast.LENGTH_LONG).show();
                }
            } else if (what == USERNAME_ERROR_CALLBACK) {
                dismissProgressDialog();  // if login show first,then send get request,progress dialog should be dismissed.
                Toast.makeText(this, R.string.error_account_not_found, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        Toast.makeText(ELearningCategory.this, R.string.error_password, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        Toast.makeText(ELearningCategory.this, R.string.connection_timeout, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        fab.close(true);  //make sure these are not two http requests at the same time
        fabInnovationCredit.setClickable(false);
        fabExamQuery.setClickable(false);
        fabRecordQuery.setClickable(false);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.GONE);
        fabInnovationCredit.setClickable(true);  //make sure these are not two http requests at the same time
        fabExamQuery.setClickable(true);
        fabRecordQuery.setClickable(true);
    }

    @Override
    public void onNetworkDisabled() {
        Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.fab_menu_exam_query, R.id.fab_menu_innovation_credit, R.id.fab_menu_record_query})
    public void floatingActionBarMenuClickHandle(View view) {
        switch (view.getId()) {
            case R.id.fab_menu_exam_query:
                goToSignInOrFetchData(INTENT_TYPE_EXAM_QUERY);
                toolbar.setTitle(getTitleByType(INTENT_TYPE_EXAM_QUERY));
                changeSubtitle(null);
                break;
            case R.id.fab_menu_innovation_credit:
                goToSignInOrFetchData(INTENT_TYPE_INNOVATION_CREDIT);
                toolbar.setTitle(getTitleByType(INTENT_TYPE_INNOVATION_CREDIT));
                changeSubtitle(null);
                break;
            case R.id.fab_menu_record_query:
                goToSignInOrFetchData(INTENT_TYPE_SCORE_QUERY);
                toolbar.setTitle(getTitleByType(INTENT_TYPE_SCORE_QUERY));
                changeSubtitle(null);
                break;
        }
    }

    /**
     * if not login,to login and then get data.
     * if login,get data.
     */
    private void goToSignInOrFetchData(int type) {
        if (loginStatus) {
            fetchELearningData(type, true);
        } else {
            switch (type) {
                case INTENT_TYPE_EXAM_QUERY:
                    Login(LoginDialog.newInstanceForELearning(), Tags.E_LEARNING, LOGIN_FEEDBACK_TYPE_EXAM_QUERY);
                    break;
                case INTENT_TYPE_INNOVATION_CREDIT:
                    Login(LoginDialog.newInstanceForELearning(), Tags.E_LEARNING, LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT);
                    break;
                case INTENT_TYPE_SCORE_QUERY:
                    Login(LoginDialog.newInstanceForELearning(), Tags.E_LEARNING, LOGIN_FEEDBACK_TYPE_SCORE_QUERY);
                    break;
            }
        }
    }

    private int getTitleByType(int type) {
        switch (type) {
            case INTENT_TYPE_EXAM_QUERY:
                return R.string.exam_query_toolbar_title;
            case INTENT_TYPE_INNOVATION_CREDIT:
                return R.string.innovation_credit_toolbar_title;
            case INTENT_TYPE_SCORE_QUERY:
                return R.string.score_query_toolbar_title;
        }
        return R.string.score_query_toolbar_title;
    }

    private void fetchELearningData(int type, boolean showProgress) {
        switch (type) {
            case INTENT_TYPE_EXAM_QUERY:
                DBAccounts account = QueryData.queryAccountByType(((MyApplication) getApplication()).getDaoSession(), LoginDialog.UserType.ELE);  //todo save username while login
                if (account != null) {  //query username from DB
                    attemptHttpRequest(HttpClients.HTTP_POST, getString(R.string.ele_exam_time_place_query), Tags.E_LEARNING, DATA_FETCH_FEEDBACK_TYPE_EXAM_QUERY,
                            Tags.POST.ID_E_LEARNING_EXAM_LIST, HttpClients.CHARSET_BTF8, ELearningExamQueryFragment.loadExamPlaceQueryRequestParams(account.getUsername()), showProgress);
                } else {
                    onOk(USERNAME_ERROR_CALLBACK, new ArrayList<String>());  //error:no username found
                }
                break;
            case INTENT_TYPE_INNOVATION_CREDIT:
                attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.ele_innovation_credit), Tags.E_LEARNING,
                        DATA_FETCH_FEEDBACK_TYPE_INNOVATION_CREDIT, Tags.GET.ID_E_LEARNING_INNOVATION_CREDIT, HttpClients.CHARSET_BTF8, null, showProgress);
                break;
            case INTENT_TYPE_SCORE_QUERY:
                attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.ele_score_query), Tags.E_LEARNING,
                        DATA_FETCH_FEEDBACK_TYPE_SCORE_QUERY, Tags.GET.ID_E_LEARNING_SCORE_QUERY, HttpClients.CHARSET_BTF8, null, showProgress);
                break;
        }
    }

    @Override
    public void onInnovationCreditCalculated(float creditSum) {
        changeSubtitle(getString(R.string.innovation_credit_toolbar_sub_title, creditSum));
    }

    public void changeSubtitle(@Nullable String subtitle) {
        if (subtitle == null) {
            toolbar.setLayoutTransition(null);
            toolbar.setSubtitle(null);
        } else {
            toolbar.setLayoutTransition(new LayoutTransition()); //animation
            toolbar.setSubtitle(subtitle);
        }

    }
}
