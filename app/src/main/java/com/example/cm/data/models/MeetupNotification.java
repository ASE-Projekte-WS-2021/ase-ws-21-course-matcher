package com.example.cm.data.models;

import java.util.Date;

public class MeetupNotification extends Notification{

    private String meetupId;
    private String location;
    private String meetupAt;

    public MeetupNotification() {
        super();
        type = NotificationType.MEETUP_REQUEST;
    }

    public MeetupNotification(String meetupId, String senderId, String senderName, String receiverId, String location, String meetupAt) {
        super(senderId, senderName, receiverId, NotificationType.MEETUP_REQUEST);
        this.location = location;
        this.meetupAt = meetupAt;
        this.meetupId = meetupId;
    }

    public String getMeetupId() {
        return meetupId;
    }

    public void setMeetupId(String meetupId) {
        this.meetupId = meetupId;
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
