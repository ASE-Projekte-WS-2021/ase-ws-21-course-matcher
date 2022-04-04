package com.example.cm.ui.add_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class AddFriendsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final FriendRequestRepository requestRepository;

    public MutableLiveData<List<User>> users;
    public MutableLiveData<User> currentUser;
    public MutableLiveData<List<FriendRequest>> receivedFriendRequests;
    public MutableLiveData<List<FriendRequest>> sentFriendRequestsPending;
    public MutableLiveData<List<FriendRequest>> receivedFriendRequestsPending;
    public String query = "";

    public OnRequestSentListener listener;

    public AddFriendsViewModel() {
        userRepository = new UserRepository();
        users = userRepository.getUsersNotFriends();
        currentUser = userRepository.getCurrentUser();

        requestRepository = new FriendRequestRepository();
        receivedFriendRequests = requestRepository.getFriendRequestsForUser();
    }

    public void setSearchQuery(String searchQuery) {
        query = searchQuery;
        sentFriendRequestsPending = requestRepository
                .getFriendRequestsSentBy(userRepository.getFirebaseUser().getUid());
        receivedFriendRequestsPending = requestRepository
                .getFriendRequestsReceived(userRepository.getFirebaseUser().getUid());
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<FriendRequest>> getSentFriendRequestsPending() {
        sentFriendRequestsPending = requestRepository
                .getFriendRequestsSentBy(userRepository.getFirebaseUser().getUid());
        return sentFriendRequestsPending;
    }

    public MutableLiveData<List<FriendRequest>> getReceivedFriendRequestsPending() {
        receivedFriendRequestsPending = requestRepository
                .getFriendRequestsReceived(userRepository.getFirebaseUser().getUid());
        return receivedFriendRequestsPending;
    }

    public void setOnRequestSentListener(OnRequestSentListener listener) {
        this.listener = listener;
    }

    public List<User> getFilteredUsers() {
        if (users.getValue() == null) {
            return null;
        }

        List<User> filteredUsers = new ArrayList<>();
        for (User user : users.getValue()) {
            boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
            boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

            if (isQueryInUsername || isQueryInFullName) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
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
        requestRepository.deleteFriendRequest(receiverId, userRepository.getFirebaseUser().getUid());
        listener.onRequestDeleted();
    }

    /**
     * checks whether current user sent an friend request to user with given id
     *
     * @param requests   list of friend requests
     * @param receiverId id of the friend to check if has received friend request of
     *                   current
     * @return has current user sent an friend request to user with given id
     */

    private boolean hasReceivedFriendRequest(List<FriendRequest> requests, String receiverId) {
        for (FriendRequest request : requests) {
            if (request.getReceiverId().equals(receiverId)
                    && request.getSenderId().equals(userRepository.getFirebaseUser().getUid())) {
                return true;
            }
        }
        return false;
    }

    public interface OnRequestSentListener {
        void onRequestAdded();

        void onRequestDeleted();
    }
}