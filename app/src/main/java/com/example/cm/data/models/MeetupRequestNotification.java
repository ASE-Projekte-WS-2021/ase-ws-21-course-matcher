package com.example.cm.data.models;

public class MeetupRequestNotification extends MeetupNotification {

    public MeetupRequestNotification() {
        super(NotificationType.MEETUP_REQUEST);
        state = NotificationState.NOTIFICATION_DECLINED;
    }

    public MeetupRequestNotification(String meetupId, String senderId, String senderName, String receiverId, String location, String meetupAt) {
        super(meetupId, senderId, senderName, receiverId, location, meetupAt, NotificationType.MEETUP_REQUEST);
        state = NotificationState.NOTIFICATION_DECLINED;
    }
}
