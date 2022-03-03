package com.example.cm.data.repositories;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * inspired by https://learntodroid.com/how-to-use-firebase-authentication-in-an-android-app-using-mvvm/
 */

public class AuthRepository extends Repository {
    private static final String TAG = "AuthRepository";
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<Boolean> loggedOutLiveData;
    private Application application = null;

    public AuthRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.loggedOutLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutLiveData.postValue(false);
        }
    }

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.loggedOutLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutLiveData.postValue(false);
        }
    }

    public void updatePassword(String currentPassword, String newPassword, Callback callback) {
        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseAuth.getCurrentUser().getEmail(), currentPassword);
        firebaseAuth.getCurrentUser()
                .reauthenticate(credential)
                .addOnSuccessListener(executorService, task -> {
                    // User re-authenticated successfully -> Update Password
                    firebaseAuth.getCurrentUser().updatePassword(newPassword)
                            .addOnSuccessListener(executorService, updatePasswordTask -> {
                                callback.onSuccess(null);
                            })
                            .addOnFailureListener(executorService, e -> {
                                callback.onError(e);
                            });
                })
                .addOnFailureListener(executorService, e -> {
                    // User could not be re-authenticated with email and password
                    callback.onError(e);
                });
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(executorService, task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        Toast.makeText(application.getApplicationContext(), "Login Failure: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Task<AuthResult> register(String email, String password, String userName) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public void logOut() {
        firebaseAuth.signOut();
        loggedOutLiveData.postValue(true);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }
}
