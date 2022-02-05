package com.example.cm.data.models;

public class MeetupNotification extends Notification{

    private String meetupId;
    private String location;
    private String meetupAt;
    private NotificationType type;

    public MeetupNotification(NotificationType type) {
        super();
        this.type = type;
        if(type == NotificationType.MEETUP_ACCEPTED || type == NotificationType.MEETUP_DECLINED){
            state = NotificationState.NOTIFICATION_ANSWERED;
        }
    }

    public MeetupNotification(String meetupId, String senderId, String senderName,
                              String receiverId, String location, String meetupAt, NotificationType type) {
        super(senderId, senderName, receiverId);
        this.meetupId = meetupId;
        this.location = location;
        this.meetupAt = meetupAt;
        if(type == NotificationType.MEETUP_ACCEPTED || type == NotificationType.MEETUP_DECLINED){
            state = NotificationState.NOTIFICATION_ANSWERED;
        }
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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
        String meetupString = "Treffen " + meetupAt + " Uhr - " + location;
        switch(type){
            case MEETUP_REQUEST:
                return meetupString + "?";
            case MEETUP_ACCEPTED:
                return "Zusage für " + meetupString;
            case MEETUP_DECLINED:
                return "Absage für " + meetupString;
            default:
                return meetupString;
        }
    }

    public enum NotificationType {
        MEETUP_REQUEST,
        MEETUP_DECLINED,
        MEETUP_ACCEPTED,
    }
}
