package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

public class MeetupDetailedViewModel extends ViewModel {

    private String meetupId, currentUserId;
    private MutableLiveData<Meetup> meetup;
    private UserRepository userRepository;
    private MeetupRepository meetupRepository;
    private MeetupRequestRepository meetupRequestRepository;

    public MeetupDetailedViewModel(String meetupId) {
        userRepository = UserRepository.getInstance();
        meetupRepository = MeetupRepository.getInstance();
        meetupRequestRepository = MeetupRequestRepository.getInstance();
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

    public void onDelete() {
        meetupRepository.deleteMeetup(meetupId);
        meetupRequestRepository.deleteRequestForMeetup(meetupId);
    }

    public LatLng getMeetupLocation() {
        if (meetup.getValue() == null) {
            return null;
        }
        return meetup.getValue().getLocation();
    }
}