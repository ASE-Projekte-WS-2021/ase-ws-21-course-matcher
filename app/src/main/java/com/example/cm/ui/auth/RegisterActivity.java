package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initViewModel();
        initListeners();
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
    }

    private void initListeners() {
        binding.registerRegisterBtn.setOnClickListener(v -> register(v));
        binding.registerLoginBtn.setOnClickListener(v -> goToLogin(v));
    }

    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    public void register(View view) {
        String userName = binding.registerUserNameEditText.getText().toString();
        String email = binding.registerEmailEditText.getText().toString();
        String password = binding.registerPasswordEditText.getText().toString();
        String firstName = binding.registerFirstNameEditText.getText().toString();
        String lastName = binding.registerLastNameEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields must be entered", Toast.LENGTH_SHORT).show();
            return;
        }
        authViewModel.register(email, password, userName, firstName, lastName);
    }
}