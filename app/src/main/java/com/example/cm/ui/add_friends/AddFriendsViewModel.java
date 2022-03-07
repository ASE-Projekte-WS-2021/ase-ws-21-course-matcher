package com.example.cm.ui.add_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class AddFriendsViewModel extends ViewModel implements FriendRequestRepository.Callback {

    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;

    public MutableLiveData<List<User>> users;
    public MutableLiveData<User> currentUser;
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> receivedFriendRequests;
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> sentFriendRequestsPending;
    public MutableLiveData<List<MutableLiveData<FriendRequest>>> receivedFriendRequestsPending;

    public OnRequestSentListener listener;

    public AddFriendsViewModel() {
        userRepository = new UserRepository();
        users = userRepository.getUsersNotFriends();
        currentUser = userRepository.getCurrentUser();

        requestRepository = new FriendRequestRepository();
        receivedFriendRequests = requestRepository.getFriendRequestsForUser(this);

        sentFriendRequestsPending = requestRepository.getFriendRequestsSentBy(userRepository.getFirebaseUser().getUid());
        receivedFriendRequestsPending = requestRepository.getFriendRequestsReceived(userRepository.getFirebaseUser().getUid());
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getSentFriendRequestsPending() {
        return sentFriendRequestsPending;
    }

    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getReceivedFriendRequestsPending() {
        return receivedFriendRequestsPending;
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
            users = userRepository.getUsersNotFriends();
            return;
        }
        users = userRepository.getUsersNotFriendsByQuery(query);
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     * Otherwise delete the friend request
     *
     * @param receiverId the id of the receiver
     */
    public void sendOrDeleteFriendRequest(String receiverId) {
        if (sentFriendRequestsPending.getValue() == null) {
            return;
        }

        if (hasReceivedFriendRequest(sentFriendRequestsPending.getValue(), receiverId)) {
            onFriendRequestExists(receiverId);
        } else {
            onFriendRequestDoesNotExist(receiverId);
        }
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestDoesNotExist(String receiverId) {
        Timber.d("Sending friend request to %s", receiverId);
        if (currentUser.getValue() == null) {
            return;
        }

        FriendRequest request = new FriendRequest(currentUser.getValue().getId(), receiverId);
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
        requestRepository.deleteFriendRequest(receiverId, userRepository.getFirebaseUser().getUid());
        listener.onRequestDeleted();
    }

    /**
     * checks whether current user sent an friend request to user with given id
     *
     * @param requests list of friend requests
     * @param receiverId id of the friend to check if has received friend request of current
     * @return has current user sent an friend request to user with given id
     */
    private boolean hasReceivedFriendRequest(List<MutableLiveData<FriendRequest>> requests, String receiverId) {
        for (MutableLiveData<FriendRequest> request : requests) {
            if (Objects.requireNonNull(request.getValue()).getReceiverId().equals(receiverId)
                    && request.getValue().getSenderId().equals(userRepository.getFirebaseUser().getUid())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFriendRequestsRetrieved(List<FriendRequest> friendRequests) {

    }


    public interface OnRequestSentListener {
        void onRequestAdded();
        void onRequestDeleted();
    }
}
