package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

public class MeetupDetailedViewModel extends ViewModel {

    private final String meetupId;
    private final String currentUserId;
    private final MutableLiveData<Meetup> meetup;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;
    private final MeetupRequestRepository meetupRequestRepository;

    public MeetupDetailedViewModel(String meetupId) {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();

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

    public void onDecline() {
        meetupRepository.addDeclined(meetupId, currentUserId);
    }

    public void onLeave() {
        meetupRepository.addLeft(meetupId, currentUserId);
    }

    public void onJoin() {
        meetupRepository.addConfirmed(meetupId, currentUserId);
    }

    public void onLate(boolean isComingLate) {
        meetupRepository.addLate(meetupId, currentUserId, isComingLate);
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