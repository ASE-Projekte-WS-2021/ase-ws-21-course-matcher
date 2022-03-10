package com.example.cm.ui.meetup.MeetupRequests;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_ACCEPTED;
import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.MeetupRequestDTO;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MeetupRequestsViewModel extends ViewModel implements MeetupRequestRepository.Callback, UserRepository.UserNamesCallback {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;

    private final MeetupRepository meetupRepository;
    private final MeetupRequestRepository meetupRequestRepository;
    private final MutableLiveData<List<MeetupRequest>> requestList;
    public MutableLiveData<List<MeetupRequestDTO>> requestDTOList = new MutableLiveData<>();

    public MeetupRequestsViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();

        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();
        requestList = meetupRequestRepository.getMeetupRequestsForUser(this);
    }

    public MutableLiveData<List<MeetupRequestDTO>> getMeetupRequests() {
        return requestDTOList;
    }

    public void deleteMeetupRequest(int position) {
        MeetupRequest request = getMeetupRequestByPosition(position);
        if (request != null) {
            meetupRequestRepository.deleteMeetupRequest(request);
        }
    }

    public void acceptMeetupRequest(int position) {
        if (currentUser.getValue() == null) {
            return;
        }

        MeetupRequest request = getMeetupRequestByPosition(position);
        if (request == null) {
            return;
        }

        request.setState(Request.RequestState.REQUEST_ACCEPTED);
        request.setCreatedAtToNow();
        meetupRequestRepository.accept(request);

        meetupRepository.addConfirmed(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.addMeetupRequest(new MeetupRequest(
                request.getMeetupId(),
                currentUser.getValue().getId(),
                request.getSenderId(),
                request.getLocation(),
                request.getMeetupAt(),
                MEETUP_INFO_ACCEPTED
        ));
    }

    public void declineMeetupRequest(int position) {
        if (currentUser.getValue() == null) {
            return;
        }

        MeetupRequest request = getMeetupRequestByPosition(position);
        if (request != null) {
            meetupRequestRepository.deleteMeetupRequest(request);
        }

        meetupRepository.addDeclined(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.addMeetupRequest(new MeetupRequest(
                request.getMeetupId(),
                currentUser.getValue().getId(),
                request.getSenderId(),
                request.getLocation(),
                request.getMeetupAt(),
                MEETUP_INFO_DECLINED
        ));

        request.setState(Request.RequestState.REQUEST_DECLINED);
        meetupRequestRepository.decline(request);
        Objects.requireNonNull(requestList.getValue()).remove(request);
    }

    public void undoDeclineMeetupRequest(MeetupRequestDTO request, int position) {
        request.setState(Request.RequestState.REQUEST_PENDING);
        meetupRepository.addPending(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.undoDecline(convertToMeetupRequest(request));
        Objects.requireNonNull(requestDTOList.getValue()).add(position, request);
    }

    public void undoDeleteMeetupRequest(MeetupRequestDTO request, int position, Request.RequestState previousState) {
        request.setState(previousState);
        meetupRequestRepository.addMeetupRequest(convertToMeetupRequest(request));
        Objects.requireNonNull(requestDTOList.getValue()).add(position, request);
    }

    private List<MeetupRequestDTO> convertToRequestDTOs(HashMap<String, String> userNames) {
        List<MeetupRequestDTO> friendRequestDTOs = new ArrayList<>();
        if (requestList.getValue() == null || requestList.getValue().isEmpty() || userNames.isEmpty()) {
            return friendRequestDTOs;
        } else {
            for (int i = 0; i < requestList.getValue().size(); i++) {
                MeetupRequest meetupRequest = requestList.getValue().get(i);
                MeetupRequestDTO meetupRequestDTO = new MeetupRequestDTO(
                        meetupRequest.getMeetupId(),
                        meetupRequest.getSenderId(),
                        meetupRequest.getReceiverId(),
                        userNames.get(meetupRequest.getSenderId()),
                        meetupRequest.getLocation(),
                        meetupRequest.getMeetupAt(), meetupRequest.getType(),
                        meetupRequest.getPhase(),
                        meetupRequest.getFormattedTime(),
                        meetupRequest.getState()
                );
                meetupRequestDTO.setId(meetupRequest.getId());

                friendRequestDTOs.add(meetupRequestDTO);
            }
            return friendRequestDTOs;
        }
    }
    
    private MeetupRequest convertToMeetupRequest(MeetupRequestDTO requestDTO){
        MeetupRequest request = new MeetupRequest(
                requestDTO.getMeetupId(),
                requestDTO.getSenderId(),
                requestDTO.getReceiverId(),
                requestDTO.getLocation(),
                requestDTO.getMeetupAt(),
                requestDTO.getType()
        );
        request.setCreatedAt(requestDTO.getCreatedAt());
        request.setState(requestDTO.getState());
        request.setPhase(requestDTO.getPhase());
        return request;
    }

    private MeetupRequest getMeetupRequestByPosition(int position) {
        if (requestList.getValue() != null) {
            return requestList.getValue().get(position);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMeetupRequestsRetrieved(List<MeetupRequest> meetupRequests) {
        List<String> userIds = meetupRequests.stream().map(Request::getSenderId).collect(Collectors.toList());
        userRepository.getUserNamesMapByIds(userIds, this);
    }

    @Override
    public void onUsersMapRetrieved(HashMap<String, String> names, HashMap<String, String> userNames) {
        requestDTOList.postValue(convertToRequestDTOs(userNames));
    }
}
