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

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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