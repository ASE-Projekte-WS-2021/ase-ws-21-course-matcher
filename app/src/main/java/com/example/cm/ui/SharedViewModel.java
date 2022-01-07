package com.example.cm.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.ui.InviteFriends.InviteFriendsRepository;

public class SharedViewModel extends ViewModel {

    private final InviteFriendsRepository inviteFriendsRepository;
    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<String> meetupTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    public SharedViewModel() {
        this.inviteFriendsRepository = new InviteFriendsRepository();
    }

    public LiveData<String> getMeetupLocation() {
        return meetupLocation;
    }

    public LiveData<String> getMeetupTime() {
        return meetupTime;
    }

    public LiveData<Boolean> getMeetupIsPrivate() {
        return meetupIsPrivate;
    }

    public void setLocation(String location) {
        meetupLocation.setValue(location);
    }

    public void setTime(String time) {
        meetupTime.setValue(time);
    }

    public void setIsPrivate(Boolean isPrivate) {
        meetupIsPrivate.setValue(isPrivate);
    }


    public void createMeetup2(Meetup meetup) {
        inviteFriendsRepository.addMeetup2(meetup);
    }


}
