package com.example.cm.ui.select_friends;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendsNotification;
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
    public MutableLiveData<List<Notification>> sentFriendRequests = new MutableLiveData<>();
    private OnNotificationSentListener notificationSentListener;
    private User currentUser;

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

    public MutableLiveData<List<Notification>> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }

    public void sendOrDeleteFriendRequest(String receiverId) {
        if (hasAlreadyReceivedFriendRequest(receiverId, currentUser.getId())) {
            notificationRepository.deleteNotification(receiverId, currentUser.getId(), NotificationType.FRIEND_REQUEST);
            notificationSentListener.onNotificationDeleted();
            return;
        }

        // Create a new notification
        FriendsNotification notification = new FriendsNotification(currentUser.getId(), currentUser.getFullName(), receiverId);
        notificationRepository.addNotification(notification);
        notificationSentListener.onNotificationSent();
    }


    private Boolean hasAlreadyReceivedFriendRequest(String receiverId, String senderId) {
        if(sentFriendRequests.getValue() == null) {
            return false;
        }

        for (int i = 0; i < sentFriendRequests.getValue().size(); i++) {
            Notification notification = sentFriendRequests.getValue().get(i);
            if (notification.getReceiverId().equals(receiverId) && notification.getSenderId().equals(senderId) && notification.getType().equals(NotificationType.FRIEND_REQUEST)) {
                return true;
            }
        }

        return false;
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
        notificationRepository.getFriendRequestsOfSender(user.getId());
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        this.sentFriendRequests.postValue(notifications);
    }

    public interface OnNotificationSentListener {
        void onNotificationSent();
        void onNotificationDeleted();
    }
}
