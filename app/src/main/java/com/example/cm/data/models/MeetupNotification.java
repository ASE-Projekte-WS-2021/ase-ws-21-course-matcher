package com.example.cm.data.models;

import java.util.Date;

public class MeetupNotification extends Notification{

    private String location;
    private String meetupAt;

    public MeetupNotification() {
        super();
        type = NotificationType.MEETUP_REQUEST;
    }

    public MeetupNotification(String senderId, String senderName, String receiverId, String location, String meetupAt) {
        super(senderId, senderName, receiverId, NotificationType.MEETUP_REQUEST);
        this.location = location;
        this.meetupAt = meetupAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetupAt() {
        return meetupAt;
    }

    public void setMeetupAt(String meetupAt) {
        this.meetupAt = meetupAt;
    }
}
