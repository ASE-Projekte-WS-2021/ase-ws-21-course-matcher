package com.example.cm.data.models;

public class MeetupDeclinedNotification extends MeetupNotification{

    public MeetupDeclinedNotification() {
        super(NotificationType.MEETUP_DECLINED);
        state = NotificationState.NOTIFICATION_DECLINED;
    }

    public MeetupDeclinedNotification(String meetupId, String senderId, String senderName, String receiverId, String location, String meetupAt) {
        super(meetupId, senderId, senderName, receiverId, location, meetupAt, NotificationType.MEETUP_DECLINED);
        state = NotificationState.NOTIFICATION_DECLINED;
    }
}
