package com.example.cm.data.repositories;


import androidx.lifecycle.MutableLiveData;

import com.example.cm.data.listener.Callback;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.utils.FirebaseErrorTranslator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    /**
     * Update a users' password
     *
     * @param currentPassword The current password
     * @param newPassword     The new password
     * @param callback        Callback when the password is updated or an error occurs
     */
    public void updatePassword(String currentPassword, String newPassword, Callback callback) {
        if (firebaseAuth.getCurrentUser() == null || firebaseAuth.getCurrentUser().getEmail() == null) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(firebaseAuth.getCurrentUser().getEmail(), currentPassword);
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

    /**
     * Login with email and password
     *
     * @param email    Email of the user
     * @param password Password of the user
     */
    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userLiveData.postValue(firebaseAuth.getCurrentUser());
            } else {
                if (task.getException() != null) {
                    error.postValue(FirebaseErrorTranslator.getErrorMessage(task.getException()));
                }
            }
        });
    }

    /**
     * Register a new user
     *
     * @param email     Email of the user
     * @param password  Password of the user
     * @param userName  Username of the user
     * @param firstName First name of the user
     * @param lastName  Last name of the user
     * @param callback  Callback when the user is registered or an error occurs
     */
    public void register(String email, String password, String userName, String firstName, String lastName, RegisterCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser authUser = getCurrentUser();
                userLiveData.postValue(authUser);
                User newUser = new User(authUser.getUid(), userName, firstName, lastName, email);
                callback.onRegisterSuccess(newUser);

            } else {
                if (task.getException() != null) {
                    error.postValue(FirebaseErrorTranslator.getErrorMessage(task.getException()));
                }
            }
        });
    }

    /**
     * Logout the current user
     */
    public void logOut() {
        firebaseAuth.signOut();
    }

    /**
     * Delete the current user
     *
     * @param listener Callback when the user is deleted or an error occurs
     */
    public void deleteUser(UserListener<Boolean> listener) {
        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        firebaseAuth.getCurrentUser().delete()
                .addOnFailureListener(executorService, listener::onUserError)
                .addOnSuccessListener(executorService, task -> {
                    userLiveData.postValue(null);
                    listener.onUserSuccess(true);
                });
    }

    /**
     * Get the current user
     *
     * @return The current user
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Get the current user
     *
     * @return LiveData of the current user
     */
    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    /**
     * Get any error that occurred
     *
     * @return LiveData of the error
     */
    public MutableLiveData<String> getErrorLiveData() {
        return error;
    }

    public interface RegisterCallback {
        void onRegisterSuccess(User user);
    }
}