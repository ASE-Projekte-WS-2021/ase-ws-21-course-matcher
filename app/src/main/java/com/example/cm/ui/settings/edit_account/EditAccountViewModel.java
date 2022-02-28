package com.example.cm.ui.settings.edit_account;

import android.content.res.Resources;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.R;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.AuthRepository;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.InputValidator;
import com.google.android.gms.tasks.OnSuccessListener;

public class EditAccountViewModel extends ViewModel implements Callback {
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private MutableLiveData<User> user;

    public EditAccountViewModel() {
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updatePassword(String currentPassword, String newPassword, String newPasswordConfirm) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_account_error_password_not_empty));
            return;
        }

        if (!InputValidator.hasMinLength(currentPassword, Constants.MIN_PASSWORD_LENGTH)
                || !InputValidator.hasMinLength(newPassword, Constants.MIN_PASSWORD_LENGTH)
                || !InputValidator.hasMinLength(newPasswordConfirm, Constants.MIN_PASSWORD_LENGTH)) {

            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_account_error_password_min_length));
            return;
        }

        if (currentPassword.equals(newPassword)) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_account_error_password_must_differ));
            return;
        }

        if (!newPassword.equals(newPasswordConfirm)) {
            status.postValue(new Status(StatusFlag.ERROR, R.string.edit_account_error_password_must_match));
            return;
        }

        authRepository.updatePassword(currentPassword, newPassword, this);
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        status.postValue(new Status(StatusFlag.SUCCESS, R.string.edit_account_success_password_change));
        user = userRepository.getCurrentUser();
        return null;
    }

    @Override
    public void onError(Object object) {
        status.postValue(new Status(StatusFlag.ERROR, R.string.edit_account_error_password_not_changed));
    }
}
