package com.example.cm.ui.invite_friends;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InviteMoreFriendsFactory implements ViewModelProvider.Factory {

    private final String meetupId;

    public InviteMoreFriendsFactory(String meetupId) {
        this.meetupId = meetupId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new InviteMoreFriendsViewModel(meetupId);
    }
}
