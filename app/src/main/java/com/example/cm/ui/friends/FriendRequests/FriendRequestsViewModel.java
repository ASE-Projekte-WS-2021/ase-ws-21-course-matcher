package com.example.cm.ui.friends.FriendRequests;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.FriendRequestDTO;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import timber.log.Timber;

public class FriendRequestsViewModel extends ViewModel implements FriendRequestRepository.Callback {

    private final UserRepository userRepository;
    private MutableLiveData<List<MutableLiveData<FriendRequest>>> receivedRequests = new MutableLiveData<>();
    /*public LiveData<List<User>> userNames =
            Transformations.switchMap(receivedRequests, request -> {
                System.out.println("on user names");
                return null;
            });*/
    private final MutableLiveData<List<MutableLiveData<FriendRequestDTO>>> receivedRequestDTOs;
    private final MutableLiveData<List<String>> userIds = new MutableLiveData<>();
    private final FriendRequestRepository friendRequestRepository;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FriendRequestsViewModel() {
        userRepository = new UserRepository();
        friendRequestRepository = new FriendRequestRepository();
        receivedRequests = friendRequestRepository.getFriendRequestsForUser(this);
        receivedRequestDTOs = new MutableLiveData<>();
    }

    public MutableLiveData<List<MutableLiveData<FriendRequestDTO>>> getFriendRequests() {
        return receivedRequestDTOs;
    }

    public MutableLiveData<List<MutableLiveData<FriendRequest>>> getReceivedRequests() {
        return receivedRequests;
    }

    public void declineOrDeleteFriendRequest(int position) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            friendRequestRepository.decline(request);
        } else {
            Timber.d("Request is null");
        }
    }

    public void acceptFriendRequest(int position) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            request.setState(Request.RequestState.REQUEST_ACCEPTED);
            request.setCreatedAtToNow();
            friendRequestRepository.accept(request);
            userRepository.addFriends(request.getSenderId(), request.getReceiverId());
        } else {
            Timber.d("Request is null");
        }
    }

    public void undoFriendRequest(int position, Request.RequestState previousState) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            MutableLiveData<FriendRequest> requestMDL = new MutableLiveData<>();
            request.setState(previousState);
            friendRequestRepository.addFriendRequest(request);
            requestMDL.postValue(request);
            Objects.requireNonNull(receivedRequests.getValue()).add(position, requestMDL);
        } else {
            Timber.d("Request is null");
        }
    }

    private FriendRequest getFriendRequestByPosition(int position) {
        if (receivedRequests.getValue() != null) {
            return receivedRequests.getValue().get(position).getValue();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFriendRequestsRetrieved(List<FriendRequest> friendRequests) {
        List<String> userIds = friendRequests.stream().map(Request::getSenderId).collect(Collectors.toList());
        userRepository.getUsersByIds(userIds);
    }
}
