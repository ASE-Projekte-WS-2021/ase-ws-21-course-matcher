package com.example.cm.ui.meetup.CreateMeetup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

public class CreateMeetupViewModel extends ViewModel implements
        MeetupRequestRepository.OnMeetupRequestRepositoryListener {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final MeetupRequestRepository meetupRequestRepository;

    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<String> meetupTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    private final MutableLiveData<Date> meetupTimestamp = new MutableLiveData<>();
    public MutableLiveData<List<User>> users = new MutableLiveData<>();;
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser;


    public CreateMeetupViewModel() {
        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository(this);
        userRepository = new UserRepository();
        users = userRepository.getFriends();
        currentUser = userRepository.getCurrentUser();
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<String>> getSelectedUsers() {
        return selectedUsers;
    }

    public void toggleSelectUser(String id) {
        List<String> currentlySelectedUsers = new ArrayList<>();

        if (selectedUsers.getValue() != null) {
            currentlySelectedUsers = selectedUsers.getValue();
        }

        if (currentlySelectedUsers.contains(id)) {
            currentlySelectedUsers.remove(id);
        } else {
            currentlySelectedUsers.add(id);
        }
        selectedUsers.postValue(currentlySelectedUsers);
    }

    public LiveData<String> getMeetupLocation() {
        return meetupLocation;
    }

    public LiveData<String> getMeetupTime() {
        return meetupTime;
    }

    public LiveData<Boolean> getMeetupIsPrivate() {
        return meetupIsPrivate;
    }

    public LiveData<Date> getMeetupTimestamp() {
        return meetupTimestamp;
    }

    public void setMeetupTimestamp(Date timestamp) {
        meetupTimestamp.postValue(timestamp);
    }

    public void setLocation(String location) {
        meetupLocation.postValue(location);
    }

    public void setTime(String time) {
        meetupTime.postValue(time);
    }

    public void setIsPrivate(Boolean isPrivate) {
        meetupIsPrivate.postValue(isPrivate);
    }

    public void createMeetup() {
        Objects.requireNonNull(selectedUsers.getValue());
        String meetupId = UUID.randomUUID().toString();
        Meetup meetupToAdd = new Meetup(
                meetupId,
                userRepository.getFirebaseUser().getUid(),
                meetupLocation.getValue(),
                meetupTime.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue(),
                meetupTimestamp.getValue());

        meetupRepository.addMeetup(meetupToAdd);

        sendMeetupRequest(meetupToAdd.getId());
    }

    private void sendMeetupRequest(String meetupId) {
        // Create notifications for each invited user
        if (selectedUsers.getValue() != null && currentUser.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupRequest request = new MeetupRequest(
                        meetupId,
                        userRepository.getFirebaseUser().getUid(),
                        currentUser.getValue().getFullName(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTime.getValue(),
                        MEETUP_REQUEST);
                meetupRequestRepository.addMeetupRequest(request);
            }
            selectedUsers.getValue().clear();
        }
    }

    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }

    @Override
    public void onMeetupRequestsRetrieved(List<MeetupRequest> requests) {

    }
}