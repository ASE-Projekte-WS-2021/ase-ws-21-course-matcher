package com.example.cm.data.models;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;
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
    private LatLng location;
    private Date timestamp;
    private boolean isPrivate;
    private String locationImageUrl;
    private List<String> invitedFriends;
    private List<String> confirmedFriends;
    private List<String> declinedFriends;
    private List<String> lateFriends;
    private MeetupPhase phase;

    private final Calendar calendarNow = GregorianCalendar.getInstance();
    private final Calendar calendarMeetup = GregorianCalendar.getInstance();

    public Meetup() {
    }

    public Meetup(String id, String requestingUser, LatLng location, Date timestamp, boolean isPrivate, List<String> invitedFriends, String locationImageUrl) {
        this.id = id;
        this.requestingUser = requestingUser;
        this.location = location;
        this.timestamp = timestamp;
        calendarMeetup.setTime(timestamp);
        this.isPrivate = isPrivate;
        this.invitedFriends = invitedFriends;
        this.locationImageUrl = locationImageUrl;
        confirmedFriends = Collections.singletonList(requestingUser);
        phase = getPhase();
    }

    @DocumentId
    public String getId() {
        return id;
    }

    @DocumentId
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
            if (TimeUnit.MILLISECONDS.toSeconds(now.getTime() - timestamp.getTime()) >= 0) {
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getLocationImageUrl() {
        return locationImageUrl;
    }

    public void setLocationImageUrl(String locationImageUrl) {
        this.locationImageUrl = locationImageUrl;
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

    public List<String> getLateFriends() {
        return lateFriends;
    }

    public void setLateFriends(List<String> lateFriends) {
        this.lateFriends = lateFriends;
    }
}
