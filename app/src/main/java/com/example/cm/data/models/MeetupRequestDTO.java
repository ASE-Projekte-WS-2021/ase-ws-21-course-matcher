package com.example.cm.data.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MeetupRequestDTO extends Request {

    private String meetupId;
    private Date meetupAt;
    private MeetupRequest.MeetupRequestType type;
    private MeetupPhase phase;


    public MeetupRequestDTO(String meetupId, String senderId,
                            String receiverId, Date meetupAt, MeetupRequest.MeetupRequestType type, MeetupPhase phase) {
        super(senderId, receiverId);
        this.meetupId = meetupId;
        this.meetupAt = meetupAt;
        this.type = type;
        this.phase = phase;
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
}
