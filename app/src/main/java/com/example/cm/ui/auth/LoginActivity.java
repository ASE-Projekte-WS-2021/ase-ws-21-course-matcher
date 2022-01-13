package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.MainActivity;
import com.example.cm.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        authViewModel = new ViewModelProvider(LoginActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(View view) {
        String email = ((EditText) findViewById(R.id.loginEmailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPasswordEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            authViewModel.login(email, password);
        } else {
            Toast.makeText(LoginActivity.this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


}