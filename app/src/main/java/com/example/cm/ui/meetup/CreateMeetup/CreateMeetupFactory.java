package com.example.cm.ui.meetup.CreateMeetup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CreateMeetupFactory implements ViewModelProvider.Factory {

    private final Context context;

    public CreateMeetupFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CreateMeetupViewModel(context);
    }
}
