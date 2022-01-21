package com.example.cm.data.models;

public class MeetupDeclinedNotification extends Notification{

    private String meetupId;

    public MeetupDeclinedNotification() {
        type = NotificationType.MEETUP_DECLINED;
    }

    public MeetupDeclinedNotification(String senderId, String senderName, String receiverId, String meetupId) {
        super(senderId, senderName, receiverId, NotificationType.MEETUP_DECLINED);
        this.meetupId = meetupId;
    }

    public String getMeetupId() {
        return meetupId;
    }

    public void setMeetupId(String meetupId) {
        this.meetupId = meetupId;
    }
}
