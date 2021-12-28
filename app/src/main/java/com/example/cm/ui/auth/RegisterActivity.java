package com.example.cm.ui.auth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cm.MainActivity;
import com.example.cm.R;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(RegisterActivity.this).get(AuthViewModel.class);
        setContentView(R.layout.activity_register);

        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(View view) {
        String userName = ((EditText)findViewById(R.id.registerUserNameEditText)).getText().toString();
        String email = ((EditText)findViewById(R.id.registerEmailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.registerPasswordEditText)).getText().toString();

        if (userName.length() > 0 && email.length() > 0 && password.length() > 0) {
            authViewModel.register(email, password, userName);
        } else {
            Toast.makeText(RegisterActivity.this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
        }
    }
}