package me.gensh.utils;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import me.gensh.helloustb.R;

/**
 * Helper class to manage notification channels, and create notifications.
 */

public class NotificationUtils extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */

    public NotificationUtils(Context ctx) {
        super(ctx);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chanDefault = new NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.notification_channel_default), NotificationManager.IMPORTANCE_HIGH);
            chanDefault.setLightColor(Color.BLUE);
            chanDefault.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chanDefault);
        }
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body  Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Builder getDefaultNotification(String title, String ticker, String body) {
        Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Builder(getApplicationContext(), PRIMARY_CHANNEL);
        } else {
            builder = new Builder(getApplicationContext());
        }

        builder.setContentTitle(title)
                .setContentText(body)
                .setTicker(ticker)
                .setAutoCancel(true);
        //set icon
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_adjust_white_24dp);
            builder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        return builder;
    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * cancel a notification.
     *
     * @param id The ID of the notification
     */
    public void cancel(int id) {
        getManager().cancel(id);
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}