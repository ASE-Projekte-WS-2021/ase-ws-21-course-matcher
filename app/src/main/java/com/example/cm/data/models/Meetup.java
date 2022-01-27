package com.example.cm.data.models;

import java.util.Collections;
import java.util.List;

public class Meetup {

    private String requestingUser;
    private String location;
    private String time;
    private boolean isPrivate;
    private List<String> invitedFriends;
    private List<String> confirmedFriends;
    private String id;
    private String timestamp;

    public Meetup() {
    }

    public Meetup(String requestingUser, String location, String time, boolean isPrivate, List<String> invitedFriends, String timestamp) {
        this.requestingUser = requestingUser;
        this.location = location;
        this.time = time;
        this.isPrivate = isPrivate;
        this.invitedFriends = invitedFriends;
        confirmedFriends = Collections.singletonList(requestingUser);
        this.timestamp = timestamp;
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
        confirmedFriends.add(requestingUser);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
