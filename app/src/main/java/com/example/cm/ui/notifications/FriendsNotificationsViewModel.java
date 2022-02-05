package com.example.cm.ui.notifications;

import com.example.cm.data.models.Notification;
import com.example.cm.data.repositories.FriendsNotificationRepository;

public class FriendsNotificationsViewModel extends NotificationsViewModel{

    public FriendsNotificationsViewModel() {
        super();
        notificationRepository = new FriendsNotificationRepository(this);
        notificationRepository.getNotificationsForUser();
    }

    @Override
    public void acceptRequest(Notification notification) {
        super.acceptRequest(notification);
        userRepository.addFriends(notification.getSenderId(), notification.getReceiverId());
    }
}
