package com.example.sagip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                if (isOfflineActivityActive(context)) {
                    closeOfflineActivity(context);
                }

                if (!isMainActivityActive(context)) {
                    openMainActivity(context);
                }
            } else {
                if (isMainActivityActive(context)) {
                    closeMainActivity(context);
                }

                if (!isOfflineActivityActive(context)) {

                    openOfflineActivity(context);
                }
            }
        }
    }

    private boolean isMainActivityActive(Context context) {
        return MainActivity.isMainActivityActive;
    }

    private boolean isOfflineActivityActive(Context context) {
        return OfflineActivity.isOfflineActivityActive;
    }

    private void openMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        MainActivity.isMainActivityActive = true;
    }

    private void closeMainActivity(Context context) {
        if (isMainActivityActive(context)) {
            MainActivity.isMainActivityActive = false;
            ((Activity) context).finish();
        }
    }


    private void openOfflineActivity(Context context) {
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        OfflineActivity.isOfflineActivityActive = true;
    }

    private void closeOfflineActivity(Context context) {

        if (isOfflineActivityActive(context)) {
            OfflineActivity.isOfflineActivityActive = false;
            ((Activity) context).finish();
        }
    }
}
