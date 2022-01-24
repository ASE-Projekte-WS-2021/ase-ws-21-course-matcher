package com.example.cm.ui.meetup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeetupListViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public MeetupListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Meetupssss");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

