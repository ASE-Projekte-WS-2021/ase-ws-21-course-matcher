package com.example.cm.ui.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class FriendsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private MutableLiveData<List<User>> friends;

    public FriendsViewModel() {
        userRepository = new UserRepository();
        friends = userRepository.getFriends();
    }

    public MutableLiveData<List<User>> getFriends() {
        return friends;
    }

    public void searchUsers(String query) {

        if (query.isEmpty()) {
            friends = userRepository.getFriends();
            return;
        }
        friends.getValue().clear();
        friends = userRepository.getFriendsByUsername(query);
    }
}