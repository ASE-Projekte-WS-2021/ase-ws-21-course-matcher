package com.example.cm.ui.friends.FriendRequests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class FriendRequestsViewModel extends ViewModel {


    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<List<FriendRequest>> receivedRequests;

    private final FriendRequestRepository friendRequestRepository;

    private final MutableLiveData<List<String>> userIds = new MutableLiveData<>();
    private final LiveData<List<User>> userLiveData = Transformations.switchMap(userIds, userRepository::getUsersByIds);

    public FriendRequestsViewModel() {
        friendRequestRepository = new FriendRequestRepository();
        receivedRequests = friendRequestRepository.getReceivedAndSentRequestsForUser();
    }

    public MutableLiveData<List<FriendRequest>> getFriendRequests() {
        return receivedRequests;
    }

    public void declineOrDeleteFriendRequest(FriendRequest request) {
        friendRequestRepository.decline(request);
    }

    public void acceptFriendRequest(FriendRequest request) {
        request.setState(Request.RequestState.REQUEST_ACCEPTED);
        request.setCreatedAtToNow();
        friendRequestRepository.accept(request);
        userRepository.addFriends(request.getSenderId(), request.getReceiverId());
    }

    public void undoFriendRequest(FriendRequest request, int position, Request.RequestState previousState) {
        request.setState(previousState);
        friendRequestRepository.addFriendRequest(request);
        Objects.requireNonNull(receivedRequests.getValue()).add(position, request);
    }

    public LiveData<List<User>> getUsers() {
        return userLiveData;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds.setValue(userIds);
    }
}
