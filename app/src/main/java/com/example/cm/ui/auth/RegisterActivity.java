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
        binding.registerUsernameEditText.inputLabel.setText(R.string.registerUsernameText);
        binding.registerUsernameEditText.inputField.setHint(R.string.registerUsernameHint);

        binding.registerFirstNameEditText.inputLabel.setText(R.string.registerFirstnameText);
        binding.registerFirstNameEditText.inputField.setHint(R.string.registerFirstNameHint);

        binding.registerLastNameEditText.inputLabel.setText(R.string.registerLastnameText);
        binding.registerLastNameEditText.inputField.setHint(R.string.registerLastNameHint);

        binding.registerEmailEditText.inputLabel.setText(R.string.registerEmailText);
        binding.registerEmailEditText.inputField.setHint(R.string.userEmailHint);

        binding.registerPasswordEditText.inputLabel.setText(R.string.registerPasswordText);
        binding.registerPasswordEditText.inputField.setHint(R.string.userPasswordHint);
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

        if (userName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerUsernameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (firstName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerFirstnameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (lastName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerLastnameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerEmailEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty() || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Snackbar.make(binding.getRoot(), R.string.registerPasswordEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }


        authViewModel.register(email, password, userName, firstName, lastName);
        registerBtn.setEnabled(false);
    }
}