package com.example.cm.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.utils.Navigator;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel";
    private final UserRepository userRepository;
    public MutableLiveData<User> currentUser = new MutableLiveData<>();

    public ProfileViewModel() {
        userRepository = new UserRepository();
    }

    public MutableLiveData<List<User>> getCurrentUser() {
        return null;
    }


}
