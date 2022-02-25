package com.example.cm.ui.settings.edit_profile;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.R;
import com.example.cm.data.models.Status;
import com.example.cm.data.models.StatusFlag;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.Callback;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.InputValidator;
import com.google.android.gms.tasks.OnSuccessListener;

public class EditProfileViewModel extends ViewModel implements Callback {
    public MutableLiveData<Status> status = new MutableLiveData<>();
    private UserRepository userRepository;
    private MutableLiveData<User> user;
    private Context context;

    public EditProfileViewModel(Context context) {
        this.context = context;
        userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updateField(String field, String value) {
        if (value.trim().isEmpty()) {
            status.postValue(new Status(StatusFlag.ERROR, "Field cannot be empty"));
            return;
        }

        status.postValue(new Status(StatusFlag.LOADING, null));

        String trimmedValue = value.trim();

        switch (field) {
            case "Benutzername":
                if (!InputValidator.hasMinLength(trimmedValue, 4)) {
                    // Get string resource
                    String message = context.getResources().getString(R.string.edit_profile_error_min_length);
                    message = message.replace("{length}", "4").replace("{field}", field);

                    status.postValue(new Status(StatusFlag.ERROR, message));
                    break;
                }
                userRepository.updateField("username", trimmedValue, this);
                break;
            case "Vorname":
                if (!InputValidator.hasMinLength(trimmedValue, 2)) {
                    String message = context.getResources().getString(R.string.edit_profile_error_min_length);
                    message = message.replace("{length}", "2").replace("{field}", field);
                    status.postValue(new Status(StatusFlag.ERROR, message));

                    status.postValue(new Status(StatusFlag.ERROR, message));
                    break;
                }
                userRepository.updateField("firstName", trimmedValue, this);
                break;
            case "Nachname":
                if (!InputValidator.hasMinLength(trimmedValue, 2)) {
                    String message = context.getResources().getString(R.string.edit_profile_error_min_length);
                    message = message.replace("{length}", "2").replace("{field}", field);
                    status.postValue(new Status(StatusFlag.ERROR, message));

                    status.postValue(new Status(StatusFlag.ERROR, message));
                    break;
                }
                userRepository.updateField("lastName", trimmedValue, this);
                break;
            case "Bio":
                userRepository.updateField("bio", trimmedValue, this);
            default:
                break;
        }
    }

    @Override
    public OnSuccessListener<? super Void> onSuccess(Object object) {
        status.postValue(new Status(StatusFlag.SUCCESS, "Erfolgreich aktualisiert"));
        user = userRepository.getCurrentUser();
        return null;
    }

    @Override
    public void onError(Object object) {
        status.postValue(new Status(StatusFlag.ERROR, "Ein Fehler ist aufgetreten"));
    }
}
