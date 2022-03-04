package com.example.cm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cm.ui.onboarding.OnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isAuthenticated = true;
        }

        setupUI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_splash_screen);
        ImageView splashImage = findViewById(R.id.splash_img);
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        splashImage.setAnimation(splashAnim);

        SharedPreferences onBoardingSP = getSharedPreferences("onBoarding", MODE_PRIVATE);

        boolean isFirstTime = onBoardingSP.getBoolean("firstTime", true);

        if (isAuthenticated) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (isFirstTime) {
            SharedPreferences.Editor editor = onBoardingSP.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();

            Intent intent = new Intent(getApplication(), OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplication(), AuthActivity.class);
            startActivity(intent);
        }
        finish();
    }
}