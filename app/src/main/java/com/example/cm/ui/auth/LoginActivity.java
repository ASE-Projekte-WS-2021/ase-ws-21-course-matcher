package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.databinding.ActivityLoginBinding;
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
        initTexts();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(LoginActivity.this).get(AuthViewModel.class);
        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.loginLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            loginBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.loginEmailEditText.inputLabel.setText(R.string.registerEmailText);
        binding.loginEmailEditText.inputField.setHint(R.string.userEmailHint);

        binding.loginPasswordEditText.inputLabel.setText(R.string.registerPasswordText);
        binding.loginPasswordEditText.inputField.setHint(R.string.userPasswordHint);
    }

    private void initListeners() {
        binding.loginLoginBtn.setOnClickListener(this::login);
        binding.loginRegisterBtn.setOnClickListener(this::goToRegister);
    }

    public void login(View view) {
        String email = binding.loginEmailEditText.inputField.getText().toString();
        String password = binding.loginPasswordEditText.inputField.getText().toString();

        if (email.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.loginEmailNeeded, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.loginPasswordNeeded, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty() && password.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.loginEmailPasswordNeeded, Snackbar.LENGTH_LONG).show();
            return;
        }

        authViewModel.login(email, password, null);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}