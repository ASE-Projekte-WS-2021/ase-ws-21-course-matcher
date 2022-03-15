package com.example.cm.ui.invite_friends;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InviteMoreFriendsFactory implements ViewModelProvider.Factory {

    private final String mParam;

    public InviteMoreFriendsFactory(String param) {
        mParam = param;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new InviteMoreFriendsViewModel(mParam);
    }
}
