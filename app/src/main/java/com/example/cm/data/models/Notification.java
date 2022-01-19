package com.example.cm.data.models;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Notification {

    private String id;
    private String senderId;
    private String senderName;
    private String receiverId;
    private NotificationType type;
    private Date createdAt;
    private NotificationState state;

    public Notification() {
    }

    public Notification(String senderId, String senderName, String receiverId, NotificationType type) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.type = type;
        this.createdAt = new Date();
        this.state = NotificationState.NOTIFICATION_PENDING;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Exclude
    public String getCreationTimeAgo(){
        String result = "vor ";
        Date now = new Date();
        long seconds=TimeUnit.MILLISECONDS.toSeconds(now.getTime() - createdAt.getTime());
        long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - createdAt.getTime());
        long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - createdAt.getTime());
        long days= TimeUnit.MILLISECONDS.toDays(now.getTime() - createdAt.getTime());
        long weeks = days / 7;
        if(seconds < 60) {
            result = "jetzt";
        } else if(minutes < 60) {
            result += minutes + " Minuten";
        } else if(hours < 24) {
            result += hours + " Stunden";
        } else if(weeks < 2){
            result += days + " Tagen";
        } else {
            result += (int) weeks + " Wochen";
        }
        return result;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedAtToNow() {
        this.createdAt = new Date();
    }

    public NotificationState getState() {
        return state;
    }

    public void setState(NotificationState state) {
        this.state = state;
    }

    public enum NotificationType {
        FRIEND_REQUEST,
        MEETUP_REQUEST,
        MEETUP_CANCELLED
    }

    public enum NotificationState {
        NOTIFICATION_ACCEPTED,
        NOTIFICATION_DECLINED,
        NOTIFICATION_PENDING
    }
}
