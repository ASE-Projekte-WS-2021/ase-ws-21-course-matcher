package com.example.cm.ui.settings.edit_profile;


import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.StorageManager;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.InputValidator;
import com.google.android.gms.tasks.OnSuccessListener;


public class EditProfileViewModel extends ViewModel implements Callback, StorageManager.Callback {
    private final UserRepository userRepository;
    private final StorageManager storageRepository;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private MutableLiveData<User> user;

    public EditProfileViewModel(Context context) {
        userRepository = new UserRepository();
        storageRepository = new StorageManager(context);
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }


    public void updateImage(Uri uri) {
        if (uri == null || user.getValue() == null) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
            return;
        }
        storageRepository.uploadImage(uri, user.getValue().getId(), this, Constants.ImageType.PROFILE_IMAGE);
    }

    public void updateField(String field, String value) {
        if (value.trim().isEmpty()) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_field_not_empty));
            return;
        }

        String trimmedValue = value.trim();

        switch (field) {
            case "Benutzername":
                if (!InputValidator.hasMinLength(trimmedValue, 4)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_username_min_length));
                    break;
                }
                userRepository.updateField("username", trimmedValue, this);
                break;
            case "Vorname":
                if (!InputValidator.hasMinLength(trimmedValue, 2)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_first_name_min_length));
                    break;
                }
                userRepository.updateField("firstName", trimmedValue, this);
                break;
            case "Nachname":
                if (!InputValidator.hasMinLength(trimmedValue, 2)) {
                    status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_last_name_min_length));
                    break;
                }
                userRepository.updateField("lastName", trimmedValue, this);
                break;
            case "Bio":
                userRepository.updateField("bio", trimmedValue, this);
                break;
            default:
                break;
        }
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        status.postValue(new Status(StatusFlag.SUCCESS, R.string.edit_profile_success));
        user = userRepository.getCurrentUser();
        return null;
    }

    @Override
    public void onError(Object object) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
    }

    @Override
    public void onSuccess(String url) {
        userRepository.updateField("profileImageUrl", url, this);
    }

    @Override
    public void onError(Exception e) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_profile_general_error));
    }
}
