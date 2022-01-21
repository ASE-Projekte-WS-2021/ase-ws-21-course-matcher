package com.example.cm.data.models;

public class MeetupAcceptedNotification extends MeetupNotification{

    public MeetupAcceptedNotification() {
        super(NotificationType.MEETUP_ACCEPTED);
    }

    public MeetupAcceptedNotification(String meetupId, String senderId, String senderName,
                                      String receiverId, String location, String meetupAt) {
        super(meetupId, senderId, senderName, receiverId, location, meetupAt, NotificationType.MEETUP_ACCEPTED);
    }
}
