package com.example.cm.data.models;

import java.util.Date;

public class MeetupNotification extends Notification{

    private String meetupId;
    private String location;
    private String meetupAt;

    public MeetupNotification(NotificationType type) {
        super();
        this.type = type;
    }

    public MeetupNotification(String meetupId, String senderId, String senderName,
                              String receiverId, String location, String meetupAt, NotificationType type) {
        super(senderId, senderName, receiverId, type);
        this.meetupId = meetupId;
        this.location = location;
        this.meetupAt = meetupAt;
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

    @Override
    public String toString() {
        return "Treffen " + meetupAt + " Uhr - " + location;
    }
}
