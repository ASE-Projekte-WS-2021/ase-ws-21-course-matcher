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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendRequestsViewModel extends ViewModel implements FriendRequestRepository.Callback, UserRepository.UserNamesCallback {

    private final UserRepository userRepository;
    private final MutableLiveData<List<FriendRequest>> receivedRequests;
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
            Objects.requireNonNull(receivedRequestDTOs.getValue()).remove(position);
        }
    }

    public void acceptFriendRequest(int position) {
        FriendRequest request = getFriendRequestByPosition(position);
        if (request != null) {
            request.setState(Request.RequestState.REQUEST_ACCEPTED);
            request.setCreatedAtToNow();
            if (receivedRequestDTOs.getValue() != null) {
                receivedRequestDTOs.getValue().get(position).setState(Request.RequestState.REQUEST_ACCEPTED);
                receivedRequestDTOs.getValue().get(position).setCreatedAtToNow();
            }
            friendRequestRepository.accept(request);
            userRepository.addFriends(request.getSenderId(), request.getReceiverId());
        }
    }

    public void undoFriendRequest(FriendRequestDTO requestDTO, int position, Request.RequestState previousState) {
        FriendRequest request = convertToFriendRequest(requestDTO);
        request.setState(previousState);
        friendRequestRepository.addFriendRequest(request);
/*        Objects.requireNonNull(receivedRequestDTOs.getValue()).add(position, requestDTO);*/
    }

    private FriendRequest getFriendRequestByPosition(int position) {
        if (receivedRequests.getValue() != null) {
            return receivedRequests.getValue().get(position);
        }
        return null;
    }

    private FriendRequest convertToFriendRequest(FriendRequestDTO requestDTO) {
        FriendRequest request = new FriendRequest(
                requestDTO.getSenderId(),
                requestDTO.getReceiverId()
        );
        request.setCreatedAt(requestDTO.getCreatedAt());
        request.setId(requestDTO.getId());
        request.setState(requestDTO.getState());
        return request;
    }

    private List<FriendRequestDTO> convertToRequestDTOs(HashMap<String, String> names, HashMap<String, String> userNames) {
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        if (receivedRequests.getValue() == null || receivedRequests.getValue().isEmpty() || userNames.isEmpty()) {
            return friendRequestDTOs;
        } else {
            for (int i = 0; i < receivedRequests.getValue().size(); i++) {
                FriendRequest friendRequest = receivedRequests.getValue().get(i);
                FriendRequestDTO friendRequestDTO = new FriendRequestDTO(
                        friendRequest.getSenderId(),
                        names.get(friendRequest.getSenderId()),
                        userNames.get(friendRequest.getSenderId()),
                        friendRequest.getReceiverId()
                );
                friendRequestDTO.setId(friendRequest.getId());
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
        userRepository.getUserNamesMapByIds(userIds, this);
    }

    @Override
    public void onUsersMapRetrieved(HashMap<String, String> names, HashMap<String, String> userNames) {
        receivedRequestDTOs.postValue(convertToRequestDTOs(names, userNames));
    }
}
