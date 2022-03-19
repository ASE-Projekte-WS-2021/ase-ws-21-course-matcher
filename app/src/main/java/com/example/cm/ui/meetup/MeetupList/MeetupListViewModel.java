package com.example.cm.ui.meetup.MeetupList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class MeetupListViewModel extends ViewModel {

    private final MeetupRepository meetupRepository = new MeetupRepository();
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<List<Meetup>> meetupList;
    private final MutableLiveData<List<String>> userIds = new MutableLiveData<>();
    private final LiveData<List<User>> userLiveData = Transformations.switchMap(userIds, userRepository::getUsersByIds);

    public MeetupListViewModel() {
        meetupList = meetupRepository.getMeetups();
    }

    public MutableLiveData<List<Meetup>> getMeetups() {
        return meetupList;
    }

    public LiveData<List<User>> getUsers() {
        return userLiveData;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds.setValue(userIds);
    }
}
