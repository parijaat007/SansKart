package com.example.sanskart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_Screen extends AppCompatActivity {

    TextView dubstep, dabba;
    ImageView logo;

    private static int SPLASH_SCREEN = 2500; //2.5 secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        logo = findViewById(R.id.logoImage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash_Screen.this, LoginActivity.class));
                finish();
            }
        },SPLASH_SCREEN);
    }
}
