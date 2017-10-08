package me.gensh.helloustb;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Calendar;

import me.gensh.io.IOUtils;
import me.gensh.views.CircularImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class Account extends AppCompatActivity {
    final static int PICK_PHOTO_REQUEST_CODE = 0x100;
    Bitmap bitmap;
    CircularImageView avatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        avatarImageView = findViewById(R.id.avatar);
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountPermissionsDispatcher.pickImageForCropWithPermissionCheck(Account.this);
            }
        });
        setAvatar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
//                AccountPermissionsDispatcher.pickImageForCropWithPermissionCheck(this);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        ((ImageView) findViewById(R.id.account_background)).setImageResource(drawable);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO_REQUEST_CODE:
                    Uri sourceUri = intent.getData();
                    File f = new File(getFilesDir(), IOUtils.HELLO_USTB_AVATAR_NAME);

                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//                    options.setLogoColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary)); // color of all active button
                    options.setDimmedLayerColor(ContextCompat.getColor(this, R.color.colorPrimary)); //background of crop frame
//                    options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    UCrop.of(sourceUri, Uri.fromFile(f))  //todo contentURi from file
                            .withOptions(options)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(300, 300)
                            .start(this);
                    break;
                case UCrop.REQUEST_CROP:
//                    final Uri resultUri = UCrop.getOutput(intent);
                    setAvatar();
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, R.string.edit_course_week_id, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDestroy();
    }

    private void setAvatar() {
        File avatarFile = new File(getFilesDir(), IOUtils.HELLO_USTB_AVATAR_NAME);
        if (avatarFile.exists()) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeFile(avatarFile.getPath());
            avatarImageView.setImageBitmap(bitmap);
        }
    }

    public void AccountClick(View v) {
        switch (v.getId()) {
            case R.id.account_ac:
                Intent account_intent = new Intent(this, AccountManager.class);
                startActivity(account_intent);
                break;
            case R.id.account_msg:
                break;
            case R.id.account_collection:
                break;

        }
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void pickImageForCrop() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_REQUEST_CODE);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showReadWriteExternalStorageRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_account_read_write_external_storage_rationale)
                .setPositiveButton(R.string.permission_button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForReadWriteExternalStorage() {
        Toast.makeText(this, R.string.permission_account_read_write_external_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForReadWriteExternalStorage() {
        Toast.makeText(this, R.string.permission_account_read_write_external_storage_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        AccountPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
