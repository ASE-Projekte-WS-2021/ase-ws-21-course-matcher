package com.example.cm.ui.friends.FriendRequests;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class FriendRequestsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<List<MutableLiveData<FriendRequest>>> receivedRequests;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestsViewModel() {
        userRepository = new UserRepository();
        friendRequestRepository = new FriendRequestRepository();
        receivedRequests = friendRequestRepository.getFriendRequestsForUser();
    }

    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getFriendRequests() {
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
        MutableLiveData<FriendRequest> requestMDL = new MutableLiveData<>();
        request.setState(previousState);
        friendRequestRepository.addFriendRequest(request);
        requestMDL.postValue(request);
        Objects.requireNonNull(receivedRequests.getValue()).add(position, requestMDL);
    }
}
