package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupDetailedFriendsListViewModel extends ViewModel {

    private final MutableLiveData<List<User>> users;
    private UserRepository userRepository;

    public MeetupDetailedFriendsListViewModel(List<String> userIds) {
        userRepository = new UserRepository();
        users = userRepository.getUsersByIds(userIds);
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public boolean isOwnUserId(String id) {
        return id.equals(userRepository.getCurrentAuthUserId());
    }
}