package com.example.cm.ui.meetup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.MeetupRepository;

import java.util.List;

public class MeetupListViewModel extends ViewModel {
    private MutableLiveData<List<Meetup>> meetupListMLD = new MutableLiveData<>();
    MeetupRepository meetup2Repository = new MeetupRepository();

    public MeetupListViewModel() {
        meetupListMLD = meetup2Repository.getMeetupsMLD();
    }

    public MutableLiveData<List<Meetup>> getLiveMeetupData() {
        return meetupListMLD;
    }
}
