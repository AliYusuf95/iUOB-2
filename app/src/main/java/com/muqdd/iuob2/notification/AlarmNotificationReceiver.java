package com.muqdd.iuob2.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.muqdd.iuob2.R;
import com.muqdd.iuob2.features.main.MainActivity;

/**
 * Created by Ali Yusuf on 4/14/2017.
 * iUOB-2
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_TITLE = "notification-title";
    public static String NOTIFICATION_TEXT = "notification-text";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(!intent.hasExtra(NOTIFICATION_TITLE) && !intent.hasExtra(NOTIFICATION_TEXT)
                && !intent.hasExtra(NOTIFICATION_ID))
            return;

        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String body = intent.getStringExtra(NOTIFICATION_TEXT);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_logo_white)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        Notification notification = builder.build();

        Intent nIntent = new Intent(context, MainActivity.class);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(context, 0,
                nIntent, 0);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, notification);

    }
}
