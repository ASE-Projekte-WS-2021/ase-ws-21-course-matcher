package com.example.cm.ui.meetup.MeetupRequests;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_ACCEPTED;
import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_INFO_DECLINED;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.Request;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class MeetupRequestsViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<User> currentUser;

    private final MeetupRepository meetupRepository = new MeetupRepository();
    private final MeetupRequestRepository meetupRequestRepository;
    private final MutableLiveData<List<MeetupRequest>> requestList;
    private final MutableLiveData<List<String>> userIds = new MutableLiveData<>();
    private final MutableLiveData<List<String>> meetupIds = new MutableLiveData<>();
    private final LiveData<List<Meetup>> meetupLiveData = Transformations.switchMap(meetupIds, meetupRepository::getMeetupsByIds);
    private final LiveData<List<User>> userLiveData = Transformations.switchMap(userIds,
            userRepository::getUsersByIds);

    public MeetupRequestsViewModel() {
        currentUser = userRepository.getCurrentUser();
        meetupRequestRepository = new MeetupRequestRepository();
        requestList = meetupRequestRepository.getMeetupRequestsForUser();
    }

    public MutableLiveData<List<MeetupRequest>> getMeetupRequests() {
        return requestList;
    }

    public LiveData<List<User>> getUsers() {
        return userLiveData;
    }

    public LiveData<List<Meetup>> getMeetups() {
        return meetupLiveData;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds.setValue(userIds);
    }

    public void setMeetupIds(List<String> meetupIds) {
        this.meetupIds.setValue(meetupIds);
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
                MEETUP_INFO_ACCEPTED));
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
                MEETUP_INFO_DECLINED));

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
