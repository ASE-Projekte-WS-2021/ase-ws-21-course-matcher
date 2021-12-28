package com.example.cm.ui.auth;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

/**
 * inspired by https://learntodroid.com/how-to-use-firebase-authentication-in-an-android-app-using-mvvm/
 * */

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<FirebaseUser> userLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userLiveData = authRepository.getUserLiveData();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String password, String userName) {
        authRepository.register(email, password, userName);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
}