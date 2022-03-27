package com.example.cm.ui.own_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.User;
import com.example.cm.data.listener.Callback;
import com.example.cm.data.repositories.UserRepository;

public class OwnProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser;

    public OwnProfileViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void updateAvailability(Availability availabilityState, UserListener<Availability> listener) {
        userRepository.updateField("availability", availabilityState, new Callback() {
            @Override
            public void onSuccess(Object object) {
                listener.onUserSuccess(availabilityState);
            }

            @Override
            public void onError(Object object) {
                listener.onUserError((Exception) object);
            }
        });
    }
}