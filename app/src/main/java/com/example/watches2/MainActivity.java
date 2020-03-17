package com.example.watches2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static int TIME_FOR_SPLASH_SCREEN = 2000;

    Animation topAnim, bottomAnim, showingAnim;
    ImageView image1, image2;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Start animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        showingAnim = AnimationUtils.loadAnimation(this, R.anim.showing_animation);

        image1 = findViewById(R.id.top_image);
        image2 = findViewById(R.id.bottom_image);

        if(image2 == null){
            image1.setAnimation(showingAnim);
        }else{
            image1.setAnimation(bottomAnim);
            image2.setAnimation(topAnim);
        }


        //to set calling main activity
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, TickerActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_FOR_SPLASH_SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, TickerActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_FOR_SPLASH_SCREEN);
    }
}

