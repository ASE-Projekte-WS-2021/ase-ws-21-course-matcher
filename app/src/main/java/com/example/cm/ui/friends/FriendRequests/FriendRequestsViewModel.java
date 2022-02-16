package com.example.cm.ui.friends.FriendRequests;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendRequestsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private MutableLiveData<List<FriendRequest>> receivedRequests;
    private MutableLiveData<List<FriendRequest>> sentRequests = new MutableLiveData<>();
    private FriendRequestRepository friendRequestRepository;

    public FriendRequestsViewModel() {
        userRepository = new UserRepository();
        friendRequestRepository = new FriendRequestRepository();
        receivedRequests = friendRequestRepository.getFriendRequestsForUser();
    }

    public MutableLiveData<List<FriendRequest>> getFriendRequests() {
        return receivedRequests;
    }

    public void deleteFriendRequest(FriendRequest request) {
        friendRequestRepository.decline(request);
    }

    public void acceptFriendRequest(FriendRequest request) {
        request.setState(Request.RequestState.REQUEST_ACCEPTED);
        request.setCreatedAtToNow();
        friendRequestRepository.accept(request);
        userRepository.addFriends(request.getSenderId(), request.getReceiverId());
    }

    public void declineFriendRequest(FriendRequest request) {
        request.setState(Request.RequestState.REQUEST_DECLINED);
        friendRequestRepository.decline(request);
        Objects.requireNonNull(receivedRequests.getValue()).remove(request);
    }

    public void undoFriendRequest(FriendRequest request, int position, Request.RequestState previousState) {
        request.setState(previousState);
        friendRequestRepository.undo(request);
        Objects.requireNonNull(receivedRequests.getValue()).add(position, request);
    }

    public void refresh() {
        receivedRequests = friendRequestRepository.getFriendRequestsForUser();
    }
}
