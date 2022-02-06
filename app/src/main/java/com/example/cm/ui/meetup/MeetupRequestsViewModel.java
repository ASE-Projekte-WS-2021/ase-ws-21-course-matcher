package com.example.cm.ui.meetup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_ACCEPTED;
import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED;

public class MeetupRequestsViewModel extends ViewModel implements
        MeetupRequestRepository.OnMeetupRequestRepositoryListener,
        UserRepository.OnUserRepositoryListener {

    private User currentUser;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;
    private MeetupRequestRepository meetupRequestRepository;
    private final MutableLiveData<List<MeetupRequest>> requests = new MutableLiveData<>();

    public MeetupRequestsViewModel() {
        userRepository = new UserRepository(this);
        userRepository.getUserById(userRepository.getCurrentUser().getUid());
        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository(this);
        meetupRequestRepository.getMeetupRequestsForUser();
    }

    public MutableLiveData<List<MeetupRequest>> getMeetupRequests() {
        return requests;
    }

    public void acceptMeetupRequest(MeetupRequest request) {
        request.setState(Request.RequestState.REQUEST_ACCEPTED);
        request.setCreatedAtToNow();
        meetupRequestRepository.accept(request);

        meetupRepository.addConfirmed(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.addMeetupRequest(new MeetupRequest(
                request.getMeetupId(),
                currentUser.getId(),
                currentUser.getFullName(),
                request.getSenderId(),
                request.getLocation(),
                request.getMeetupAt(),
                MEETUP_INFO_ACCEPTED
        ));
    }

    public void declineMeetupRequest(MeetupRequest request) {
        meetupRepository.addDeclined(request.getMeetupId(), request.getReceiverId());
        meetupRequestRepository.addMeetupRequest(new MeetupRequest(
                request.getMeetupId(),
                currentUser.getId(),
                currentUser.getFullName(),
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
        meetupRequestRepository.getMeetupRequestsForUser();
    }

    @Override
    public void onMeetupRequestsRetrieved(List<MeetupRequest> requests) {
        ArrayList<MeetupRequest> requestsDisplayed = new ArrayList<>();
        for (MeetupRequest request : requests) {
            if (request.getState() != Request.RequestState.REQUEST_DECLINED) {
                requestsDisplayed.add(request);
            }
        }
        this.requests.postValue(requestsDisplayed);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser = user;
    }

    @Override
    public void onUsersRetrieved(List<User> users) {

    }
}
