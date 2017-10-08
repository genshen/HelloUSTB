package me.gensh.utils;

import android.content.Context;
import android.content.Intent;

import me.gensh.helloustb.Feedback;

/**
 * Created by R Ankit on 28-10-2016.
 */

public class EasyFeedback {
    private Context context;
    private String emailId;

    public EasyFeedback(Builder builder) {
        this.emailId = builder.emailId;
        this.context = builder.context;
    }

    public static class Builder {
        private Context context;
        private String emailId;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withEmail(String email) {
            this.emailId = email;
            return this;
        }

        public EasyFeedback build() {
            return new EasyFeedback(this);
        }
    }

    public void start() {
        Intent intent = new Intent(context, Feedback.class);
        intent.putExtra(Feedback.KEY_EMAIL, emailId);
        context.startActivity(intent);
    }

}
