package com.example.cm.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel implements Callback {
    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<List<MutableLiveData<User>>> friends;
    private final MutableLiveData<Set<User>> usersToShow = new MutableLiveData<>();

    public HomeViewModel() {
        userRepository = UserRepository.getInstance();
        currentUser = userRepository.getStaticCurrentUser();
        friends = userRepository.getStaticFriends();
    }

    public MutableLiveData<List<MutableLiveData<User>>> getFriends() {
        return friends;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<Set<User>> getUsersToShow() {
        return usersToShow;
    }

    public void addUserToShow(User user) {
        Set<User> users = usersToShow.getValue();
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(user);
        usersToShow.setValue(users);
    }


    public void updateLocation(LatLng latLng) {
        List<Double> location = new ArrayList<>();
        location.add(latLng.latitude);
        location.add(latLng.longitude);

        userRepository.updateField("location", location, this);
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        return null;
    }

    @Override
    public void onError(Object object) {

    }
}