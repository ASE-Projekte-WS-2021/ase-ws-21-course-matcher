package com.example.cm.ui.settings.edit_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

public class EditProfileViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<User> user;

    public EditProfileViewModel() {
        userRepository = new UserRepository();
        user = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void updateField(String field, String value) {
        switch  (field) {
            case "Benutzername":
                userRepository.updateField("username", value);
                break;
            case "Vorname":
                userRepository.updateField("firstName", value);
                break;
            case "Nachname":
                userRepository.updateField("lastName", value);
                break;
            default:
                break;
        }
    }

}
