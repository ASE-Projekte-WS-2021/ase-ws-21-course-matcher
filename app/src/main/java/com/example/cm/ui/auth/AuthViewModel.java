package com.example.cm.ui.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;


public class AuthViewModel extends ViewModel {
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

    public void login(String email, String password, AuthRepository.LoginCallback callback) {
        authRepository.login(email, password, callback);
    }

    public void logout() {
        authRepository.logOut();
    }

    public void register(String email, String password, String userName, String displayName, String imgString, String bio, AuthRepository.RegisterCallback callback) {
        authRepository.register(email, password, userName, displayName, imgString, bio, callback);
    }

    public void createUser(User user) {
        userRepository.createUser(user);
    }

    public LiveData<User> getUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return error;
    }

    public void getUsernames(UserRepository.UsernamesRetrievedCallback callback) {
        userRepository.getUsernames(callback);
    }
}