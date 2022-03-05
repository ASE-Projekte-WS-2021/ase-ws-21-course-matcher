package com.example.cm.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel implements AuthRepository.RegisterCallback {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> error;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        userLiveData = authRepository.getUserLiveData();
        error = authRepository.getErrorLiveData();
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void register(String email, String password, String userName, String firstName, String lastName) {
        authRepository.register(email, password, userName, firstName, lastName, this);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return error;
    }

    @Override
    public void onRegisterSuccess(User user) {
        userRepository.createUser(user);
    }
}