package com.example.cm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cm.databinding.ActivityAuthBinding;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.onboarding.OnboardingActivity;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListeners();

    }


    private void initListeners() {
        binding.startLoginBtn.setOnClickListener(this::toLogin);
        binding.startRegisterBtn.setOnClickListener(this::toRegister);
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