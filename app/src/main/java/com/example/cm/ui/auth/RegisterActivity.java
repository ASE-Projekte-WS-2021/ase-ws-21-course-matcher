package com.example.cm.ui.auth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.AuthActivity;
import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.databinding.ActivityRegisterBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import timber.log.Timber;

public class RegisterActivity extends AppCompatActivity implements AuthRepository.LoginCallback, UserRepository.UsernamesRetrievedCallback {

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
        initListeners();
        initTexts();
        initTemporaryAuth();
    }

    private void initTemporaryAuth() {
        authViewModel.login(Constants.TEMP_EMAIL, Constants.TEMP_PASSWORD, this);
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
        binding.registerUsernameEditText.textInputLayout.setHint(R.string.input_label_username);
        binding.registerUsernameEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });

        binding.registerEmailEditText.textInputLayout.setHint(R.string.input_label_email);

        binding.registerPasswordEditText.textInputLayout.setHint(R.string.input_label_password);
        binding.registerPasswordEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });

        binding.registerPasswordRepeatEditText.textInputLayout.setHint(R.string.input_label_repeat_password);
        binding.registerPasswordRepeatEditText.inputField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_CHARACTER_NAME) });
    }

    private void initListeners() {
        binding.registerEmailEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.registerEmailEditText.textInputLayout.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.registerUsernameEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.registerRegisterBtn.setEnabled(false);

                // check if username is in use already
                if (usernames == null) {
                    binding.registerUsernameEditText.textInputLayout.setError(getString(R.string.error_loading));
                }
                if (usernames != null && usernames.contains(charSequence.toString())) {
                    binding.registerUsernameEditText.textInputLayout.setError(getString(R.string.registerUsernameAlreadyExists));
                } else {
                    binding.registerUsernameEditText.textInputLayout.setErrorEnabled(false);
                    binding.registerRegisterBtn.setEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.registerPasswordEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.registerPasswordEditText.textInputLayout.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.registerPasswordRepeatEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.registerPasswordRepeatEditText.textInputLayout.setErrorEnabled(false);
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
        boolean error = false;
        String email = binding.registerEmailEditText.inputField.getText().toString();
        String username = binding.registerUsernameEditText.inputField.getText().toString();
        String password = binding.registerPasswordEditText.inputField.getText().toString();
        String passwordRepeated = binding.registerPasswordRepeatEditText.inputField.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = true;
            binding.registerEmailEditText.textInputLayout.setError(getString(R.string.registerEmailEmpty));
        }

        if (username.isEmpty() || username.length() < Constants.MIN_USERNAME_LENGTH) {
            error = true;
            binding.registerUsernameEditText.textInputLayout.setError(getString(R.string.registerUsernameEmpty));
        }

        if (usernames != null && usernames.contains(username)) {
            error = true;
            binding.registerUsernameEditText.textInputLayout.setError(getString(R.string.registerUsernameAlreadyExists));
        }

        if (password.isEmpty() || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            error = true;
            binding.registerPasswordEditText.textInputLayout.setError(getString(R.string.registerPasswordEmpty));
        }

        if (passwordRepeated.isEmpty()) {
            error = true;
            binding.registerPasswordRepeatEditText.textInputLayout.setError(getString(R.string.registerPasswordRepeatedEmpty));
        }

        if (!password.equals(passwordRepeated)) {
            error = true;
            binding.registerPasswordEditText.textInputLayout.setError(getString(R.string.registerPasswordRepeatNotEqual));
            binding.registerPasswordRepeatEditText.textInputLayout.setError(getString(R.string.registerPasswordRepeatNotEqual));
        }

        if (!error) {
            disableBtn();
            goToCreateProfile(email, username, password);
        }
    }

    private void disableBtn() {
        binding.registerRegisterBtn.setEnabled(false);
        binding.registerRegisterBtn.setText(getResources().getText(R.string.confirm_button_loading));

        Drawable buttonDrawable = DrawableCompat.wrap(binding.registerRegisterBtn.getBackground());
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.gray600));
        binding.registerRegisterBtn.setBackground(buttonDrawable);
    }

    private void startTimer() {
        handler.postDelayed(runnable, Constants.MAX_REGISTRATION_TIME);
    }

    private void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        endTemporaryAuth();
        finish();
    }

    private void goToCreateProfile(String email, String username, String password) {
        Intent intent = new Intent(RegisterActivity.this, CreateProfileActivity.class);
        intent.putExtra(Constants.KEY_EMAIL, email);
        intent.putExtra(Constants.KEY_USERNAME, username);
        intent.putExtra(Constants.KEY_PASSWORD, password);
        startActivity(intent);
    }

    private void endTemporaryAuth() {
        if (authViewModel.getUserLiveData().getValue() != null
                && authViewModel.getUserLiveData().getValue().getEmail().equals(Constants.TEMP_EMAIL)) {
            authViewModel.logout();
        }
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        endTemporaryAuth();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.registerRegisterBtn.setEnabled(true);
        binding.registerRegisterBtn.setText(getResources().getText(R.string.registerRegisterBtn));
        binding.registerRegisterBtn.setBackground(getDrawable(R.drawable.btn_rounded));
    }

    @Override
    public void onUsernamesRetrieved(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public void onLoginSuccess(String email) {
        if (email.equals(Constants.TEMP_EMAIL)) {
            startTimer();
            authViewModel.getUsernames(this);
        } else {
            Timber.d(Constants.UNEXPECTED_USER);
        }
    }
}