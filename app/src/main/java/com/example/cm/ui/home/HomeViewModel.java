package com.example.cm.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements Callback {
    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;

    public HomeViewModel() {
        userRepository = UserRepository.getInstance();
        currentUser = userRepository.getStaticCurrentUser();
    }

    public void getFriends(UserListener<List<User>> listener) {
        userRepository.getStaticFriends(new UserListener<List<User>>() {
            @Override
            public void onUserSuccess(List<User> users) {
                listener.onUserSuccess(users);
            }

            @Override
            public void onUserError(Exception error) {
                listener.onUserError(error);
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

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        return null;
    }

    @Override
    public void onError(Object object) {

    }
}