package com.example.cm.data.repositories;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthRepository extends Repository {
    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
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
                })
                // User could not be re-authenticated with email and password
                .addOnFailureListener(executorService, callback::onError);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Task<AuthResult> register(String email, String password, String userName) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
