package com.example.cm.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser;

    public ProfileViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void getLoggedInUser() {
        currentUser = userRepository.getCurrentUser();
    }

    public void getUserById(String userId) {
        currentUser = userRepository.getUserById(userId);
    }
}