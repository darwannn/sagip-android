package com.example.sagip;

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
                // Internet is available
                if (isOfflineActivityActive(context)) {
                    // Close OfflineActivity if active
                    closeOfflineActivity(context);
                }

                if (!isMainActivityActive(context)) {
                    // Open MainActivity if not already active
                    openMainActivity(context);
                }
            } else {
                // No internet available
                if (isMainActivityActive(context)) {
                    // Close MainActivity if active
                    closeMainActivity(context);
                }

                if (!isOfflineActivityActive(context)) {
                    // Open OfflineActivity if not already active
                    openOfflineActivity(context);
                }
            }
        }
    }

    private boolean isMainActivityActive(Context context) {
        // Implement a way to check if MainActivity is active or not
        // You can use a static variable, or a shared preference, or any other mechanism to store the state of MainActivity.
        // For example, you can use a boolean flag to indicate if MainActivity is active or not.
        return MainActivity.isMainActivityActive; // Replace with the appropriate variable or mechanism to check activity state.
    }

    private boolean isOfflineActivityActive(Context context) {
        // Implement a way to check if OfflineActivity is active or not
        // You can use a static variable, or a shared preference, or any other mechanism to store the state of OfflineActivity.
        // For example, you can use a boolean flag to indicate if OfflineActivity is active or not.
        return OfflineActivity.isOfflineActivityActive; // Replace with the appropriate variable or mechanism to check activity state.
    }

    private void openMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MainActivity.isMainActivityActive = true; // Update MainActivity state
    }

    private void closeMainActivity(Context context) {
        // Implement a way to close MainActivity
        // Depending on your app structure, you may need to use some other approach here, like calling finish() on the MainActivity.
        MainActivity.isMainActivityActive = false; // Update MainActivity state
    }

    private void openOfflineActivity(Context context) {
        Intent intent = new Intent(context, OfflineActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        OfflineActivity.isOfflineActivityActive = true; // Update OfflineActivity state
    }

    private void closeOfflineActivity(Context context) {
        // Implement a way to close OfflineActivity
        // Depending on your app structure, you may need to use some other approach here, like calling finish() on the OfflineActivity.
        OfflineActivity.isOfflineActivityActive = false; // Update OfflineActivity state
    }
}
