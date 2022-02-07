package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupDetailedFriendsListViewModel extends ViewModel {

    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final UserRepository userRepository = new UserRepository();

    public MeetupDetailedFriendsListViewModel(List<String> userIds) {
        /*users = userRepository.getUsersByIds(userIds);*/
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }
}
