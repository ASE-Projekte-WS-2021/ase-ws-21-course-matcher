package com.example.cm.data.models;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Meetup {

    @DocumentId
    private String id;
    private String requestingUser;
    private String location;
    private Date timestamp;
    private boolean isPrivate;
    private List<String> invitedFriends;
    private List<String> confirmedFriends;
    private List<String> declinedFriends;

    private final Calendar calendarNow = GregorianCalendar.getInstance();
    private final Calendar calendarMeetup = GregorianCalendar.getInstance();

    public Meetup() {
    }

    public Meetup(String id, String requestingUser, String location, Date timestamp, boolean isPrivate, List<String> invitedFriends) {
        this.id = id;
        this.requestingUser = requestingUser;
        this.location = location;
        this.timestamp = timestamp;
        calendarMeetup.setTime(timestamp);
        this.isPrivate = isPrivate;
        this.invitedFriends = invitedFriends;
        confirmedFriends = Collections.singletonList(requestingUser);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
        if (confirmedFriends == null) {
            confirmedFriends = Collections.singletonList(requestingUser);
        }
        if (!confirmedFriends.contains(requestingUser)){
            confirmedFriends.add(requestingUser);
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        calendarMeetup.setTime(timestamp);
    }

    @SuppressLint("DefaultLocale")
    @Exclude
    public String getTimeDisplayed(){
        return String.format("%02d:%02d Uhr", calendarMeetup.get(Calendar.HOUR_OF_DAY), calendarMeetup.get(Calendar.MINUTE));
    }

    @Exclude
    public MeetupPhase getPhase() {
        Date now = new Date();
        calendarNow.setTime(now);
        // is today?
        if (calendarNow.get(Calendar.YEAR) == calendarMeetup.get(Calendar.YEAR)
                && calendarNow.get(Calendar.MONTH) == calendarMeetup.get(Calendar.MONTH)
                && calendarNow.get(Calendar.DAY_OF_MONTH) == calendarMeetup.get(Calendar.DAY_OF_MONTH)) {
            // has started?
            if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime()) >= 0) {
                return MeetupPhase.MEETUP_ACTIVE;
            } else {
                return MeetupPhase.MEETUP_UPCOMING;
            }
        }
        return MeetupPhase.MEETUP_ENDED;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<String> getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(List<String> invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public List<String> getConfirmedFriends() {
        return confirmedFriends;
    }

    public void setConfirmedFriends(List<String> confirmedFriends) {
        this.confirmedFriends = confirmedFriends;
    }

    public List<String> getDeclinedFriends() {
        return declinedFriends;
    }

    public void setDeclinedFriends(List<String> declinedFriends) {
        this.declinedFriends = declinedFriends;
    }
}
