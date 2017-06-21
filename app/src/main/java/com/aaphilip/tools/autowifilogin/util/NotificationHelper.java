package com.aaphilip.tools.autowifilogin.util;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.aaphilip.tools.autowifilogin.BuildConfig;
import com.aaphilip.tools.autowifilogin.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    private static final int SUCCESS_NOTIF_ID = 1;
    private static final int ALREADY_IN_NOTIF_ID = 2;

    public static void showSuccessfulLoginNotification(Context context) {
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.wifi)
                        .setContentTitle("Intuit Wifi Buddy")
                        .setContentText("You've just been logged in!");

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(SUCCESS_NOTIF_ID, notifBuilder.build());

    }

    public static void showAlreadyLoggedInNotification(Context context) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.wifi)
                        .setContentTitle("Intuit Wifi Buddy")
                        .setContentText("Your login has been refreshed!");

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(ALREADY_IN_NOTIF_ID, notifBuilder.build());

    }
}
