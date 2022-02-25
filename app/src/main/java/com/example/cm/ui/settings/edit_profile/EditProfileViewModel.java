package com.example.cm.ui.settings.edit_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.tasks.OnSuccessListener;

enum Status {
    SUCCESS,
    ERROR,
    LOADING
}

public class EditProfileViewModel extends ViewModel implements Callback {
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private UserRepository userRepository;
    private MutableLiveData<User> user;

    public EditProfileViewModel() {
        userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updateField(String field, String value) {
        if (value.isEmpty()) {
            status.postValue(Status.ERROR);
            return;
        }

        status.postValue(Status.LOADING);
        switch (field) {
            case "Benutzername":
                userRepository.updateField("username", value, this);
                break;
            case "Vorname":
                userRepository.updateField("firstName", value, this);
                break;
            case "Nachname":
                userRepository.updateField("lastName", value, this);
                break;
            case "Bio":
                userRepository.updateField("bio", value, this);
            default:
                break;
        }
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        status.postValue(Status.SUCCESS);
        user = userRepository.getCurrentUser();
        return null;
    }

    @Override
    public void onError(Object object) {
        status.postValue(Status.ERROR);
    }
}
