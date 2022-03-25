package com.example.cm.ui.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.cm.Constants;
import com.example.cm.MainActivity;
import com.example.cm.R;
import com.example.cm.databinding.ActivityRegisterProfileBinding;
import com.example.cm.utils.Navigator;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateProfileActivity extends AppCompatActivity {

    private Bundle bundle;
    private AuthViewModel authViewModel;
    private ActivityRegisterProfileBinding binding;
    private ActivityResultLauncher<String> storagePermissionRequestLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imgString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register_profile);
        binding = ActivityRegisterProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bundle = getIntent().getExtras();

        initViewModel();
        initListeners();
        initImagePicker();
        initPermissionRequest();
        initTexts();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(CreateProfileActivity.this).get(AuthViewModel.class);
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.registerLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            binding.createProfileBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.registerUsernameEditText.inputLabel.setText(R.string.registerUsernameText);
        binding.registerUsernameEditText.inputField.setHint(R.string.registerUsernameHint);

        binding.registerDisplayNameEditText.inputLabel.setText(R.string.registerFirstnameText);
        binding.registerDisplayNameEditText.inputField.setHint(R.string.registerFirstNameHint);
    }

    private void initListeners() {
        binding.editProfileImageBtn.setOnClickListener(this::onEditImgClicked);
        binding.createProfileBtn.setOnClickListener(this::registerAndStart);
        binding.registerUsernameEditText.inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                binding.createProfileBtn.setEnabled(false);
                if (authViewModel.doesUsernameExist(charSequence.toString())) {
                    binding.usernameAlreadyExistsTv.setVisibility(View.VISIBLE);
                } else {
                    binding.usernameAlreadyExistsTv.setVisibility(View.GONE);
                    binding.createProfileBtn.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Initialize the image picker
     */
    private void initImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Intent intent = result.getData();
                            Uri uri = intent.getData();
                            setImgString(uri);
                        } catch (FileNotFoundException e) {
                            Snackbar.make(binding.getRoot(), R.string.edit_profile_error_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setImgString(Uri uri) throws FileNotFoundException {
        InputStream imageStream = getContentResolver().openInputStream(uri);
        Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.QUALITY_PROFILE_IMG, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        imgString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * Initialize the permission request
     */
    private void initPermissionRequest() {
        storagePermissionRequestLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        imagePickerLauncher.launch(intent);
                    }
                });
    }

    private void onEditImgClicked(View view) {
        boolean hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!hasReadExternalStoragePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            storagePermissionRequestLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    public void registerAndStart(View view) {
        String email = bundle.getString(Constants.KEY_EMAIL);
        String password = bundle.getString(Constants.KEY_PASSWORD);
        String userName = binding.registerUsernameEditText.inputField.getText().toString();
        String displayName = binding.registerDisplayNameEditText.inputField.getText().toString();

        if (userName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerUsernameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (displayName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerDisplayNameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (authViewModel.doesUsernameExist(userName))

        authViewModel.register(email, password, userName, displayName, imgString);
        binding.createProfileBtn.setEnabled(false);
    }
}