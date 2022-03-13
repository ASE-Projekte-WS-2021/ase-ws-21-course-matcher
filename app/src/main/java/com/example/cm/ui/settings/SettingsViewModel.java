package com.example.cm.ui.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.tasks.OnSuccessListener;

public class SettingsViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public SettingsViewModel() {
        authRepository = new AuthRepository();
        userRepository = UserRepository.getInstance();
    }

    public MutableLiveData<User> getUser() {
        return userRepository.getStaticCurrentUser();
    }

    public void updateLocationSharing(boolean enabled, UserListener<Boolean> listener) {
        userRepository.updateField("isSharingLocation", enabled, new Callback() {
            @Override
            public OnSuccessListener<? super Void> onSuccess(Object object) {
                listener.onUserSuccess(enabled);
                return null;
            }

            @Override
            public void onError(Object object) {
                listener.onUserError((Exception) object);
            }
        });
    }

    public void logOut() {
        authRepository.logOut();
    }
}
