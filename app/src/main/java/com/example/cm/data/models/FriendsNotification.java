package com.example.cm.data.models;

public class FriendsNotification extends Notification{

    public FriendsNotification() {
        super();
        type = NotificationType.FRIEND_REQUEST;
    }

    public FriendsNotification(String senderId, String senderName, String receiverId) {
        super(senderId, senderName, receiverId, NotificationType.FRIEND_REQUEST);
    }

}
