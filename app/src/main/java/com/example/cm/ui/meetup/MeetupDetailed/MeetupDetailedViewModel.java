package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

public class MeetupDetailedViewModel extends ViewModel {

    private String meetupId, currentUserId;
    private MutableLiveData<Meetup> meetup;
    private UserRepository userRepository;
    private MeetupRepository meetupRepository;

    public MeetupDetailedViewModel(String meetupId) {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();
        meetup = meetupRepository.getMeetup(meetupId);
        currentUserId = userRepository.getCurrentAuthUserId();
        this.meetupId = meetupId;
    }

    public MutableLiveData<Meetup> getMeetup() {
        return meetup;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void onLeave() {
        meetupRepository.addDeclined(meetupId, currentUserId);
    }

    public void onJoin() {
        meetupRepository.addConfirmed(meetupId, currentUserId);
    }

    public void onLate() {
        meetupRepository.addLate(meetupId, currentUserId);
    }
}