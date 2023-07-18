package com.example.sagip;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(OnboardingActivtiy.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }, 2000);
    }
}
