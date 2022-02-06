package com.example.cm.ui.meetup.MeetupList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;

import java.util.List;

public class MeetupListViewModel extends ViewModel {
    private MutableLiveData<List<Meetup>> meetupList = new MutableLiveData<>();
    private final MeetupRepository meetupRepository = new MeetupRepository();

    public MeetupListViewModel() {
        meetupList = meetupRepository.getMeetups();
    }

    public MutableLiveData<List<Meetup>> getMeetups() {
        return meetupList;
    }
}
