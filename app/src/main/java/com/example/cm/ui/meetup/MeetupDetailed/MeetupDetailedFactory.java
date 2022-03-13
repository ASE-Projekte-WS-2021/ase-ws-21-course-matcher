package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MeetupDetailedFactory implements ViewModelProvider.Factory {

    private final String mParam;

    public MeetupDetailedFactory(String param) {
        mParam = param;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MeetupDetailedViewModel(mParam);
    }
}
