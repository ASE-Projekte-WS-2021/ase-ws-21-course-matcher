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
import com.example.cm.ui.onboarding.OnboardingActivity;
import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginLoginBtn);
        authViewModel = new ViewModelProvider(LoginActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.loginLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            loginBtn.setEnabled(true);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(View view) {
        String email = ((EditText) findViewById(R.id.loginEmailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPasswordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            authViewModel.login(email, password);
            loginBtn.setEnabled(false);
        } else {
            Snackbar.make(findViewById(R.id.loginLayout), R.string.loginEmailPasswordNeeded, Snackbar.LENGTH_LONG).show();
        }
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }


}