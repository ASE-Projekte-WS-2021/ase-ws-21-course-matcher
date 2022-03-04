package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.databinding.ActivityLoginBinding;
import com.example.cm.ui.onboarding.OnboardingActivity;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private Button loginBtn;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginLoginBtn);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initViewModel();
        initListeners();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(LoginActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.loginLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            loginBtn.setEnabled(true);
        });
    }

    private void initListeners() {
        binding.loginLoginBtn.setOnClickListener(v -> login(v));
        binding.loginRegisterBtn.setOnClickListener(v -> goToRegister(v));
    }

    public void login(View view) {
        String email = binding.loginEmailEditText.getText().toString();
        String password = binding.loginPasswordEditText.getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            authViewModel.login(email, password);
            loginBtn.setEnabled(false);
        } else {
            Snackbar.make(findViewById(R.id.loginLayout), R.string.loginEmailPasswordNeeded, Snackbar.LENGTH_LONG)
                    .show();
        }
        authViewModel.login(email, password);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }
}