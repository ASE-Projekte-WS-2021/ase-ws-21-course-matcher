package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

public class MeetupDetailedViewModel extends ViewModel {

    private String meetupId;
    private MutableLiveData<Meetup> meetup;
    private UserRepository userRepository;
    private MeetupRepository meetupRepository;

    public MeetupDetailedViewModel(String meetupId) {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();
        meetup = meetupRepository.getMeetup(meetupId);
        this.meetupId = meetupId;
    }

    public MutableLiveData<Meetup> getMeetup() {
        return meetup;
    }

    public String getCurrentUserId() {
        return userRepository.getCurrentAuthUserId();
    }

    public void onLeave() {
        //todo
    }

    public void onJoin() {
        //todo
    }

    public void onLate() {
        //todo
    }
}