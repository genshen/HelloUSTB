package me.gensh.helloustb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gensh.helloustb.http.HttpClients;
import me.gensh.helloustb.http.Tags;
import me.gensh.network.HttpRequestTask;
import me.gensh.utils.LoginDialog;
import me.gensh.utils.LoginNetworkActivity;
import me.gensh.utils.StringUtils;
import me.gensh.views.RecyclerViewWithEmptySupport;

public class CampusCardConsumption extends LoginNetworkActivity implements HttpRequestTask.OnTaskFinished {
    private final static int CAMPUS_CARD_CONSUME_TAG_TIME = 1;
    private final static int CAMPUS_CARD_CONSUME_TAG_PLACE = 2;
    private final static int CAMPUS_CARD_CONSUME_TAG_MONEY = 3;
    private final static int CAMPUS_CARD_CONSUME_TAG_LEFT = 4;

    @BindView(R.id.progress_bar)
    GoogleProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.campus_card_recycler_view)
    RecyclerViewWithEmptySupport mRecyclerView;
    List<SparseArray<String>> campusCardDataList = new ArrayList<>();
    CampusCardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_card_consumption);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //todo weather to set setDisplayShowHomeEnabled.

        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CampusCardAdapter(campusCardDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(R.layout.list_empty_view, (ViewGroup) findViewById(R.id.recycle_view_container), true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasLogin) { //then just do get request.
                    attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.campus_card_query_link), Tags.E, 0x102, Tags.GET.ID_E_CAMPUS_CARD, HttpClients.CHARSET_BTF8, null, true);
                } else {
                    Login(LoginDialog.newInstanceForE(), Tags.E, 0x101);
                }
            }
        });

        Login(LoginDialog.newInstanceForE(), Tags.E, 0x101);
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
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkDisabled() {
        Snackbar.make(fab, R.string.network_unavailable, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private boolean hasLogin;

    @Override
    public void onOk(int what, @NonNull ArrayList<String> data) {
        if (what == 0x101) {
            hasLogin = true;
            savePassword();
            attemptHttpRequest(HttpClients.HTTP_GET, getString(R.string.campus_card_query_link), Tags.E, 0x102, Tags.GET.ID_E_CAMPUS_CARD, HttpClients.CHARSET_BTF8, null, false);
        } else if (what == 0x102) {
            dismissProgressDialog();
            int size = data.size() / 4;
            for (int i = 0; i < size; i++) {
                if (data.get(4 * i).isEmpty() || data.get(4 * i + 1).isEmpty() ||
                        data.get(4 * i + 2).isEmpty() || data.get(4 * i + 3).isEmpty()) {
                    //filter empty content.
                    continue;
                }
                System.out.println(data.get(4 * i));
                SparseArray<String> stringSparseArray = new SparseArray<>();
                stringSparseArray.put(CAMPUS_CARD_CONSUME_TAG_TIME, data.get(4 * i));
                stringSparseArray.put(CAMPUS_CARD_CONSUME_TAG_PLACE, data.get(4 * i + 1));
                stringSparseArray.put(CAMPUS_CARD_CONSUME_TAG_MONEY, data.get(4 * i + 2));
                stringSparseArray.put(CAMPUS_CARD_CONSUME_TAG_LEFT, data.get(4 * i + 3));
                campusCardDataList.add(stringSparseArray);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPasswordError() {
        dismissProgressDialog();
        Snackbar.make(fab, R.string.error_password, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void onTimeoutError() {
        dismissProgressDialog();
        Snackbar.make(fab, R.string.connection_timeout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    //
    private class CampusCardAdapter extends RecyclerViewWithEmptySupport.Adapter<CampusCardAdapter.ViewHolder> {
        List<SparseArray<String>> data;

        public CampusCardAdapter(List<SparseArray<String>> data) {
            this.data = data;
        }

        @Override
        public CampusCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_campus_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CampusCardAdapter.ViewHolder holder, int position) {
            SparseArray<String> item = data.get(position);
            holder.consumePlace.setText(item.get(CAMPUS_CARD_CONSUME_TAG_PLACE));
            holder.consumeTime.setText(item.get(CAMPUS_CARD_CONSUME_TAG_TIME));
            holder.consumeMoney.setText(item.get(CAMPUS_CARD_CONSUME_TAG_MONEY));
            holder.consumeLeft.setText(item.get(CAMPUS_CARD_CONSUME_TAG_LEFT));
            holder.tagImage.setImageResource(StringUtils.getConsumptionTypeResourceByPlace(item.get(CAMPUS_CARD_CONSUME_TAG_PLACE)));
        }

        @Override
        public int getItemCount() {
            return data.size(); //todo size ==0,show default blank page.
        }

        public class ViewHolder extends RecyclerViewWithEmptySupport.ViewHolder {
            AppCompatImageView tagImage;
            AppCompatTextView consumePlace, consumeTime, consumeMoney, consumeLeft;

            public ViewHolder(View itemView) {
                super(itemView);
                tagImage = itemView.findViewById(R.id.campus_card_tag_image);
                consumePlace = itemView.findViewById(R.id.campus_card_consume_place);
                consumeTime = itemView.findViewById(R.id.campus_card_consume_time);
                consumeMoney = itemView.findViewById(R.id.campus_card_consume_money);
                consumeLeft = itemView.findViewById(R.id.campus_card_consume_left);
            }
        }
    }
}
