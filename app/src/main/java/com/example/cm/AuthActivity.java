package com.example.cm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.auth.RegisterActivity;
import com.example.cm.ui.onboarding.OnboardingActivity;

public class AuthActivity extends AppCompatActivity {

    Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_auth);

        initUI();
    }

    private void initUI() {
        registerBtn = findViewById(R.id.start_register_btn);
        loginBtn = findViewById(R.id.start_login_btn);
    }

    public void toLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void toRegister(View view) {
        startActivity(new Intent(this, OnboardingActivity.class));
        finish();
    }
}