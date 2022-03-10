package com.example.cm.ui.meetup.CreateMeetup;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateMeetupViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;
    private final MeetupRepository meetupRepository;
    private final MutableLiveData<LatLng> meetupLatLng = new MutableLiveData<>();
    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    private final MutableLiveData<Date> meetupTimestamp = new MutableLiveData<>();
    private final MeetupRequestRepository meetupRequestRepository;
    public MutableLiveData<List<User>> users;
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();

    public CreateMeetupViewModel() {
        userRepository = new UserRepository();
        currentUser = userRepository.getCurrentUser();
        users = userRepository.getFriends();

        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();
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

    public LiveData<LatLng> getMeetupLatLng() {
        return meetupLatLng;
    }

    public void setMeetupLatLng(LatLng latLng) {
        meetupLatLng.postValue(latLng);
    }

    public void setMeetupLocation(String location) {
        meetupLocation.postValue(location);
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

    public void setIsPrivate(Boolean isPrivate) {
        meetupIsPrivate.postValue(isPrivate);
    }

    public boolean createMeetup() {
        Objects.requireNonNull(selectedUsers.getValue());
        String meetupId = UUID.randomUUID().toString();

        Meetup meetupToAdd = new Meetup(
                meetupId,
                userRepository.getFirebaseUser().getUid(),
                meetupLatLng.getValue(),
                meetupTimestamp.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue());

        boolean isSuccessful = meetupRepository.addMeetup(meetupToAdd);

        if (isSuccessful) {
            sendMeetupRequest(meetupToAdd.getId());
            return true;
        }

        return false;
    }

    private void sendMeetupRequest(String meetupId) {
        // Create notifications for each invited user
        if (selectedUsers.getValue() != null && currentUser.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupRequest request = new MeetupRequest(
                        meetupId,
                        userRepository.getFirebaseUser().getUid(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTimestamp.getValue(),
                        MEETUP_REQUEST);
                meetupRequestRepository.addMeetupRequest(request);
            }
            selectedUsers.getValue().clear();
        }
    }

    public void searchUsers(String query) {
        if (users.getValue() != null) {
            users.getValue().clear();
            users = userRepository.getFriendsByUsername(query);
        }
    }
}