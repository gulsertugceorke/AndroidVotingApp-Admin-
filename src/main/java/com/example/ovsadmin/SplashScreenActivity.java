package com.example.ovsadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    long delay = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Timer runTimer = new Timer();
        TimerTask showTimer = new TimerTask() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
            }
        };

        runTimer.schedule(showTimer,delay);
    }
}