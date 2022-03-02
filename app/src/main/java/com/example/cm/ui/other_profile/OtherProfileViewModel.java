package com.example.cm.ui.other_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.Collection;
import java.util.List;


public class OtherProfileViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    public MutableLiveData<User> currentUser;

    public OtherProfileViewModel() {
        userRepository = new UserRepository();
        friendRequestRepository = new FriendRequestRepository();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void getUserById(String userId) {
        currentUser = userRepository.getUserById(userId);
    }

    public MutableLiveData<Boolean> isBefriended(String friendId) {
        return userRepository.isUserBefriended(friendId);
    }

    public boolean isFriendRequestPending(String userIdToCheck) {
        String ownId = userRepository.getCurrentAuthUserId();
        List<FriendRequest> sentRequests = friendRequestRepository.getFriendRequests().getValue();
        if (sentRequests != null) {
            for (FriendRequest request : sentRequests) {
                if (request.getState() == Request.RequestState.REQUEST_PENDING) {
                    if (request.getReceiverId().equals(ownId) && request.getSenderId().equals(userIdToCheck) ||
                            request.getReceiverId().equals(userIdToCheck) && request.getSenderId().equals(ownId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void sendFriendRequestTo(String userIdToAdd) {
        if (!isFriendRequestPending(userIdToAdd)) {
            FriendRequest friendRequest = new FriendRequest(
                    userRepository.getCurrentAuthUserId(),
                    currentUser.getValue().getFullName(),
                    userIdToAdd
            );
            friendRequestRepository.addFriendRequest(friendRequest);
        }
    }

    public void unfriend(String userIdToUnfriend) {
        userRepository.unfriend(userIdToUnfriend);
    }
}