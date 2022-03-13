package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class MeetupDetailedFriendsListFactory implements ViewModelProvider.Factory {
    private final List<String> userIds;
    private final String meetupId;

    public MeetupDetailedFriendsListFactory(List<String> userIds, String meetupId) {
        this.userIds = userIds;
        this.meetupId = meetupId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MeetupDetailedFriendsListViewModel(userIds, meetupId);
    }
}