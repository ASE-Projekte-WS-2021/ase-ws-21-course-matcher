package com.example.cm.ui.profile;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class ProfileViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener {

    private static final String TAG = "ProfileViewModel";
    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser = new MutableLiveData<>();

    public ProfileViewModel() {
        userRepository = new UserRepository(this);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void getUserById(String userId) {
        userRepository.getUserById(userId);
    }

    @Override
    public void onUsersRetrieved(List<User> users) {}

    @Override
    public void onUserRetrieved(User user) {
        currentUser.postValue(user);
    }
}