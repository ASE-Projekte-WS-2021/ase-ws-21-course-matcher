package com.example.cm.ui.add_friends;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cm.data.models.FriendsNotification;
import com.example.cm.data.models.Notification;
import com.example.cm.data.models.Notification.NotificationType;
import com.example.cm.data.models.User;
import com.example.cm.data.repositories.NotificationRepository;
import com.example.cm.data.repositories.UserRepository;

import java.util.List;

public class AddFriendsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    public MutableLiveData<List<User>> users;
    public MutableLiveData<List<Notification>> sentFriendRequests;
    public MutableLiveData<User> currentUser;
    public OnNotificationSentListener listener;

    public AddFriendsViewModel() {
        userRepository = new UserRepository();
        notificationRepository = new NotificationRepository();

        users = userRepository.getUsersNotFriends();
        currentUser = userRepository.getCurrentUser();
        sentFriendRequests = notificationRepository.getFriendRequests();
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
            users = userRepository.getUsersNotFriends();
            return;
        }
        users = userRepository.getUsersByUsername(query);
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     * Otherwise delete the friend request
     *
     * @param receiverId the id of the receiver
     */
    public void sendOrDeleteFriendRequest(String receiverId) {
        if (sentFriendRequests.getValue() == null) {
            return;
        }

        if (hasReceivedFriendRequest(sentFriendRequests.getValue(), receiverId)) {
            onFriendRequestExists(receiverId);
        } else {
            onFriendRequestDoesNotExist(receiverId);
        }
    }

    /**
     * Add a friend request if the user has not sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestDoesNotExist(String receiverId) {
        if (currentUser.getValue() == null) {
            return;
        }

        FriendsNotification notification = new FriendsNotification(currentUser.getValue().getId(), currentUser.getValue().getFullName(), receiverId);
        notificationRepository.addNotification(notification);
        listener.onNotificationAdded();
    }

    /**
     * Delete a friend request if the user has sent one to the receiver
     *
     * @param receiverId the id of the receiver
     */
    private void onFriendRequestExists(String receiverId) {
        notificationRepository.deleteNotification(receiverId, NotificationType.FRIEND_REQUEST);
        listener.onNotificationDeleted();
    }

    private Boolean hasReceivedFriendRequest(List<Notification> notifications, String receiverId) {
        for (Notification notification : notifications) {
            if (notification.getReceiverId().equals(receiverId) && notification.getSenderId().equals(userRepository.getCurrentUser().getValue().getId()) && notification.getType().equals(NotificationType.FRIEND_REQUEST)) {
                return true;
            }
        }
        return false;
    }


    public void setOnNotificationSentListener(OnNotificationSentListener listener) {
        this.listener = listener;
    }

    public interface OnNotificationSentListener {
        void onNotificationAdded();

        void onNotificationDeleted();
    }
}
