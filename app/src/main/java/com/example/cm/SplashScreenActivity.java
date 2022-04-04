package com.example.cm;

import static com.example.cm.Constants.PREFS_FIRST_TIME_KEY;
import static com.example.cm.Constants.PREFS_ONBOARDING_KEY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cm.databinding.ActivitySplashScreenBinding;
import com.example.cm.ui.onboarding.OnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private boolean isAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_splash_screen);

        initAuthentication();
        initUI();
    }

    private void initAuthentication() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isAuthenticated = true;
        }
    }

    private void initUI() {
        SharedPreferences onBoardingSP = getSharedPreferences(PREFS_ONBOARDING_KEY, MODE_PRIVATE);
        boolean isFirstTime = onBoardingSP.getBoolean(PREFS_FIRST_TIME_KEY, true);
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        binding.splashImg.setAnimation(splashAnim);

        if (isAuthenticated) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (isFirstTime) {
            SharedPreferences.Editor editor = onBoardingSP.edit();
            editor.putBoolean(PREFS_FIRST_TIME_KEY, false);
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