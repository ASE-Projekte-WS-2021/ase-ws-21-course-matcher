package com.example.cm.ui.auth;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

/**
 * inspired by https://learntodroid.com/how-to-use-firebase-authentication-in-an-android-app-using-mvvm/
 */

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final Application application;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        authRepository = new AuthRepository(application);
        userRepository = new UserRepository();
        userLiveData = authRepository.getUserLiveData();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String password, String userName, String firstName, String lastName) {
        authRepository.register(email, password, userName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addUser(userName, firstName, lastName, email);
            } else {
                Toast.makeText(application.getApplicationContext(), "Registration Failure: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUser(String username, String firstName, String lastName, String email) {
        FirebaseUser authUser = authRepository.getCurrentUser();
        if (authUser != null) {
            User user = new User(authUser.getUid(), username, firstName, lastName, email, new ArrayList<String>());
            userRepository.createUser(user);
            userLiveData.postValue(authUser);
        }
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
}