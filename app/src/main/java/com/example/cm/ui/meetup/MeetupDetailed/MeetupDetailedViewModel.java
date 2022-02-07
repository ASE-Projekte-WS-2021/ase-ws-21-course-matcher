package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;

public class MeetupDetailedViewModel extends ViewModel {

    private MutableLiveData<Meetup> meetup = new MutableLiveData<>();

    public MeetupDetailedViewModel(String meetupId) {
        MeetupRepository meetupRepository = new MeetupRepository();
        meetup = meetupRepository.getMeetup(meetupId);
    }

    public MutableLiveData<Meetup> getMeetup() {
        return meetup;
    }
}
