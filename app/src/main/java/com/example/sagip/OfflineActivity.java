package com.example.sagip;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OfflineActivity extends AppCompatActivity {
    private NetworkReceiver networkStateChangeReceiver;
    Button button1, button2, button3, button4;
    public static boolean isOfflineActivityActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        // removes action bar
       // ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        networkStateChangeReceiver = new NetworkReceiver();

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneApp("09999999999");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneApp("09999999999");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneApp("09999999999");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneApp("09999999999");
            }
        });
    }

    private void openPhoneApp(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(android.net.Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOfflineActivityActive = true;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOfflineActivityActive = false;
        unregisterReceiver(networkStateChangeReceiver);
    }

}
