package com.example.cm.ui.auth;

import android.Manifest;
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
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.databinding.ActivityRegisterProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.cm.Constants.MAX_CHAR_COUNT;

public class CreateProfileActivity extends AppCompatActivity implements AuthRepository.RegisterCallback {

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
        initImagePicker();
        initPermissionRequest();
        initTexts();
        initListeners();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(CreateProfileActivity.this).get(AuthViewModel.class);
        authViewModel.getErrorLiveData().observe(this, errorMsg -> {
            Snackbar.make(findViewById(R.id.registerLayout), errorMsg, Snackbar.LENGTH_LONG).show();
            binding.createProfileBtn.setEnabled(true);
        });
    }

    private void initTexts() {
        binding.registerDisplayNameEditText.inputLabel.setText(R.string.registerDisplaynameText);
        binding.registerDisplayNameEditText.inputField.setHint(R.string.registerFirstNameHint);
    }

    private void initListeners() {
        binding.editProfileImageBtn.setOnClickListener(this::onEditImgClicked);
        binding.createProfileBtn.setOnClickListener(this::registerAndStart);

        binding.inputFieldBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currCharCount = s.length();
                binding.bioCharacterCount.setText(String.format("%d/%d", currCharCount, MAX_CHAR_COUNT));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_CHAR_COUNT) {
                    binding.inputFieldBio.getText().delete(MAX_CHAR_COUNT, s.length());
                }
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
        String username = bundle.getString(Constants.KEY_USERNAME);
        String password = bundle.getString(Constants.KEY_PASSWORD);
        String displayName = binding.registerDisplayNameEditText.inputField.getText().toString();
        String bio = binding.inputFieldBio.getText().toString();

        if (displayName.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.registerDisplayNameEmpty, Snackbar.LENGTH_LONG).show();
            return;
        }

        authViewModel.deleteCurrentAuth();
        authViewModel.register(email, password, username, displayName, imgString, bio, this);
        binding.createProfileBtn.setEnabled(false);
    }

    @Override
    public void onRegisterSuccess(User user) {
        authViewModel.createUser(user);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}