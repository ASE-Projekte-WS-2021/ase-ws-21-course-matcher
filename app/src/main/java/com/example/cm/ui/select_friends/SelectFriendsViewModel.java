package com.example.cm.ui.select_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Notification;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.data.repositories.NotificationRepository.OnNotificationRepositoryListener;
import com.example.cm.data.repositories.UserRepository.OnUserRepositoryListener;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendsViewModel extends ViewModel implements OnUserRepositoryListener, OnNotificationRepositoryListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private OnNotificationSentListener notificationSentListener;

    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();

    public SelectFriendsViewModel() {
        userRepository = new UserRepository(this);
        notificationRepository = new NotificationRepository(this);

        userRepository.getUsers();
    }

    public void setOnNotificationSentListener(OnNotificationSentListener listener) {
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

    public void sendFriendRequest() {
        if (selectedUsers.getValue() == null) {
            return;
        }

        for(String userId : selectedUsers.getValue()) {

            boolean requestAlreadySent = false;

            // TODO: check if notification already exists
            // Check if a notification already exists
            /*
            if(notifications.getValue() == null) {
                return;
            }
            for(Notification notification : Objects.requireNonNull(notifications.getValue())) {
                if (notification.getUserId().equals(userId) && notification.getType().equals(Notification.NotificationType.FRIEND_REQUEST)) {
                    requestAlreadySent = true;
                    break;
                }
            }

            if(requestAlreadySent) {
                continue;
            }
             */

            Notification notification = new Notification(
                    "Neue Freundschaftsanfrage",
                    "Hallo,\n\n" + "[CURRENTLY LOGGED IN USER]" + " möchte mit dir befreundet sein.\n\nBitte bestätige diese Anfrage, indem du auf den Button klickst.",
                    Notification.NotificationType.FRIEND_REQUEST,
                    userId);
            // notificationRepository.addNotification(notification);
            notificationSentListener.onNotificationSent();
        }
    }

    @Override
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
    }
}
