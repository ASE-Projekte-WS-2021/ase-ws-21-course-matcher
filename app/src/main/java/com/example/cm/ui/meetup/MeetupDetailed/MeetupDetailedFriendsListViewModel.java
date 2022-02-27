package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupDetailedFriendsListViewModel extends ViewModel {

    private MutableLiveData<List<MutableLiveData<User>>> users;

    public MeetupDetailedFriendsListViewModel(List<String> userIds) {
        UserRepository userRepository = new UserRepository();
        users = userRepository.getUsersByIds(userIds);
    }

    public MutableLiveData<List<MutableLiveData<User>>> getUsers() {
        return users;
    }
}