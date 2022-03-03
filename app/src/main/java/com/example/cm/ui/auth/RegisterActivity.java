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
        String userName = ((EditText) findViewById(R.id.registerUserNameEditText)).getText().toString();
        String email = ((EditText) findViewById(R.id.registerEmailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.registerPasswordEditText)).getText().toString();
        String firstName = ((EditText) findViewById(R.id.registerFirstNameEditText)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.registerLastNameEditText)).getText().toString();

        if (userName.length() > 0 && email.length() > 0 && password.length() > 0 && firstName.length() > 0 && lastName.length() > 0) {
            authViewModel.register(email, password, userName, firstName, lastName);
        } else {
            Toast.makeText(RegisterActivity.this, "All fields must be entered", Toast.LENGTH_SHORT).show();
        }
    }
}