package com.example.cm.ui.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
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

    public List<User> getFilteredUsers(String query) {
        if (friends.getValue() == null) {
            return null;
        }

        List<User> filteredUsers = new ArrayList<>();
        for (User user : friends.getValue()) {
            boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
            boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

            if (isQueryInUsername || isQueryInFullName) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}