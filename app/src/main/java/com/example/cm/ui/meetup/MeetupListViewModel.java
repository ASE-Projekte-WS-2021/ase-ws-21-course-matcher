package com.example.cm.ui.meetup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;

import java.util.List;

public class MeetupListViewModel extends ViewModel implements MeetupRepository.OnMeetupRepositoryListener{
    private final MeetupRepository meetupRepository;
    private final MutableLiveData<List<Meetup>> meetups = new MutableLiveData<>();

    public MeetupListViewModel() {
        this.meetupRepository = new MeetupRepository(this);
        meetupRepository.getMeetupsByCurrentUser();
    }

    public MutableLiveData<List<Meetup>> getMeetups() {
        return meetups;
    }

    @Override
    public void onMeetupsRetrieved(List<Meetup> meetups) {
        this.meetups.postValue(meetups);
    }

    @Override
    public void onMeetupAdded(String meetupId) {

    }
}

