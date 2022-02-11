package com.example.cm.ui.meetup;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeetupRequestsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private MutableLiveData<User> currentUser;

    private final MeetupRepository meetupRepository;
    private MeetupRequestRepository meetupRequestRepository;
    private MutableLiveData<List<MeetupRequest>> requests;

    public MeetupRequestsViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();

        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();
        requests = meetupRequestRepository.getMeetupRequestsForUser();
    }

    public MutableLiveData<List<MeetupRequest>> getMeetupRequests() {
        return requests;
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
                currentUser.getValue().getFullName(),
                request.getSenderId(),
                request.getLocation(),
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
                currentUser.getValue().getFullName(),
                request.getSenderId(),
                request.getLocation(),
                request.getMeetupAt(),
                MEETUP_INFO_DECLINED
        ));

        request.setState(Request.RequestState.REQUEST_DECLINED);
        meetupRequestRepository.decline(request);
        Objects.requireNonNull(requests.getValue()).remove(request);
    }

    public void undoDeclineMeetupRequest(MeetupRequest request, int position) {
        request.setState(Request.RequestState.REQUEST_PENDING);
        meetupRepository.addPending(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.undo(request);
        Objects.requireNonNull(requests.getValue()).add(position, request);
    }

    public void refresh() {
        requests = meetupRequestRepository.getMeetupRequestsForUser();
    }
}