package com.java.threathawk;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.android.material.color.DynamicColors;
import com.java.threathawk.Activity.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = findViewById(R.id.text_logo);
        Animation translationAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_from_bottom);
        Animation fadeInAnim = new AlphaAnimation(0, 1);
        fadeInAnim.setInterpolator(new DecelerateInterpolator());
        fadeInAnim.setDuration(1200);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(translationAnim);
        animation.addAnimation(fadeInAnim);


        imageView.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}