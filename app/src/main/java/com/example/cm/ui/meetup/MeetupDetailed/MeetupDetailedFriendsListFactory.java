package com.example.cm.ui.meetup.MeetupDetailed;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class MeetupDetailedFriendsListFactory implements ViewModelProvider.Factory {
    private final List<String> userIds;

    public MeetupDetailedFriendsListFactory(List<String> userIds) {
        this.userIds = userIds;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MeetupDetailedFriendsListViewModel(userIds);
    }
}