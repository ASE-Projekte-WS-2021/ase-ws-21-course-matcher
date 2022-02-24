package com.example.cm.ui.settings.edit_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import timber.log.Timber;

public class EditProfileViewModel extends ViewModel implements Callback {
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
            default:
                break;
        }
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        user = userRepository.getCurrentUser();
        return null;
    }

    @Override
    public void onError(Object object) {
        Timber.e("onError: %s", object);
    }
}
