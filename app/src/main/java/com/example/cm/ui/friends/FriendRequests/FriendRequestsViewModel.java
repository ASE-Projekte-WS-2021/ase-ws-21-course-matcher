package com.example.cm.ui.friends.FriendRequests;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendRequest;
import com.example.cm.data.models.FriendRequestDTO;
import com.example.cm.data.models.Request;
import com.example.cm.data.repositories.FriendRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import timber.log.Timber;

public class FriendRequestsViewModel extends ViewModel implements FriendRequestRepository.Callback, UserRepository.UserNamesCallback {

    private final UserRepository userRepository;
    private MutableLiveData<List<MutableLiveData<FriendRequest>>> receivedRequests;
    public MutableLiveData<List<FriendRequestDTO>> receivedRequestDTOs = new MutableLiveData<>();

    private final FriendRequestRepository friendRequestRepository;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FriendRequestsViewModel() {
        userRepository = new UserRepository();
        friendRequestRepository = new FriendRequestRepository();
        receivedRequests = friendRequestRepository.getFriendRequestsForUser(this);
    }

    public LiveData<List<FriendRequestDTO>> getFriendRequests() {
        return receivedRequestDTOs;
    }

    public void declineOrDeleteFriendRequest(int position) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            friendRequestRepository.decline(request);
        }
    }

    public void acceptFriendRequest(int position) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            request.setState(Request.RequestState.REQUEST_ACCEPTED);
            request.setCreatedAtToNow();
            friendRequestRepository.accept(request);
            userRepository.addFriends(request.getSenderId(), request.getReceiverId());
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
        }
    }

    private FriendRequest getFriendRequestByPosition(int position) {
        if (receivedRequests.getValue() != null) {
            return receivedRequests.getValue().get(position).getValue();
        }
        return null;
    }

    private List<FriendRequestDTO> convertToRequestDTOs(List<String> userNames) {
        Timber.d("on convert");
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        if(receivedRequests.getValue() != null || receivedRequests.getValue().isEmpty() || userNames.isEmpty()) {
            for (int i = 0; i < receivedRequests.getValue().size(); i++) {
                FriendRequest friendRequest = receivedRequests.getValue().get(i).getValue();
                FriendRequestDTO friendRequestDTO = new FriendRequestDTO(
                        friendRequest.getSenderId(),
                        userNames.get(i),
                        friendRequest.getReceiverId()
                );
                friendRequestDTO.setState(friendRequest.getState());
                friendRequestDTO.setCreatedAt(friendRequest.getCreatedAt());
                friendRequestDTOs.add(friendRequestDTO);
            }
        }
        return friendRequestDTOs;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFriendRequestsRetrieved(List<FriendRequest> friendRequests) {
        List<String> userIds = friendRequests.stream().map(Request::getSenderId).collect(Collectors.toList());
        userRepository.getUserNamesByIds(userIds, this);
    }

    @Override
    public void onUsersRetrieved(List<String> names) {
        receivedRequestDTOs.postValue(convertToRequestDTOs(names));
    }
}
