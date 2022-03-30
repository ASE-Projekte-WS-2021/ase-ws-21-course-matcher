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
import com.example.cm.databinding.ActivityRegisterBinding;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private Button registerBtn;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register);
        registerBtn = findViewById(R.id.registerRegisterBtn);

        authViewModel = new ViewModelProvider(RegisterActivity.this).get(AuthViewModel.class);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initViewModel();
        initListeners();
        initTexts();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(RegisterActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.registerLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            registerBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.registerUsernameEditText.textInputLayout.setHint(R.string.registerUsernameText);
        binding.registerFirstNameEditText.textInputLayout.setHint(R.string.registerFirstnameText);
        binding.registerLastNameEditText.textInputLayout.setHint(R.string.registerLastnameText);
        binding.registerEmailEditText.textInputLayout.setHint(R.string.registerEmailText);
        binding.registerPasswordEditText.textInputLayout.setHint(R.string.registerPasswordText);
    }

    private void initListeners() {
        binding.registerRegisterBtn.setOnClickListener(this::register);
        binding.registerLoginBtn.setOnClickListener(this::goToLogin);
    }

    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    public void register(View view) {
        String userName = binding.registerUsernameEditText.inputField.getText().toString();
        String email = binding.registerEmailEditText.inputField.getText().toString();
        String password = binding.registerPasswordEditText.inputField.getText().toString();
        String firstName = binding.registerFirstNameEditText.inputField.getText().toString();
        String lastName = binding.registerLastNameEditText.inputField.getText().toString();

        // Reset error fields
        binding.registerUsernameEditText.textInputLayout.setErrorEnabled(false);
        binding.registerEmailEditText.textInputLayout.setErrorEnabled(false);
        binding.registerPasswordEditText.textInputLayout.setErrorEnabled(false);
        binding.registerFirstNameEditText.textInputLayout.setErrorEnabled(false);
        binding.registerLastNameEditText.textInputLayout.setErrorEnabled(false);

        if (userName.isEmpty()) {
            binding.registerUsernameEditText.textInputLayout.setError(getString(R.string.registerUsernameEmpty));
            return;
        }

        if (firstName.isEmpty()) {
            binding.registerFirstNameEditText.textInputLayout.setError(getString(R.string.registerFirstnameEmpty));
            return;
        }

        if (lastName.isEmpty()) {
            binding.registerLastNameEditText.textInputLayout.setError(getString(R.string.registerLastnameEmpty));
            return;
        }

        if (email.isEmpty()) {
            binding.registerEmailEditText.textInputLayout.setError(getString(R.string.registerEmailEmpty));
            return;
        }

        if (password.isEmpty() || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            binding.registerPasswordEditText.textInputLayout.setError(getString(R.string.registerPasswordEmpty));
            return;
        }

        authViewModel.register(email, password, userName, firstName, lastName);
        registerBtn.setEnabled(false);
    }
}