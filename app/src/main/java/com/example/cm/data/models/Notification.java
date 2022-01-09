package com.example.cm.data.models;

import java.util.Date;

public class Notification {

    private String id;
    private String senderId;
    private String senderName;
    private String receiverId;
    private NotificationType type;
    private Date createdAt;

    public Notification() {
    }

    public Notification(String senderId, String senderName, String receiverId, NotificationType type) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.type = type;
        this.createdAt = new Date();
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

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public enum NotificationType {
        FRIEND_REQUEST,
        MEETUP_REQUEST,
        MEETUP_CANCELLED
    }
}
