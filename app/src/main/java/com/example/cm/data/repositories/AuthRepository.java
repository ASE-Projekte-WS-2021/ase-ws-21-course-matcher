package com.example.cm.data.repositories;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.utils.FirebaseErrorTranslator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                            })
                            .addOnFailureListener(executorService, callback::onError);
                    // User could not be re-authenticated with email and password
                }).addOnFailureListener(executorService, callback::onError);
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userLiveData.postValue(firebaseAuth.getCurrentUser());
            } else {
                if(task.getException() != null){
                    error.postValue(FirebaseErrorTranslator.getErrorMessage(task.getException()));
                }
            }
        });
    }

    public void register(String email, String password, String username, String displayName, String imgString, String bio, RegisterCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = getCurrentUser();
                userLiveData.postValue(authUser);
                User newUser = new User(authUser.getUid(), username, displayName, email, imgString, bio);
                callback.onRegisterSuccess(newUser);
            } else {
                if(task.getException() != null){
                    error.postValue(FirebaseErrorTranslator.getErrorMessage(task.getException()));
                }
            }
        });
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public void deleteUser(UserListener<Boolean> listener) {
        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        firebaseAuth.getCurrentUser().delete()
                .addOnSuccessListener(executorService, task -> {
                    userLiveData.postValue(null);
                    listener.onUserSuccess(true);
                })
                .addOnFailureListener(executorService, error -> {
                    listener.onUserError(error);
                });
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