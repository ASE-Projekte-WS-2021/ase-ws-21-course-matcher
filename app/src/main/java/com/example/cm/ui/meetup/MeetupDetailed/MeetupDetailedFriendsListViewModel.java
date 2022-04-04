package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupDetailedFriendsListViewModel extends ViewModel {

    private final MutableLiveData<List<User>> users;
    private final MutableLiveData<List<String>> lateUsers;
    private final MutableLiveData<User> currentUser;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;

    public MeetupDetailedFriendsListViewModel(List<String> userIds, String meetupId) {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();

        users = userRepository.getUsersByIds(userIds);
        lateUsers = meetupRepository.getLateUsers(meetupId);
        currentUser = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<String>> getLateUsers() {
        return lateUsers;
    }

    public boolean isOwnUserId(String id) {
        return id.equals(userRepository.getCurrentAuthUserId());
    }
}