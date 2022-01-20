package com.example.cm.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.models.MeetupNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.MeetupRepository;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CreateMeetupViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener, NotificationRepository.OnNotificationRepositoryListener {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<String> meetupTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private User currentUser;

    public CreateMeetupViewModel() {
        this.meetupRepository = new MeetupRepository();
        userRepository = new UserRepository(this);
        notificationRepository = new NotificationRepository(this);
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
        Meetup meetup = new Meetup(
                userRepository.getCurrentUser().getUid(),
                meetupLocation.getValue(),
                meetupTime.getValue(),
                Boolean.TRUE.equals(meetupIsPrivate.getValue()),
                selectedUsers.getValue());
        meetupRepository.addMeetup(meetup);

        // Create notification for each invited user
        // userRepository.getUserById(userRepository.getCurrentUser().getUid());
        if(selectedUsers.getValue() != null) {
            //ArrayList<Notification> notificationsToSend = new ArrayList<>();
            for(String invitedFriendId : selectedUsers.getValue()){
                MeetupNotification notification = new MeetupNotification(
                        userRepository.getCurrentUser().getUid(),
                        currentUser.getFullName(),
                        invitedFriendId,
                        meetupLocation.getValue(),
                        meetupTime.getValue());
                notificationRepository.addNotification(notification);
            }
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
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.getId().equals(this.currentUser.getId())) {
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
}
