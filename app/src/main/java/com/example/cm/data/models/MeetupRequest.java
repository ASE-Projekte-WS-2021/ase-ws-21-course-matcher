package com.example.cm.data.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MeetupRequest extends Request {

    private String meetupId;
    private String location;
    private Date meetupAt;
    private MeetupRequestType type;
    private MeetupPhase phase;

    private final Calendar calendarNow = GregorianCalendar.getInstance();
    private final Calendar calendarMeetup = GregorianCalendar.getInstance();

    public MeetupRequest(MeetupRequestType type) {
        super();
        this.type = type;
        if(type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED){
            state = RequestState.REQUEST_ANSWERED;
        }
    }

    public MeetupRequest(String meetupId, String senderId, String senderName,
                         String receiverId, String location, Date meetupAt, MeetupRequestType type) {
        super(senderId, senderName, receiverId);
        this.meetupId = meetupId;
        this.location = location;
        this.meetupAt = meetupAt;
        calendarMeetup.setTime(meetupAt);
        this.type = type;
        if(type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED){
            state = RequestState.REQUEST_ANSWERED;
        }
        phase = getPhase();
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

    public Date getMeetupAt() {
        return meetupAt;
    }

    public void setMeetupAt(Date meetupAt) {
        this.meetupAt = meetupAt;
        calendarMeetup.setTime(meetupAt);
    }

    @SuppressLint("DefaultLocale")
    @Exclude
    public String getFormattedTime(){
        return String.format("%02d:%02d Uhr", calendarMeetup.get(Calendar.HOUR_OF_DAY), calendarMeetup.get(Calendar.MINUTE));
    }

    public MeetupPhase getPhase() {
        Date now = new Date();
        calendarNow.setTime(now);
        // is today?
        if (calendarNow.get(Calendar.YEAR) == calendarMeetup.get(Calendar.YEAR)
                && calendarNow.get(Calendar.MONTH) == calendarMeetup.get(Calendar.MONTH)
                && calendarNow.get(Calendar.DAY_OF_MONTH) == calendarMeetup.get(Calendar.DAY_OF_MONTH)) {
            // has started?
            if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - meetupAt.getTime()) >= 0) {
                phase = MeetupPhase.MEETUP_ACTIVE;
            } else {
                phase = MeetupPhase.MEETUP_UPCOMING;
            }
        } else {
            phase = MeetupPhase.MEETUP_ENDED;
        }
        return phase;
    }

    public void setPhase(MeetupPhase phase) {
        this.phase = phase;
    }

    @NonNull
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
