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

public class Meetup {

    @DocumentId
    private String id;

    private String requestingUser;
    private LatLng location;
    private Date timestamp;
    private boolean isPrivate;
    private String locationImageString;
    private List<String> invitedFriends;
    private List<String> confirmedFriends;
    private List<String> declinedFriends;
    private List<String> lateFriends;
    private String locationName;

    public Meetup() {
    }

    public Meetup(String id, String requestingUser, LatLng location, String locationName, Date timestamp, boolean isPrivate, List<String> invitedFriends, String locationImageString) {
        this.id = id;
        this.requestingUser = requestingUser;
        this.location = location;
        this.locationName = locationName;
        this.timestamp = timestamp;
        this.isPrivate = isPrivate;
        this.invitedFriends = invitedFriends;
        this.locationImageString = locationImageString;
        confirmedFriends = Collections.singletonList(requestingUser);
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
    }

    @SuppressLint("DefaultLocale")
    @Exclude
    public String getFormattedTime(){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(timestamp);
        return String.format("%02d:%02d Uhr", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocationName(String locationName){
        this.locationName = locationName;
    }

    public String getLocationName(){
        return locationName;
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

    public String getLocationImageString() {
        return locationImageString;
    }

    public void setLocationImageString(String locationImageString) {
        this.locationImageString = locationImageString;
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
