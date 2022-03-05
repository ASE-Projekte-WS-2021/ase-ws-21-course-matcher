package com.example.cm.ui.settings;

import androidx.lifecycle.ViewModel;

import com.example.cm.data.repositories.AuthRepository;

public class SettingsViewModel extends ViewModel {
    private AuthRepository authRepository;

    public SettingsViewModel() {
        authRepository = new AuthRepository();
    }


    public void logOut() {
        authRepository.logOut();
    }

}
