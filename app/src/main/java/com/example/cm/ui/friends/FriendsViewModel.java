package com.example.cm.ui.friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.User;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.data.repositories.UserRepository.OnUserRepositoryListener;

import java.util.List;

public class FriendsViewModel extends ViewModel implements OnUserRepositoryListener {

    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    public MutableLiveData<List<User>> friends = new MutableLiveData<>();

    public FriendsViewModel() {
        userRepository = new UserRepository(this);
        if(currentUser.getValue() == null) {
            // TODO: Replace with firebase auth's getCurrentUser()
            userRepository.getUserById("0woDiT794x84PeYtXzjb");
        }
    }

    public MutableLiveData<List<User>> getFriends() {
        return friends;
    }

    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsersByIds(currentUser.getValue().getFriends());
            return;
        }
        userRepository.getFriendsByUsername(currentUser.getValue().getFriends(), query);
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
        this.friends.postValue(users);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser.postValue(user);
        userRepository.getUsersByIds(user.getFriends());
    }
}
