package com.example.cm.ui.own_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

public class OwnProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser;

    public OwnProfileViewModel() {
        userRepository = UserRepository.getInstance();
        currentUser = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }
}