package me.gensh.helloustb;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import me.gensh.encrypt.MailAccount;
import me.gensh.network.MailSender;

public class Feedback extends AppCompatActivity {
    EditText FeedbackContact, FeedbackContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FeedbackContent = (EditText) findViewById(R.id.FeedbackContent);
        FeedbackContact = (EditText) findViewById(R.id.FeedbackContact);
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
                String body = FeedbackContent.getText().toString();
                if (body.isEmpty()) {
                    Snackbar.make(FeedbackContact, R.string.submit_fail_empty, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                } else if (!((MyApplication) getApplication()).CheckNetwork()) {
                    Snackbar.make(FeedbackContact, R.string.NoNetwork, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                }
                sendMail(body);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMail(String body) {
        String contactWay = FeedbackContact.getText().toString();
        String service = getServiceInformation();
        if (!contactWay.equals("")) {
            body += "\n联系方式：" + contactWay;
        }
        body += service;
        SenderRunnable senderRunnable = new SenderRunnable(MailAccount.getMailAccount(), MailAccount.getMailPassword());
        senderRunnable.setMail("MY USTB意见反馈", body, MailAccount.getMailAccount(), null);
        Toast.makeText(Feedback.this, R.string.submitSuccess, Toast.LENGTH_SHORT).show();
        new Thread(senderRunnable).start();

        FeedbackContent.setText("");    //clear EditText
        FeedbackContact.setText("");
    }

    private String getServiceInformation() {
        return "\n设备型号：" + android.os.Build.MODEL +
                "\nAndroid版本：" + android.os.Build.VERSION.RELEASE;
    }

    class SenderRunnable implements Runnable {

        private String user;
        private String password;
        private String subject;
        private String body;
        private String receiver;
        private MailSender sender;
        private String attachment;

        public SenderRunnable(String user, String password) {
            this.user = user;
            this.password = password;
            sender = new MailSender(user, password);
            String mailhost = user.substring(user.lastIndexOf("@") + 1, user.lastIndexOf("."));
            if (!mailhost.equals("gmail")) {
                mailhost = "smtp." + mailhost + ".com";
                Log.i("hello", mailhost);
                sender.setMailhost(mailhost);
            }
        }

        public void setMail(String subject, String body, String receiver, String attachment) {
            this.subject = subject;
            this.body = body;
            this.receiver = receiver;
            this.attachment = attachment;
        }

        public void run() {
            try {
                sender.sendMail(subject, body, user, receiver, attachment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

