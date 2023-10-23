package com.example.sagip;


import static com.example.sagip.ForegroundService.KEY_URL;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sagip.MainActivity;
import com.example.sagip.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

public class CloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID ="101" ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),  remoteMessage.getData().get("linkId"));
    }

    private void showNotification(String title, String message, String linkId) {
        String webpageUrl = "https://www.sagip.live/notification";

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_URL, webpageUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("com.example.sagip.FINISH_ACTIVITY");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.sagip_status_bar_icon)
                // Add BigTextStyle for expandable notification
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)); // Use the message as the expanded text

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }


}
