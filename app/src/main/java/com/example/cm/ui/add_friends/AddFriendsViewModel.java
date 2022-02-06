package com.example.cm.ui.add_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.data.repositories.UserRepository.OnUserRepositoryListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class AddFriendsViewModel extends ViewModel implements
        OnUserRepositoryListener,
        FriendRequestRepository.OnFriendRequestRepositoryListener {

    private User currentUser;
    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<FriendRequest>> sentFriendRequests = new MutableLiveData<>();
    public OnRequestSentListener listener;

    public AddFriendsViewModel() {
        userRepository = new UserRepository(this);
        requestRepository = new FriendRequestRepository(this);
        FirebaseUser firebaseUser = userRepository.getCurrentUser();
        userRepository.getUserByEmail(firebaseUser.getEmail());
        userRepository.getUsersNotFriends();
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<FriendRequest>> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setOnRequestSentListener(OnRequestSentListener listener) {
        this.listener = listener;
    }

    /**
     * Search a user by their username
     *
     * @param query the username to search for
     */
    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     * Otherwise delete the friend request
     *
     * @param receiverId the id of the receiver
     */
    public void sendOrDeleteFriendRequest(String receiverId) {
        requestRepository.getFriendRequestsSentBy(currentUser.getId(), requests -> {
            if (requests == null) {
                return;
            }

            if (hasReceivedFriendRequest(requests, receiverId)) {
                onFriendRequestExists(receiverId);
            } else {
                onFriendRequestDoesNotExist(receiverId);
            }

            sentFriendRequests.postValue(requests);
        });
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestDoesNotExist(String receiverId) {
        Timber.d("Sending friend request to %s", receiverId);
        FriendRequest request = new FriendRequest(currentUser.getId(), currentUser.getFullName(), receiverId);
        requestRepository.addFriendRequest(request);
        listener.onRequestAdded();
    }

    /**
     * Delete a friend request if the user has sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestExists(String receiverId) {
        Timber.d("Deleting friend request to %s", receiverId);
        requestRepository.deleteFriendRequest(receiverId, currentUser.getId());
        listener.onRequestDeleted();
    }

    private Boolean hasReceivedFriendRequest(List<FriendRequest> requests, String receiverId) {
        for (FriendRequest request : requests) {
            if (request.getReceiverId().equals(receiverId) && request.getSenderId().equals(currentUser.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.getId().equals(this.currentUser.getId())) {
                filteredUsers.add(user);
            }
        }

        this.users.postValue(filteredUsers);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser = user;
        requestRepository.getFriendRequestsSentBy(user.getId(), requests -> {
            sentFriendRequests.postValue(requests);
        });
    }

    @Override
    public void onFriendRequestsRetrieved(List<FriendRequest> requests) {
        this.sentFriendRequests.postValue(requests);
    }

    public interface OnRequestSentListener {
        void onRequestAdded();
        void onRequestDeleted();
    }
}
