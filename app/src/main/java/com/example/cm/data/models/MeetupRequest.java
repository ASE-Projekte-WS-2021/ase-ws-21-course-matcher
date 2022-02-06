package com.example.cm.data.models;

public class MeetupRequest extends Request {

    private String meetupId;
    private String location;
    private String meetupAt;
    private MeetupRequestType type;

    public MeetupRequest(MeetupRequestType type) {
        super();
        this.type = type;
        if(type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED){
            state = RequestState.REQUEST_ANSWERED;
        }
    }

    public MeetupRequest(String meetupId, String senderId, String senderName,
                         String receiverId, String location, String meetupAt, MeetupRequestType type) {
        super(senderId, senderName, receiverId);
        this.meetupId = meetupId;
        this.location = location;
        this.meetupAt = meetupAt;
        this.type = type;
        if(type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED){
            state = RequestState.REQUEST_ANSWERED;
        }
    }

    public MeetupRequestType getType() {
        return type;
    }

    public void setType(MeetupRequestType type) {
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
            case MEETUP_INFO_ACCEPTED:
                return "Zusage für " + meetupString;
            case MEETUP_INFO_DECLINED:
                return "Absage für " + meetupString;
            default:
                return meetupString;
        }
    }

    public enum MeetupRequestType {
        MEETUP_REQUEST,
        MEETUP_INFO_DECLINED,
        MEETUP_INFO_ACCEPTED,
    }
}
