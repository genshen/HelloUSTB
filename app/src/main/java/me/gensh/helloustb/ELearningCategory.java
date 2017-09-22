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
import me.gensh.fragments.ELearningExamQueryFragment;
import me.gensh.fragments.ELearningRecordQueryFragment;
import me.gensh.fragments.InnovationCreditFragment;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;
import me.gensh.utils.StrUtils;

/**
 * created by gensh on 2017/09/10
 */
public class ELearningCategory extends LoginNetworkActivity implements InnovationCreditFragment.OnInnovationCreditLoadListener, HttpRequestTask.OnTaskFinished {
    public final static int INTENT_TYPE_SCORE_QUERY = 1, INTENT_TYPE_EXAM_QUERY = 2, INTENT_TYPE_INNOVATION_CREDIT = 3;
    private final static int LOGIN_FEEDBACK_TYPE_SCORE_QUERY = 0x101, LOGIN_FEEDBACK_TYPE_EXAM_QUERY = 0x102, LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT = 0x103;
    private final static int DATA_FETCH_FEEDBACK_TYPE_SCORE_QUERY = 0x201, DATA_FETCH_FEEDBACK_TYPE_EXAM_QUERY = 0x202, DATA_FETCH_FEEDBACK_TYPE_INNOVATION_CREDIT = 0x203;
    public final static String E_LEARNING_EXTRA_TYPE = "e_learning";

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
            savePasswordToLocal();
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
            }
        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        Toast.makeText(ELearningCategory.this, R.string.errorPassword, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        Toast.makeText(ELearningCategory.this, R.string.connectionTimeout, Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, R.string.NoNetwork, Toast.LENGTH_SHORT).show();
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
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", LOGIN_FEEDBACK_TYPE_EXAM_QUERY);
                    break;
                case INTENT_TYPE_INNOVATION_CREDIT:
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", LOGIN_FEEDBACK_TYPE_INNOVATION_CREDIT);
                    break;
                case INTENT_TYPE_SCORE_QUERY:
                    Login(new LoginDialog(LoginDialog.LoginEle), "ELE", LOGIN_FEEDBACK_TYPE_SCORE_QUERY);
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
                final String passFileName = "/MyUstb/Pass_store/sch_ele_pass.ustb"; //todo READ DATA.
                String account[] = StrUtils.ReadWithEncryption(passFileName).split("@"); //todo file permission
                attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_POST, getString(R.string.ele_exam_time_place_query), "ELE", DATA_FETCH_FEEDBACK_TYPE_EXAM_QUERY,
                        6, "UTF-8", ELearningExamQueryFragment.loadExamPlaceQueryRequestParams(account[0]), showProgress);
                break;
            case INTENT_TYPE_INNOVATION_CREDIT:
                attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.ele_innovation_credit),
                        "ELE", DATA_FETCH_FEEDBACK_TYPE_INNOVATION_CREDIT, 11, "UTF-8", null, showProgress);
                break;
            case INTENT_TYPE_SCORE_QUERY:
                attemptHttpRequest(HttpRequestTask.REQUEST_TYPE_GET, getString(R.string.ele_score_query),
                        "ELE", DATA_FETCH_FEEDBACK_TYPE_SCORE_QUERY, 3, "UTF-8", null, showProgress);
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
