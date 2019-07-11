package me.gensh.helloustb;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.database.DBAccounts;
import me.gensh.database.DeleteData;
import me.gensh.database.QueryData;
import me.gensh.utils.LoginDialog;

public class AccountManager extends AppCompatActivity {
    final static private int buttonsCount = 4;
    final static private int[] USER_TYPE = {LoginDialog.UserType.ELE, LoginDialog.UserType.E, LoginDialog.UserType.NET, LoginDialog.UserType.VOL};
    @BindViews({R.id.account_manager_logout_ele, R.id.account_manager_logout_e,
            R.id.account_manager_logout_network, R.id.account_manager_logout_volunteer})
    List<Button> LogoutButtons;

    SparseArray<Long> accountIdMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<DBAccounts> accounts = QueryData.queryAllAccount(((MyApplication) getApplication()).getDaoSession());
        Long id;
        for (int i = 0; i < buttonsCount; i++) {
            if ((id = getAccountIdByUserType(accounts, USER_TYPE[i])) != null) {
                LogoutButtons.get(i).setClickable(true);
                LogoutButtons.get(i).setEnabled(true);
                accountIdMap.put(USER_TYPE[i], id);
            }
        }
    }

    Long getAccountIdByUserType(List<DBAccounts> accounts, int type) {
        for (DBAccounts account : accounts) {
            if (account.getType() == type) {
                return account.getId();
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.account_manager_logout_ele, R.id.account_manager_logout_e,
            R.id.account_manager_logout_network, R.id.account_manager_logout_volunteer})
    public void delButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.account_manager_logout_ele:
                deleteConfirm(view, accountIdMap.get(LoginDialog.UserType.ELE), R.string.logout_ele, R.string.logout_ele_confirm, R.string.logout_ele_success);
                break;
            case R.id.account_manager_logout_e:
                deleteConfirm(view, accountIdMap.get(LoginDialog.UserType.E), R.string.logout_e, R.string.logout_e_confirm, R.string.logout_e_success);
                break;
            case R.id.account_manager_logout_network:
                deleteConfirm(view, accountIdMap.get(LoginDialog.UserType.NET), R.string.logout_network, R.string.logout_net_confirm, R.string.logout_net_success);
                break;
            case R.id.account_manager_logout_volunteer:
                deleteConfirm(view, accountIdMap.get(LoginDialog.UserType.VOL), R.string.logout_volunteer, R.string.logout_volunteer, R.string.logout_vol_success);
                break;
        }
    }

    private void deleteConfirm(final View view, final long idInDB, int dialogTitleRes, int dialogMessageRes, final int successMessageRes) {
        new AlertDialog.Builder(this)
                .setTitle(dialogTitleRes)
                .setMessage(dialogMessageRes)
                .setNegativeButton(R.string.alert_cancel, null)
                .setPositiveButton(R.string.alert_sign_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DeleteData.deleteAccountById(((MyApplication) getApplication()).getDaoSession(), idInDB)) {
                            Toast.makeText(AccountManager.this, successMessageRes, Toast.LENGTH_SHORT).show();
                            view.setClickable(false);
                            view.setEnabled(false);
                        }
                    }
                })
                .show();
    }

}
