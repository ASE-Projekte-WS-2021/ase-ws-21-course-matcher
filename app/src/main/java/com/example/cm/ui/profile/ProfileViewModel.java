package com.example.cm.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener {

    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser = new MutableLiveData<>();

    public ProfileViewModel() {
        userRepository = new UserRepository(this);
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void getLoggedInUser() {
        FirebaseUser firebaseUser = userRepository.getCurrentUser();
        userRepository.getUserByEmail(firebaseUser.getEmail());
    }

    public void getUserById(String userId) {
        userRepository.getUserById(userId);
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
    }

    @Override
    public void onUserRetrieved(User user) {
        currentUser.postValue(user);
    }
}