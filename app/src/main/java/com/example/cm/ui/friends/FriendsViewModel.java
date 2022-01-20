package com.example.cm.ui.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.data.repositories.UserRepository.OnUserRepositoryListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsViewModel extends ViewModel implements OnUserRepositoryListener {

    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<User>> friends = new MutableLiveData<>(new ArrayList<>());

    public FriendsViewModel() {
        userRepository = new UserRepository(this);
        if (currentUser.getValue() == null) {
            FirebaseUser firebaseUser = userRepository.getCurrentUser();
            userRepository.getUserByEmail(firebaseUser.getEmail());
        }
    }

    public MutableLiveData<List<User>> getFriends() {
        return friends;
    }

    public void searchUsers(String query) {
        if (currentUser.getValue() == null) {
            return;
        }

        if (query.isEmpty()) {
            userRepository.getUsersByIds(currentUser.getValue().getFriends());
            return;
        }
        userRepository.getFriendsByUsername(currentUser.getValue().getFriends(), query);
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
        this.friends.postValue(users);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser.postValue(user);
        userRepository.getUsersByIds(user.getFriends());
    }
}