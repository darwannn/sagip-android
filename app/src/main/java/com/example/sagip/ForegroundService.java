package com.example.sagip;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;

public class ForegroundService extends Service {

    //public static final String STOP_SERVICE_ACTION = "com.example.sagip.STOP_SERVICE";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String KEY_URL = "url";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        Timer intervalTimer = MainActivity.TimerManager.getIntervalTimer();
        // Perform your service logic here
        return START_STICKY;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Check if the received intent has the STOP_SERVICE_ACTION action
//        if (intent != null && STOP_SERVICE_ACTION.equals(intent.getAction())) {
//            // Handle the stop command
//
//            // First, stop the service from running in the foreground
//            stopForeground(true);
//
//            // Then, stop the service itself
//            stopSelf();
//
//            // Return START_NOT_STICKY to indicate that if the service is killed, it should not be restarted
//            return START_NOT_STICKY;
//        } else {
//            // Create a notification channel for the foreground service (if not already created)
//            createNotificationChannel();
//
//            // Start the service in the foreground with a notification
//            startForeground(NOTIFICATION_ID, buildNotification());
//
//            // Return START_STICKY to indicate that if the service is killed, it should be restarted
//            return START_STICKY;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        // Modify this URL with the desired webpage
        String webpageUrl = "https://google.com";

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_URL, webpageUrl);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SAGIP")
                .setContentText("Running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setNumber(0);

        if (isDarkModeEnabled()) {
            builder.setSmallIcon(R.drawable.sagip_white);
        } else {
            builder.setSmallIcon(R.drawable.sagip_black);
        }

        return builder.build();


    }

    private boolean isDarkModeEnabled() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

}
