package com.example.cm.ui.settings.edit_profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.listener.Callback;
import com.example.cm.data.repositories.StorageManager;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.InputValidator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


public class EditProfileViewModel extends ViewModel implements Callback, StorageManager.Callback {
    private final UserRepository userRepository;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private MutableLiveData<User> user;

    public EditProfileViewModel() {
        userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updateImage(Uri uri, Context context) throws FileNotFoundException {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.QUALITY_PROFILE_IMG, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String imageBaseString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        userRepository.updateProfileImage(imageBaseString, Objects.requireNonNull(user.getValue()).getId());
    }

    public void updateField(String field, String value) {
        if (value.trim().isEmpty()) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_field_not_empty));
            return;
        }

        String trimmedValue = value.trim();

        switch (field) {
            case "Benutzername":
                if (!InputValidator.hasMinLength(trimmedValue, Constants.MIN_USERNAME_LENGTH)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_username_min_length));
                    break;
                }
                userRepository.updateField("username", trimmedValue, this);
                break;
            case "Vorname":
                if (!InputValidator.hasMinLength(trimmedValue, Constants.MIN_NAME_LENGTH)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_display_name_min_length));
                    break;
                }
                userRepository.updateField("displayName", trimmedValue, this);
                break;
            case "Bio":
                userRepository.updateField("bio", trimmedValue, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(Object object) {
        status.postValue(new Status(StatusFlag.SUCCESS, R.string.edit_profile_success));
        user = userRepository.getCurrentUser();
    }

    @Override
    public void onError(Object object) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
    }

    @Override
    public void onSuccess(String urlOnline, Uri uriLocal) {
        userRepository.updateField("profileImageString", urlOnline, this);
    }

    @Override
    public void onError(Exception e) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
    }
}
