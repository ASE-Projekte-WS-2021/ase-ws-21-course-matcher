package com.example.cm.ui.select_friends;

import android.util.Log;

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

    public void sendFriendRequest(String receiverId) {
        // Create a new notification
        String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();

        Notification notification = new Notification(currentUser.getId(), fullName, receiverId, NotificationType.FRIEND_REQUEST);
        notificationRepository.addNotification(notification);
        notificationSentListener.onNotificationSent();
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

        for (Notification notification : notifications) {
            Log.d("SelectFriendsViewModel", "Notification: " + notification.getSenderId() + " : " + notification.getType());
        }

    }

    public interface OnNotificationSentListener {
        void onNotificationSent();
    }
}
