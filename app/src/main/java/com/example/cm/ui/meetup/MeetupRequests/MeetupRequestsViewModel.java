package com.example.cm.ui.meetup.MeetupRequests;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_ACCEPTED;
import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class MeetupRequestsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;

    private final MeetupRepository meetupRepository;
    private final MeetupRequestRepository meetupRequestRepository;
    private final MutableLiveData<List<MeetupRequest>> requestList;

    public MeetupRequestsViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();

        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();
        requestList = meetupRequestRepository.getMeetupRequestsForUser();
    }

    public MutableLiveData<List<MeetupRequest>> getMeetupRequests() {
        return requestList;
    }

    public void deleteMeetupRequest(MeetupRequest request) {
        meetupRequestRepository.deleteMeetupRequest(request);
    }

    public void acceptMeetupRequest(MeetupRequest request) {
        if (currentUser.getValue() == null) {
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
                request.getMeetupAt(),
                MEETUP_INFO_ACCEPTED
        ));
    }

    public void declineMeetupRequest(MeetupRequest request) {
        if (currentUser.getValue() == null) {
            return;
        }

        meetupRepository.addDeclined(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.addMeetupRequest(new MeetupRequest(
                request.getMeetupId(),
                currentUser.getValue().getId(),
                request.getSenderId(),
                request.getMeetupAt(),
                MEETUP_INFO_DECLINED
        ));

        request.setState(Request.RequestState.REQUEST_DECLINED);
        meetupRequestRepository.decline(request);
        Objects.requireNonNull(requestList.getValue()).remove(request);
    }

    public void undoDeclineMeetupRequest(MeetupRequest request, int position) {
        request.setState(Request.RequestState.REQUEST_PENDING);
        meetupRepository.addPending(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.undoDecline(request);
        Objects.requireNonNull(requestList.getValue()).add(position, request);
    }

    public void undoDeleteMeetupRequest(MeetupRequest request, int position, Request.RequestState previousState) {
        request.setState(previousState);
        meetupRequestRepository.addMeetupRequest(request);
        Objects.requireNonNull(requestList.getValue()).add(position, request);
    }
}
