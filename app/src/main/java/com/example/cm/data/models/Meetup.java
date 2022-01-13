package com.example.cm.data.models;


import java.util.List;

public class Meetup {

    private String requestingUser;
    private String location;
    private String time;
    private boolean isPrivate;
    private List<String> invitedFriends;

    public Meetup(String requestingUser, String location, String time, boolean isPrivate, List<String> invitedFriends) {
        this.requestingUser = requestingUser;
        this.location = location;
        this.time = time;
        this.isPrivate = isPrivate;
        this.invitedFriends = invitedFriends;
    }



    public String getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
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
}
