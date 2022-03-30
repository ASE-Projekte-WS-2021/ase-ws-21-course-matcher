package com.example.cm.ui.other_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.listener.Callback;
import com.example.cm.data.listener.UserListener;
import com.example.cm.data.models.Availability;
import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.Objects;


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


    // TODO Verknüpfen
    public MutableLiveData<Boolean> isFriendRequestPending(String userIdToCheck) {
        return friendRequestRepository.isFriendRequestPendingFor(userIdToCheck);
    }

    public void sendFriendRequestTo(String userIdToAdd) {
        if (!isFriendRequestPending(userIdToAdd).getValue()) {
            FriendRequest friendRequest = new FriendRequest(
                    userRepository.getCurrentAuthUserId(),
                    userIdToAdd
            );
            friendRequestRepository.addFriendRequest(friendRequest);
        }
    }

    public void unfriend(String userIdToUnfriend) {
        userRepository.unfriend(userIdToUnfriend);
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