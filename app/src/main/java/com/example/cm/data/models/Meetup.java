package com.example.cm.data.models;


import java.util.Collections;
import java.util.List;

public class Meetup {

    private String requestingUser;
    private String location;
    private String time;
    private boolean isPrivate;
    private List<String> participants;
    private List<String> confirmedFriends;
    private String id;

    public Meetup(){
    }

    public Meetup(String requestingUser, String location, String time, boolean isPrivate, List<String> participants) {
        this.requestingUser = requestingUser;
        this.location = location;
        this.time = time;
        this.isPrivate = isPrivate;
        this.participants = participants;
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

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getConfirmedFriends() {
        return confirmedFriends;
    }

    public void setConfirmedFriends(List<String> confirmedFriends) {
        this.confirmedFriends = confirmedFriends;
    }
}
