package com.example.sagip;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkReceiver extends BroadcastReceiver {
    private static boolean isMainActivityActive = false;
    private static boolean isOfflineActivityActive = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Internet is available
                if (isOfflineActivityActive) {
                    // Close OfflineActivity if active
                    closeOfflineActivity(context);
                }

                if (!isMainActivityActive) {
                    // Open MainActivity if not already active
                    openMainActivity(context);
                }
            } else {
                // No internet available
                if (isMainActivityActive) {
                    // Close MainActivity if active
                    closeMainActivity(context);
                }

                if (!isOfflineActivityActive) {
                    // Open OfflineActivity if not already active
                    openOfflineActivity(context);
                }
            }
        }
    }

    private void openMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
        isMainActivityActive = true;
    }

    private void closeMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        isMainActivityActive = false;
    }

    private void openOfflineActivity(Context context) {
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
        isOfflineActivityActive = true;
    }

    private void closeOfflineActivity(Context context) {
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        isOfflineActivityActive = false;
    }
}
