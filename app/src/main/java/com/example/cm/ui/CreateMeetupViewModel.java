package com.example.cm.ui;

import android.app.Notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Meetup;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.ui.InviteFriends.InviteFriendsRepository;
import com.example.cm.ui.select_friends.SelectFriendsViewModel;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class CreateMeetupViewModel extends ViewModel implements UserRepository.OnUserRepositoryListener, NotificationRepository.OnNotificationRepositoryListener {

    private final InviteFriendsRepository inviteFriendsRepository;
    private final MutableLiveData<String> meetupLocation = new MutableLiveData<>();
    private final MutableLiveData<String> meetupTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> meetupIsPrivate = new MutableLiveData<>();
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private SelectFriendsViewModel.OnNotificationSentListener notificationSentListener;
    //private Notification notification;

    public CreateMeetupViewModel() {
        this.inviteFriendsRepository = new InviteFriendsRepository();
        userRepository = new UserRepository(this);
        notificationRepository = new NotificationRepository(this);

        userRepository.getUsers();
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
        meetupLocation.setValue(location);
    }

    public void setTime(String time) {
        meetupTime.setValue(time);
    }

    public void setIsPrivate(Boolean isPrivate) {
        meetupIsPrivate.setValue(isPrivate);
    }


    public void createMeetup2(Meetup meetup) {
        inviteFriendsRepository.addMeetup2(meetup);
    }


    public void setOnNotificationSentListener(SelectFriendsViewModel.OnNotificationSentListener listener) {
        this.notificationSentListener = listener;
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

    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }



    @Override
    public void onUsersRetrieved(List<com.example.cm.data.models.User> users) {

    }

    @Override
    public void onUserRetrieved(com.example.cm.data.models.User user) {

    }

    @Override
    public void onNotificationsRetrieved(List<com.example.cm.data.models.Notification> notification) {

    }

/*    @Override
    public void onUsersRetrieved(List<User> users) {
        this.users.postValue(users);
    }

    @Override
    public void onUserRetrieved(User user) {

    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notification) {
        this.notifications.postValue(notification);
    }

    public interface OnNotificationSentListener {
        void onNotificationSent();
    }*/
}
