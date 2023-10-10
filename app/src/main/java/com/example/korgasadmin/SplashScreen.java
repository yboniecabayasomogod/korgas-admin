package com.example.korgasadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_SCREEN_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        start new handler to start main activity close the splash activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                create a intent to start the main activity
                Intent mainActivity = new Intent(SplashScreen.this, Login.class);
                startActivity(mainActivity);
                finish();
                //            no animation
                overridePendingTransition(0,0);
            }
        }, SPLASH_SCREEN_DISPLAY_LENGTH);

    }
}