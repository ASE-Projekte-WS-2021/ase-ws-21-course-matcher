package com.example.cm.ui.meetup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupNotificationRepository;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateMeetupViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener,
        NotificationRepository.OnNotificationRepositoryListener {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final MeetupNotificationRepository notificationRepository;

    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<String> meetupTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    private final MutableLiveData<Date> meetupTimestamp = new MutableLiveData<>();
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();

    private User currentUser;
    private Meetup meetupToAdd;

    public CreateMeetupViewModel() {
        this.meetupRepository = new MeetupRepository();
        userRepository = new UserRepository(this);
        notificationRepository = new MeetupNotificationRepository(this);
        FirebaseUser firebaseUser = userRepository.getCurrentUser();
        userRepository.getUserByEmail(firebaseUser.getEmail());
        userRepository.getUsers();
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
        FirebaseUser currentUser = userRepository.getCurrentUser();
        Objects.requireNonNull(selectedUsers.getValue());
        meetupToAdd = new Meetup(
                currentUser.getUid(),
                meetupLocation.getValue(),
                meetupTime.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue(),
                meetupTimestamp.getValue());

        meetupRepository.addMeetup(meetupToAdd);

        sendMeetupNotification();
    }

    private void sendMeetupNotification() {
        if (selectedUsers.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupNotification notification = new MeetupNotification(
                        meetupToAdd.getId(),
                        userRepository.getCurrentUser().getUid(),
                        currentUser.getFullName(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTime.getValue(),
                        Notification.NotificationType.MEETUP_REQUEST);
                notificationRepository.addNotification(notification);
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
    public void onUsersRetrieved(List<User> users) {
        String currentUserId = userRepository.getCurrentUser().getUid();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.getId().equals(currentUserId) &&
                    (user.getFriends() != null && user.getFriends().contains(currentUserId))) {
                filteredUsers.add(user);
            }
        }

        this.users.postValue(filteredUsers);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.currentUser = user;
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notification) {

    }

    @Override
    public void onMeetupsRetrieved(List<Meetup> meetups) {

    }

    @Override
    public void onMeetupAdded(String meetupId) {
        meetupToAdd.setId(meetupId);

        // Create notifications for each invited user
        if (selectedUsers.getValue() != null) {
            for (String invitedFriendId : selectedUsers.getValue()) {
                MeetupNotification notification = new MeetupNotification(
                        meetupId,
                        userRepository.getCurrentUser().getUid(),
                        currentUser.getFullName(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTime.getValue(),
                        MeetupNotification.NotificationType.MEETUP_REQUEST);
                notificationRepository.addNotification(notification);
            }
            selectedUsers.getValue().clear();
        }
    }
}