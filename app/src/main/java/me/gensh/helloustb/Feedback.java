package me.gensh.helloustb;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gensh.io.IOUtils;
import me.gensh.utils.DeviceInfo;
import me.gensh.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * updated by genshen on 2017.10.07
 * involved classed:
 * {@link me.gensh.utils.DeviceInfo}
 * {@link me.gensh.utils.Utils}
 * {@link me.gensh.utils.EasyFeedback}
 */
@RuntimePermissions
public class Feedback extends AppCompatActivity implements View.OnClickListener {

    final static public String KEY_EMAIL = "email";
    final static private int REQUEST_APP_SETTINGS = 321;
    final static private int PICK_IMAGE_REQUEST = 125;

    private boolean withInfo;
    public String LOG_TO_STRING = SystemLog.extractLogToString();
    private String emailId, deviceInfo;
    private String imagePath;

    @BindView(R.id.selectedImageView)
    AppCompatImageView selectedImageView;
    @BindView(R.id.selectContainer)
    LinearLayout selectContainer;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.info_legal)
    AppCompatTextView infoLegal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.feedback_submit:
// TODO: 2017/10/7
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        emailId = getIntent().getStringExtra(KEY_EMAIL);
        deviceInfo = DeviceInfo.getAllDeviceInfo(this, false);

        AppCompatCheckBox uploadInfoCheckBox = findViewById(R.id.upload_log_info_check);
        withInfo = uploadInfoCheckBox.isChecked();
        uploadInfoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                withInfo = checked;
                if (checked) {
                    infoLegal.setVisibility(View.VISIBLE);
                } else {
                    infoLegal.setVisibility(View.INVISIBLE);
                }
            }
        });

        CharSequence infoFeedbackStart = getResources().getString(R.string.info_fedback_legal_start);
        SpannableString deviceInfo = new SpannableString(getResources().getString(R.string.info_fedback_legal_system_info));
        deviceInfo.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                new AlertDialog.Builder(Feedback.this)
                        .setTitle(R.string.info_fedback_legal_system_info)
                        .setMessage(Feedback.this.deviceInfo)
                        .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }, 0, deviceInfo.length(), 0);

        CharSequence infoFeedbackAnd = getResources().getString(R.string.info_fedback_legal_and);
        SpannableString systemLog = new SpannableString(getResources().getString(R.string.info_fedback_legal_log_data));
        systemLog.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                new AlertDialog.Builder(Feedback.this)
                        .setTitle(R.string.info_fedback_legal_log_data)
                        .setMessage(Feedback.this.LOG_TO_STRING)
                        .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }, 0, systemLog.length(), 0);
        CharSequence infoFeedbackEnd = getResources().getString(R.string.info_fedback_legal_will_be_sent, getString(R.string.app_name));
        Spanned finalLegal = (Spanned) TextUtils.concat(infoFeedbackStart, deviceInfo, infoFeedbackAnd, systemLog, infoFeedbackEnd);

        infoLegal.setText(finalLegal);
        infoLegal.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    @OnClick({R.id.submitSuggestion, R.id.selectImage})
    public void onClick(View view) {
        if (view.getId() == R.id.submitSuggestion) {
            String suggestion = editText.getText().toString();
            if (suggestion.trim().length() > 0) {
                sendEmail(suggestion);
                finish();
            } else
                editText.setError(getString(R.string.please_write));
        } else if (view.getId() == R.id.selectImage)
            FeedbackPermissionsDispatcher.selectPictureWithPermissionCheck(this);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void selectPicture() {
        imagePath = null;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), PICK_IMAGE_REQUEST);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForReadExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_feedback_external_storage_rationale)
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

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForReadExternalStorage() {
        Toast.makeText(this, R.string.permission_feedback_external_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForReadExternalStorage() {
//        Toast.makeText(this, R.string.permission_feedback_external_storage_neverask, Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_feedback_external_storage_neverask)
                .setPositiveButton(R.string.permission_button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        settings.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(settings); //note: not ForResult.
                    }
                })
                .setNegativeButton(R.string.permission_button_deny, null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FeedbackPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == REQUEST_APP_SETTINGS) {
            FeedbackPermissionsDispatcher.selectPictureWithPermissionCheck(this);
        } else */
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagePath = Utils.getPath(this, data.getData());
            selectedImageView.setImageBitmap(Utils.decodeSampledBitmap(imagePath, selectedImageView.getWidth(), selectedImageView.getHeight()));
            selectContainer.setVisibility(View.GONE);

            Toast.makeText(this, getString(R.string.click_again), Toast.LENGTH_SHORT).show();  //// TODO: 2017/10/8 toast again
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendEmail(String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_mail_subject, getString(R.string.app_name)));
        ArrayList<String> extra_text = new ArrayList<>();
        extra_text.add(body);
        emailIntent.putStringArrayListExtra(Intent.EXTRA_TEXT, extra_text);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        emailIntent.putExtra(Intent.EXTRA_TEXT, body); //see https://issuetracker.google.com/issues/36956569

        ArrayList<Uri> uris = new ArrayList<>();
        if (withInfo) {
            File deviceInfoFile = createFileFromString(new File(getExternalCacheDir(), IOUtils.CACHE_LOGS_DIRECTORY), deviceInfo, getString(R.string.file_name_device_info));
            if (deviceInfoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uris.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", deviceInfoFile));
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uris.add(Uri.fromFile(deviceInfoFile));
                }
            }

            File logFile = createFileFromString(new File(getExternalCacheDir(), IOUtils.CACHE_LOGS_DIRECTORY), LOG_TO_STRING, getString(R.string.file_name_device_log));
            if (logFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uris.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", logFile));
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uris.add(Uri.fromFile(logFile));
                }
            }
        }

        if (imagePath != null) {
            Uri imageUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(imagePath));
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                imageUri = Uri.parse("file://" + imagePath);
            }
            uris.add(imageUri);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        //        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback_two)));
        startActivity(Utils.createEmailOnlyChooserIntent(this, emailIntent, getString(R.string.send_feedback_two))); //todo understand
    }

    /**
     * no permission needed see: http://unclechen.github.io/2016/03/06/Android6.0%E6%9D%83%E9%99%90%E9%80%82%E9%85%8D%E4%B9%8BSD%E5%8D%A1%E5%86%99%E5%85%A5/
     *
     * @param parent parent directory
     * @param text   the text to be wrote
     * @param name   file name
     * @return file or null if error
     */
    private File createFileFromString(File parent, String text, String name) {
        try {
            File file = IOUtils.createFile(parent, name);
            //BufferedWriter for performance, false to overrite to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, false));
            buf.write(text);
            buf.close();
            return file;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    private static class SystemLog {
        final static String COMMAND = "logcat -d -v threadtime *:*";

        static String extractLogToString() {
            StringBuilder result = new StringBuilder("\n\n ==== SYSTEM-LOG ===\n\n");
            int pid = android.os.Process.myPid();
            try {
                Process process = Runtime.getRuntime().exec(COMMAND);

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    if (currentLine.contains(String.valueOf(pid))) {
                        result.append(currentLine);
                        result.append("\n");
                    }
                }
                //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            //clear the log
            try {
                Runtime.getRuntime().exec("logcat -c");
            } catch (IOException e) {
                // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            return result.toString();
        }
    }
}
