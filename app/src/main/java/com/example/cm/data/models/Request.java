package com.example.cm.data.models;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Request {

    protected String id;
    protected String senderId;
    protected String senderName;
    protected String receiverId;
    protected Date createdAt;
    protected RequestState state;

    public Request() {
    }

    public Request(String senderId, String senderName, String receiverId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.createdAt = new Date();
        this.state = RequestState.REQUEST_PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Exclude
    public String getCreationTimeAgo() {
        String result = "vor ";
        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - createdAt.getTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - createdAt.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - createdAt.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - createdAt.getTime());
        long weeks = days / 7;
        if (seconds < 60) {
            result = "jetzt";
        } else if (minutes < 60) {
            result += minutes + " Minuten";
        } else if (hours < 24) {
            result += hours + " Stunden";
        } else if (weeks < 2) {
            result += days + " Tagen";
        } else {
            result += (int) weeks + " Wochen";
        }
        return result;
    }

    public void setCreatedAtToNow() {
        this.createdAt = new Date();
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public enum RequestState {
        REQUEST_ACCEPTED,
        REQUEST_DECLINED,
        REQUEST_PENDING,
        REQUEST_ANSWERED
    }
}
