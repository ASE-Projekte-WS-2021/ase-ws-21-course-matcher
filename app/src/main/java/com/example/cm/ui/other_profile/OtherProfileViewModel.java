package com.example.cm.ui.other_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class OtherProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser;
    public OtherProfileViewModel() {
        userRepository = new UserRepository();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void getUserById(String userId) {
        currentUser = userRepository.getUserById(userId);
    }

    public MutableLiveData<Boolean> isBefriended(String friendId) {
        return userRepository.isUserBefriended(friendId);
    }
}