package com.example.cm.data.repositories;


import androidx.lifecycle.MutableLiveData;

import com.example.cm.data.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

public class AuthRepository extends Repository {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> error;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.error = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    public void updatePassword(String currentPassword, String newPassword, Callback callback) {
        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseAuth.getCurrentUser().getEmail()), currentPassword);
        // User could not be re-authenticated with email and password
        firebaseAuth.getCurrentUser()
                .reauthenticate(credential)
                .addOnSuccessListener(executorService, task -> {
                    // User re-authenticated successfully -> Update Password
                    firebaseAuth.getCurrentUser().updatePassword(newPassword)
                            .addOnSuccessListener(executorService, updatePasswordTask -> {
                                callback.onSuccess(null);
                            });
                });
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userLiveData.postValue(firebaseAuth.getCurrentUser());
            } else {
                error.postValue(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void register(String email, String password, String userName, String firstName, String lastName, RegisterCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = getCurrentUser();
                userLiveData.postValue(authUser);
                User newUser = new User(authUser.getUid(), userName, firstName, lastName, email, "", "", new ArrayList<String>());
                callback.onRegisterSuccess(newUser);

            } else {
                error.postValue(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return error;
    }

    public interface RegisterCallback {
        void onRegisterSuccess(User user);
    }
}


