package com.example.cm.ui.meetup.CreateMeetup;

import static com.example.cm.data.models.MeetupRequest.MeetupRequestType.MEETUP_REQUEST;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.Constants;
import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupRequest;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.MeetupRequestRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateMeetupViewModel extends ViewModel implements Serializable {

    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser;
    private final MeetupRepository meetupRepository;
    private final MutableLiveData<LatLng> meetupLatLng = new MutableLiveData<>();
    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>(true);
    private final MutableLiveData<Date> meetupTimestamp = new MutableLiveData<>();
    private final MutableLiveData<String> meetupLocationName = new MutableLiveData<>();
    private final MeetupRequestRepository meetupRequestRepository;
    private final MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    public MutableLiveData<List<User>> users;
    private String imageBaseString;
    private String meetupId;

    public CreateMeetupViewModel() {
        userRepository = new UserRepository();
        meetupRepository = new MeetupRepository();
        meetupRequestRepository = new MeetupRequestRepository();

        currentUser = userRepository.getCurrentUser();
        users = userRepository.getFriends();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
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

    public void setMeetupLocationName(String locationName) {
        meetupLocationName.postValue((locationName));
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

    public void setMeetupImg(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, Constants.QUALITY_MEETUP_IMG, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        imageBaseString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void createMeetup() {
        Objects.requireNonNull(selectedUsers.getValue());
        meetupId = UUID.randomUUID().toString();

        Meetup meetupToAdd = new Meetup(
                meetupId,
                userRepository.getFirebaseUser().getUid(),
                meetupLatLng.getValue(),
                meetupLocationName.getValue(),
                meetupTimestamp.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue(),
                imageBaseString);

        meetupRepository.addMeetup(meetupToAdd);
        sendMeetupRequests();
    }

    public void sendMeetupRequests() {
        // Create notifications for each invited user
        if (selectedUsers.getValue() != null && currentUser.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupRequest request = new MeetupRequest(
                        meetupId,
                        userRepository.getFirebaseUser().getUid(),
                        invitedFriendId,
                        MEETUP_REQUEST);
                meetupRequestRepository.addMeetupRequest(request);
            }
            selectedUsers.getValue().clear();
        }
    }

    public List<User> getFilteredUsers(String query) {
        if (users.getValue() == null) {
            return null;
        }

        List<User> filteredUsers = new ArrayList<>();
        for (User user : users.getValue()) {
            boolean isQueryInUsername = user.getUsername().toLowerCase().contains(query.toLowerCase());
            boolean isQueryInFullName = user.getDisplayName().toLowerCase().contains(query.toLowerCase());

            if (isQueryInUsername || isQueryInFullName) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    public void clearSelectedUsers() {
        if (selectedUsers.getValue() != null) {
            selectedUsers.getValue().clear();
        }
    }
}