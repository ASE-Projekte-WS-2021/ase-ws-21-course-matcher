package com.example.cm.data.models;

import java.util.Date;

public class MeetupRequestDTO extends Request {

    private String meetupId;
    private final Date meetupAt;
    private MeetupRequest.MeetupRequestType type;
    private MeetupPhase phase;
    private String location;
    private String senderName;
    private String formattedTime;
    private RequestState requestState;

    public MeetupRequestDTO(String meetupId, String senderId,
                            String receiverId, String senderName, String location, Date meetupAt, MeetupRequest.MeetupRequestType type, MeetupPhase phase, String formattedTime, RequestState state) {
        super(senderId, receiverId);
        this.meetupId = meetupId;
        this.senderName = senderName;
        this.location = location;
        this.meetupAt = meetupAt;
        this.type = type;
        this.phase = phase;
        this.formattedTime = formattedTime;
        this.requestState = state;
    }

    public RequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(RequestState requestState) {
        this.requestState = requestState;
    }

    public MeetupRequest.MeetupRequestType getType() {
        return type;
    }

    public void setType(MeetupRequest.MeetupRequestType type) {
        this.type = type;
    }

    public String getMeetupId() {
        return meetupId;
    }

    public void setMeetupId(String meetupId) {
        this.meetupId = meetupId;
    }

    public Date getMeetupAt() {
        return meetupAt;
    }

    public void setPhase(MeetupPhase phase) {
        this.phase = phase;
    }

    public MeetupPhase getPhase() {
        return phase;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
}
