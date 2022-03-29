package com.example.cm.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.AuthActivity;
import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.databinding.ActivityRegisterBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import timber.log.Timber;

public class RegisterActivity extends AppCompatActivity implements AuthRepository.RegisterCallback, UserRepository.UsernamesRetrievedCallback {

    private AuthViewModel authViewModel;
    private ActivityRegisterBinding binding;
    private List<String> usernames;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTimer();
        initViewModel();
        initTexts();
        initTemporaryAuth();
    }

    private void initTemporaryAuth() {
        authViewModel.createTemporaryUser(this);
    }

    private void initTimer() {
        handler = new Handler();
        runnable = () -> {
            closeActivityOnTimeout();
        };
    }

    private void closeActivityOnTimeout() {
        binding.registerRegisterBtn.setEnabled(false);
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.registrationTooLong, Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });
        snackbar.show();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(RegisterActivity.this).get(AuthViewModel.class);
        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.registerLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            binding.registerRegisterBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.registerUsernameEditText.inputLabel.setText(R.string.registerUsernameText);
        binding.registerUsernameEditText.inputField.setHint(R.string.registerUsernameHint);
        binding.registerUsernameEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });

        binding.registerEmailEditText.inputLabel.setText(R.string.registerEmailText);
        binding.registerEmailEditText.inputField.setHint(R.string.userEmailHint);

        binding.registerPasswordEditText.inputLabel.setText(R.string.registerPasswordText);
        binding.registerPasswordEditText.inputField.setHint(R.string.userPasswordHint);
        binding.registerPasswordEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });

        binding.registerPasswordRepeatEditText.inputLabel.setText(R.string.registerPasswordRepeatText);
        binding.registerPasswordRepeatEditText.inputField.setHint(R.string.userPasswordHint);
        binding.registerPasswordRepeatEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });
    }

    private void initListeners() {
        authViewModel.getUsernames(this);
        binding.registerUsernameEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.registerRegisterBtn.setEnabled(false);

                // check if username is in use already
                if (usernames != null && usernames.contains(charSequence.toString())) {
                    binding.usernameAlreadyExistsTv.setVisibility(View.VISIBLE);
                } else {
                    binding.usernameAlreadyExistsTv.setVisibility(View.GONE);
                    binding.registerRegisterBtn.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.registerLoginBtn.setOnClickListener(this::goToLogin);
        binding.registerRegisterBtn.setOnClickListener(this::register);
    }

    private void register(View view) {
        String email = binding.registerEmailEditText.inputField.getText().toString();
        String username = binding.registerUsernameEditText.inputField.getText().toString();
        String password = binding.registerPasswordEditText.inputField.getText().toString();
        String passwordRepeated = binding.registerPasswordRepeatEditText.inputField.getText().toString();

        if (email.isEmpty() && username.isEmpty() ||
                email.isEmpty() && password.isEmpty() ||
                email.isEmpty() && passwordRepeated.isEmpty() ||
                username.isEmpty() && password.isEmpty() ||
                username.isEmpty() && passwordRepeated.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerMultipleFieldsEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerEmailEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (username.isEmpty() || username.length() < Constants.MIN_USERNAME_LENGTH) {
            Snackbar.make(binding.getRoot(), R.string.registerUsernameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (usernames != null && usernames.contains(username)) {
            Snackbar.make(binding.getRoot(), R.string.registerUsernameAlreadyExists, Snackbar.LENGTH_LONG).show();
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
        goToCreateProfile(email, username, password);
    }

    private void startTimer() {
        handler.postDelayed(runnable, Constants.MAX_REGISTRATION_TIME);
    }

    private void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        end();
        finish();
    }

    private void goToCreateProfile(String email, String username, String password) {
        Intent intent = new Intent(RegisterActivity.this, CreateProfileActivity.class);
        intent.putExtra(Constants.KEY_EMAIL, email);
        intent.putExtra(Constants.KEY_USERNAME, username);
        intent.putExtra(Constants.KEY_PASSWORD, password);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterSuccess(User user) {
        if (user.getEmail().equals(Constants.TEMP_EMAIL)) {
            startTimer();
            initListeners();
        } else {
            Timber.d(Constants.UNEXPECTED_USER);
        }
    }

    private void end() {
        if (authViewModel.getUserLiveData().getValue() != null
                && authViewModel.getUserLiveData().getValue().getEmail().equals(Constants.TEMP_EMAIL)) {
            authViewModel.deleteCurrentAuth();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        end();
        super.onDestroy();
    }

    @Override
    public void onUsernamesRetrieved(List<String> usernames) {
        this.usernames = usernames;
    }
}