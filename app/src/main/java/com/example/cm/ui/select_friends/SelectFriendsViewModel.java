package com.example.cm.ui.select_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.Notification;
import com.example.cm.data.models.Notification.NotificationType;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.NotificationRepository.OnNotificationRepositoryListener;
import com.example.cm.data.repositories.UserRepository;
import com.example.cm.data.repositories.UserRepository.OnUserRepositoryListener;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendsViewModel extends ViewModel implements OnUserRepositoryListener, OnNotificationRepositoryListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    public MutableLiveData<List<String>> selectedUsers = new MutableLiveData<>();
    private OnNotificationSentListener notificationSentListener;
    private User user;

    public SelectFriendsViewModel() {
        userRepository = new UserRepository(this);
        notificationRepository = new NotificationRepository(this);
        FirebaseUser firebaseUser = userRepository.getCurrentUser();
        userRepository.getUserByEmail(firebaseUser.getEmail());
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
        // Check if users have been selected
        if (selectedUsers.getValue() == null) return;
        // Get a list of sent notifications by the current user
        notificationRepository.getNotificationsForUser();

        int sentNotifications = 0;

        for (String userId : selectedUsers.getValue()) {
            boolean requestAlreadySent = false;

            if (notifications.getValue() == null) return;

            for (Notification notification : notifications.getValue()) {
                // If a user has already received a friend request from the current user
                if (notification.getReceiverId().equals(userId) && notification.getType().equals(NotificationType.FRIEND_REQUEST)) {
                    requestAlreadySent = true;
                    break;
                }
            }

            if (requestAlreadySent) continue;
            String fullName = user.getFirstName() + " " + user.getLastName();
            // TODO: Replace with actual user id of currently logged in user
            Notification notification = new Notification(user.getId(), fullName, userId, NotificationType.FRIEND_REQUEST);
            notificationRepository.addNotification(notification);
            sentNotifications++;
        }
        if (sentNotifications > 0) {
            notificationSentListener.onNotificationSent();
        }
    }

    @Override
    public void onUsersRetrieved(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for(User user : users) {
            if (!user.getId().equals(this.user.getId())) {
                filteredUsers.add(user);
            }
        }

        this.users.postValue(filteredUsers);
    }

    @Override
    public void onUserRetrieved(User user) {
        this.user = user;
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        this.notifications.postValue(notifications);
    }

    public interface OnNotificationSentListener {
        void onNotificationSent();
    }
}
