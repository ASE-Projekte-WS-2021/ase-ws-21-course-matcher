package com.example.cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.ui.auth.AuthViewModel;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.onboarding.OnboardingActivity;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIMER = 1000;
    ImageView splashImage;
    Animation splashAnim;
    SharedPreferences onBoardingSP;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        FirebaseUser firebaseUser = authViewModel.getUserLiveData().getValue();

        if (firebaseUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_splash_screen);

        setupUI();
    }

    private void setupUI() {
        splashImage = findViewById(R.id.splash_img);
        splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);

        splashImage.setAnimation(splashAnim);

        new Handler().postDelayed(() -> {
            onBoardingSP = getSharedPreferences("onBoarding", MODE_PRIVATE);

            boolean isFirstTime = onBoardingSP.getBoolean("firstTime", true);

            if(isFirstTime) {

                SharedPreferences.Editor editor = onBoardingSP.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();

                Intent intent = new Intent(SplashScreenActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }, SPLASH_TIMER);
    }
}