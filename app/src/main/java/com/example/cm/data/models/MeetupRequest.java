package com.example.cm.data.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MeetupRequest extends Request {

    private String meetupId;
    private MeetupRequestType type;

    private final Calendar calendarMeetup = GregorianCalendar.getInstance();

    public MeetupRequest(MeetupRequestType type) {
        super();
        this.type = type;
        if (type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED) {
            state = RequestState.REQUEST_ANSWERED;
        }
    }

    public MeetupRequest(String meetupId, String senderId,
                         String receiverId, MeetupRequestType type) {
        super(senderId, receiverId);
        this.meetupId = meetupId;
        this.type = type;
        if (type == MeetupRequestType.MEETUP_INFO_ACCEPTED || type == MeetupRequestType.MEETUP_INFO_DECLINED) {
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

    @SuppressLint("DefaultLocale")
    @Exclude
    public String getFormattedTime() {
        return String.format("%02d:%02d Uhr", calendarMeetup.get(Calendar.HOUR_OF_DAY),
                calendarMeetup.get(Calendar.MINUTE));
    }

    @NonNull
    @Override
    public String toString() {
        String meetupString = "Treffen ";
        switch (type) {
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
