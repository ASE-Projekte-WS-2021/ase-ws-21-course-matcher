package com.example.cm.ui.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class FriendsViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener {

    private static final String TAG = "FriendsViewModel";
    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();

    public FriendsViewModel() {
        userRepository = new UserRepository(this);
        // TODO: Replace with firebase auth's getCurrentUser()
        userRepository.getUserById("0woDiT794x84PeYtXzjb");
    }


    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<String>> getSelectedUsers() {
        return selectedUsers;
    }

    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
        this.users.postValue(users);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser.postValue(user);
        userRepository.getUsersByIds(user.getFriends());
    }
}
