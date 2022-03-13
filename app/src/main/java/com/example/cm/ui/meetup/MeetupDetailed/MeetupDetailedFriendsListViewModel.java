package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupDetailedFriendsListViewModel extends ViewModel {

    private final MutableLiveData<List<MutableLiveData<User>>> users;
    private final MutableLiveData<List<String>> lateUsers;

    private UserRepository userRepository;
    private MeetupRepository meetupRepository;

    public MeetupDetailedFriendsListViewModel(List<String> userIds, String meetupId) {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();
        users = userRepository.getUsersByIds(userIds);
        lateUsers = meetupRepository.getLateUsers(meetupId);
    }

    public MutableLiveData<List<MutableLiveData<User>>> getUsers() {
        return users;
    }

    public MutableLiveData<List<String>> getLateUsers() {
        return lateUsers;
    }

    public boolean isOwnUserId(String id) {
        return id.equals(userRepository.getCurrentAuthUserId());
    }
}