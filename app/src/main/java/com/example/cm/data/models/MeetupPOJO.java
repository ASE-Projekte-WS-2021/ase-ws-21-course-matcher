package com.example.cm.data.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeetupPOJO {
    private String id;
    private String requestingUser;
    private List<Double> location;
    private Date timestamp;
    private boolean isPrivate;
    private List<String> invitedFriends;
    private List<String> confirmedFriends;
    private List<String> declinedFriends;
    private String locationName;
    private Availability availability;

    private List<String> lateFriends;
    private MeetupPhase phase;

    public MeetupPOJO() {
    }

    public MeetupPOJO(Meetup meetup) {
        this.id = meetup.getId();
        this.requestingUser = meetup.getRequestingUser();
        this.isPrivate = meetup.isPrivate();
        this.invitedFriends = meetup.getInvitedFriends();
        this.confirmedFriends = meetup.getConfirmedFriends();
        this.declinedFriends = meetup.getDeclinedFriends();
        this.lateFriends = meetup.getLateFriends();
        this.phase = meetup.getPhase();
        this.timestamp = meetup.getTimestamp();

        this.location = new ArrayList<Double>();
        this.location.add(meetup.getLocation().latitude);
        this.location.add(meetup.getLocation().longitude);
        this.locationName = meetup.getLocationName();
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
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<String> getLateFriends() {
        return lateFriends;
    }

    public void setLateFriends(List<String> lateFriends) {
        this.lateFriends = lateFriends;
    }

    public MeetupPhase getPhase() {
        return phase;
    }

    public void setPhase(MeetupPhase phase) {
        this.phase = phase;
    }

    public Meetup toObject() {
        Meetup meetup = new Meetup();
        meetup.setId(this.id);
        meetup.setRequestingUser(this.requestingUser);
        meetup.setPrivate(this.isPrivate);
        meetup.setInvitedFriends(this.invitedFriends);
        meetup.setConfirmedFriends(this.confirmedFriends);
        meetup.setDeclinedFriends(this.declinedFriends);
        meetup.setLateFriends(this.lateFriends);
        meetup.setPhase(this.phase);
        meetup.setTimestamp(this.timestamp);
        meetup.setLocationName(this.locationName);

        LatLng location = new LatLng(this.location.get(0), this.location.get(1));
        meetup.setLocation(location);

        return meetup;
    }
}
