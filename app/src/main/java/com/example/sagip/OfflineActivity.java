package com.example.sagip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

public class OfflineActivity extends AppCompatActivity {
    private NetworkReceiver networkStateChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        networkStateChangeReceiver = new NetworkReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver to listen for network state changes
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is paused
        unregisterReceiver(networkStateChangeReceiver);
    }
}
