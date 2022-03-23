package com.example.cm.ui.settings;

import android.content.Context;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
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

    public void reauthenticate(Context context, String password, UserListener<Boolean> listener) {
        if(password.isEmpty() || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            listener.onUserError(new Exception(context.getString(R.string.edit_account_error_password_min_length)));
            return;
        }

        FirebaseUser user = authRepository.getCurrentUser();
        if (user == null) {
            listener.onUserError(new Exception(context.getString(R.string.no_user_found)));
            return;
        }

        String emailAddress = user.getEmail();
        if (emailAddress == null) {
            listener.onUserError(new Exception());
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, password);
        user.reauthenticate(credential)
                .addOnFailureListener(e -> {
                    listener.onUserError(new Exception(context.getString(R.string.delete_account_wrong_password)));
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

    public void updateAvailablilty(Availability availabilityState, UserListener<Availability> listener) {
        userRepository.updateField("availability", availabilityState, new Callback() {
            @Override
            public OnSuccessListener<? super Void> onSuccess(Object object) {
                listener.onUserSuccess(availabilityState);
                return null;
            }

            @Override
            public void onError(Object object) {
                listener.onUserError((Exception) object);
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
                onDeleteUserFromFriendsLists(userId, listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteUserFromFriendsLists(String userId, UserListener<Boolean> listener) {
        userRepository.deleteUserFromFriendsLists(userId, new UserListener<Boolean>() {

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
        userRepository.deleteUser(userId, new UserListener<Boolean>() {

            @Override
            public void onUserSuccess(Boolean aBoolean) {
                onDeleteFromAuthRepo(listener);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    private void onDeleteFromAuthRepo(UserListener<Boolean> listener) {
        authRepository.deleteUser(new UserListener<Boolean>() {

            @Override
            public void onUserSuccess(Boolean aBoolean) {
                logOut();
                listener.onUserSuccess(true);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    public void logOut() {
        authRepository.logOut();
    }
}