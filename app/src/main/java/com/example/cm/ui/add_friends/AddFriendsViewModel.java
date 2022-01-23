package com.example.cm.ui.add_friends;

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

import timber.log.Timber;

public class AddFriendsViewModel extends ViewModel implements OnUserRepositoryListener, OnNotificationRepositoryListener {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    public MutableLiveData<List<User>> users = new MutableLiveData<>();
    public MutableLiveData<List<Notification>> sentFriendRequests = new MutableLiveData<>();
    public OnNotificationSentListener listener;
    private User currentUser;

    public AddFriendsViewModel() {
        userRepository = new UserRepository(this);
        notificationRepository = new NotificationRepository(this);
        FirebaseUser firebaseUser = userRepository.getCurrentUser();
        userRepository.getUserByEmail(firebaseUser.getEmail());
        userRepository.getUsersNotFriends();
    }

    public MutableLiveData<List<User>> getUsers() {
        return users;
    }

    public MutableLiveData<List<Notification>> getSentFriendRequests() {
        return sentFriendRequests;
    }

    /**
     * Search a user by their username
     *
     * @param query the username to search for
     */
    public void searchUsers(String query) {
        if (query.isEmpty()) {
            userRepository.getUsers();
            return;
        }
        userRepository.getUsersByUsername(query);
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     * Otherwise delete the friend request
     *
     * @param receiverId the id of the receiver
     */
    public void sendOrDeleteFriendRequest(String receiverId) {
        notificationRepository.getFriendRequests(currentUser.getId(), notifications -> {
            if (notifications == null) {
                return;
            }

            if (hasReceivedFriendRequest(notifications, receiverId)) {
                onFriendRequestExists(receiverId);
            } else {
                onFriendRequestDoesNotExist(receiverId);
            }

            sentFriendRequests.postValue(notifications);
        });
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestDoesNotExist(String receiverId) {
        Timber.d("Sending friend request to %s", receiverId);
        FriendsNotification notification = new FriendsNotification(currentUser.getId(), currentUser.getFullName(), receiverId);
        notificationRepository.addNotification(notification);
        listener.onNotificationAdded();
    }

    /**
     * Delete a friend request if the user has sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestExists(String receiverId) {
        Timber.d("Deleting friend request to %s", receiverId);
        notificationRepository.deleteNotification(receiverId, currentUser.getId(), NotificationType.FRIEND_REQUEST);
        listener.onNotificationDeleted();
    }

    private Boolean hasReceivedFriendRequest(List<Notification> notifications, String receiverId) {
        for (Notification notification : notifications) {
            if (notification.getReceiverId().equals(receiverId) && notification.getSenderId().equals(currentUser.getId()) && notification.getType().equals(NotificationType.FRIEND_REQUEST)) {
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
        notificationRepository.getFriendRequests(user.getId(), notifications -> {
            sentFriendRequests.postValue(notifications);
        });
    }

    @Override
    public void onNotificationsRetrieved(List<Notification> notifications) {
        this.sentFriendRequests.postValue(notifications);
    }

    public void setOnNotificationSentListener(OnNotificationSentListener listener) {
        this.listener = listener;
    }

    public interface OnNotificationSentListener {

        void onNotificationAdded();

        void onNotificationDeleted();
    }
}
