package com.example.cm.ui.settings.edit_account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.UserRepository;

public class EditAccountViewModel extends ViewModel {
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private MutableLiveData<User> user;

    public EditAccountViewModel() {
        userRepository = new UserRepository();
        //authRepository = new AuthRepository();

        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updatePassword(String currentPassword, String newPassword, String newPasswordConfirm) {
        //authRepository.updatePassword(newPassword);
    }

}
