package com.example.cm.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.MeetupListener;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.User;
import com.example.cm.data.listener.Callback;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class HomeViewModel extends ViewModel implements Callback {
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;
    private final MutableLiveData<User> currentUser;
    private final List<User> friends = new ArrayList<>();

    public HomeViewModel() {
        userRepository = UserRepository.getInstance();
        meetupRepository = MeetupRepository.getInstance();
        currentUser = userRepository.getStaticCurrentUser();
    }

    public void resetUserList() {
        friends.clear();
    }

    public void getFriends(UserListener<List<User>> listener) {
        userRepository.getStaticFriends(new UserListener<List<User>>() {
            @Override
            public void onUserSuccess(List<User> users) {
                if (currentUser.getValue() == null) {
                    return;
                }
                friends.addAll(users);

                int currentUserFriendCount = currentUser.getValue().getFriends().size();
                int friendsCount = friends.size();
                if (friendsCount == currentUserFriendCount) {
                    Set<User> friendsSet = new HashSet<>(friends);
                    friends.clear();
                    friends.addAll(friendsSet);
                    listener.onUserSuccess(friends);
                    resetUserList();
                }
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
            }
        });
    }

    public void getCurrentMeetups(MeetupListener<List<Meetup>> listener) {
        meetupRepository.getCurrentMeetups(new MeetupListener<List<Meetup>>() {
            @Override
            public void onMeetupSuccess(List<Meetup> meetups) {
                listener.onMeetupSuccess(meetups);
            }

            @Override
            public void onMeetupError(Exception error) {
                listener.onMeetupError(error);
            }
        });
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void updateLocation(LatLng latLng) {
        List<Double> location = new ArrayList<>();
        location.add(latLng.latitude);
        location.add(latLng.longitude);

        userRepository.updateField("location", location, this);
    }

    public void updateLocationSharing(boolean enabled) {
        userRepository.updateField("isSharingLocation", enabled, this);
    }

    public void updateLocationSharing(boolean enabled, UserListener<Boolean> listener) {
        userRepository.updateField("isSharingLocation", enabled, new Callback() {
            @Override
            public void onSuccess(Object object) {
                listener.onUserSuccess(enabled);
            }

            @Override
            public void onError(Object object) {
                listener.onUserError((Exception) object);
            }
        });
    }

    @Override
    public void onSuccess(Object object) {
    }

    @Override
    public void onError(Object object) {

    }
}