package com.example.cm.ui.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class SettingsViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;
    private final MeetupRequestRepository meetupRequestRepository;
    private final FriendRequestRepository friendRequestRepository;

    public SettingsViewModel() {
        authRepository = new AuthRepository();
        userRepository = UserRepository.getInstance();
        meetupRepository = MeetupRepository.getInstance();
        meetupRequestRepository = new MeetupRequestRepository();
        friendRequestRepository = new FriendRequestRepository();
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

    public void reauthenticate(String password, UserListener<Boolean> listener) {
        FirebaseUser user = authRepository.getCurrentUser();
        if (user == null) {
            listener.onUserError(new Exception("User is null"));
            return;
        }

        String emailAddress = user.getEmail();
        if (emailAddress == null) {
            listener.onUserError(new Exception("Email is null"));
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, password);
        user.reauthenticate(credential)
                .addOnFailureListener(e -> {
                    listener.onUserError(e);
                })
                .addOnSuccessListener(e -> {
                    listener.onUserSuccess(true);
                });

    }

    public void deleteAccount(UserListener<Boolean> listener) {
        if (authRepository.getCurrentUser() == null) {
            return;
        }

        String userId = authRepository.getCurrentUser().getUid();
        onDeleteFromMeetupRequestRepo(userId, listener);
    }

    private void onDeleteFromMeetupRequestRepo(String userId, UserListener<Boolean> listener) {
        meetupRequestRepository.deleteRequestsForUser(userId, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean aBoolean) {
                onDeleteFromFriendRequestRepo(userId, listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteFromFriendRequestRepo(String userId, UserListener<Boolean> listener) {
        friendRequestRepository.deleteRequestsForUser(userId, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean aBoolean) {
                onDeleteUserFromMeetups(userId, listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteUserFromMeetups(String userId, UserListener<Boolean> listener) {
        meetupRepository.deleteUserFromMeetups(userId, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean aBoolean) {
                onDeleteMeetupsFromUser(userId, listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteMeetupsFromUser(String userId, UserListener<Boolean> listener) {
        meetupRepository.deleteMeetupsFromUser(userId, new UserListener<Boolean>() {
            @Override
            public void onUserSuccess(Boolean aBoolean) {
                onDeleteFromUserRepo(userId, listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteFromUserRepo(String userId, UserListener<Boolean> listener) {
        listener.onUserSuccess(true);
    }

    private void onDeleteFromAuthRepo(UserListener<Boolean> listener) {

    }


    public void logOut() {
        authRepository.logOut();
    }
}
