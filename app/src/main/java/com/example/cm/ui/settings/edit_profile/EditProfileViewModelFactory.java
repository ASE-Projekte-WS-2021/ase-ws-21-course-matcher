package com.example.cm.ui.settings.edit_profile;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EditProfileViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public EditProfileViewModelFactory(Context context) {
        this.context = context;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new EditProfileViewModel(context);
    }
}