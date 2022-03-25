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
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register);
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
            binding.registerRegisterBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.registerEmailEditText.inputLabel.setText(R.string.registerEmailText);
        binding.registerEmailEditText.inputField.setHint(R.string.userEmailHint);

        binding.registerPasswordEditText.inputLabel.setText(R.string.registerPasswordText);
        binding.registerPasswordEditText.inputField.setHint(R.string.userPasswordHint);

        binding.registerPasswordRepeatEditText.inputLabel.setText(R.string.registerPasswordRepeatText);
        binding.registerPasswordRepeatEditText.inputField.setHint(R.string.userPasswordHint);
    }


    private void initListeners() {
        binding.registerLoginBtn.setOnClickListener(this::goToLogin);
        binding.registerRegisterBtn.setOnClickListener(this::register);
    }

    private void register(View view) {
        String email = binding.registerEmailEditText.inputField.getText().toString();
        String password = binding.registerPasswordEditText.inputField.getText().toString();
        String passwordRepeated = binding.registerPasswordRepeatEditText.inputField.getText().toString();

        if (email.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerEmailEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty() || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Snackbar.make(binding.getRoot(), R.string.registerPasswordEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordRepeated)) {
            Snackbar.make(binding.getRoot(), R.string.registerPasswordRepeatNotEqual, Snackbar.LENGTH_LONG).show();
            return;
        }

        binding.registerRegisterBtn.setEnabled(false);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_EMAIL, email);
        bundle.putString(Constants.KEY_PASSWORD, password);
        goToCreateProfile(bundle);
    }

    private void goToCreateProfile(Bundle bundle) {
        startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class), bundle);
        finish();
    }

    private void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

}