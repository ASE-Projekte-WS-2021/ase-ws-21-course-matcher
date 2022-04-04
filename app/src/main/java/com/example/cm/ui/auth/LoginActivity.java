package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity implements AuthRepository.LoginCallback {

    private AuthViewModel authViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
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
            binding.loginLoginBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.loginEmailEditText.textInputLayout.setHint(R.string.registerEmailText);
        binding.loginPasswordEditText.textInputLayout.setHint(R.string.registerPasswordText);
    }

    private void initListeners() {
        binding.loginLoginBtn.setOnClickListener(this::login);
        binding.loginRegisterBtn.setOnClickListener(this::goToRegister);
    }

    public void login(View view) {
        if(binding.loginEmailEditText.inputField.getText() == null || binding.loginPasswordEditText.inputField.getText() == null) {
            return;
        }

        String email = binding.loginEmailEditText.inputField.getText().toString().trim();
        String password = binding.loginPasswordEditText.inputField.getText().toString().trim();

        // Reset error fields
        binding.loginEmailEditText.textInputLayout.setErrorEnabled(false);
        binding.loginPasswordEditText.textInputLayout.setErrorEnabled(false);

        if (email.isEmpty() && password.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.loginEmailPasswordNeeded, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty()) {
            binding.loginEmailEditText.textInputLayout.setError(getString(R.string.loginEmailNeeded));
            return;
        }

        if (email.equals(Constants.TEMP_EMAIL)) {
            binding.loginEmailEditText.textInputLayout.setError(getString(R.string.loginEmailUnexpected));
            return;
        }

        if (password.isEmpty()) {
            binding.loginPasswordEditText.textInputLayout.setError(getString(R.string.loginPasswordNeeded));
            return;
        }

        authViewModel.login(email, password, this);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSuccess(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}