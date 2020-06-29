package com.muqdd.iuob2.notification;

import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage.Notification;
import com.google.firebase.messaging.RemoteMessage;
import com.muqdd.iuob2.R;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Ali Yusuf on 4/12/2017.
 * iUOB-2
 */

public class FCMMessageHandler extends FirebaseMessagingService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Map<String, String> data = remoteMessage.getData();
        // String from = remoteMessage.getFrom();

        // get notification details
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            createNotification(notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    // Creates notification based on course and body received
    private void createNotification(Notification notification) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "")
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setSmallIcon(R.drawable.ic_stat_logo_white)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
        }
    }

}